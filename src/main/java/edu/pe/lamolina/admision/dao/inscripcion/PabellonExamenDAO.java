package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.PabellonExamen;

public interface PabellonExamenDAO extends Crud<PabellonExamen> {

    List<PabellonExamen> allByCiclo(CicloPostula ciclo);

    PabellonExamen findByPabellonCiclo(Aula pab, CicloPostula ciclo);

}
