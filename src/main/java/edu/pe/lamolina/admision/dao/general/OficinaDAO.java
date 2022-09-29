package edu.pe.lamolina.admision.dao.general;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Oficina;

public interface OficinaDAO extends EasyDAO<Oficina> {

    Oficina findByCodigo(String codigo);

}
