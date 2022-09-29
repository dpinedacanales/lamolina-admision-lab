package edu.pe.lamolina.admision.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface MatriculaResumenDAO extends EasyDAO<MatriculaResumen> {

    void deleleByCiclo(CicloPostula ciclo);

}
