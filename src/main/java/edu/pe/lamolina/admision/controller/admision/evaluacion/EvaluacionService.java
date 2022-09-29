package edu.pe.lamolina.admision.controller.admision.evaluacion;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;

public interface EvaluacionService {

    boolean tienePermiso(Interesado interesado);

    List<RespuestaInteresado> findEvaluacionPostulante(ExamenInteresado evaluacion);

    void addRespuesta(Long pregunta, Long opcion, Map<Long, RespuestaInteresado> mapRespuestas, ExamenInteresado examen);

    void finalizarEvaluacionInteresado(Interesado interesado);

    void finalizarEvaluacion(ExamenInteresado examenInteresado);

    ExamenInteresado findOrCreateEvaluacionByInteresado(Interesado interesado, ExamenInteresado evaluacion);

    ExamenInteresado findEvaluacionByInteresado(Interesado interesado, ExamenInteresado examenInteresado);

    List<ExamenInteresado> allEvaluacionByInteresado(Interesado interesado);

    Map<Long, RespuestaInteresado> allRespuestaInteresado(ExamenInteresado evaluacion);

    PreguntaExamen findPreguntaById(Long idPregunta, CicloPostula cicloPostula);

    ExamenVirtual findExamenByCiclo(CicloPostula cicloPostula);

    List<PreguntaExamen> allPreguntasByExamen(ExamenVirtual examen);

    PreguntaExamen findPreguntaSiguiente(PreguntaExamen pregunta);

    PreguntaExamen findPreguntaAnterior(PreguntaExamen pregunta);

    List<ExamenInteresado> allDynatableEvaluacionByInteresado(Interesado interesado, DynatableFilter filter);

    Long cantidadEvaluaciones(Interesado interesado);

    Persona findPersonaByInteresado(Interesado interesado);

}
