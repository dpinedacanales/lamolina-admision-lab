package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;

public interface TurnoEntrevistaObuaeDAO extends Crud<TurnoEntrevistaObuae> {

    TurnoEntrevistaObuae findByOrdenGrupoCiclo(String ordenAtencion, String grupo, CicloPostula ciclo);

    public List<TurnoEntrevistaObuae> allByCicloAcademico(CicloAcademico ciclo);

}
