package edu.pe.lamolina.admision.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.SituacionAcademica;

public interface SituacionAcademicaDAO extends EasyDAO<SituacionAcademica> {

    SituacionAcademica findByCodigo(String codigo);

}
