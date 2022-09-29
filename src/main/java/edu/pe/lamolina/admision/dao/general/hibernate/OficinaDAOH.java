package edu.pe.lamolina.admision.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.OficinaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Oficina;

@Repository
public class OficinaDAOH extends AbstractEasyDAO<Oficina> implements OficinaDAO {

    public OficinaDAOH() {
        super();
        setClazz(Oficina.class);
    }

    @Override
    public Oficina findByCodigo(String codigo) {
        Octavia sql = Octavia.query(Oficina.class)
                .filter("codigo", codigo);
        return (Oficina) sql.find(getCurrentSession());
    }

}
