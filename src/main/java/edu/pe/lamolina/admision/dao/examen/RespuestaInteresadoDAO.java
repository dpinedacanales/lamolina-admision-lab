package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;

public interface RespuestaInteresadoDAO extends Crud<RespuestaInteresado> {

    List<RespuestaInteresado> allByInteresado(ExamenInteresado ei);

}
