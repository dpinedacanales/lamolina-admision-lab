package edu.pe.lamolina.admision.dao.seguridad;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface TokenIngresanteDAO extends EasyDAO<TokenIngresante> {

    TokenIngresante findUltimoVigente(Persona persona);

    List<TokenIngresante> allActivos(Persona persona, Usuario usuarioDb);

}
