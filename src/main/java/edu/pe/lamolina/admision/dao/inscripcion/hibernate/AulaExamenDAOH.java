package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.AulaExamenDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.inscripcion.AgrupacionModalidades;
import pe.edu.lamolina.model.inscripcion.AulaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.PabellonExamen;

@Repository
public class AulaExamenDAOH extends AbstractDAO<AulaExamen> implements AulaExamenDAO {

    public AulaExamenDAOH() {
        super();
        setClazz(AulaExamen.class);
    }

    @Override
    public List<AulaExamen> allByPabellon(PabellonExamen pex) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aex")
                .parents("pabellonExamen pex", "aula au")
                .filter("pex.id", pex);
        return all(sqlUtil);
    }

    @Override
    public AulaExamen findByAulaCiclo(Aula aula, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aex")
                .parents("pabellonExamen pex", "_pex.cicloPostula ci", "aula au")
                .filter("ci.id", ciclo)
                .filter("au.id", aula);
        return find(sqlUtil);
    }

    @Override
    public List<AulaExamen> allByAgrupacion(AgrupacionModalidades agrupa) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("aex")
                .parents("pabellonExamen pex", "agrupacionModalidades agp", "_pex.cicloPostula ci", "aula au")
                .filter("agp.id", agrupa)
                .orderBy("au.prioridad");
        return all(sqlUtil);
    }
}
