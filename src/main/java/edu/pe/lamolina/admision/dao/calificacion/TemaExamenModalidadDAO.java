package edu.pe.lamolina.admision.dao.calificacion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.calificacion.TemaExamenModalidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

public interface TemaExamenModalidadDAO extends EasyDAO<TemaExamenModalidad> {

    List<TemaExamenModalidad> allByCicloModalidad(CicloPostula cicloPostula, ModalidadIngreso modalidadIngreso);

}
