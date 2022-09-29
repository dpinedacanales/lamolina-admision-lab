package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.DescuentoExamen;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

public interface DescuentoExamenDAO extends Crud<DescuentoExamen> {

    List<DescuentoExamen> allByModalidad(ModalidadIngreso modalidad, String idGestion);

}
