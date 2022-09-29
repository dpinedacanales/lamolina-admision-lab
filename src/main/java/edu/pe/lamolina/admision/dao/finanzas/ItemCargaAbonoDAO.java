package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface ItemCargaAbonoDAO extends EasyDAO<ItemCargaAbono> {

    List<ItemCargaAbono> allActivosByPostulante(Postulante postulatenBD);

}
