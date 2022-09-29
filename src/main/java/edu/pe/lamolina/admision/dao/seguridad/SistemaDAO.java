package edu.pe.lamolina.admision.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.seguridad.Sistema;

public interface SistemaDAO extends Crud<Sistema> {

    List<Sistema> allByName(String nombre);

    Sistema findByCodigo(String codigo);

    Sistema find(Long id);

}
