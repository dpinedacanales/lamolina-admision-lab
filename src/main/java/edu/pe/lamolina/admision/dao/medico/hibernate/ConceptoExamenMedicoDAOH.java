package edu.pe.lamolina.admision.dao.medico.hibernate;

import edu.pe.lamolina.admision.dao.medico.ConceptoExamenMedicoDAO;
import java.util.List;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.medico.ConceptoExamenMedico;
import pe.edu.lamolina.model.medico.ExamenMedico;

@Repository
public class ConceptoExamenMedicoDAOH extends AbstractEasyDAO<ConceptoExamenMedico> implements ConceptoExamenMedicoDAO {

    public ConceptoExamenMedicoDAOH() {
        super();
        setClazz(ConceptoExamenMedico.class);
    }

    @Override
    public ConceptoExamenMedico findByExamenMedico(ExamenMedico examenMedico) {
        Octavia sql = Octavia.query()
                .from(ConceptoExamenMedico.class, "cp")
                .join("examenMedico em", "conceptoMedicoCiclo cmc")
                .filter("em.id", examenMedico);

        return find(sql);
    }

    @Override
    public List<ConceptoExamenMedico> allByExamenMedico(ExamenMedico examenMedico) {
        Octavia sql = Octavia.query()
                .from(ConceptoExamenMedico.class, "cp")
                .join("examenMedico em", "conceptoMedicoCiclo cmc")
                .join("cmc.conceptoMedico cmed", "cmed.cuentaBancaria cban")
                .filter("em.id", examenMedico);
        return all(sql);
    }

    @Override
    public void deleteByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(ConceptoExamenMedico.class.getSimpleName()).append(" as cem ");
        sql.append("  where exists ( ");
        sql.append("    select  a.id ");
        sql.append("      from ").append(ExamenMedico.class.getSimpleName()).append(" as em ");
        sql.append("      join em.alumno a ");
        sql.append("      join a.postulantePregrado po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("     where ci.id = :CICLO ");
        sql.append("       and cem.examenMedico.id = em.id ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public ConceptoExamenMedico findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(ConceptoExamenMedico.class, "cp")
                .join("examenMedico em", "em.alumno alu", "em.cicloAcademico ci")
                .join("conceptoMedicoCiclo cmc", "cmc.conceptoMedico cm", "cm.cuentaBancaria cb")
                .filter("alu.id", alumno)
                .filter("ci.id", cicloAcademico);

        return find(sql);
    }
}
