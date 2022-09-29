package edu.pe.lamolina.admision.dao.seguridad.hibernate;

import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.seguridad.UsuarioRolDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

@Repository
public class UsuarioRolDAOH extends AbstractEasyDAO<UsuarioRol> implements UsuarioRolDAO {

    public UsuarioRolDAOH() {
        super();
        setClazz(UsuarioRol.class);
    }

    @Override
    public UsuarioRol findByUserRol(Usuario usuario, Rol rol) {
        Octavia sql = Octavia.query()
                .from(UsuarioRol.class, "ur")
                .join("usuario u", "rol ro")
                .filter("u.id", usuario)
                .filter("ro.id", rol);

        return find(sql);
    }
}
