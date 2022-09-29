package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.NivelModalidadColegioDAO;
import pe.edu.lamolina.model.general.NivelModalidadColegio;

@Repository
public class NivelModalidadColegioDAOH extends AbstractDAO<NivelModalidadColegio> implements NivelModalidadColegioDAO {

    public NivelModalidadColegioDAOH() {
        super();
        setClazz(NivelModalidadColegio.class);
    }
}
