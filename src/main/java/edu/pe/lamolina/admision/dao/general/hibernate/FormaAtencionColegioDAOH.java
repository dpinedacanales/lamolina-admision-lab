package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.FormaAtencionColegioDAO;
import pe.edu.lamolina.model.general.FormaAtencionColegio;

@Repository
public class FormaAtencionColegioDAOH extends AbstractDAO<FormaAtencionColegio> implements FormaAtencionColegioDAO {

    public FormaAtencionColegioDAOH() {
        super();
        setClazz(FormaAtencionColegio.class);
    }
}
