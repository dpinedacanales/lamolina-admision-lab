package edu.pe.lamolina.admision.dao.inscripcion;

import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.PostulanteBlacklist;

public interface PostulanteBlacklistDAO extends Crud<PostulanteBlacklist> {

    PostulanteBlacklist findByDNI(String numeroDocIdentidad, CicloAcademico cicloAcademico);

}
