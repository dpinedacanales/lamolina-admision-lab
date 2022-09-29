package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.RecorridoIngresanteDAO;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class RecorridoIngresanteDAOH extends AbstractEasyDAO<RecorridoIngresante> implements RecorridoIngresanteDAO {

    public RecorridoIngresanteDAOH() {
        super();
        setClazz(RecorridoIngresante.class);
    }

    @Override
    public List<RecorridoIngresante> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "re")
                .join("alumno a", "cicloAcademico ci", "a.persona per")
                .leftJoin("per.tipoDocumento")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public void deleteByciclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(RecorridoIngresante.class.getSimpleName()).append(" as ri ");
        sql.append("  where exists ( ");
        sql.append("    select  a.id ");
        sql.append("      from ").append(Alumno.class.getSimpleName()).append(" as a ");
        sql.append("      join a.postulantePregrado po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("     where ci.id = :CICLO ");
        sql.append("       and ri.alumno.id = a.id ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public RecorridoIngresante findAlumnoAndCiclo(Alumno alum, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "re")
                .join("alumno a", "cicloAcademico ci", "a.persona per")
                .leftJoin("turnoEntrevistaObuae")
                .filter("ci.id", ciclo)
                .filter("a.id", alum);

        return find(sql);
    }

    @Override
    public List<RecorridoIngresante> allByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(RecorridoIngresante.class, "ri")
                .join("cicloAcademico ci", "alumno al")
                .join("al.persona per", "al.carrera car")
                .leftJoin("al.postulantePregrado pst", "turnoEntrevistaObuae tu", "per.tipoDocumento td", "pst.aulaExamen auex", "auex.aula au")
                .leftJoin("pst.modalidadIngreso moda", "pst.cicloPostula cpo", "pst.colegioProcedencia colep")
                .filter("ci.id", ciclo)
                .searchFields("al.codigo", "car.nombre", "per.numeroDocIdentidad", "td.simbolo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ri.id desc");

        return all(sql);
    }

    @Override
    public RecorridoIngresante lastAtencion(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "re")
                .join("alumno a", "cicloAcademico ci", "a.persona per")
                .filter("ci.id", cicloAcademico)
                .orderBy("re.numeroAtencion desc")
                .limit(1);
        return find(sql);
    }

    @Override
    public List<RecorridoIngresante> find(RecorridoIngresante recorrido) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "re")
                .join("alumno a", "cicloAcademico ci", "a.persona per")
                .leftJoin("per.tipoDocumento", "turnoEntrevistaObuae tur")
                .filter("re.id", recorrido);

        return all(sql);
    }

    @Override
    public RecorridoIngresante findByAlumnoCiclo(Alumno alum, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(RecorridoIngresante.class, "re")
                .join("alumno a", "cicloAcademico ci", "a.persona per")
                .filter("ci.id", ciclo)
                .filter("a.id", alum);

        return find(sql);
    }

}
