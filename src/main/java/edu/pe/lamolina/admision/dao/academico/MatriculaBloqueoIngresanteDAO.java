package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;
import pe.edu.lamolina.model.inscripcion.Ingresante;

public interface MatriculaBloqueoIngresanteDAO extends EasyDAO<MatriculaBloqueoIngresante> {

    List<MatriculaBloqueoIngresante> allByCicloAcademico(CicloAcademico cicloAcademico);

    MatriculaBloqueoIngresante findByCicloAcademicoIngresante(CicloAcademico cicloAcademico, Ingresante ingresante);

}
