package edu.pe.lamolina.admision.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.ColegioDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.Ubicacion;

@Repository
public class ColegioDAOH extends AbstractDAO<Colegio> implements ColegioDAO {

    final List NIVEL_SECUNDARIA = Arrays.asList("F0", "G0", "D2");

    public ColegioDAOH() {
        super();
        setClazz(Colegio.class);
    }

    @Override
    public List<Colegio> allSecundariaByName(String nombre, Long idUbicacion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("c")
                .parents("left nivelModalidad nm", "left formaAtencion fa", "left gestionDependencia gd", "left gestion ge", "left caracteristica ca")
                .parents("left ubicacion di", "left _di.ubicacionSuperior pr", "left _pr.ubicacionSuperior de")
                .parents("left _di.tipoUbicacion u1", "left _pr.tipoUbicacion u2", "left _de.tipoUbicacion u3")
                .filterStr("c.nombre LIKE", nombre)
                .filter("di.id", idUbicacion)
                .filterIn("nm.codigo", NIVEL_SECUNDARIA)
                .orderBy("c.nombre");
        sqlUtil.setPageSize(15);

        return this.all(sqlUtil);
    }

    @Override
    public List<Colegio> allCoarSecundariaByName(String nombre, Long idUbicacion) {
        Octavia sql = Octavia.query()
                .from(Colegio.class, "c")
                .leftJoin("nivelModalidad nm", "formaAtencion fa", "gestionDependencia gd", "gestion ge", "caracteristica ca")
                .leftJoin("ubicacion di", "di.ubicacionSuperior pr", "pr.ubicacionSuperior de")
                .leftJoin("di.tipoUbicacion u1", "pr.tipoUbicacion u2", "de.tipoUbicacion u3")
                .filter("c.nombre", "like", nombre)
                .filter("c.esCoar", 1)
                .in("nm.codigo", NIVEL_SECUNDARIA)
                .orderBy("c.nombre")
                .limit(50);

        if (idUbicacion != -1L) {
            sql.filter("di.id", idUbicacion);
        }

        return sql.all(getCurrentSession());
    }

    @Override
    public Colegio find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("c")
                .parents("left nivelModalidad nm", "left formaAtencion fa", "left gestionDependencia gd", "left gestion ge", "left caracteristica ca")
                .parents("left ubicacion di", "left _di.ubicacionSuperior pr", "left _pr.ubicacionSuperior de")
                .parents("left _di.tipoUbicacion u1", "left _pr.tipoUbicacion u2", "left _de.tipoUbicacion u3")
                .filter("c.id", id);

        return find(sqlUtil);
    }

    @Override
    public Colegio findByCode(String codigoModular) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("c")
                .filter("c.codigo", codigoModular);

        return find(sqlUtil);
    }

    @Override
    public List<Colegio> allByCode(String codigoModular) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("c")
                .filter("c.codigo", codigoModular);
        return all(sqlUtil);
    }

    @Override
    public List<Colegio> allSecundariaByUbicacion(Ubicacion ubicacion) {
        Octavia sql = Octavia.query()
                .from(Colegio.class, "c")
                .leftJoin("nivelModalidad nm", "formaAtencion fa", "gestionDependencia gd", "gestion ge", "caracteristica ca")
                .leftJoin("ubicacion di", "di.ubicacionSuperior pr", "pr.ubicacionSuperior de")
                .leftJoin("di.tipoUbicacion u1", "pr.tipoUbicacion u2", "de.tipoUbicacion u3")
                .filter("di.id", ubicacion)
                .in("nm.codigo", NIVEL_SECUNDARIA)
                .orderBy("c.nombre");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Colegio> allCoarSecundaria() {
        Octavia sql = Octavia.query()
                .from(Colegio.class, "c")
                .leftJoin("nivelModalidad nm", "formaAtencion fa", "gestionDependencia gd", "gestion ge", "caracteristica ca")
                .leftJoin("ubicacion di", "di.ubicacionSuperior pr", "pr.ubicacionSuperior de")
                .leftJoin("di.tipoUbicacion u1", "pr.tipoUbicacion u2", "de.tipoUbicacion u3")
                .filter("c.esCoar", 1)
                .in("nm.codigo", NIVEL_SECUNDARIA)
                .orderBy("c.nombre")
                .limit(500);

        return sql.all(getCurrentSession());
    }

}
