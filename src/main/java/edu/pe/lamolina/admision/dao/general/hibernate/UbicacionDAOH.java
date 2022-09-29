package edu.pe.lamolina.admision.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.UbicacionDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.general.Ubicacion;

@Repository
public class UbicacionDAOH extends AbstractDAO<Ubicacion> implements UbicacionDAO {

    public UbicacionDAOH() {
        super();
        setClazz(Ubicacion.class);
    }

    @Override
    public Ubicacion find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ubdi")
                .parents("tipoUbicacion ti", "ubicacionSuperior ubpr", "_ubpr.ubicacionSuperior ubde")
                .parents("_ubpr.tipoUbicacion", "_ubde.tipoUbicacion")
                .filter("ubdi.id", id)
                .filter("ti.simbolo", "DIST");
        return find(sqlUtil);
    }

    @Override
    public List<Ubicacion> allDistritos(String nombre) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ubdi")
                .parents("tipoUbicacion ti", "ubicacionSuperior ubpr", "_ubpr.ubicacionSuperior ubde")
                .parents("_ubpr.tipoUbicacion", "_ubde.tipoUbicacion")
                .filterStr("ubdi.nombre like", nombre)
                .filter("ti.simbolo", "DIST")
                .orderBy("ubdi.nombre", "ubpr.nombre", "ubde.nombre")
                .setPageSize(15);

        return this.all(sqlUtil);
    }

    @Override
    public List<Ubicacion> allDistritos() {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ubdi")
                .parents("tipoUbicacion ti", "ubicacionSuperior ubpr", "_ubpr.ubicacionSuperior ubde")
                .parents("_ubpr.tipoUbicacion", "_ubde.tipoUbicacion")
                .filter("ti.simbolo", "DIST")
                .orderBy("ubdi.nombre", "ubpr.nombre", "ubde.nombre");

        return this.all(sqlUtil);
    }

    @Override
    public Ubicacion findByCode(String codigo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ubdi")
                .parents("tipoUbicacion ti", "ubicacionSuperior ubpr", "_ubpr.ubicacionSuperior ubde")
                .parents("_ubpr.tipoUbicacion", "_ubde.tipoUbicacion")
                .filterStr("ubdi.codigo", codigo)
                .filter("ti.simbolo", "DIST");
        return find(sqlUtil);
    }

    @Override
    public List<Ubicacion> allByTipoParent(String tipo, Long parent) {
        SqlUtil sqlUtil;

        if (parent.compareTo(0L) > 0) {
            sqlUtil = SqlUtil.creaSqlUtil("ubdi")
                    .parents("tipoUbicacion ti", "ubicacionSuperior ubpr")
                    .filter("ti.simbolo", tipo)
                    .filter("ubpr.id", parent);
        } else {
            sqlUtil = SqlUtil.creaSqlUtil("ubdi")
                    .parents("tipoUbicacion ti")
                    .filter("ti.simbolo", tipo);
        }

        sqlUtil = sqlUtil.orderBy("ubdi.nombre");

        return this.all(sqlUtil);
    }
}
