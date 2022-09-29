package edu.pe.lamolina.admision.dao.finanzas;

import java.util.Date;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.CampagnaDescuento;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

public interface CampagnaDescuentoDAO extends EasyDAO<CampagnaDescuento> {

    CampagnaDescuento findByCiclo(CicloPostula ciclo, ModalidadIngreso modalidadIngreso, Date hoy, EstadoEnum estadoEnum);

}
