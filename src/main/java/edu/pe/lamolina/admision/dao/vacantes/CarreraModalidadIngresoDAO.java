package edu.pe.lamolina.admision.dao.vacantes;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.vacantes.CarreraModalidadIngreso;

public interface CarreraModalidadIngresoDAO extends Crud<CarreraModalidadIngreso> {

    List<CarreraModalidadIngreso> allByModalidadesCiclo(List<ModalidadIngreso> modalidades, CicloPostula ciclo);

    List<CarreraModalidadIngreso> allByCiclo(CicloPostula ciclo);

    CarreraModalidadIngreso findByCicloModalidadCarrera(CicloPostula ciclo, ModalidadIngreso modalidad, CarreraPostula carrera);

    List<CarreraModalidadIngreso> allByModalidadCiclo(ModalidadIngreso modalidadIngreso, CicloPostula ciclo);

}
