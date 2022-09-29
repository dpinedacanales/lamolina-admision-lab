package edu.pe.lamolina.admision.dao.calificacion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.calificacion.InfoExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface InfoExamenDAO extends Crud<InfoExamen> {

    InfoExamen findByCiclo(CicloPostula ciclo);

    List<InfoExamen> allInfoExamen(CicloPostula cicloPostula);

}
