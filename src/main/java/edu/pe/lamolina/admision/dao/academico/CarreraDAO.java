package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;

public interface CarreraDAO extends EasyDAO<Carrera> {

    Carrera findByUpperNombre(String nombre);

    List<Carrera> allActivos();

}
