package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.OficinaRecorrido;

public interface OficinaRecorridoDAO extends EasyDAO<OficinaRecorrido> {

    List<OficinaRecorrido> allOrderByOrdenAsc();

}
