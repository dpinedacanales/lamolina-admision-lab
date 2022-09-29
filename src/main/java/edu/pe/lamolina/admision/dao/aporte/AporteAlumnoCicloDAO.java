package edu.pe.lamolina.admision.dao.aporte;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;

public interface AporteAlumnoCicloDAO extends EasyDAO<AporteAlumnoCiclo> {

    List<AporteAlumnoCiclo> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);
}
