package edu.pe.lamolina.admision.dao.calificacion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.calificacion.TemaExamenDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.calificacion.TemaExamen;

@Repository
public class TemaExamenDAOH extends AbstractDAO<TemaExamen> implements TemaExamenDAO {

    public TemaExamenDAOH() {
        super();
        setClazz(TemaExamen.class);
    }

    @Override
    public TemaExamen find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("te")
                .filter("te.id", id);
        return find(sqlUtil);
    }

    @Override
    public TemaExamen findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("te")
                .filter("te.codigo", codigo);
        return find(sqlUtil);
    }
}
