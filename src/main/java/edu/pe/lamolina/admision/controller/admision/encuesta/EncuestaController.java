package edu.pe.lamolina.admision.controller.admision.encuesta;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import static pe.edu.lamolina.model.enums.ModalidadIngresoEnum.CEPRE;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Controller
@RequestMapping("inscripcion/encuesta")
public class EncuestaController {

    @Autowired
    EncuestaService service;

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
    public String index(Model model, HttpSession session, RedirectAttributes redirectAttr) {

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Interesado interesado = ds.getInteresado();
            Postulante postulante = service.findPostulante(interesado);
            Boolean esPreLamolina = postulante.getModalidadIngreso() != null ? postulante.getModalidadIngreso().getCodigo().equals(CEPRE.getCode()) : false;
            Persona persona = postulante.getPersona();
            String estimado = "Estimad" + (persona.esFemenino() ? "a" : "o");

            if (postulante.getFechaEncuesta() != null) {
                return "redirect:/inscripcion/postpago";
            }

            List<EncuestaPostulante> respuestas = service.allRespuestasByPostulante(postulante, ds.getCicloPostula());
            List<PreguntaExamen> preguntas = service.allPreguntasByPostulante(postulante, respuestas, ds.getCicloPostula());

            model.addAttribute("esPreLamolina", esPreLamolina);
            model.addAttribute("ciclo", ds.getCicloPostula());
            model.addAttribute("persona", persona);
            model.addAttribute("preguntas", preguntas);
            model.addAttribute("estimado", estimado);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/inscripcion/postpago";

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/";

        }

        return "admision/inscripcion/encuesta/encuesta";
    }

    @ResponseBody
    @RequestMapping("saveRespuesta")
    public JsonResponse saveRespuesta(EncuestaPostulante encuestaPostulante, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Interesado interesado = ds.getInteresado();
            Postulante postulante = service.findPostulante(interesado);
            service.saveRespuesta(encuestaPostulante, postulante, ds.getCicloPostula());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "finalizar", method = RequestMethod.POST)
    public JsonResponse finalizar(HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Interesado interesado = ds.getInteresado();
            service.finalizarEncuesta(interesado);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;

    }
}
