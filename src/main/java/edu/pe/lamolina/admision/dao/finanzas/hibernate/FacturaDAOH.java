package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.FacturaDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import static pe.edu.lamolina.model.enums.FacturaEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.FacturaEstadoEnum.ANU;
import pe.edu.lamolina.model.finanzas.Factura;

@Repository
public class FacturaDAOH extends AbstractDAO<Factura> implements FacturaDAO {

    public FacturaDAOH() {
        super();
        setClazz(Factura.class);
    }

    @Override
    public List<Factura> allByDynatable(DynatableFilter filter) {

        List<String> fieldsFiltro = Arrays.asList("fac.fecha", "po.codigo", "per.numeroDocIdentidad");
        filter.complexField("concat(per.paterno,' ',per.materno,' ',per.nombres)");
        filter.complexField("concat(per.nombres,' ',per.paterno,' ',per.materno)");

        filter.setAlias("fac");
        filter.setFields(fieldsFiltro);
        filter.setParents("itemCargaAbono ica", "_ica.postulante po", "_po.persona per");
        filter.filterFix("ica.redundante", 0);
        filter.filterFix("ica.extornado", 0);
        filter.filterInFix("fac.estado", Arrays.asList(ACT.name(), ANU.name()));

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        for (Object key : filtersFix.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
        }

        Map filtersInFix = filter.getFiltersInFixed();
        for (Object key : filtersInFix.keySet()) {
            this.filterInFixed(sqlUtil, (String) key, (List) filtersInFix.get(key));
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
                .orderBy("fac.id DESC");

        return this.all(sqlUtil);
    }

    @Override
    public List<Factura> allGeneradosByFecha(Date fechaAbono) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("fact")
                .parents("itemCargaAbono ica")
                .filter("ica.fechaAbono", fechaAbono)
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0);
        return all(sqlUtil);
    }

    @Override
    public Factura findByNumeroSerie(String serie, Long lastNumFactura) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("fac")
                .filter("fac.serie", serie)
                .filter("fac.numero", lastNumFactura);
        return this.find(sqlUtil);
    }

    @Override
    public List<Factura> allBySerieRangoNumero(Long serie, Long numeroInicio, Long numeroFin) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("f")
                .filter("f.serie", serie)
                .filter("f.numero >=", numeroInicio)
                .filter("f.numero <=", numeroFin)
                .filterIn("f.estado", Arrays.asList(ACT.name(), ANU.name()));
        return all(sqlUtil);
    }

    @Override
    public Factura find(Factura factura) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("f")
                .parents("itemCargaAbono")
                .filter("f.id", factura);
        return this.find(sqlUtil);
    }

    @Override
    public List<Factura> allFacturasExportar() {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("fac")
                .parents("itemCargaAbono ica", "_ica.conceptoPago", "_ica.postulante po", "_po.persona pe")
                .filter("ica.redundante", 0)
                .filter("ica.extornado", 0)
                .filterIn("fac.estado", Arrays.asList(ACT.name(), ANU.name()))
                .orderBy("fac.fecha DESC", "fac.id DESC");
        return all(sqlUtil);
    }

}
