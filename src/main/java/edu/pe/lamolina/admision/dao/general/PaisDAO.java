package edu.pe.lamolina.admision.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.general.Pais;

public interface PaisDAO extends Crud<Pais> {

    List<Pais> allByName(String nombre);

    Pais findByCode(String codigo);

    List<Pais> allByCodigo(String codigo);

    List<Pais> allByNotCodigo(String nombre, String codigo);

}
