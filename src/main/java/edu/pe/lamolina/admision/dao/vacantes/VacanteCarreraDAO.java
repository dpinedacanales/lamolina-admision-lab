package edu.pe.lamolina.admision.dao.vacantes;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.vacantes.VacanteCarrera;

public interface VacanteCarreraDAO extends EasyDAO<VacanteCarrera> {

    List<VacanteCarrera> allByCiclo(CicloPostula ciclo);

    List<VacanteCarrera> allByCarreraCiclo(CarreraPostula carrera, CicloPostula ciclo);

    List<VacanteCarrera> allSupernumerariasByCiclo(CicloPostula ciclo);

    List<VacanteCarrera> allVacanteCarrera(ModalidadIngreso modalidad, CicloPostula ciclo);
}
