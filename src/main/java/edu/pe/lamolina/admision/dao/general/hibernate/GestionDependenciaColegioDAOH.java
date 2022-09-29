package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.GestionDependenciaColegioDAO;
import pe.edu.lamolina.model.general.GestionDependenciaColegio;

@Repository
public class GestionDependenciaColegioDAOH extends AbstractDAO<GestionDependenciaColegio> implements GestionDependenciaColegioDAO {

    public GestionDependenciaColegioDAOH() {
        super();
        setClazz(GestionDependenciaColegio.class);
    }

}
