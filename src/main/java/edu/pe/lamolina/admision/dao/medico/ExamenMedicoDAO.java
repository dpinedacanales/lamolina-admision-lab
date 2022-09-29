package edu.pe.lamolina.admision.dao.medico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.medico.ExamenMedico;

public interface ExamenMedicoDAO extends EasyDAO<ExamenMedico> {

    List<ExamenMedico> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<ExamenMedico> allByCiclo(CicloAcademico cicloAcademico);

    List<ExamenMedico> allByCicloAcademicoEstado(CicloAcademico cicloAcademico);

    void deleteByCiclo(CicloPostula ciclo);

    ExamenMedico findByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico);

}
