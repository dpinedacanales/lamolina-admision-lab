package edu.pe.lamolina.admision.dao.seguridad.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.seguridad.RolDAO;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.seguridad.Rol;

@Repository
public class RolDAOH extends AbstractDAO<Rol> implements RolDAO {

    public RolDAOH() {
        super();
        setClazz(Rol.class);
    }

    @Override
    public Rol findCodigoEnum(RolEnum rolEnum) {
        Octavia sql = new Octavia()
                .from(Rol.class, "rol")
                .filter("codigo", rolEnum);

        return (Rol) sql.find(getCurrentSession());
    }
}
