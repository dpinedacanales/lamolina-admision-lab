package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;

public interface DeudaAlumnoDAO extends EasyDAO<DeudaAlumno> {

    List<DeudaAlumno> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    DeudaAlumno allByAlumnoCicloAndCuenta(Alumno alumno, CicloAcademico ciclo, CuentaBancaria bancaria);

    public List<DeudaAlumno> allByCiclo(CicloAcademico cicloAcademico);
}
