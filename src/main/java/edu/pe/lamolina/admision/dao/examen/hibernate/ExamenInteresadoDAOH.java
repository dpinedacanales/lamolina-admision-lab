package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.ExamenInteresadoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;

@Repository
public class ExamenInteresadoDAOH extends AbstractDAO<ExamenInteresado> implements ExamenInteresadoDAO {

    public ExamenInteresadoDAOH() {
        super();
        setClazz(ExamenInteresado.class);
    }

    @Override
    public ExamenInteresado findExamenInteresadoActivo(Interesado interesado) {

        Criteria criteria = getCurrentSession().createCriteria(ExamenInteresado.class);
        criteria.add(Restrictions.eq("interesado", interesado));
        criteria.add(Restrictions.isNull("fechaFin"));
        criteria.setMaxResults(1);
        return (ExamenInteresado) criteria.uniqueResult();
    }

    @Override
    public ExamenInteresado findLastEvaluacion(Interesado interesado) {

        Criteria criteria = getCurrentSession().createCriteria(ExamenInteresado.class);
        criteria.add(Restrictions.eq("interesado", interesado));
        criteria.addOrder(Order.desc("fechaInicio"));
        criteria.setMaxResults(1);
        return (ExamenInteresado) criteria.uniqueResult();
    }

    @Override
    public List<ExamenInteresado> allEvaluacionByInteresado(Interesado interesado) {
        Criteria criteria = getCurrentSession().createCriteria(ExamenInteresado.class);
        criteria.add(Restrictions.eq("interesado", interesado));
        criteria.addOrder(Order.desc("fechaInicio"));
        return criteria.list();
    }

    @Override
    public List<ExamenInteresado> allDynatableEvaluacionByInteresado(Interesado interesado, DynatableFilter filter) {

        filter.setAlias("exain");
        filter.setParents("interesado inte");
        filter.filterFix("inte.id", interesado.getId());

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        for (Object key : filtersFix.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
        }

        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue(), filter.getComplexFields());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage())
                .orderBy("exain.fechaInicio DESC");

        return all(sqlUtil);

    }

    @Override
    public Long countByInteresado(Interesado interesado) {
        SqlUtil sqlUtil = SqlUtil.creaCountSql("ei")
                .parents("interesado i")
                .filter("i.id", interesado);
        return count(sqlUtil);
    }

    @Override
    public List<ExamenInteresado> allFinalizadasByInteresado(Interesado interesado) {
        Octavia sql = Octavia.query(ExamenInteresado.class, "ei")
                .join("interesado i")
                .filter("i.id", interesado)
                .isNotNull("ei.fechaFin")
                .orderBy("ei.fechaInicio desc");

        return sql.all(getCurrentSession());
    }

}
