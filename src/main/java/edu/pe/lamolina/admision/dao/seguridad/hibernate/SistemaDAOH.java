package edu.pe.lamolina.admision.dao.seguridad.hibernate;

import edu.pe.lamolina.admision.dao.seguridad.SistemaDAO;
import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.seguridad.Sistema;

@Repository
public class SistemaDAOH extends AbstractDAO<Sistema> implements SistemaDAO {

    public SistemaDAOH() {
        super();
        setClazz(Sistema.class);
    }

    @Override
    public List<Sistema> allByName(String nombre) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("s")
                .filterStr("s.nombre LIKE", nombre)
                .orderBy("s.nombre")
                .setPageSize(15);

        return all(sqlUtil);
    }

    @Override
    public Sistema findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(Sistema.class, "s")
                .filter("codigo", codigo);
        
        return (Sistema) sql.find(getCurrentSession());
    }
    
    @Override
    public Sistema find(Long id) {
        Octavia sql = Octavia.query()
                .from(Sistema.class, "s")
                .filter("id", id);
        
        return (Sistema) sql.find(getCurrentSession());
    }
}
