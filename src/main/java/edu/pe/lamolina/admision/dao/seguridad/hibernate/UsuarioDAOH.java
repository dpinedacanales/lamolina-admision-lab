package edu.pe.lamolina.admision.dao.seguridad.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.seguridad.UsuarioDAO;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;

@Repository
public class UsuarioDAOH extends AbstractDAO<Usuario> implements UsuarioDAO {

    public UsuarioDAOH() {
        super();
        setClazz(Usuario.class);
    }

    @Override
    public Usuario findByEmail(String email) {
        Criteria criteria = this.getCurrentSession().createCriteria(Usuario.class);
        criteria.add(Restrictions.eq("usuario", email));
        return (Usuario) criteria.uniqueResult();
    }

    @Override
    public Usuario findByPersona(Persona persona) {

        Octavia sql = new Octavia()
                .from(Usuario.class, "us")
                .join("persona per")
                .filter("per.id", persona);
        return (Usuario) sql.find(getCurrentSession());
    }

}
