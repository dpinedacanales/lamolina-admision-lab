package edu.pe.lamolina.admision.dao.inscripcion;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Prospecto;

public interface GuiaPostulanteDAO extends Crud<Prospecto> {

    Prospecto findByCodeCiclo(String codigo, CicloPostula ciclo);

}
