package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface RecorridoIngresanteDAO extends EasyDAO<RecorridoIngresante> {

    List<RecorridoIngresante> allByCiclo(CicloAcademico ciclo);

    public void deleteByciclo(CicloPostula ciclo);

    public RecorridoIngresante findAlumnoAndCiclo(Alumno alum, CicloAcademico cicloPostula);

    public List<RecorridoIngresante> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo);

    public RecorridoIngresante lastAtencion(CicloAcademico cicloAcademico);

    public List<RecorridoIngresante> find(RecorridoIngresante recorridoIngresante);

    RecorridoIngresante findByAlumnoCiclo(Alumno alum, CicloAcademico ciclo);

}
