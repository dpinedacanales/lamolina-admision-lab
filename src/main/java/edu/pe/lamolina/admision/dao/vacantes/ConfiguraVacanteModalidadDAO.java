package edu.pe.lamolina.admision.dao.vacantes;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.vacantes.ConfiguraVacanteModalidad;

public interface ConfiguraVacanteModalidadDAO extends Crud<ConfiguraVacanteModalidad> {

    List<ConfiguraVacanteModalidad> allByCiclo(CicloPostula ciclo);

}
