package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface CicloPostulaDAO extends Crud<CicloPostula> {

    CicloPostula findActivo(ModalidadEstudio modalidad);

    List<CicloPostula> allByDaynatable(DynatableFilter filter, ModalidadEstudio modalidad);

    CicloPostula findByCicloAcademico(CicloAcademico cicloAcadBD);

    List<CicloPostula> allCicloPostula();

    CicloPostula findRegularByYearNumeroCiclo(Integer year1, Integer numCiclo1);

}
