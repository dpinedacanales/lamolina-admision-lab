package edu.pe.lamolina.admision.dao.inscripcion;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.MetalesPostulante;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface MetalesPostulanteDAO extends Crud<MetalesPostulante> {

    MetalesPostulante findByPostulante(Postulante postulante);

}
