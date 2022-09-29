package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;

@Repository
public class ModalidadIngresoDAOH extends AbstractDAO<ModalidadIngreso> implements ModalidadIngresoDAO {

    public ModalidadIngresoDAOH() {
        super();
        setClazz(ModalidadIngreso.class);
    }

    @Override
    public ModalidadIngreso find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mi");
        sqlUtil.parents("left modalidadEstudio me", "left modalidadSuperior ms");
        sqlUtil.filter("mi.id", id);
        return find(sqlUtil);
    }

    @Override
    public List<ModalidadIngreso> allByName(String nombre) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("m")
                .filterStr("m.nombre LIKE", nombre)
                .orderBy("m.nombre");
        sqlUtil.setPageSize(15);
        return this.all(sqlUtil);
    }

    @Override
    public List<ModalidadIngreso> allByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct mi ");
        sql.append("   from ").append(ModalidadIngresoCiclo.class.getSimpleName()).append(" mic ");
        sql.append("  inner join mic.cicloPostula cp ");
        sql.append("  inner join mic.modalidadIngreso mi ");
        sql.append("  left join fetch mi.modalidadSuperior ms ");
        sql.append("  where cp.id = :CICLO ");
        sql.append("  order by mi.orden ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        return query.list();
    }

    @Override
    public ModalidadIngreso findByCodeCliclo(String codigo, CicloPostula cicloPostula) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("m")
                .filterStr("m.codigo", codigo);
        sqlUtil.setPageSize(1);
        return this.find(sqlUtil);
    }

    @Override
    public List<ModalidadIngresoCiclo> allByCicloModalidades(CicloPostula ciclo, List<ModalidadIngreso> modalidades) {
        Octavia sql = Octavia.query(ModalidadIngresoCiclo.class, "mic")
                .join("modalidadIngreso mi")
                .join("cicloPostula cp")
                .filter("cp.id", ciclo)
                .in("mi.id", modalidades)
                .orderBy("mi.orden");
        return sql.all(getCurrentSession());
    }

    @Override
    public ModalidadIngreso findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngreso.class, "m")
                .filter("m.codigo", codigo);

        return (ModalidadIngreso) sql.find(getCurrentSession());
    }
}
