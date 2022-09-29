package edu.pe.lamolina.admision.dao.inscripcion;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.Evento;

public interface EventoDAO extends Crud<Evento> {

    Evento findByCode(String exam);

}
