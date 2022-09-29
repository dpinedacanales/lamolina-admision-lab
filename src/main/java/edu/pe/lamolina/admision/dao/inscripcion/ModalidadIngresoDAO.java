package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;

public interface ModalidadIngresoDAO extends Crud<ModalidadIngreso> {

    List<ModalidadIngreso> allByName(String nombre);

    List<ModalidadIngreso> allByCiclo(CicloPostula ciclo);

    ModalidadIngreso findByCodeCliclo(String codigo, CicloPostula cicloPostula);

    List<ModalidadIngresoCiclo> allByCicloModalidades(CicloPostula ciclo, List<ModalidadIngreso> modalidades);

    public ModalidadIngreso findByCode(String CODE_MODALIDAD_CEPRE);

}
