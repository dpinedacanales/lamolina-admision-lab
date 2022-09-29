package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.TipoUbicacionDAO;
import pe.edu.lamolina.model.general.TipoUbicacion;

@Repository
public class TipoUbicacionDAOH extends AbstractDAO<TipoUbicacion> implements TipoUbicacionDAO {

    public TipoUbicacionDAOH() {
        super();
        setClazz(TipoUbicacion.class);
    }
}
