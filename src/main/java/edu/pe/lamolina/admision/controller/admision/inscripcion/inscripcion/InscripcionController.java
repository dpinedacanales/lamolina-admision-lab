package edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.inscripcion.Taller;

@Controller
@SessionAttributes("interesado")
@RequestMapping("inscripcion/facebook")
public class InscripcionController {

    @Autowired
    InscripcionService service;

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
    public String index(@RequestParam(name = "objId", required = false) String objId, Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        CicloPostula ciclo = ds.getCicloPostula();
        Interesado interesado = ds.getInteresado();
        List<TipoDocIdentidad> documentos = service.allTiposDocIdentidad();
        List<Carrera> carreras = service.allCarreras();
        List<CarreraNueva> carrerasNuevas = service.allCarreraNuevas();
        ContenidoCarta contenido = null;
        if (interesado.getEstadoEnum() != InteresadoEstadoEnum.CRE && StringUtils.isBlank(objId)) {
            return "redirect:/";
        }

        if (ciclo.getEsVirtual()) {
            contenido = service.findContenidoCartaByCodigoEnum(ContenidoCartaEnum.DECLARA_VIRTUAL);
        }

        model.addAttribute("contenido", contenido);
        model.addAttribute("interesado", interesado);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("documentos", documentos);
        model.addAttribute("carreras", carreras);
        model.addAttribute("carrerasNuevas", carrerasNuevas);
        model.addAttribute("rutaForm", "/inscripcion/facebook/save");
        model.addAttribute("esContingencia", false);
        return "admision/postulante/interesado/interesado";
    }

    @RequestMapping("save")
    public String save(@ModelAttribute("interesado") Interesado interesado, Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesadoSession = ds.getInteresado();

        if (interesadoSession.getEstadoEnum() != InteresadoEstadoEnum.CRE && interesado.getIdTaller() == null) {
            return "redirect:/";
        }

        interesado.setId(interesadoSession.getId());
        service.updateInteresado(interesado);

        ds.setInteresado(interesado);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

        Prelamolina cepre = service.buscarComoCepre(interesado, ds.getCicloPostula(), 1);

        if (interesado.getIdTaller() != null) {
            service.saveInscritoTaller(interesado);
            return "redirect:/inscripcion/facebook/taller?id=" + interesado.getIdTaller();
        }

        if (cepre == null) {
            return "redirect:/inscripcion/facebook/opcion";
        }

        ds.setPrelamolina(cepre);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

        return "redirect:/inscripcion/facebook/ingresanteCepre";
    }

    @RequestMapping("opcion")
    public String opcion(Model model, HttpSession session, RedirectAttributes redirectAttr) {

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            CicloPostula cicloPostula = service.findCicloPostula(ds.getCicloPostula());
            Interesado interesado = ds.getInteresado();

            List<DeudaInteresado> deudas = service.allDeudasInteresado(interesado);
            if (!deudas.isEmpty()) {
                return "redirect:/inscripcion/guiapostulante/regular/tipo";
            }

            Boolean esIngresantePre = service.esIngresantePre(ds);
            List<EventoCiclo> eventosHoy = service.allEventosDiaByCiclo(ds.getCicloPostula());
            Boolean esPrelamolinaActiva = service.esPrelamolinaActiva(eventosHoy, ds.getCicloPostula());
            Boolean esInscripcionActiva = service.esInscripcionActiva(eventosHoy, ds.getCicloPostula());
            Boolean esExtemporaneaActiva = service.esExtemporaneaActiva(eventosHoy, ds.getCicloPostula());
            Boolean esFinalInscripciones = service.esFinalInscripciones(ds.getCicloPostula());

            model.addAttribute("ciclo", cicloPostula);
            model.addAttribute("interesado", interesado);
            model.addAttribute("esPrelamolinaActiva", esPrelamolinaActiva);
            model.addAttribute("esInscripcionActiva", esInscripcionActiva);
            model.addAttribute("esExtemporaneaActiva", esExtemporaneaActiva);
            model.addAttribute("esFinalInscripciones", esFinalInscripciones);
            model.addAttribute("esIngresantePre", esIngresantePre);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/";

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/";
        }

        return "admision/postulante/interesado/finInteresado";

    }

    @RequestMapping("ingresanteCepre")
    public String ingresanteCepre(Model model, HttpSession session, RedirectAttributes redirectAttr) {

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            CicloPostula cicloPostula = ds.getCicloPostula();
            Interesado interesado = ds.getInteresado();
            Prelamolina cepre = ds.getPrelamolina();

            List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();
            model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);

            model.addAttribute("cepre", cepre);
            model.addAttribute("ciclo", cicloPostula);
            model.addAttribute("interesado", interesado);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/";

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/";
        }

        return "admision/postulante/interesado/ingresanteCepre";
    }

    @ResponseBody
    @RequestMapping("verificarCepre")
    public JsonResponse verificarCepre(FormCepre formCepre, Model model, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        
        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            service.verificarCepre(formCepre, session);
            service.updateCepre(session);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Credenciales correctas");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;
    }

    @RequestMapping("taller")
    public String finTaller(@RequestParam(name = "id", required = false) Long id,
            HttpSession session, Model model) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        if (ds == null) {
            return "redirect:/";
        }

        logger.debug("ID DE TALLER {}", id);

        Taller taller = service.findTaller(id);
        Interesado interesado = ds.getInteresado();
        CicloPostula cicloPostula = ds.getCicloPostula();

        model.addAttribute("taller", taller);
        model.addAttribute("ciclo", cicloPostula);
        model.addAttribute("interesado", interesado);

        return "admision/postulante/interesado/finInteresadoTaller";
    }

}
