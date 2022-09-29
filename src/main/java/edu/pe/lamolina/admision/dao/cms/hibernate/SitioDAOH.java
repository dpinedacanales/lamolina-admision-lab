package edu.pe.lamolina.admision.dao.cms.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import edu.pe.lamolina.admision.dao.cms.SitioDAO;
import pe.edu.lamolina.model.cms.Sitio;

@Repository
public class SitioDAOH extends AbstractEasyDAO<Sitio> implements SitioDAO {

    public SitioDAOH() {
        super();
        setClazz(Sitio.class);
    }
}
