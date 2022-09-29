package edu.pe.lamolina.admision.controller.admision.evaluacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;

@Controller
@RequestMapping("inscripcion/evaluacion")
public class EvaluacionController {

    @Autowired
    EvaluacionService service;

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
            if (!service.tienePermiso(interesado)) {
                return "redirect:/";
            }

            ExamenInteresado examen = service.findEvaluacionByInteresado(interesado, ds.getExamenInteresado());
            if (examen != null) {
                return "redirect:/inscripcion/evaluacion/eva";
            }

            model.addAttribute("cantidad", service.cantidadEvaluaciones(interesado));
            model.addAttribute("interesado", interesado);
            model.addAttribute("ciclo", ds.getCicloPostula());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/";

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/";
        }

        return "admision/inscripcion/evaluacion/resultado";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Interesado interesado = ds.getInteresado();
            List<ExamenInteresado> evaluacions = service.allDynatableEvaluacionByInteresado(interesado, filter);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (ExamenInteresado eva : evaluacions) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", eva.getId());
                node.put("fecha", eva.getFechaInicio() != null ? new DateTime(eva.getFechaInicio()).toString("dd/MM/yyyy") : "");
                StringBuilder hora = new StringBuilder();
                hora.append(eva.getFechaInicio() != null ? new DateTime(eva.getFechaInicio()).toString("HH:mm:ss") : "");
                hora.append(eva.getFechaFin() != null ? " - " : "");
                hora.append(eva.getFechaFin() != null ? new DateTime(eva.getFechaFin()).toString("HH:mm:ss") : "");
                node.put("hora", hora.toString());
                node.put("correcta", eva.getRespuestasCorrectas());
                node.put("incorrecta", eva.getRespuestasIncorrectas());
                node.put("vacia", eva.getRespuestasVacias());
                node.put("puntaje", eva.getPuntaje());
                node.put("nota", eva.getNota());
                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @RequestMapping("eva")
    public String eva(Model model, HttpSession session, RedirectAttributes redirectAttr) {

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Interesado interesado = ds.getInteresado();
            if (!service.tienePermiso(interesado)) {
                return "redirect:/";
            }

            ExamenInteresado evaluacion = service.findOrCreateEvaluacionByInteresado(interesado, ds.getExamenInteresado());
            if (!evaluacion.isVigente()) {
                return "redirect:/inscripcion/evaluacion/finalizar";
            }
            
            Persona persona = service.findPersonaByInteresado(interesado);

            Long tiempoActual = TypesUtil.getUnixTime();
            Long tiempoInicial = evaluacion.getFechaInicio().getTime();
            Long tiempoFinal = tiempoInicial + evaluacion.getTiempoEvaluacion();

            Map<Long, RespuestaInteresado> mapRespuestas = service.allRespuestaInteresado(evaluacion);
            List<RespuestaInteresado> respuestas = new ArrayList(mapRespuestas.values());
            Collections.sort(respuestas, new RespuestaInteresado.ComparePreguntas());

            ds.setRespuestaInteresado(mapRespuestas);
            ds.setExamenInteresado(evaluacion);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

            model.addAttribute("meLim", tiempoFinal);
            model.addAttribute("meRef", AdmisionConstantine.TIME_REFRESH);
            model.addAttribute("meAct", tiempoActual);

            model.addAttribute("ciclo", ds.getCicloPostula());
            model.addAttribute("interesado", interesado);
            model.addAttribute("persona", persona);
            model.addAttribute("evaluacion", evaluacion);
            model.addAttribute("preguntas", respuestas);
            model.addAttribute("zelp", new ZelperEvaluacion());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/";

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/";

        }

        return "admision/inscripcion/evaluacion/evaluacion";
    }

    @ResponseBody
    @RequestMapping("saveRespuesta")
    public JsonResponse saveRespuesta(
            @RequestParam("pregunta") Long preguntaId,
            @RequestParam(value = "opcion", required = false) Long opcionId,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Map<Long, RespuestaInteresado> mapRespuestas = ds.getRespuestaInteresado();
            ExamenInteresado examen = ds.getExamenInteresado();
            service.addRespuesta(preguntaId, opcionId, mapRespuestas, examen);

            ds.setRespuestaInteresado(mapRespuestas);
            ds.setExamenInteresado(examen);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;
    }

    @RequestMapping("finalizar")
    public String finalizar(HttpSession session, RedirectAttributes redirectAttr) {

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Interesado interesado = ds.getInteresado();
            service.finalizarEvaluacionInteresado(interesado);
            ds.setExamenInteresado(null);
            ds.setRespuestaInteresado(null);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/";

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/";
        }

        return "redirect:/inscripcion/evaluacion";

    }

    @ResponseBody
    @RequestMapping("counterdown")
    public JsonResponse counterdown(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;
    }

    @RequestMapping("revision")
    public String revision(Model model, HttpSession session, RedirectAttributes redirectAttr) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = ds.getInteresado();
        if (interesado.getEsAdministrador() != 1) {
            return "redirect:/inscripcion/evaluacion";
        }
        ExamenVirtual examen = service.findExamenByCiclo(ds.getCicloPostula());
        List<PreguntaExamen> preguntas = service.allPreguntasByExamen(examen);

        return "redirect:/inscripcion/evaluacion/" + preguntas.get(0).getId() + "/pregunta";
    }

    @RequestMapping("{pregunta}/pregunta")
    public String pregunta(@PathVariable("pregunta") Long idPregunta, Model model, HttpSession session, RedirectAttributes redirectAttr) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = ds.getInteresado();
        if (interesado.getEsAdministrador() != 1) {
            return "redirect:/inscripcion/evaluacion";
        }

        PreguntaExamen pregunta = service.findPreguntaById(idPregunta, ds.getCicloPostula());
        PreguntaExamen siguiente = service.findPreguntaSiguiente(pregunta);
        PreguntaExamen anterior = service.findPreguntaAnterior(pregunta);
        List<PreguntaExamen> preguntas = service.allPreguntasByExamen(pregunta.getExamenVirtual());

        model.addAttribute("preguntas", preguntas);
        model.addAttribute("pregunta", pregunta);
        model.addAttribute("siguiente", siguiente);
        model.addAttribute("anterior", anterior);
        model.addAttribute("ciclo", ds.getCicloPostula());
        model.addAttribute("zelp", new ZelperEvaluacion());
        return "admision/inscripcion/evaluacion/pregunta";

    }

}
