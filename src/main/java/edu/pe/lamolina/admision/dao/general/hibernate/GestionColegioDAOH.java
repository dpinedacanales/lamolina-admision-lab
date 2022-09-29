package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.GestionColegioDAO;
import pe.edu.lamolina.model.general.GestionColegio;

@Repository
public class GestionColegioDAOH extends AbstractDAO<GestionColegio> implements GestionColegioDAO {

    public GestionColegioDAOH() {
        super();
        setClazz(GestionColegio.class);
    }
}
