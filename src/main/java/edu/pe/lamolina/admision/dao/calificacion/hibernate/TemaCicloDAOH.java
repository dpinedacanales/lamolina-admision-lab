package edu.pe.lamolina.admision.dao.calificacion.hibernate;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.calificacion.TemaCicloDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.calificacion.TemaCiclo;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class TemaCicloDAOH extends AbstractDAO<TemaCiclo> implements TemaCicloDAO {

    public TemaCicloDAOH() {
        super();
        setClazz(TemaCiclo.class);
    }

    @Override
    public TemaCiclo find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("tc");
        sqlUtil.parents("cicloPostula cp", "temaExamen te");
        sqlUtil.filter("tc.id", id);
        return find(sqlUtil);
    }

    @Override
    public List<TemaCiclo> allByFiltrosDynatable(DynatableFilter filter, CicloPostula cicloPostula) {
        filter.setAlias("tc");
        filter.setParents("cicloPostula cp", "temaExamen te");
        filter.filterFix("cp.id", cicloPostula.getId());

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        if (filtersFix != null) {
            for (Object key : filtersFix.keySet()) {
                this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
            }
        }
        Map filtersInFix = filter.getFiltersInFixed();
        if (filtersInFix != null) {
            for (Object key : filtersInFix.keySet()) {
                this.filterInFixed(sqlUtil, (String) key, (List) filtersInFix.get(key));
            }
        }

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
                .orderBy("tc.orden");

        return this.all(sqlUtil);
    }

    @Override
    public List<TemaCiclo> allByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("tc")
                .parents("cicloPostula cp", "temaExamen te")
                .filter("cp.id", ciclo)
                .orderBy("tc.orden");

        return all(sqlUtil);
    }

}
