package edu.pe.lamolina.admision.dao.medico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.medico.ConceptoExamenMedico;
import pe.edu.lamolina.model.medico.ExamenMedico;

public interface ConceptoExamenMedicoDAO extends EasyDAO<ConceptoExamenMedico> {

    ConceptoExamenMedico findByExamenMedico(ExamenMedico examenMedico);

    public void deleteByCiclo(CicloPostula ciclo);

    public ConceptoExamenMedico findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    List<ConceptoExamenMedico> allByExamenMedico(ExamenMedico examenMedico);
}
