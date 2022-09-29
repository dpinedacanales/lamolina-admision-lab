package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Prospecto;
import edu.pe.lamolina.admision.dao.inscripcion.GuiaPostulanteDAO;

@Repository
public class GuiaPostulanteDAOH extends AbstractDAO<Prospecto> implements GuiaPostulanteDAO {

    public GuiaPostulanteDAOH() {
        super();
        setClazz(Prospecto.class);
    }

    @Override
    public Prospecto findByCodeCiclo(String codigo, CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pro")
                .parents("cicloPostula cp")
                .filter("cp.id", ciclo)
                .filter("pro.codigo", codigo);
        return find(sqlUtil);
    }
}
