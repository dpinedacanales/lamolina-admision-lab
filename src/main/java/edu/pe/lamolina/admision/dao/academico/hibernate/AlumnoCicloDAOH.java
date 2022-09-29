package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.AlumnoCicloDAO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class AlumnoCicloDAOH extends AbstractEasyDAO<AlumnoCiclo> implements AlumnoCicloDAO {

    public AlumnoCicloDAOH() {
        super();
        setClazz(AlumnoCiclo.class);
    }

    @Override
    public AlumnoCiclo findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoCiclo.class, "ac")
                .join("alumno alu", "cicloAcademico ca")
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);

        return find(sql);
    }

    @Override
    public void deleteByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(AlumnoCiclo.class.getSimpleName()).append(" as ac ");
        sql.append("  where exists ( ");
        sql.append("    select  a.id ");
        sql.append("      from ").append(Alumno.class.getSimpleName()).append(" as a ");
        sql.append("      join a.postulantePregrado po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("     where ci.id = :CICLO ");
        sql.append("       and ac.alumno.id = a.id ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}
