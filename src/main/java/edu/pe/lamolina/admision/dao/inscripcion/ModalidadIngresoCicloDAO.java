package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;

public interface ModalidadIngresoCicloDAO extends Crud<ModalidadIngresoCiclo> {

    List<ModalidadIngresoCiclo> allByCiclo(CicloPostula ciclo);

    List<ModalidadIngresoCiclo> allSuperioresByCiclo(CicloPostula ciclo);

    List<ModalidadIngresoCiclo> allByModalidadPadreCiclo(ModalidadIngreso modalidad, CicloPostula ciclo);

    ModalidadIngresoCiclo findByModalidadIngresoCiclo(ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula);

    public List<ModalidadIngresoCiclo> allByCicloAcademico(CicloAcademico ciclo);

}
