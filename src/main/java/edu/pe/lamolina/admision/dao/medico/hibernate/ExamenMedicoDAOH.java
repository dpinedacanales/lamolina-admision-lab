package edu.pe.lamolina.admision.dao.medico.hibernate;

import edu.pe.lamolina.admision.dao.medico.ExamenMedicoDAO;
import java.util.List;
import org.hibernate.Query;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ExamenMedicoEstadoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.medico.ExamenMedico;

@Repository
public class ExamenMedicoDAOH extends AbstractEasyDAO<ExamenMedico> implements ExamenMedicoDAO {

    public ExamenMedicoDAOH() {
        super();
        setClazz(ExamenMedico.class);
    }

    @Override
    public List<ExamenMedico> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ExamenMedico.class, "em")
                .join("alumno alum", "cicloAcademico ca")
                .left("alum.persona per", "per.tipoDocumento tdoc", "alum.cicloIngreso ci", "alum.cicloActivo cia", "alum.carrera car", "alum.situacionAcademica sita")
                .filter("ca.id", cicloAcademico)
                .searchFields("per.numeroDocIdentidad", "alum.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .orderBy("alum.id desc");
        return all(sql);
    }

    @Override
    public List<ExamenMedico> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(ExamenMedico.class, "em")
                .join("alumno alum", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public ExamenMedico findByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(ExamenMedico.class, "em")
                .join("alumno alum", "cicloAcademico ca")
                .filter("alum.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

    @Override
    public List<ExamenMedico> allByCicloAcademicoEstado(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(ExamenMedico.class, "em")
                .join("alumno alum", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .notLike("em.estado", ExamenMedicoEstadoEnum.EXAM.name());
        return all(sql);
    }

    @Override
    public void deleteByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(ExamenMedico.class.getSimpleName()).append(" as em ");
        sql.append("  where exists ( ");
        sql.append("    select  a.id ");
        sql.append("      from ").append(Alumno.class.getSimpleName()).append(" as a ");
        sql.append("      join a.postulantePregrado po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("     where ci.id = :CICLO ");
        sql.append("       and em.alumno.id = a.id ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}
