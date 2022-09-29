package edu.pe.lamolina.admision.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface AlumnoCicloDAO extends EasyDAO<AlumnoCiclo> {

    AlumnoCiclo findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    void deleteByCiclo(CicloPostula ciclo);

}
