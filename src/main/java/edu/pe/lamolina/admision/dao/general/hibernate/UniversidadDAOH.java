package edu.pe.lamolina.admision.dao.general.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.UniversidadDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;

@Repository
public class UniversidadDAOH extends AbstractDAO<Universidad> implements UniversidadDAO {

    public UniversidadDAOH() {
        super();
        setClazz(Universidad.class);
    }

    @Override
    public Universidad find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("u")
                .parents("pais pa")
                .filter("u.id", id);
        return find(sqlUtil);
    }

    @Override
    public List<Universidad> allActivasByPais(Pais pa) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("u")
                .parents("pais pa")
                .filter("pa.id", pa)
                .filter("u.estado", EstadoEnum.ACT.name());
        return all(sqlUtil);
    }

    @Override
    public List<Universidad> allByEstadoPais(String nombre, EstadoEnum estadoEnum, Pais pais) {
        nombre = "%" + nombre + "%";
        Octavia sql = Octavia.query(Universidad.class, "u")
                .join("pais pa")
                .leftJoin("ubicacion ub")
                .filter("u.estado", estadoEnum)
                .filter("pa.id", pais)
                .like("u.nombre", nombre);
        return sql.all(getCurrentSession());
    }
}
