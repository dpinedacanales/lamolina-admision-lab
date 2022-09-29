package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.OficinaRecorridoDAO;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.OficinaRecorrido;

@Repository
public class OficinaRecorridoDAOH extends AbstractEasyDAO<OficinaRecorrido> implements OficinaRecorridoDAO {

    public OficinaRecorridoDAOH() {
        super();
        setClazz(OficinaRecorrido.class);
    }

    @Override
    public List<OficinaRecorrido> allOrderByOrdenAsc() {
        Octavia sql = Octavia.query(OficinaRecorrido.class, "ofr")
                .join("oficina ofi")
                .orderBy("ofr.orden asc");
        return all(sql);
    }

}
