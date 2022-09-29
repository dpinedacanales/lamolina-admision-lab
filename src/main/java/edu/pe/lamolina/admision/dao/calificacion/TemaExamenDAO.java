package edu.pe.lamolina.admision.dao.calificacion;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.calificacion.TemaExamen;

public interface TemaExamenDAO extends Crud<TemaExamen> {

    TemaExamen findByCode(String codigo);

}
