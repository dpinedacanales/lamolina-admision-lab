package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.TerminosPostulanteDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.TerminosPostulante;

@Repository
public class TerminosPostulanteDAOH extends AbstractDAO<TerminosPostulante> implements TerminosPostulanteDAO {

    public TerminosPostulanteDAOH() {
        super();
        setClazz(TerminosPostulante.class);
    }

    @Override
    public List<TerminosPostulante> allByPostulante(Postulante postulante) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("tp")
                .parents("postulante pos", "contenidoCarta")
                .filter("pos.id", postulante);
        return all(sqlUtil);
    }
}
