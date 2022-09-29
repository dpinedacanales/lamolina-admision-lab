package edu.pe.lamolina.admision.dao.examen;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface ExamenVirtualDAO extends Crud<ExamenVirtual> {

    ExamenVirtual findEncuestaByCicloPostula(CicloPostula cicloPostula, String tipoEncuesta);

    ExamenVirtual findExamenByCicloPostula(CicloPostula cicloPostula, String tipoExamen);

    ExamenVirtual findEncuestaActivaByCicloPostula(CicloPostula ciclo);

}
