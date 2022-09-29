package edu.pe.lamolina.admision.dao.calificacion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.calificacion.TemaCiclo;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface TemaCicloDAO extends Crud<TemaCiclo> {

    List<TemaCiclo> allByFiltrosDynatable(DynatableFilter filter, CicloPostula cicloPostula);

    List<TemaCiclo> allByCiclo(CicloPostula ciclo);

}
