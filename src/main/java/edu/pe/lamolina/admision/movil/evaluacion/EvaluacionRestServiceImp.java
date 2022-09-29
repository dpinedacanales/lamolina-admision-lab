package edu.pe.lamolina.admision.movil.evaluacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.controller.admision.evaluacion.EvaluacionService;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.examen.ExamenInteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;

@Service
@Transactional(readOnly = true)
public class EvaluacionRestServiceImp implements EvaluacionRestService {

    @Autowired
    EvaluacionService evaluacionService;

    @Autowired
    InteresadoDAO interesadoDAO;

    @Autowired
    CicloPostulaDAO cicloPostulaDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    ExamenInteresadoDAO examenInteresadoDAO;

    @Override
    public List<ExamenInteresado> allEvaluacionesByInteresado(Long idInteresado) {
        Interesado interesado = interesadoDAO.find(idInteresado);
        return examenInteresadoDAO.allFinalizadasByInteresado(interesado);
    }

    @Override
    public ObjectNode findActualByInteresado(Long idInteresado) {
        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);

        CicloPostula ciclo = findCicloPostulaAcual();
        Interesado interesado = interesadoDAO.find(idInteresado);

        if (!evaluacionService.tienePermiso(interesado)) {
            throw new PhobosException("No tiene permiso para realizar esta acción");
        }

        ExamenInteresado evaluacion = evaluacionService.findOrCreateEvaluacionByInteresado(interesado, null);

        Long tiempoActual = TypesUtil.getUnixTime();
        Long tiempoInicial = evaluacion.getFechaInicio().getTime();
        Long tiempoFinal = tiempoInicial + evaluacion.getTiempoEvaluacion();

        Map<Long, RespuestaInteresado> mapRespuestas = evaluacionService.allRespuestaInteresado(evaluacion);
        List<RespuestaInteresado> respuestas = new ArrayList(mapRespuestas.values());
        Collections.sort(respuestas, new RespuestaInteresado.ComparePreguntas());

        ArrayNode respuestasJson = new ArrayNode(JsonNodeFactory.instance);
        for (RespuestaInteresado respuesta : respuestas) {
            respuestasJson.add(JsonHelper.createJson(respuesta, JsonNodeFactory.instance, new String[]{
                "id",
                "opcion.id",
                "pregunta.id",
                "pregunta.texto",
                "pregunta.tema.id",
                "pregunta.tema.nombre",
                "pregunta.subtitulo.id",
                "pregunta.subtitulo.nombre",
                "pregunta.subtitulo.temaExamen.id",
                "pregunta.subtitulo.temaExamen.nombre",
                "pregunta.bloquePreguntas.id",
                "pregunta.bloquePreguntas.nombre",
                "pregunta.bloquePreguntas.contenido",
                "pregunta.bloquePreguntas.subTituloExamen.id",
                "pregunta.bloquePreguntas.subTituloExamen.nombre",
                "pregunta.bloquePreguntas.subTituloExamen.temaExamen.id",
                "pregunta.bloquePreguntas.subTituloExamen.temaExamen.nombre",
                "pregunta.opcionPregunta.id",
                "pregunta.opcionPregunta.letra",
                "pregunta.opcionPregunta.contenido",
                "pregunta.opcionPregunta.esOtro"
            }));
        }

        ObjectNode mapRespuestasJson = new ObjectNode(JsonNodeFactory.instance);
        for (Map.Entry<Long, RespuestaInteresado> entry : mapRespuestas.entrySet()) {
            mapRespuestasJson.set(entry.getKey().toString(), JsonHelper.createJson(entry.getValue(), JsonNodeFactory.instance, new String[]{
                "*",
                "examenInteresado.id",
                "opcion.id",
                "pregunta.id"
            }));
        }

        response.put("meLim", tiempoFinal);
        response.put("meRef", AdmisionConstantine.TIME_REFRESH);
        response.put("meAct", tiempoActual);
        response.set("ciclo", JsonHelper.createJson(ciclo, JsonNodeFactory.instance));
        response.set("evaluacion", JsonHelper.createJson(evaluacion, JsonNodeFactory.instance));
        response.set("preguntas", respuestasJson);
        response.set("respuestas", mapRespuestasJson);

        return response;
    }

    @Override
    public PreguntaExamen findPreguntaById(Long idPregunta) {
        return evaluacionService.findPreguntaById(idPregunta, findCicloPostulaAcual());
    }

    @Override
    public PreguntaExamen findPreguntaSiguiente(PreguntaExamen pregunta) {
        return evaluacionService.findPreguntaSiguiente(pregunta);
    }

    @Override
    public PreguntaExamen findPreguntaAnterior(PreguntaExamen pregunta) {
        return evaluacionService.findPreguntaAnterior(pregunta);
    }

    @Override
    public List<PreguntaExamen> allPreguntasByExamen(ExamenVirtual examenVirtual) {
        return evaluacionService.allPreguntasByExamen(examenVirtual);
    }

    private CicloPostula findCicloPostulaAcual() {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);
        return ciclo;
    }

    @Override
    @Transactional
    public ObjectNode addRespuesta(Long idPregunta, Long idOpcion, Long idExamen, Map<Long, RespuestaInteresado> mapRespuestas) {
        ExamenInteresado examen = examenInteresadoDAO.find(idExamen);
        this.evaluacionService.addRespuesta(idPregunta, idOpcion, mapRespuestas, examen);
        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode mapRespuestasJson = new ObjectNode(JsonNodeFactory.instance);
        for (Map.Entry<Long, RespuestaInteresado> entry : mapRespuestas.entrySet()) {
            response.set(entry.getKey().toString(), JsonHelper.createJson(entry.getValue(), JsonNodeFactory.instance, new String[]{
                "*",
                "examenInteresado.id",
                "opcion.id",
                "pregunta.id"
            }));
        }
        return response;
    }

    @Override
    @Transactional
    public void finalizarEvaluacionInteresado(Interesado interesado) {
        this.evaluacionService.finalizarEvaluacionInteresado(interesado);
    }

    @Override
    public Boolean tieneEvaluacionActiva(Long idInteresado) {
        Interesado interesado = new Interesado(idInteresado);
        ExamenInteresado examen = examenInteresadoDAO.findExamenInteresadoActivo(interesado);
        return examen != null;
    }

    @Override
    public Boolean tienePermiso(Long idInteresado) {
        Interesado interesado = new Interesado(idInteresado);
        return evaluacionService.tienePermiso(interesado);
    }

    @Override
    public ExamenInteresado findEvaluacion(Long idEvaluacion) {
        return examenInteresadoDAO.find(idEvaluacion);
    }

}
