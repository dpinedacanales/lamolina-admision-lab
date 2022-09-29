package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface EncuestaPostulanteDAO extends EasyDAO<EncuestaPostulante> {

    List<EncuestaPostulante> allByPostulante(Postulante postulante, String tipoExamenVirtual);

    List<EncuestaPostulante> allByPostulantePreguntas(Postulante postulante, List<PreguntaExamen> preguntas);

    List<EncuestaPostulante> allByPostulanteEncuesta(Postulante postulante, ExamenVirtual encuesta);

    List<EncuestaPostulante> allByPostulantePregunta(Postulante postulante, PreguntaExamen pregunta);

}
