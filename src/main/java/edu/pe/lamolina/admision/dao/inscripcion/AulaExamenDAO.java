package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.inscripcion.AgrupacionModalidades;
import pe.edu.lamolina.model.inscripcion.AulaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.PabellonExamen;

public interface AulaExamenDAO extends Crud<AulaExamen> {

    List<AulaExamen> allByPabellon(PabellonExamen pex);

    AulaExamen findByAulaCiclo(Aula au, CicloPostula ciclo);

    List<AulaExamen> allByAgrupacion(AgrupacionModalidades agrupa);

}
