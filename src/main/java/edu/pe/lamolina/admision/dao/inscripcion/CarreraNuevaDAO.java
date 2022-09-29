package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;

public interface CarreraNuevaDAO extends Crud<CarreraNueva> {

    List<CarreraNueva> allActivo();

}
