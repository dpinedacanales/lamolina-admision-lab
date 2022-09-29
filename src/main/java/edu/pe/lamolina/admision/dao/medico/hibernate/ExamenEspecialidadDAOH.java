package edu.pe.lamolina.admision.dao.medico.hibernate;

import edu.pe.lamolina.admision.dao.medico.ExamenEspecialidadDAO;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.medico.ExamenEspecialidad;
import pe.edu.lamolina.model.medico.ExamenMedico;

@Repository
public class ExamenEspecialidadDAOH extends AbstractEasyDAO<ExamenEspecialidad> implements ExamenEspecialidadDAO {

    public ExamenEspecialidadDAOH() {
        super();
        setClazz(ExamenEspecialidad.class);
    }

    @Override
    public void deleteByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(ExamenEspecialidad.class.getSimpleName()).append(" as ee ");
        sql.append("  where exists ( ");
        sql.append("    select  a.id ");
        sql.append("      from ").append(ExamenMedico.class.getSimpleName()).append(" as em ");
        sql.append("      join em.alumno a ");
        sql.append("      join a.postulantePregrado po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("     where ci.id = :CICLO ");
        sql.append("       and ee.examenMedico.id = em.id ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}
