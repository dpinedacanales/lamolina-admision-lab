package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.CostoModalidadDAO;
import pe.edu.lamolina.model.finanzas.CostoModalidad;

@Repository
public class CostoModalidadDAOH extends AbstractDAO<CostoModalidad> implements CostoModalidadDAO {

    public CostoModalidadDAOH() {
        super();
        setClazz(CostoModalidad.class);
    }
}
