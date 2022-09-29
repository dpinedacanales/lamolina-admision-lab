package edu.pe.lamolina.admision.dao.seguridad;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface UsuarioDAO extends Crud<Usuario> {

    Usuario findByEmail(String email);

    public Usuario findByPersona(Persona persona);
}
