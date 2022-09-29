package edu.pe.lamolina.admision.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;

public interface UniversidadDAO extends Crud<Universidad> {

    List<Universidad> allActivasByPais(Pais pa);

    List<Universidad> allByEstadoPais(String nombre, EstadoEnum estadoEnum, Pais pais);
}
