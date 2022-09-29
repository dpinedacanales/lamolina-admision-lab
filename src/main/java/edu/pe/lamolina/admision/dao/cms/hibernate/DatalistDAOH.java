package edu.pe.lamolina.admision.dao.cms.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import edu.pe.lamolina.admision.dao.cms.DatalistDAO;
import pe.edu.lamolina.model.cms.Datalist;

@Repository
public class DatalistDAOH extends AbstractEasyDAO<Datalist> implements DatalistDAO {

    public DatalistDAOH() {
        super();
        setClazz(Datalist.class);
    }
}
