package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

@Repository
public class CicloPostulaDAOH extends AbstractDAO<CicloPostula> implements CicloPostulaDAO {

    public CicloPostulaDAOH() {
        super();
        setClazz(CicloPostula.class);
    }

    @Override
    public CicloPostula find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cp")
                .parents("cicloAcademico ca", "_ca.modalidadEstudio me")
                .filter("cp.id", id);
        return this.find(sqlUtil);
    }

    @Override
    public CicloPostula findActivo(ModalidadEstudio modalidad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cp")
                .parents("cicloAcademico ca", "_ca.modalidadEstudio me")
                .filter("me.id", modalidad)
                .filter("cp.estado", CicloAcademicoEstadoEnum.ACT.name());
        return this.find(sqlUtil);
    }

    @Override
    public List<CicloPostula> allByDaynatable(DynatableFilter filter, ModalidadEstudio modalidad) {
        List<String> fieldsFiltro = Arrays.asList(
                "ca.year", "ca.numeroCiclo", "ca.codigo", "cip.estado");

        filter.setAlias("cip");
        filter.setFields(fieldsFiltro);
        filter.setParents("cicloAcademico ca", "_ca.modalidadEstudio me");
        filter.filterFix("me.id", modalidad);

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        for (Object key : filtersFix.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
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
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC");

        return this.all(sqlUtil);
    }

    @Override
    public CicloPostula findByCicloAcademico(CicloAcademico cicloAcad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cp")
                .parents("cicloAcademico ca", "_ca.modalidadEstudio me")
                .filter("cp.esSimulacro", 0)
                .filter("ca.id", cicloAcad);
        return this.find(sqlUtil);
    }

    @Override
    public List<CicloPostula> allCicloPostula() {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cp")
                .parents("cicloAcademico ca", "_ca.modalidadEstudio me");
        return this.all(sqlUtil);
    }

    @Override
    public CicloPostula findRegularByYearNumeroCiclo(Integer year, Integer numCiclo) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca")
                .filter("ca.year", year)
                .filter("ca.numeroCiclo", numCiclo)
                .filter("cp.esSimulacro", 0)
                .filter("ca.tipo", "REG");
        return (CicloPostula) sql.find(getCurrentSession());
    }

}
