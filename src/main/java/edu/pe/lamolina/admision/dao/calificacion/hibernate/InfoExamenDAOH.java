package edu.pe.lamolina.admision.dao.calificacion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.calificacion.InfoExamenDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.calificacion.InfoExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class InfoExamenDAOH extends AbstractDAO<InfoExamen> implements InfoExamenDAO {

    public InfoExamenDAOH() {
        super();
        setClazz(InfoExamen.class);
    }

    @Override
    public InfoExamen findByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ix")
                .parents("cicloPostula cp")
                .filter("cp.id", ciclo);
        return find(sqlUtil);
    }

    @Override
    public List<InfoExamen> allInfoExamen(CicloPostula cicloPostula) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ix")
                .parents("cicloPostula cp")
                .filter("cp.id", cicloPostula);
        return all(sqlUtil);
    }

}
