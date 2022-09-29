package edu.pe.lamolina.admision.dao.cms.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import edu.pe.lamolina.admision.dao.cms.TaxonomyDAO;
import pe.edu.lamolina.model.cms.Taxonomy;

@Repository
public class TaxonomyDAOH extends AbstractEasyDAO<Taxonomy> implements TaxonomyDAO {

    public TaxonomyDAOH() {
        super();
        setClazz(Taxonomy.class);
    }
}
