package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteBlacklistDAO;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.PostulanteBlacklist;

@Repository
public class PostulanteBlacklistDAOH extends AbstractDAO<PostulanteBlacklist> implements PostulanteBlacklistDAO {

    public PostulanteBlacklistDAOH() {
        super();
        setClazz(PostulanteBlacklist.class);
    }

    @Override
    public PostulanteBlacklist findByDNI(String numeroDocIdentidad, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(PostulanteBlacklist.class, "pbl")
                .join("persona per")
                .leftJoin("cicloFin cf")
                .filter("per.numeroDocIdentidad", numeroDocIdentidad);
        return (PostulanteBlacklist) sql.find(getCurrentSession());
    }

}
