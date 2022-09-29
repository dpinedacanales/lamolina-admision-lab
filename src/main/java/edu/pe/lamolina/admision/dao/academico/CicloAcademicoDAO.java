package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;

public interface CicloAcademicoDAO extends Crud<CicloAcademico> {

    CicloAcademico findActivo(ModalidadEstudio modalidad);

    CicloAcademico findByYearNumero(CicloAcademico cicloAcad);

    CicloAcademico findUltimo(ModalidadEstudio modalidad);

    List<CicloAcademico> allAnterioresByCicloCantidad(CicloAcademico academico, Integer cantidad);

    CicloAcademico findCicloAnteriorRegular(CicloAcademico cicloAcademico);

}
