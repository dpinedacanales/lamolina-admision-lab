package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.controller.general.comun.BuscarService;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import edu.pe.lamolina.admision.zelper.pdf.PDFFormatoEnum;
import edu.pe.lamolina.admision.zelper.pdf.PdfHtmlView;
import java.util.Collections;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.NPOST;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.POST;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PRE;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.IDOC;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.ING;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.INS;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.NEXM;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.NING;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PAGO;
import static pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum.COMP;
import static pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum.PEND;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.MetalesPostulante;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Controller
@RequestMapping("inscripcion/postulante")
public class PostulanteController {

    @Autowired
    PostulanteService service;

    @Autowired
    InscripcionService inscripcionService;

    @Autowired
    BuscarService buscarService;

    @Autowired
    PdfHtmlView pdfHtmlView;

    @Autowired
    DespliegueConfig despliegueConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();

        Postulante postulante = service.findPostulanteActivoByInteresado(interesado);

        if (postulante == null) {
            ds.setPasoInscripcion(0);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
            return "redirect:/inscripcion/postulante/0/paso";
        }

        if (interesado.getEstadoEnum() == NPOST) {
            model.addAttribute("ciclo", ciclo);
            model.addAttribute("interesado", interesado);
            return "admision/postulante/postulante/postulantePotencial";
        }

        if (Arrays.asList(INS, IDOC, ING, NING, NEXM).contains(postulante.getEstadoEnum())) {
            return "redirect:/inscripcion/postpago";
        }

        if (postulante.getEstadoEnum() == PRE && postulante.getInteresado().getEstadoEnum() == POST) {
            ds.setPasoInscripcion(6);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
            return "redirect:/inscripcion/postulante/6/paso";
        }

        if (postulante.getEstadoEnum() == PAGO && postulante.getInteresado().getEstadoEnum() == POST) {
            return "redirect:/inscripcion/postpago";
        }

        ds.setPasoInscripcion(0);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
        return "redirect:/inscripcion/postulante/0/paso";

    }

    @RequestMapping("{paso}/paso")
    public String paso(@PathVariable("paso") Integer paso, Model model, HttpServletRequest request, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        logger.info("Paso ? {}", paso);
        logger.info("Paso Pre Inscripcion {}", ds.getPasoInscripcion());

        if (ds.getPasoInscripcion() == null) {
            return "redirect:/inscripcion/postulante";
        }

        if (paso > ds.getPasoInscripcion()) {
            return "redirect:/inscripcion/postulante/" + ds.getPasoInscripcion() + "/paso";
        }

        Interesado interesadoSession = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();
        interesadoSession.setCicloPostula(ciclo);
        Postulante postulante = service.findPostulanteActivoByInteresado(interesadoSession);
        logger.info("Postulante ----- {}", postulante);
        if (postulante != null) {
            if (postulante.getEstadoEnum() == PRE) {
                if (paso != 6) {
                    return "redirect:/inscripcion/postulante";
                }
            }
        }

        Boolean esFinalInscripciones = service.esFinalInscripciones(ds.getCicloPostula());

        CicloPostula cicloPostula = inscripcionService.findCicloPostula(ds.getCicloPostula());
        model.addAttribute("ciclo", cicloPostula);
        model.addAttribute("esSimulacro", cicloPostula.getEsSimulacro());
        model.addAttribute("esVirtual", cicloPostula.getEsVirtual());
        model.addAttribute("helper", new PostulanteHelper());
        model.addAttribute("interesado", interesadoSession);
        model.addAttribute("esFinalInscripciones", esFinalInscripciones);

        if (paso == 0) {
            ContenidoCarta contenido = service.findContenidoCartaByCodigoEnum(ContenidoCartaEnum.TERMS);
            model.addAttribute("contenido", contenido);
        }

        if (paso == 1) {

            if (postulante == null) {
                postulante = new Postulante();
            }

            Persona persona = null;
            if (postulante.getPersona() != null && !postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
                logger.info("Primera condicion------------------------------------------");
                persona = service.getPersona(postulante.getPersona());
                ds.setPersona(persona);
                session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
            }
            if (postulante.getPersona() != null && postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
                logger.info("Segundao cocnicion.......................");
                postulante.setPersona(null);
            }

            if (persona == null) {
                service.completarPostulante(postulante, interesadoSession, ciclo);
                persona = postulante.getPersona();

                if (persona.getTipoDocumento() == null) {
                    List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();
                    model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);

                } else {
                    ds.setPersona(persona);
                    session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
                }
            }

            DateTime haceYears = new DateTime().minusYears(12);
            Date limiteFechaNacer = new DateTime(haceYears.getYear() + "-12-31").toDate();

            model.addAttribute("postulante", postulante);
            model.addAttribute("persona", persona);
            model.addAttribute("paisNacer", persona.getPaisNacer());
            model.addAttribute("paisDomicilio", persona.getPaisDomicilio());
            model.addAttribute("ubicacionNacer", persona.getUbicacionNacer());
            model.addAttribute("nacionalidad", persona.getNacionalidad());
            model.addAttribute("ubicacionDomicilio", persona.getUbicacionDomicilio());
            model.addAttribute("limiteFechaNacer", limiteFechaNacer);
        }

        if (paso == 2) {
            service.crearPrelamolinaByPersona(postulante, ciclo);
            ds.setPostulante(postulante);
            ds.setPersona(postulante.getPersona());
            ds.setPasoInscripcion(paso + 1);

            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
            return "redirect:/inscripcion/postulante/3/paso";
        }

        if (paso == 3) {
            List<ModalidadWeb> modalidadez = new ArrayList();
            List<GradoSecundaria> grados = service.allGrado();
            List<ModalidadIngreso> modalidades = service.allModalidadesByCiclo(ciclo);
            for (ModalidadIngreso modal : modalidades) {
                modalidadez.add(new ModalidadWeb(modal));
            }

            model.addAttribute("carreraz", new ArrayList());
            model.addAttribute("modalidadez", modalidadez);
            model.addAttribute("modalidades", modalidades);
            model.addAttribute("grados", grados);
            model.addAttribute("postulante", postulante);
            model.addAttribute("modalidad", postulante.getModalidadIngreso());
        }
        if (paso == 4) {
            MetalesPostulante metalPos = service.findMetalPostulanteByPostulante(postulante);
            model.addAttribute("postulante", postulante);
            model.addAttribute("metalPostulante", metalPos);
        }

        if (paso == 5) {
            ModalidadIngresoCiclo modalidadCiclo = service.findModalidadCiclo(postulante.getModalidadIngreso(), ciclo);
            ContenidoCarta contenido = service.getContenidoCartaTerms(postulante, modalidadCiclo);
            ContenidoCarta medidadSeguridad = service.getContenido(postulante, ContenidoCartaEnum.MEDIDA_BIOSEG);
            ContenidoCarta prohibidoExam = service.getContenido(postulante, ContenidoCartaEnum.PROHIB_EXAMEN);
            ContenidoCarta perdereVac = service.getContenido(postulante, ContenidoCartaEnum.PERDIDA_VAC);

            if (modalidadCiclo.getRindeExamenAdmision() == 1) {
                model.addAttribute("verTerminos", false);
                model.addAttribute("titulo", "DECLARACIÓN JURADA"); //ciclo.getEsVirtual() ? "DECLARACIÓN JURADA" : "CARTA DE COMPROMISO"

            } else {
                model.addAttribute("verTerminos", true);
                model.addAttribute("contenido", contenido.getContenido());
                model.addAttribute("titulo", "TERMINOS Y CONDICIONES DEL INGRESANTE");
            }
            List<ContenidoCarta> cartas = new ArrayList<>();
            cartas.add(contenido);
            cartas.add(medidadSeguridad);
            cartas.add(prohibidoExam);
            cartas.add(perdereVac);
            model.addAttribute("cartas", cartas);
            model.addAttribute("postulante", postulante);
            model.addAttribute("pre", postulante.getModalidadIngreso().isPreLaMolina());
        }

        if (paso == 6) {

            List<DeudaInteresado> deudas = service.allDeudaActivaByPostulante(postulante);
            if (deudas.isEmpty()) {
                return "redirect:/inscripcion/postpago";
            }

            int numero = 1;
            Collections.sort(deudas, new DeudaInteresado.CompareCtaBanco());
            for (DeudaInteresado deuda : deudas) {
                deuda.setNumero(numero);
                numero++;
            }

            Boolean pagoGuiaPostulante = service.verificarPagoGuiaPostulante(postulante);
            ModalidadIngresoCiclo modalidadCiclo = service.findModalidadCiclo(postulante.getModalidadIngreso(), ciclo);

            ContenidoCarta header = service.findHeaderBoletaWeb(postulante, modalidadCiclo);
            ContenidoCarta footer = service.findFooterBoletaWeb(postulante, modalidadCiclo);

            model.addAttribute("persona", postulante.getPersona());
            model.addAttribute("estimado", postulante.getPersona().esMasculino() ? "Estimado" : "Estimada");
            model.addAttribute("postulante", postulante);
            model.addAttribute("pagoGuiaPostulante", pagoGuiaPostulante);
            model.addAttribute("modalidad", postulante.getModalidadIngreso());
            model.addAttribute("modalidadCiclo", modalidadCiclo);
            model.addAttribute("deudas", deudas);
            model.addAttribute("despliegueConfig", despliegueConfig);
            model.addAttribute("helper", new NumberFormat());
            model.addAttribute("header", header.getContenido());
            model.addAttribute("footer", footer.getContenido());
        }

        return "admision/postulante/postulante/paso" + paso;
    }

    @RequestMapping("siguientePaso")
    public String siguientePaso(Model model, HttpServletRequest request, HttpSession session) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        CicloPostula cicloPostula = inscripcionService.findCicloPostula(ds.getCicloPostula());
        if (ds.getPasoInscripcion() == 4) {
            Postulante postulante = ds.getPostulante();
            ModalidadIngreso modalidad = postulante.getModalidadIngreso();
            if ((modalidad != null && modalidad.isPreLaMolina()) || cicloPostula.getEsVirtual()) {
                ds.setPasoInscripcion(5);
                session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
            }
        }

        return "redirect:/inscripcion/postulante/" + ds.getPasoInscripcion() + "/paso";
    }

    @ResponseBody
    @RequestMapping("savePaso/{paso}/paso")
    public JsonResponse savePaso(Postulante postulanteForm, @PathVariable("paso") Integer paso, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            save(postulanteForm, paso, session);
            response.setData(ds.getPasoInscripcion());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
            response.setMessage("Lamentamos el inconveniente, por favor comuníquese al " + AdmisionConstantine.CELULAR_HELPDESK + ".");
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("saveTerminos/{idCarta}")
    public JsonResponse saveTerminos(Postulante postulanteForm, @PathVariable("idCarta") Long idCarta, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            service.saveTerminos(postulanteForm, idCarta, session);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
            response.setMessage("Lamentamos el inconveniente, por favor comuníquese al " + AdmisionConstantine.CELULAR_HELPDESK + ".");
        }

        return response;

    }

    private void save(Postulante postulanteForm, Integer paso, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesadoSession = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();
        interesadoSession.setCicloPostula(ciclo);

        if (paso == 0) {
            ds.setPasoInscripcion(paso + 1);

        } else if (paso == 1) {
            Persona personaSession = ds.getPersona();
            Postulante postulante = service.saveDatosPersonales(postulanteForm, personaSession, interesadoSession, ciclo);

            ds.setPersona(postulante.getPersona());
            ds.setPostulante(postulante);
            ds.setPasoInscripcion(paso + 1);

        } else if (paso == 2) {
            ds.setPasoInscripcion(paso + 1);

        } else if (paso == 3) {
            Postulante postulante = service.findPostulanteActivoByInteresado(interesadoSession);
            postulanteForm.setId(postulante.getId());
            postulanteForm.setCicloPostula(ciclo);
            service.saveDatosAcademicos(postulanteForm, ciclo);

            ds.setPasoInscripcion(paso + 1);
            if (postulante.getModalidadIngreso().isPreLaMolina()) {
                ds.setPasoInscripcion(paso + 2);
            }
        } else if (paso == 4) {
            Postulante postulante = service.findPostulanteActivoByInteresado(interesadoSession);
            postulanteForm.getMetalesPostulante().setPostulante(postulante);
            service.saveMetalesPostulante(postulanteForm.getMetalesPostulante());

            ds.setPasoInscripcion(paso + 1);

        } else if (paso == 5) {
            service.saveCerrarInscripcion(postulanteForm, ciclo);
            service.actualizarImportes(postulanteForm, ciclo);
            ds.setPasoInscripcion(paso + 1);

        } else {
            ds.setPasoInscripcion(0);
        }
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

    }

    @RequestMapping("resumen")
    public String resumen(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulante = ds.getPostulante();
        Postulante postul = service.findPostulante(postulante);
        List<OpcionCarrera> opciones = postul.getOpcionCarrera();

        List<SolicitudCambioInfo> cambios = service.allSolicitudCambioInfoByPostulante(postul, Arrays.asList(ACT, COMP, PEND));

        model.addAttribute("ciclo", ds.getCicloPostula());
        model.addAttribute("opciones", opciones);
        model.addAttribute("postulante", postul);
        model.addAttribute("modalidad", postul.getModalidadIngreso());
        model.addAttribute("cambios", cambios);

        return "admision/postulante/postulante/resumen";
    }

    @RequestMapping("generateCartaCompromiso")
    public ModelAndView generateCartaCompromiso(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        if (ds == null) {
            return new ModelAndView("redirect:/");
        }

        Postulante postulante = ds.getPostulante();
        Postulante postul = service.findPostulante(postulante);

        logger.debug("Inicio creacion DeclaracionJurada");
        model.addAttribute("postulante", postul);
        model.addAttribute("formatoEnum", PDFFormatoEnum.CARTA_COMPROMISO);
        model.addAttribute("nombre", "Declaracion_Jurada_" + postulante.getCodigo());

        return new ModelAndView(pdfHtmlView);

    }

    @RequestMapping("generateBoletaPago")
    public ModelAndView generateBoletaPago(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        if (ds == null) {
            return new ModelAndView("redirect:/");
        }

        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = inscripcionService.findCicloPostula(ds.getCicloPostula());
        interesado.setCicloPostula(ciclo);
        Postulante postulante = service.findPostulanteActivoByInteresado(interesado);
        String estimado = postulante.getPersona().esFemenino() ? "Estimada" : "Estimado";

        int numero = 1;
        List<DeudaInteresado> deudas = service.allDeudaActivaByPostulante(postulante);
        Collections.sort(deudas, new DeudaInteresado.CompareCtaBanco());
        for (DeudaInteresado deuda : deudas) {
            deuda.setNumero(numero);
            numero++;
        }

        ModalidadIngresoCiclo modalidadCiclo = service.findModalidadCiclo(postulante.getModalidadIngreso(), ciclo);
        ContenidoCarta header = service.findHeaderBoletaPdf(postulante, modalidadCiclo);
        ContenidoCarta footer = service.findFooterBoletaPdf(postulante, modalidadCiclo);

        model.addAttribute("header", header.getContenido());
        model.addAttribute("footer", footer.getContenido());
        model.addAttribute("estimado", estimado);
        model.addAttribute("persona", postulante.getPersona());
        model.addAttribute("postulante", postulante);
        model.addAttribute("modalidad", postulante.getModalidadIngreso());
        model.addAttribute("deudas", deudas);
        model.addAttribute("esSimulacro", ciclo.getEsSimulacro());
        model.addAttribute("esVirtual", ciclo.getEsVirtual());
        model.addAttribute("helper", new NumberFormat());
        model.addAttribute("formatoEnum", PDFFormatoEnum.BOLETA_PAGO);
        model.addAttribute("nombrePdf", "BoletaPago_" + postulante.getCodigo());

        return new ModelAndView(pdfHtmlView);

    }

    @ResponseBody
    @RequestMapping("sendEmailBoletaPago/{postulante}/postulante")
    public JsonResponse sendEmailBoletaPago(@PathVariable("postulante") Integer idPostulante, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            CicloPostula ciclo = ds.getCicloPostula();
            service.sendEmailBoletaPago(new Postulante(idPostulante), ciclo);
            response.setMessage("Email enviado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping(value = "uploadArchivosFile", method = RequestMethod.POST)
    public JsonResponse uploadBoletaFile(@RequestParam("files") MultipartFile archivo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            String fileExt = TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())).toLowerCase();
            String fileName = TypesUtil.getUnixTime() + archivo.getOriginalFilename();
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;
            FileHelper.saveToDisk(archivo, absoluteName);

            ObjectNode json = new ObjectNode(jFactory);
            json.put("name", fileName);
            json.put("originalFilename", archivo.getOriginalFilename());
            json.put("contentType", archivo.getContentType());
            json.put("size", archivo.getSize());
            json.put("ruta", absoluteName);

            response.setData(json);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
