package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface CarreraPostulaDAO extends EasyDAO<CarreraPostula> {

    List<CarreraPostula> allByCiclo(CicloPostula ciclo);

    CarreraPostula findByUpperNombre(String nombre, CicloPostula ciclo);

    List<CarreraPostula> allCarreraPostula(CicloPostula cicloPostula);
}
