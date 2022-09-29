package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;

public interface OpcionPreguntaDAO extends EasyDAO<OpcionPregunta> {

    List<OpcionPregunta> allByPreguntas(List<PreguntaExamen> preguntas);

    List<OpcionPregunta> allByPregunta(PreguntaExamen pregunta);

    List<OpcionPregunta> allOrdenadasByEncuesta(ExamenVirtual encuesta);

    List<OpcionPregunta> allByOpciones(List<OpcionPregunta> opciones);

}
