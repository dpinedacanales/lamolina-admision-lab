package edu.pe.lamolina.admision.controller.admision.inscripcion.guiapostulante;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import edu.pe.lamolina.admision.zelper.pdf.PDFFormatoEnum;
import edu.pe.lamolina.admision.zelper.pdf.PdfHtmlView;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoProspectoEnum;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.CODIGO_POSTULANTE;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.ESTIMADO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NOMBRE_PERSONA;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Controller
@RequestMapping("inscripcion/guiapostulante")
public class GuiaPostulanteController {

    @Autowired
    GuiaPostulanteService service;

    @Autowired
    InscripcionService inscripcionService;

    @Autowired
    PostulanteService postulanteService;

    @Autowired
    PdfHtmlView pdfHtmlView;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.setDisallowedFields("id");

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
    public String index(HttpSession session, Model model) {

        return "redirect:/";
    }

    @RequestMapping("{tipo}/tipo")
    public String tipo(@PathVariable("tipo") String tipo, HttpSession session, Model model) {
        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            if (ds == null) {
                return "redirect:/";
            }

            Interesado interesado = service.findInteresado(ds.getInteresado());
            if (interesado == null) {
                return "redirect:/";
            }
            ModalidadEstudio modalidad = service.findModalidadEstudio(ModalidadEstudioEnum.PRE);
            CicloPostula ciclo = service.findCicloPostulaActivo(modalidad);
            if (InteresadoEstadoEnum.PROS == interesado.getEstadoEnum()) {
                model.addAttribute("esVirtual", ciclo.getEsVirtual());
                model.addAttribute("esSimulacro", ciclo.getEsSimulacro());
                return "redirect:/inscripcion/guiapostulante/recurso";
            }

            List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();

            Boolean esFinalInscripciones = inscripcionService.esFinalInscripciones(ds.getCicloPostula());
            Date fechaExamen = service.findFechaExamen(ciclo);

            model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);
            model.addAttribute("interesado", interesado);
            model.addAttribute("ciclo", ciclo);
            model.addAttribute("postulante", new Postulante());
            model.addAttribute("esFinalInscripciones", esFinalInscripciones);

            if (InteresadoEstadoEnum.REG == interesado.getEstadoEnum()) {
                Postulante postulante = service.findPostulanteByInteresado(interesado);
                if (postulante == null) {
                    model.addAttribute("tipo", tipo);
                    return "admision/guiapostulante/simple";
                }

                List<DeudaInteresado> deudas = inscripcionService.allDeudasInteresado(interesado);
                if (deudas.isEmpty()) {
                    model.addAttribute("tipo", tipo);
                    return "admision/guiapostulante/simple";
                }

                int numero = 1;
                for (DeudaInteresado deuda : deudas) {
                    deuda.getConceptoPrecio().setNumero(numero);
                    numero++;
                }
                Boolean pagoGuiaPostulante = service.pagoGuiaPostulante(interesado, deudas);

                List<EventoCiclo> eventosHoy = inscripcionService.allEventosDiaByCiclo(ds.getCicloPostula());
                Boolean esPrelamolinaActiva = inscripcionService.esPrelamolinaActiva(eventosHoy, ds.getCicloPostula());
                Boolean esInscripcionActiva = inscripcionService.esInscripcionActiva(eventosHoy, ds.getCicloPostula());
                Boolean esExtemporaneaActiva = inscripcionService.esExtemporaneaActiva(eventosHoy, ds.getCicloPostula());

                ContenidoCarta pieBoleta = service.findContenidoCartaByCodigo(ContenidoCartaEnum.FOOTWEBBOLPAGPOS);
                String contenido = pieBoleta.getContenido();

                model.addAttribute("persona", postulante.getPersona());
                model.addAttribute("estimado", postulante.getPersona().esMasculino() ? "Estimado" : "Estimada");
                model.addAttribute("postulante", postulante);
                model.addAttribute("pagoGuiaPostulante", pagoGuiaPostulante);
                model.addAttribute("deudas", deudas);
                model.addAttribute("helper", new NumberFormat());
                model.addAttribute("esPrelamolinaActiva", esPrelamolinaActiva);
                model.addAttribute("esInscripcionActiva", esInscripcionActiva);
                model.addAttribute("esExtemporaneaActiva", esExtemporaneaActiva);
                model.addAttribute("contenido", contenido);
                model.addAttribute("fechaExamen", TypesUtil.getStringDate(fechaExamen, "dd 'de' MMMM", "es"));

                return "admision/guiapostulante/infopago";
            }

            if (InteresadoEstadoEnum.CRE == interesado.getEstadoEnum()) {
                model.addAttribute("tipo", tipo);
                return "admision/guiapostulante/guiapostulante";
            }
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(Postulante postulanteForm, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        try {
            service.savePostulante(postulanteForm, ds.getInteresado(), session);
            response.setSuccess(true);
            response.setMessage("Registro realizado satisfactoriamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
            response.setMessage("Lamentamos el inconveniente, por favor comuníquese al " + AdmisionConstantine.CELULAR_HELPDESK + ".");
        }

        return response;

    }

    @RequestMapping("recurso")
    public String recurso(Model model, HttpSession session) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = service.findInteresado(ds.getInteresado());
        CicloPostula ciclo = ds.getCicloPostula();
        Postulante postulante = service.findPostulante(ds.getPostulante());

        if (InteresadoEstadoEnum.PROS != interesado.getEstadoEnum()) {
            return "redirect:/inscripcion/guiapostulante";
        }

        List<DeudaInteresado> deudasTodas = service.allByInteresado(interesado);
        Boolean pagoGuiaPostulante = service.pagoGuiaPostulante(interesado, deudasTodas);
        int numero = 1;
        List<DeudaInteresado> deudasPendientes = postulanteService.allDeudaActivaByPostulante(postulante);
        List<ConceptoPrecio> precios = deudasPendientes.stream().map(d -> d.getConceptoPrecio()).collect(Collectors.toList());
        Boolean esFinalInscripciones = inscripcionService.esFinalInscripciones(ciclo);
        Date ultimaFecha = service.findUltimaFechaInscripciones(ciclo);

        for (ConceptoPrecio precio : precios) {
            precio.setNumero(numero);
            numero++;
        }

        model.addAttribute("deudas", deudasPendientes);
        model.addAttribute("helper", new NumberFormat());
        model.addAttribute("validandoBtnExamenVirtual", service.validandoExamenVirtual(interesado, deudasTodas));
        model.addAttribute("validandoBtnAdquirirExamen", service.validandoAdquirirExamen(interesado, deudasTodas));
        model.addAttribute("validandoBtnDescargarBoleta", service.validandoDescargaBoleta(interesado, deudasTodas));
        model.addAttribute("validandoBtnDescargarGuiaPostulante", pagoGuiaPostulante);
        model.addAttribute("esFinalInscripciones", esFinalInscripciones);

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("interesado", interesado);
        model.addAttribute("postulante", postulante);
        model.addAttribute("ultimaFecha", TypesUtil.getStringDate(ultimaFecha, "dd 'de' MMMM", "es"));
        model.addAttribute("esVirtual", ciclo.getEsVirtual());
        model.addAttribute("esSimulacro", ciclo.getEsSimulacro());
        
        return "admision/guiapostulante/recurso";
    }

    @RequestMapping("generateBoletaPago")
    public ModelAndView generateBoletaPago(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        if (ds == null) {
            return new ModelAndView("redirect:/");
        }

        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();

        Postulante postulante = postulanteService.findPostulanteActivoByInteresado(interesado);
        String estimado = postulante.getPersona().esFemenino() ? "Estimada" : "Estimado";

        List<DeudaInteresado> deudas = postulanteService.allDeudaActivaByPostulante(postulante);
        ContenidoCarta headerBoletaPdf = service.findContenidoCartaByCodigo(ContenidoCartaEnum.HEAD_BOLETAPDF_GUIA);
        ContenidoCarta footerBoletaPdf = service.findContenidoCartaByCodigo(ContenidoCartaEnum.FOOT_BOLETAPDF_GUIA);
        String footer = footerBoletaPdf.getContenido();
        String header = headerBoletaPdf.getContenido();
        header = header.replace(ESTIMADO.getValue(), estimado);
        header = header.replace(NOMBRE_PERSONA.getValue(), postulante.getPersona().getNombreCompleto());
        header = header.replace(CODIGO_POSTULANTE.getValue(), postulante.getCodigo());

        model.addAttribute("header", header);
        model.addAttribute("footer", footer);
        model.addAttribute("postulante", postulante);
        model.addAttribute("deudas", deudas);
        model.addAttribute("helper", new NumberFormat());
        model.addAttribute("formatoEnum", PDFFormatoEnum.BOLETA_PAGO_GUIAPOSTULANTE);
        model.addAttribute("nombrePdf", "BoletaPago_" + postulante.getCodigo());

        return new ModelAndView(pdfHtmlView);

    }

    @RequestMapping("saveAdquirirExamen")
    public String saveAdquirirExamen(HttpSession session, RedirectAttributes redirectAttr) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();

        Postulante postulanteForm = new Postulante();

        try {
            postulanteForm.setInteresado(interesado);
            postulanteForm.setTipoProspecto(TipoProspectoEnum.examenvirtual.toString());

            service.saveAdquirirExamen(postulanteForm, ciclo);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/";
        }

        return "redirect:/inscripcion/guiapostulante/" + postulanteForm.getTipoProspecto() + "/tipo";
    }

}
