package edu.pe.lamolina.admision.controller.admision.encuesta;

import java.util.List;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface EncuestaService {

    List<EncuestaPostulante> allRespuestasByPostulante(Postulante postulante, CicloPostula ciclo);

    List<PreguntaExamen> allPreguntasByPostulante(Postulante postulante, List<EncuestaPostulante> respuestas, CicloPostula ciclo);

    void finalizarEncuesta(Interesado interesado);

    void saveRespuesta(EncuestaPostulante encuestaPostulante, Postulante postulante, CicloPostula ciclo);

    Postulante findPostulante(Interesado interesado);

}
