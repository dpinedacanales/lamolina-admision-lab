package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.TipoActividadIngresanteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;

@Repository
public class TipoActividadIngresanteDAOH extends AbstractEasyDAO<TipoActividadIngresante> implements TipoActividadIngresanteDAO {

    public TipoActividadIngresanteDAOH() {
        super();
        setClazz(TipoActividadIngresante.class);
    }

    @Override
    public TipoActividadIngresante findByCodigo(TipoActividadIngresanteEnum tipoActividadIngresanteEnum) {
        Octavia sql = new Octavia()
                .from(TipoActividadIngresante.class,"tai")
                .filter("codigo", tipoActividadIngresanteEnum);
        
        return find(sql);
    }

}
