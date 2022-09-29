package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Taller;

public interface TallerDAO extends Crud<Taller> {

    List<Taller> allByCicloPostulaDynatable(DynatableFilter filter, CicloPostula ciclo);

    List<Taller> allVisibles();

    List<Taller> allTop(int top);

}
