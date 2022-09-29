package edu.pe.lamolina.admision.dao.medico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.medico.ExamenEspecialidad;

public interface ExamenEspecialidadDAO extends EasyDAO<ExamenEspecialidad> {

    void deleteByCiclo(CicloPostula ciclo);

}
