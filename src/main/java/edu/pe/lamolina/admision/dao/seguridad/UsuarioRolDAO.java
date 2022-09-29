package edu.pe.lamolina.admision.dao.seguridad;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

public interface UsuarioRolDAO extends EasyDAO<UsuarioRol> {

    UsuarioRol findByUserRol(Usuario usuario, Rol rol);

}
