package edu.pe.lamolina.admision.movil.evaluacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;

@RestController
@RequestMapping("movil/evaluacion")
public class EvaluacionRest {

    @Autowired
    EvaluacionRestService service;

    @RequestMapping(method = RequestMethod.GET, value = "infoByInteresado")
    public ObjectNode infoByInteresado(@RequestParam Long idInteresado) {
        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("tieneEvaluacionActiva", service.tieneEvaluacionActiva(idInteresado));
        response.put("tienePermiso", service.tienePermiso(idInteresado));
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "allByInteresado")
    public ArrayNode allByInteresado(@RequestParam Long idInteresado) {
        List<ExamenInteresado> examenes = service.allEvaluacionesByInteresado(idInteresado);

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat hour = new SimpleDateFormat("HH:mm");

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        for (ExamenInteresado examen : examenes) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("fecha", date.format(examen.getFechaInicio()));
            node.put("duracion", String.format("%s - %s", hour.format(examen.getFechaInicio()), hour.format(examen.getFechaInicio())));
            node.put("puntaje", examen.getPuntaje());
            node.put("nota", examen.getNota());
            node.put("id", examen.getId());
            array.add(node);
        }

        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "resultadosByEvaluacion")
    public ObjectNode resultadosByEvaluacion(@RequestParam Long idEvaluacion) {

        ExamenInteresado examen = service.findEvaluacion(idEvaluacion);
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat hour = new SimpleDateFormat("HH:mm");

        ObjectNode node = JsonHelper.createJson(examen, JsonNodeFactory.instance, new String []{
            "id",
            "puntaje",
            "nota",
            "respuestasCorrectas",
            "respuestasIncorrectas",
            "respuestasVacias"
        });
        
        node.put("fecha", date.format(examen.getFechaInicio()));
        node.put("duracion", String.format("%s - %s", hour.format(examen.getFechaInicio()), hour.format(examen.getFechaInicio())));

        return node;
    }

    @RequestMapping(method = RequestMethod.GET, value = "findActualByInteresado")
    public ObjectNode findActualByInteresado(@RequestParam Long idInteresado) {
        return service.findActualByInteresado(idInteresado);
    }

    @RequestMapping(method = RequestMethod.POST, value = "saveRespuesta/{idPregunta}/{idOpcion}/{idExamen}")
    public ObjectNode saveRespuesta(@PathVariable Long idPregunta, @PathVariable Long idOpcion, @PathVariable Long idExamen, @RequestBody Map<Long, RespuestaInteresado> mapRespuestas) {
        return service.addRespuesta(idPregunta, idOpcion, idExamen, mapRespuestas);
    }

    @RequestMapping(method = RequestMethod.GET, value = "findPregunta")
    public ObjectNode findPregunta(@RequestParam Long idPregunta) {
        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);

        PreguntaExamen pregunta = service.findPreguntaById(idPregunta);
        PreguntaExamen siguiente = service.findPreguntaSiguiente(pregunta);
        PreguntaExamen anterior = service.findPreguntaAnterior(pregunta);

        List<PreguntaExamen> preguntas = service.allPreguntasByExamen(pregunta.getExamenVirtual());

        json.set("pregunta", createPreguntaJson(pregunta));
        json.set("siguiente", createPreguntaJson(siguiente));
        json.set("anterior", createPreguntaJson(anterior));

        return json;
    }

    private ObjectNode createPreguntaJson(PreguntaExamen pregunta) {
        return JsonHelper.createJson(pregunta, JsonNodeFactory.instance, new String[]{
            "*",
            "opcionPregunta.*",
            "bloquePreguntas.*"
        });
    }

    @RequestMapping(method = RequestMethod.POST, value = "finalizar")
    public ObjectNode finalizar(@RequestBody Interesado interesado) {
        service.finalizarEvaluacionInteresado(interesado);
        return new ObjectNode(JsonNodeFactory.instance);
    }

}
