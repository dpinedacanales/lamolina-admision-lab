package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface ExamenInteresadoDAO extends Crud<ExamenInteresado> {

    ExamenInteresado findExamenInteresadoActivo(Interesado interesado);

    ExamenInteresado findLastEvaluacion(Interesado interesado);

    List<ExamenInteresado> allEvaluacionByInteresado(Interesado interesado);

    List<ExamenInteresado> allDynatableEvaluacionByInteresado(Interesado interesado, DynatableFilter filter);

    Long countByInteresado(Interesado interesado);

    List<ExamenInteresado> allFinalizadasByInteresado(Interesado interesado);

}
