package edu.pe.lamolina.admision.dao.examen.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.EncuestaCicloDAO;
import pe.edu.lamolina.model.inscripcion.EncuestaCiclo;

@Repository
public class EncuestaCicloDAOH extends AbstractDAO<EncuestaCiclo> implements EncuestaCicloDAO {

    public EncuestaCicloDAOH() {
        super();
        setClazz(EncuestaCiclo.class);
    }

}
