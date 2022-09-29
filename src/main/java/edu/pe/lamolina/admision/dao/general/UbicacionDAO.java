package edu.pe.lamolina.admision.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.general.Ubicacion;

public interface UbicacionDAO extends Crud<Ubicacion> {

    List<Ubicacion> allDistritos(String nombre);

    List<Ubicacion> allDistritos();

    Ubicacion findByCode(String codigo);

    List<Ubicacion> allByTipoParent(String tipo, Long parent);

}
