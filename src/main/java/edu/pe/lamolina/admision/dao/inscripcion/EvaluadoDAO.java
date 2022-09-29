package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface EvaluadoDAO extends Crud<Evaluado> {

    List<Evaluado> allByDynaTable(CicloPostula ciclo, DynatableFilter filter);

    List<Evaluado> allByPostulanntes(List<Postulante> postulantes);

    Evaluado findByPostulante(Postulante postulante);

    Evaluado findByDNI(String dni);

    List<Evaluado> allByCarrera(Carrera carrera);
}
