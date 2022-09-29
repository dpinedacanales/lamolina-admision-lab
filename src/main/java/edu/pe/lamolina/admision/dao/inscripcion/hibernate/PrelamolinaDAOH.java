package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.FormCepre;
import edu.pe.lamolina.admision.dao.inscripcion.PrelamolinaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

@Repository
public class PrelamolinaDAOH extends AbstractDAO<Prelamolina> implements PrelamolinaDAO {

    public PrelamolinaDAOH() {
        super();
        setClazz(Prelamolina.class);
    }

    @Override
    public List<Prelamolina> allDynatable(CicloPostula ciclo, DynatableFilter filter) {
        List<String> fieldsFiltro = Arrays.asList(
                "pre.codigo", "car.nombre", "pre.email", "per.email", "pre.fechaRegistro"
        );

        filter.complexField("concat(coalesce(pre.paterno,''),' ',coalesce(pre.materno,''),' ',pre.nombres )");
        filter.complexField("concat(pre.nombres,' ',coalesce(pre.paterno,''),' ',coalesce(pre.materno,'') )");
        filter.complexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',per.nombres )");
        filter.complexField("concat(per.nombres,' ',coalesce(per.paterno,''),' ',coalesce(per.materno,'') )");

        filter.setAlias("pre");
        filter.setFields(fieldsFiltro);
        filter.setParents("cicloPostula cip", "_cip.cicloAcademico ca", "left postulante po", "left _po.persona per", "carreraPostula cap", "_cap.carrera car");
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
                .orderBy("pre.id DESC");
        return this.all(sqlUtil);
    }

    @Override
    public Prelamolina findByCodigoCiclo(String codigoCepre, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pre")
                .parents("cicloPostula cic")
                .filter("pre.codigo", codigoCepre)
                .filter("cic.id", ciclo);
        return this.find(sqlUtil);
    }

    @Override
    public Prelamolina findByPersona(Persona persona, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pre")
                .parents("cicloPostula cic", "postulante po", "_po.persona per")
                .filter("per.id", persona)
                .filter("cic.id", ciclo);
        return this.find(sqlUtil);
    }

    @Override
    public List<Prelamolina> allByDocumento(Persona persona, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pre")
                .parents("cicloPostula cic", "left postulante po", "left _po.persona per")
                .filter("pre.numeroDocIdentidad", persona.getNumeroDocIdentidad())
                .filter("cic.id", ciclo);
        return this.all(sqlUtil);
    }

    @Override
    public Prelamolina findByInteresado(Interesado interesado, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Prelamolina.class, "pre")
                .join("cicloPostula cic")
                .leftJoin("persona per", "per.tipoDocumento tip", "carreraPostula", "postulante po")
                .filter("pre.paterno", "like", interesado.getPaterno())
                .filter("pre.materno", "like", interesado.getMaterno())
                .filter("pre.nombres", "like", interesado.getNombres())
                .filter("pre.esIngresante", 1)
                .filter("cic.id", ciclo)
                .filter("pre.estado", "<>", PostulanteEstadoEnum.REN.name());

        return (Prelamolina) sql.find(getCurrentSession());

    }

    @Override
    public Prelamolina findByInteresadoAndCiclo(Interesado interesado, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Prelamolina.class, "pre")
                .join("cicloPostula cic")
                .leftJoin("persona per", "per.tipoDocumento tip", "carreraPostula", "postulante po")
                .filter("pre.paterno", "like", interesado.getPaterno())
                .filter("pre.materno", "like", interesado.getMaterno())
                .filter("pre.nombres", "like", interesado.getNombres())
                .filter("cic.id", ciclo)
                .filter("pre.estado", "<>", PostulanteEstadoEnum.REN.name());

        return (Prelamolina) sql.find(getCurrentSession());

    }

    @Override
    public Prelamolina findByInteresadoCicloPostula(CicloPostula cicloPostula, FormCepre formCepre) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pre")
                .parents("cicloPostula cic", "persona per", "_per.tipoDocumento tip")
                .filter("tip.id", formCepre.getTipoDocumento())
                .filter("per.numeroDocIdentidad", formCepre.getNumeroDocIdentidad())
                .filter("pre.codigo", formCepre.getCodigo())
                .filter("cic.id", cicloPostula);
        return this.find(sqlUtil);
    }

    @Override
    public Prelamolina findByPostulante(Postulante postulante) {
        Octavia sql = new Octavia()
                .from(Prelamolina.class, "pre")
                .join("cicloPostula cic", "postulante po")
                .left(" persona per", "per.tipoDocumento tip", "carreraPostula cap", "cap.carrera")
                .filter("po.id", postulante);
        return (Prelamolina) sql.find(getCurrentSession());
    }

    @Override
    public List<Prelamolina> allByDocumentoEsIngresante(Persona persona, CicloPostula ciclo, int esIngresante) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pre")
                .parents("cicloPostula cic", "left postulante po", "left _po.persona per")
                .filter("pre.numeroDocIdentidad", persona.getNumeroDocIdentidad())
                .filter("pre.esIngresante", esIngresante)
                .filter("cic.id", ciclo);
        return this.all(sqlUtil);
    }

}
