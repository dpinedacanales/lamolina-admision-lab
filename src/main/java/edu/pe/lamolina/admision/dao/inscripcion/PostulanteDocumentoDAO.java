package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.inscripcion.DocumentoModalidad;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.PostulanteDocumento;

public interface PostulanteDocumentoDAO extends Crud<PostulanteDocumento> {

    PostulanteDocumento findByDocumentoPostulante(PostulanteDocumento docPostulante, DocumentoModalidad documentoModalidad);

    List<PostulanteDocumento> allByPostulante(Postulante postul);

    List<PostulanteDocumento> allByPostulantes(List<Postulante> postulantes);

}
