package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.MatriculaResumenDAO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class MatriculaResumenDAOH extends AbstractEasyDAO<MatriculaResumen> implements MatriculaResumenDAO {

    public MatriculaResumenDAOH() {
        super();
        setClazz(MatriculaResumen.class);
    }

    @Override
    public void deleleByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(MatriculaResumen.class.getSimpleName()).append(" as mr ");
        sql.append("  where exists ( ");
        sql.append("    select a.id ");
        sql.append("      from ").append(Alumno.class.getSimpleName()).append(" as a ");
        sql.append("      join a.postulantePregrado po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("     where ci.id = :CICLO ");
        sql.append("       and mr.alumno.id = a.id ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}
