package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.AgrupacionModalidades;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface AgrupacionModalidadesDAO extends Crud<AgrupacionModalidades> {

    List<AgrupacionModalidades> allByCiclo(CicloPostula ciclo);

}
