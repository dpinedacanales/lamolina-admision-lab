package edu.pe.lamolina.admision.dao.examen;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.examen.AlumnoTestSicologico;

public interface AlumnoTestSicologicoDAO extends EasyDAO<AlumnoTestSicologico> {

    AlumnoTestSicologico findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

}
