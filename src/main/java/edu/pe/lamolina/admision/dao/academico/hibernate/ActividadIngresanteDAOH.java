package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.ActividadIngresanteDAO;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class ActividadIngresanteDAOH extends AbstractEasyDAO<ActividadIngresante> implements ActividadIngresanteDAO {

    public ActividadIngresanteDAOH() {
        super();
        setClazz(ActividadIngresante.class);
    }

    @Override
    public void deleteByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(ActividadIngresante.class.getSimpleName()).append(" as ai ");//ac
        sql.append("  where exists ( ");
        sql.append("    select  a.id ");
        sql.append("      from ").append(RecorridoIngresante.class.getSimpleName()).append(" as ri "); //a
        sql.append("      join ri.alumno a ");
        sql.append("      join a.postulantePregrado po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("     where ci.id = :CICLO ");
        sql.append("       and ai.recorridoIngresante.id = ri.id ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public List<ActividadIngresante> allByRecorridoIngresantes(List<RecorridoIngresante> recorridoIngresantes) {
        Octavia sql = Octavia.query(ActividadIngresante.class, "ai")
                .join("recorridoIngresante ri", "tipoActividadIngresante tai")
                .in("ri.id", recorridoIngresantes);
        return all(sql);
    }

    @Override
    public List<ActividadIngresante> allByRecorridoIngresante(RecorridoIngresante recorridoIngresante) {
        Octavia sql = Octavia.query(ActividadIngresante.class, "ai")
                .join("recorridoIngresante ri", "tipoActividadIngresante tai")
                .filter("ri.id", recorridoIngresante);
        return all(sql);
    }

    @Override
    public ActividadIngresante findByRecorridoTipo(TipoActividadIngresante tipoActividadIngresante, RecorridoIngresante recorridoIngresante) {
        Octavia sql = Octavia.query(ActividadIngresante.class, "ai")
                .join("recorridoIngresante ri", "tipoActividadIngresante tai")
                .filter("ri.id", recorridoIngresante)
                .filter("tai.id", tipoActividadIngresante);
        return find(sql);
    }

    @Override
    public ActividadIngresante findByRecorridoCodigoTipo(TipoActividadIngresanteEnum tipoActividadIngresanteEnum, RecorridoIngresante recorridoIngresante) {
        Octavia sql = Octavia.query(ActividadIngresante.class, "ai")
                .join("recorridoIngresante ri", "tipoActividadIngresante tai")
                .filter("ri.id", recorridoIngresante)
                .filter("tai.codigo", tipoActividadIngresanteEnum);
        return find(sql);
    }

}
