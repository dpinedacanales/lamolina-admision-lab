package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

public interface PreguntaExamenDAO extends EasyDAO<PreguntaExamen> {

    PreguntaExamen findNext(long id, ExamenVirtual examen);

    PreguntaExamen findPrevious(long id, ExamenVirtual examen);

    List<PreguntaExamen> allByExamen(ExamenVirtual examen);

    List<PreguntaExamen> allByBloqueExamenVirtual(BloquePreguntas bloquePreguntas);

    List<PreguntaExamen> allByTemaExamenVirtual(TemaExamenVirtual temaExamenVirtual);

    List<PreguntaExamen> allBySubTituloExamenVirtual(SubTituloExamen subTituloExamen);

    List<PreguntaExamen> allActivasByEncuesta(ExamenVirtual encuesta);

    List<PreguntaExamen> allByOpcionesRefencia(List<OpcionPregunta> opcionesReferencia);

    List<PreguntaExamen> allByPreguntas(List<PreguntaExamen> preguntas);

}
