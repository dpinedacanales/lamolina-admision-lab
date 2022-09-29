package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

public interface ModalidadEstudioDAO extends Crud<ModalidadEstudio> {

    ModalidadEstudio findByCodigo(ModalidadEstudioEnum modalidadEstudioEnum);

    List<ModalidadEstudio> allModalidadEstudioAct();

}
