package edu.pe.lamolina.admision.movil.evaluacion;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;

public interface EvaluacionRestService {

    
    public List<ExamenInteresado> allEvaluacionesByInteresado(Long idInteresado);

    public ObjectNode findActualByInteresado(Long idInteresado);

    public PreguntaExamen findPreguntaById(Long idPregunta);

    public PreguntaExamen findPreguntaSiguiente(PreguntaExamen pregunta);

    public PreguntaExamen findPreguntaAnterior(PreguntaExamen pregunta);

    public List<PreguntaExamen> allPreguntasByExamen(ExamenVirtual examenVirtual);

    public ObjectNode addRespuesta(Long idPregunta, Long idOpcion, Long idExamen, Map<Long, RespuestaInteresado> mapRespuestas);

    public void finalizarEvaluacionInteresado(Interesado interesado);

    public Boolean tieneEvaluacionActiva(Long idInteresado);

    public Boolean tienePermiso(Long idInteresado);

    public ExamenInteresado findEvaluacion(Long idEvaluacion);
    
}
