package edu.pe.lamolina.admision.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.PaisDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.general.Pais;

@Repository
public class PaisDAOH extends AbstractDAO<Pais> implements PaisDAO {

    public PaisDAOH() {
        super();
        setClazz(Pais.class);
    }

    @Override
    public List<Pais> allByName(String nombre) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pa")
                .filterStr("pa.nombre LIKE", nombre)
                .orderBy("pa.nombre")
                .setPageSize(15);

        return all(sqlUtil);
    }

    @Override
    public Pais findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pa")
                .filterStr("pa.codigo", codigo);
        return find(sqlUtil);

    }

    @Override
    public List<Pais> allByCodigo(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pa")
                .filterStr("pa.codigo", codigo);
        return all(sqlUtil);
    }

    @Override
    public List<Pais> allByNotCodigo(String nombre, String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pa")
                .filterStr("pa.nombre LIKE", nombre)
                .filterStr("pa.codigo <>", codigo);
        return all(sqlUtil);
    }

}
