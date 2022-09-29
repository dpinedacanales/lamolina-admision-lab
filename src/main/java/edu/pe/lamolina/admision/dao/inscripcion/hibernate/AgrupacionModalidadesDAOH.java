package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.AgrupacionModalidadesDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.AgrupacionModalidades;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class AgrupacionModalidadesDAOH extends AbstractDAO<AgrupacionModalidades> implements AgrupacionModalidadesDAO {

    public AgrupacionModalidadesDAOH() {
        super();
        setClazz(AgrupacionModalidades.class);
    }

    @Override
    public List<AgrupacionModalidades> allByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ag")
                .parents("cicloPostula ci")
                .filter("ci.id", ciclo);
        return all(sqlUtil);
    }
}
