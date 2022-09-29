package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.EvaluadoDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class EvaluadoDAOH extends AbstractDAO<Evaluado> implements EvaluadoDAO {

    public EvaluadoDAOH() {
        super();
        setClazz(Evaluado.class);

    }

    @Override
    public List<Evaluado> allByDynaTable(CicloPostula ciclo, DynatableFilter filter) {
        List<String> fieldsFiltro = Arrays.asList(
                "po.codigo", "per.numeroDocIdentidad", "mod.nombre", "carr.nombre");
        filter.complexField("concat(per.paterno,' ',per.materno,' ',per.nombres)");
        filter.complexField("concat(per.nombres,' ',per.paterno,' ',per.materno)");

        filter.setAlias("eva");
        filter.setFields(fieldsFiltro);
        filter.setParents("postulante po", "_po.persona per", "_po.modalidadIngreso mod", "_po.cicloPostula cip", "left carreraIngreso carr");
        filter.filterFix("cip.id", ciclo);

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
                .orderBy("eva.id DESC");

        return this.all(sqlUtil);
    }

    @Override
    public List<Evaluado> allByPostulanntes(List<Postulante> postulantes) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("postulante po", "left carreraIngreso ca")
                .filterIn("po.id", postulantes);
        return all(sqlUtil);
    }

    @Override
    public Evaluado findByPostulante(Postulante postulante) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("postulante po", "left carreraIngreso ca", "_po.persona", "_po.modalidadIngreso")
                .parents("left _po.colegioProcedencia", "left _po.universidadProcedencia")
                .parents("_po.cicloPostula ci", "_ci.cicloAcademico")
                .filter("po.id", postulante);
        return find(sqlUtil);
    }

    @Override
    public Evaluado findByDNI(String dni) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("postulante po", "left carreraIngreso ca", "_po.persona per", "_po.modalidadIngreso")
                .parents("left _po.colegioProcedencia", "left _po.universidadProcedencia")
                .parents("_po.cicloPostula ci", "_ci.cicloAcademico")
                .filter("per.numeroDocIdentidad", dni);
        return find(sqlUtil);
    }

    @Override
    public List<Evaluado> allByCarrera(Carrera carrera) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .parents("postulante po", "left carreraIngreso ca", "_po.persona", "_po.modalidadIngreso")
                .parents("left _po.colegioProcedencia", "left _po.universidadProcedencia")
                .parents("_po.cicloPostula ci", "_ci.cicloAcademico")
                .filter("ca.id", carrera);
        return all(sqlUtil);
    }
}
