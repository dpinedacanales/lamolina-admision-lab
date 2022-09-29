package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.TallerDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.enums.VisibleEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Taller;

@Repository
public class TallerDAOH extends AbstractDAO<Taller> implements TallerDAO {

    public TallerDAOH() {
        super();
        setClazz(Taller.class);
    }

    @Override
    public List<Taller> allByCicloPostulaDynatable(DynatableFilter filter, CicloPostula ciclo) {
        List<String> fieldsFiltro = Arrays.asList("ta.titulo", "ta.descripcion");

        filter.setAlias("ta");
        filter.setFields(fieldsFiltro);
        filter.setParents(
                "cicloPostula cipo"
        );
        filter.filterFix("cipo.id", ciclo);

        filter.setTotal(this.count(filter));

        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map queries = filter.getQueries();
        if (queries != null) {
            for (Object key : queries.keySet()) {
                if (!((String) key).equals("search")) {
                    this.filterFixed(sqlUtil, (String) key, queries.get(key));
                }
            }
        }

        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue(), filter.getComplexFields());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage())
                .filter("cipo.id", ciclo);

        List<Taller> talleres = this.all(sqlUtil);

        return talleres;
    }

    @Override
    public List<Taller> allVisibles() {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ta")
                .filter("visible", VisibleEnum.SI.name());

        return this.all(sqlUtil);

    }

    @Override
    public List<Taller> allTop(int top) {
        Octavia sql = Octavia.query()
                .from(Taller.class)
                .filter("visible", VisibleEnum.SI.name())
                .orderBy("fecha ASC")
                .limit(top);

        return sql.all(getCurrentSession());
    }

}
