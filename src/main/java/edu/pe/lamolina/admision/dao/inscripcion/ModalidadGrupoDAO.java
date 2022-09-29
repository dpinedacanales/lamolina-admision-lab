package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.AgrupacionModalidades;
import pe.edu.lamolina.model.inscripcion.ModalidadGrupo;

public interface ModalidadGrupoDAO extends Crud<ModalidadGrupo> {

    List<ModalidadGrupo> allByAgrupaciones(List<AgrupacionModalidades> agrupaciones);

    List<ModalidadGrupo> allByAgrupacion(AgrupacionModalidades agrupa);

}
