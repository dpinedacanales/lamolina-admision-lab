package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.TurnoEntrevistaObuaeDAO;
import java.util.List;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;

@Repository
public class TurnoEntrevistaObuaeDAOH extends AbstractDAO<TurnoEntrevistaObuae> implements TurnoEntrevistaObuaeDAO {

    public TurnoEntrevistaObuaeDAOH() {
        super();
        setClazz(TurnoEntrevistaObuae.class);
    }

    @Override
    public TurnoEntrevistaObuae findByOrdenGrupoCiclo(String ordenAtencion, String grupo, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("tu")
                .parents("eventoCiclo evc", "_evc.cicloPostula cp")
                .filter("cp.id", ciclo)
                .filter("tu.grupo", grupo)
                .filter("tu.turnoInicio <=", ordenAtencion)
                .filter("tu.turnoFin >=", ordenAtencion);
        return find(sqlUtil);
    }

    @Override
    public List<TurnoEntrevistaObuae> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(TurnoEntrevistaObuae.class, "tu")
                .join("eventoCiclo evc", "evc.cicloPostula cp", "cp.cicloAcademico ca")
                .filter("ca.id", cicloAcademico);

        return sql.all(getCurrentSession());
    }
}
