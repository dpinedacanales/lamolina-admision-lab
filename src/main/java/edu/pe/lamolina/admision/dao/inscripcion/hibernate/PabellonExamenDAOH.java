package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.PabellonExamenDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.PabellonExamen;

@Repository
public class PabellonExamenDAOH extends AbstractDAO<PabellonExamen> implements PabellonExamenDAO {

    public PabellonExamenDAOH() {
        super();
        setClazz(PabellonExamen.class);
    }

    @Override
    public List<PabellonExamen> allByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pex")
                .parents("cicloPostula ci", "pabellon pab", "left agrupacionModalidades agp")
                .filter("ci.id", ciclo);
        return all(sqlUtil);
    }

    @Override
    public PabellonExamen findByPabellonCiclo(Aula pabellon, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pex")
                .parents("cicloPostula ci", "pabellon pab")
                .filter("ci.id", ciclo)
                .filter("pab.id", pabellon);
        return find(sqlUtil);
    }
}
