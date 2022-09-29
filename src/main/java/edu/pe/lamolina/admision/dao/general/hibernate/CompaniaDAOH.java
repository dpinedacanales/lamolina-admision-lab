package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.CompaniaDAO;
import pe.edu.lamolina.model.general.Compania;

@Repository
public class CompaniaDAOH extends AbstractDAO<Compania> implements CompaniaDAO {

    public CompaniaDAOH() {
        super();
        setClazz(Compania.class);
    }
}
