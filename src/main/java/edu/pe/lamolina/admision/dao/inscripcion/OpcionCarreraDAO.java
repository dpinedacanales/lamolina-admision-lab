package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface OpcionCarreraDAO extends Crud<OpcionCarrera> {

    List<OpcionCarrera> allByPostulante(Postulante postulante);

    List<OpcionCarrera> allByPostulantes(List<Postulante> postulantes);

    List<OpcionCarrera> allOpcionCarrera(CicloPostula cicloPostula);

    OpcionCarrera findByPostulante(Postulante postulante);

}
