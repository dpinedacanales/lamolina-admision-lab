package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.TerminosPostulante;

public interface TerminosPostulanteDAO extends Crud<TerminosPostulante> {

    List<TerminosPostulante> allByPostulante(Postulante postulante);

}
