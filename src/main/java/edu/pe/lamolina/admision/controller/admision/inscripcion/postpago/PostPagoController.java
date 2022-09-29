package edu.pe.lamolina.admision.controller.admision.inscripcion.postpago;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.beans.PropertyEditorSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.dao.general.ParametroDAO;
import edu.pe.lamolina.admision.dao.seguridad.SistemaDAO;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import edu.pe.lamolina.admision.zelper.pdf.PDFFormatoEnum;
import edu.pe.lamolina.admision.zelper.pdf.PdfHtmlView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.IDOC;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.ING;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.INS;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.NEXM;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.NING;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PAGO;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PRE;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PROS;
import pe.edu.lamolina.model.enums.RutaInicioEnum;
import pe.edu.lamolina.model.enums.SexoEnum;
import static pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum.COMP;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.CICLO_ACADEMICO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.IDENTIFICADO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NOMBRE_PERSONA;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NRO_DOCUMENTO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.TIPO_DOCUMENTO;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Ingresante;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.TokenIngresante;

@Controller
@RequestMapping("inscripcion/postpago")
public class PostPagoController {

    @Autowired
    PostPagoService service;

    @Autowired
    InscripcionService inscripcionService;

    @Autowired
    PdfHtmlView pdfHtmlView;

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    StorageService storageService;

    @Autowired
    SistemaDAO sistemaDAO;

    @Autowired
    ParametroDAO parametroDAO;

    @Autowired
    PostulanteService postulanteService;

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
        Postulante postulanteSession = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSession);
        CicloPostula cicloPostula = inscripcionService.findCicloPostula(ds.getCicloPostula());
        model.addAttribute("esSimulacro", cicloPostula.getEsSimulacro());
        model.addAttribute("esVirtual", cicloPostula.getEsVirtual());

        if (postulante.getEstadoEnum() == PRE) {
            return "redirect:/inscripcion/postulante";
        }

        if (Arrays.asList(INS, PAGO).contains(postulante.getEstadoEnum())
                && postulante.getFechaEncuesta() == null) {
            return "redirect:/inscripcion/encuesta";
        }

        if (Arrays.asList(INS, IDOC, ING, NING, NEXM).contains(postulante.getEstadoEnum())) {
            return "redirect:/inscripcion/postpago/recurso";
        }

        if (postulante.getEstadoEnum() == PAGO) {
            return "redirect:/inscripcion/postpago/info";
        }

        if (postulante.getEstadoEnum() == PROS) {
            return "redirect:/inscripcion/guiapostulante/recurso";
        }

        return "redirect:/";
    }

    @RequestMapping("info")
    public String savePaso(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        CicloPostula cicloPostula = inscripcionService.findCicloPostula(ds.getCicloPostula());

        Postulante postulanteSession = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSession);

        if (postulante.getEstadoEnum() != PostulanteEstadoEnum.PAGO) {
            return "redirect:/inscripcion/postpago";
        }

        service.postulanteFinalizar(postulante, cicloPostula);
        ds.setPostulante(postulante);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

        model.addAttribute("esSimulacro", cicloPostula.getEsSimulacro());
        model.addAttribute("esVirtual", cicloPostula.getEsVirtual());
        return "redirect:/inscripcion/postpago/recurso";
    }

    @RequestMapping("recurso")
    public String recurso(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        CicloPostula ciclo = inscripcionService.findCicloPostula(ds.getCicloPostula());
        Postulante postulanteSession = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSession);

        if (!Arrays.asList(INS, IDOC, ING, NING, NEXM).contains(postulante.getEstadoEnum())) {
            return "redirect:/inscripcion/postpago";
        }

        if (Arrays.asList(ING, IDOC).contains(postulante.getEstadoEnum())) {
            RecorridoIngresante recorridoIngresante = service.findRecorridoIngresante(postulante, ciclo);
            if (recorridoIngresante != null) {
                return "redirect:/inscripcion/hojarecorrido";
            }
        }

        Boolean pagoGuiaPostulante = service.verificarPagoGuiaPostulante(postulante);
        ModalidadIngresoCiclo modalidadCiclo = service.findModalidadCiclo(postulante.getModalidadIngreso(), ciclo);

        int loop = 1;
        List<DeudaInteresado> deudas = service.allDeudasByPostulante(postulante, ciclo);
        for (DeudaInteresado deuda : deudas) {
            deuda.setNumero(loop);
            loop++;
        }

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("esSimulacro", ciclo.getEsSimulacro());
        model.addAttribute("esVirtual", ciclo.getEsVirtual());
        model.addAttribute("postulante", postulante);
        model.addAttribute("pagoGuiaPostulante", pagoGuiaPostulante);
        model.addAttribute("modalidad", modalidadCiclo.getModalidadIngreso());
        model.addAttribute("modalidadCiclo", modalidadCiclo);
        model.addAttribute("encuestar", postulante.getFechaEncuesta() == null);
        model.addAttribute("helper", new NumberFormat());
        model.addAttribute("deudas", deudas);
        model.addAttribute("verFicha", !despliegueConfig.getAmbiente().equals("prod"));
        return "admision/postulante/postpago/recurso";
    }

    @RequestMapping("resumen")
    public String resumen(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulanteSession = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSession);
        List<OpcionCarrera> opciones = service.allOpcionCarreraByPostulante(postulante);
        List<SolicitudCambioInfo> cambios = service.allSolicitudCambioInfoByPostulante(postulante, Arrays.asList(ACT, COMP));
        SolicitudCambioInfo datPerso = null;
        SolicitudCambioInfo modalidad = null;
        SolicitudCambioInfo coleUni = null;
        SolicitudCambioInfo carreras = null;
        SolicitudCambioInfo numIdent = null;
        boolean color = false;

        for (SolicitudCambioInfo cambio : cambios) {
            if (cambio.getEstado().compareTo(EstadoEnum.ACT.name()) == 0) {
                color = true;
                switch (cambio.getTipoCambioInfo().getCodigoEnum()) {
                    case CCOLEUNIAI:
                    case CCOLEUNIDE:
                    case CCOLEUNIDI:
                        coleUni = cambio;
                        break;
                    case CDATGEN:
                        datPerso = cambio;
                        break;
                    case CMODAAI:
                    case CMODADI:
                        boolean change = checkColeUni(postulante, cambio.getModalidadIngresoNueva());
                        if (change) {
                            coleUni = cambio;
                        }
                        ModalidadIngresoCiclo modCiclo = service.findModalidadCiclo(cambio.getModalidadIngresoNueva(), ds.getCicloPostula());
                        modalidad = cambio;
                        modalidad.getModalidadIngresoNueva().setModalidadIngresoCicloActual(modCiclo);
                        break;
                    case COPCIONAI:
                    case COPCIONDI:
                        carreras = cambio;
                        break;
                    case CNUMDOC:
                        numIdent = cambio;
                        break;

                }
            }
        }

        CicloPostula ciclo = ds.getCicloPostula();

        model.addAttribute("color", color);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("postulante", postulante);
        model.addAttribute("modalidad", postulante.getModalidadIngreso());
        model.addAttribute("opciones", opciones);
        model.addAttribute("cambios", cambios);
        model.addAttribute("datPerso", datPerso);
        model.addAttribute("modModa", modalidad);
        model.addAttribute("coleUni", coleUni);
        model.addAttribute("numIdent", numIdent);
        model.addAttribute("carreras", service.allSolicitudCambioCarreraBySolicitud(carreras));
        return "admision/postulante/postpago/resumen";
    }

    @RequestMapping("terminos")
    public String terminos(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulanteSession = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSession);
        ModalidadIngresoCiclo modci = service.findModalidadCiclo(postulante.getModalidadIngreso(), postulante.getCicloPostula());
        ContenidoCarta contenido = new ContenidoCarta();
        if (modci.getRindeExamenAdmision() == 1) {
            contenido = service.findContenidoCartaByCodigoEnum(ContenidoCartaEnum.TERMS);
        } else {
            contenido = service.findContenidoCartaByCodigoEnum(ContenidoCartaEnum.TERMINOSSINEXAMEN);
        }

        CicloPostula ciclo = ds.getCicloPostula();

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("postulante", postulante);
        model.addAttribute("contenido", contenido);
        model.addAttribute("modIngresoCiclo", modci);
        return "admision/postulante/postpago/terminos";
    }

    @RequestMapping("compromiso")
    public String compromiso(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulanteSession = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSession);
        CicloPostula ciclo = ds.getCicloPostula();

        Persona persona = postulante.getPersona();
        String identi = persona.getSexoEnum() == SexoEnum.F ? "identificada"
                : (persona.getSexoEnum() == SexoEnum.M ? "identificado" : "identificado(a)");

        ModalidadIngresoCiclo modalidadCiclo = service.findModalidadCiclo(postulante.getModalidadIngreso(), ciclo);

        //    ContenidoCarta carta = service.findContenidoCartaByCodigoEnum(ContenidoCartaEnum.CARTA);
        ContenidoCarta contenido = postulanteService.getContenidoCartaTerms(postulante, modalidadCiclo);
        ContenidoCarta medidadSeguridad = postulanteService.getContenido(postulante, ContenidoCartaEnum.MEDIDA_BIOSEG);
        ContenidoCarta prohibidoExam = postulanteService.getContenido(postulante, ContenidoCartaEnum.PROHIB_EXAMEN);
        ContenidoCarta perdereVac = postulanteService.getContenido(postulante, ContenidoCartaEnum.PERDIDA_VAC);

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

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("esVirtual", ciclo.getEsVirtual());
        model.addAttribute("esSimulacro", ciclo.getEsSimulacro());
        model.addAttribute("postulante", postulante);
        model.addAttribute("cartas", cartas);
        return "admision/postulante/postpago/compromiso";
    }

    @RequestMapping("generateDeclaracionJurada")
    public ModelAndView generateDeclaracionJurada(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        if (ds == null) {
            return new ModelAndView("redirect:/");
        }

        Postulante postulante = ds.getPostulante();
        Postulante postul = service.findPostulante(postulante);

        model.addAttribute("postulante", postul);
        model.addAttribute("formatoEnum", PDFFormatoEnum.DECLARACION_JURADA);
        model.addAttribute("nombre", "Declaracion_Jurada_" + postulante.getCodigo());

        return new ModelAndView(pdfHtmlView);

    }

    @RequestMapping("generateCartaCompromiso")
    public ModelAndView generateCartaCompromiso(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        if (ds == null) {
            return new ModelAndView("redirect:/");
        }

        Postulante postulante = ds.getPostulante();
        Postulante postul = service.findPostulante(postulante);

        model.addAttribute("postulante", postul);
        model.addAttribute("formatoEnum", PDFFormatoEnum.CARTA_COMPROMISO);
        model.addAttribute("nombre", "Declaracion_Jurada_" + postulante.getCodigo());

        return new ModelAndView(pdfHtmlView);

    }

    @RequestMapping("verGuiaPostulante")
    public String verGuiaPostulante(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulante = ds.getPostulante();
        Postulante postulanteBD = service.findPostulante(postulante);
        boolean pagoGuiaPostulante = service.verificarPagoGuiaPostulante(postulanteBD);
        if (!pagoGuiaPostulante) {
            return "redirect:/inscripcion/postpago";
        }

        CicloPostula ciclo = service.findCiclo(ds.getCicloPostula());
        if (StringUtils.isEmpty(ciclo.getRutaProspecto())) {
            return "redirect:/inscripcion/postpago";
        }

        model.addAttribute("codigo", postulanteBD.getCodigo());
        return "admision/guiapostulante/flipGuiaPostulante";
    }

    @RequestMapping("pdf/guiapostulante_{postulante}.pdf")
    public void guiapostulantePostulante(@PathVariable("postulante") String codigo, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulante = ds.getPostulante();

        boolean pagoGuiaPostulante = service.verificarPagoGuiaPostulante(postulante);
        if (!pagoGuiaPostulante) {
            throw new PhobosException("Error, no realizó pago por la Guía del Postulante");
        }

        Postulante postulanteBD = service.findPostulante(postulante);
        if (!codigo.equals(postulanteBD.getCodigo())) {
            throw new PhobosException("Error, postulante con código incorrecto");
        }

        CicloPostula ciclo = service.findCiclo(ds.getCicloPostula());
        if (StringUtils.isEmpty(ciclo.getRutaProspecto())) {
            throw new PhobosException("Error, la guía del postulante aun no existe");
        }
        String codeCiclo = ciclo.getCicloAcademico().getCodigo();

        response.reset();
        response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline;filename=\"guiapostulante" + codeCiclo + "-" + codigo + ".pdf\"");
        response.getOutputStream();
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
//            String rutaGuiaLocal = GlobalConstantine.TMP_DIR + codeCiclo + ".prospecto.pdf";
//            InputStream fileStreamLocal;

//            File fileGuia = new File(rutaGuiaLocal);
//            if (fileGuia.exists() && !fileGuia.isDirectory()) {
//                fileStreamLocal = new FileInputStream(fileGuia);
//
//            } else {
            InputStream fileStreamS3 = storageService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, AdmisionConstantine.S3_PROSPECTO_DIR, ciclo.getRutaProspecto());
//                storageService.uploadFile(codigo, codigo, codigo, codigo, pagoGuiaPostulante);

            input = new BufferedInputStream(fileStreamS3, GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            IOUtils.copy(input, output);
            response.flushBuffer();

//                OutputStream outputStream = new FileOutputStream(new File(rutaGuiaLocal));
//                int read = 0;
//                byte[] bytes = new byte[1024];
//
//                while ((read = fileStreamS3.read(bytes)) != -1) {
//                    outputStream.write(bytes, 0, read);
//                }
//
//                fileGuia = new File(rutaGuiaLocal);
//                fileStreamLocal = new FileInputStream(fileGuia);
//            }
        } finally {

            close(output);
            close(input);

        }

    }

    @ResponseBody
    @RequestMapping("generateGuiaPostulante")
    public String generateGuiaPostulante(HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulante = ds.getPostulante();
        Postulante postulanteBD = service.findPostulante(postulante);
        boolean pagoGuiaPostulante = service.verificarPagoGuiaPostulante(postulante);
        if (!pagoGuiaPostulante) {
            throw new PhobosException("Error, no realizó pago por la guía del postulante");
        }

        CicloPostula ciclo = service.findCiclo(ds.getCicloPostula());
        if (StringUtils.isEmpty(ciclo.getRutaProspecto())) {
            throw new PhobosException("Error, guia del postulante no encontrada.");
        }

        response.reset();
        response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"GuiaPostulante_" + postulanteBD.getCodigo() + ".pdf\"");

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {

            InputStream fileStream = storageService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, AdmisionConstantine.S3_PROSPECTO_DIR, ciclo.getRutaProspecto());

            input = new BufferedInputStream(fileStream, GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            IOUtils.copy(input, output);
            response.flushBuffer();

        } finally {

            close(output);
            close(input);

        }

        return "YEAH";

    }

    @RequestMapping("generateConstanciaIngresante")
    public ModelAndView generateConstanciaIngresante(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        if (ds == null) {
            return new ModelAndView("redirect:/");
        }

        Postulante postulanteSession = ds.getPostulante();

        Ingresante ingresante = service.findIngresanteByPostulante(postulanteSession);
        Postulante postulante = ingresante.getPostulante();
        Persona persona = postulante.getPersona();
        Carrera carrera = ingresante.getCarrera();
        CicloPostula ciclo = ds.getCicloPostula();
        TurnoEntrevistaObuae turno = service.findTurnoPostulante(postulante, ciclo);

        String sexo = "el Sr. (Srta.)";
        if (persona.esFemenino()) {
            sexo = "la Srta.";
        }
        if (persona.esMasculino()) {
            sexo = "el Sr.";
        }

        String tipoCole;
        if (postulante.getTipoInstitucion().equals("Colegio")) {
            tipoCole = "del ";
        } else {
            tipoCole = "de la ";
        }
        tipoCole += postulante.getTipoInstitucion();

        model.addAttribute("postulante", ingresante);
        model.addAttribute("formatoEnum", PDFFormatoEnum.CONSTANCIA_INGRESANTE);
        model.addAttribute("nombrePdf", "Constancia_Ingresante_" + postulante.getCodigo());
        model.addAttribute("postulante", postulante);
        model.addAttribute("modalidad", postulante.getModalidadIngreso());
        model.addAttribute("persona", persona);
        model.addAttribute("turno", turno);
        model.addAttribute("sexo", sexo);
        model.addAttribute("tipoCole", tipoCole);
        model.addAttribute("carrera", carrera);
        model.addAttribute("ciclo", ciclo.getCicloAcademico());
        model.addAttribute("fechaHoy", TypesUtil.getStringDateLongFormat(new Date()));

        return new ModelAndView(pdfHtmlView);

    }

    @ResponseBody
    @RequestMapping("eventoVerAula")
    public JsonResponse eventoVerAula(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            CicloPostula ciclo = ds.getCicloPostula();

            EventoCiclo evento = service.getEventoAula(ciclo);

            ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
            Date hoy = new Date();
            Date fechaAula = evento.getFechaInicio();

            json.put("fecha", evento.getFechaInicio() != null ? TypesUtil.getStringDate(evento.getFechaInicio(), "EEEE dd 'de' MMMM", "es") : "");
            json.put("hora", evento.getFechaInicio() != null ? TypesUtil.getStringDate(evento.getFechaInicio(), "hh:mm a", "es") : "");
            json.put("mostrarAvisoAula", fechaAula.compareTo(hoy) >= 0);

            response.setData(json);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @RequestMapping("goIntranet")
    public String goIntranet(
            @RequestParam("ficha") String ficha,
            @RequestParam(value = "origen", required = false) String origen, Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        TokenIngresante token = service.findToken(ds.getPostulante(), ds.getCicloPostula());

        StringBuilder sb = new StringBuilder();
        sb.append("redirect:")
                .append(this.getRutaIntranet(session))
                .append("/mapache/")
                .append(token.getValor())
                .append("?pathh=")
                .append(RutaInicioEnum.valueOf(ficha).name());

        if (origen != null) {
            sb.append("&origen=");
            sb.append(origen);
        }

        logger.debug("** GOTO Intranet {} ", sb.toString());
        return sb.toString();

    }

    private String getRutaIntranet(HttpSession session) {

        AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());
        if (despliegueConfig.getAmbiente().equalsIgnoreCase(AmbienteAplicacionEnum.DESA.name())) {
            session.invalidate();
        }

        Sistema sistema = sistemaDAO.find(despliegueConfig.getSistema());
        logger.debug("**** Sistema {}", sistema.getId());
        logger.debug("**** Ambiente {} {}", ambiente.name(), ambiente.getValue());
        Parametro paramRutaIntranet = parametroDAO.findBySistemaAmbienteParametrosSistemas(sistema, ambiente, ParametrosSistemasEnum.SALTO_HACIA_INTRANET);
        logger.debug("*** Intranet {} {}", paramRutaIntranet.getId(), paramRutaIntranet.getValor());

        return paramRutaIntranet.getValor();

    }

    private boolean checkColeUni(Postulante postulanteDB, ModalidadIngreso modalidadIngresoNueva) {
        ModalidadIngresoCiclo modaNueva = service.findModalidadCiclo(modalidadIngresoNueva, postulanteDB.getCicloPostula());
        if (modaNueva.isRequiereSoloColegio() != postulanteDB.getModalidadIngresoCiclo().isRequiereSoloColegio()) {
            return true;
        }
        if (modaNueva.isRequiereSoloUniversidad() != postulanteDB.getModalidadIngresoCiclo().isRequiereSoloUniversidad()) {
            return true;
        }
        if (modaNueva.isRequiereSoloColegio() == postulanteDB.getModalidadIngresoCiclo().isRequiereSoloColegio()
                && "COAR".equals(modaNueva.getModalidadIngreso().getTipo())) {
            if (!"COAR".equals(postulanteDB.getModalidadIngreso().getTipo())) {
                return true;
            }
        }
        return false;
    }
}
