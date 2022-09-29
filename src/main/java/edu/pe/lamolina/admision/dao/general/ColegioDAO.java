package edu.pe.lamolina.admision.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.Ubicacion;

public interface ColegioDAO extends Crud<Colegio> {

    List<Colegio> allSecundariaByName(String nombre, Long idUbicacion);

    List<Colegio> allCoarSecundariaByName(String nombre, Long idUbicacion);

    Colegio findByCode(String codigoModular);

    List<Colegio> allByCode(String codigoModular);

    List<Colegio> allSecundariaByUbicacion(Ubicacion ubicacion);

    List<Colegio> allCoarSecundaria();

}
