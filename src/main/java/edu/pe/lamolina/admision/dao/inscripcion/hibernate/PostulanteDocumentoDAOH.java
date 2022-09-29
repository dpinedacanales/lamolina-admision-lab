package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDocumentoDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.DocumentoModalidad;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.PostulanteDocumento;

@Repository
public class PostulanteDocumentoDAOH extends AbstractDAO<PostulanteDocumento> implements PostulanteDocumentoDAO {

    public PostulanteDocumentoDAOH() {
        super();
        setClazz(PostulanteDocumento.class);
    }

    @Override
    public PostulanteDocumento findByDocumentoPostulante(PostulanteDocumento docPostulante, DocumentoModalidad documentoModalidad) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dp")
                .parents("postulante po", "documentoModalidad dm")
                .filter("po.id", docPostulante)
                .filter("dm.id", documentoModalidad);
        return this.find(sqlUtil);
    }

    @Override
    public List<PostulanteDocumento> allByPostulante(Postulante postul) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pd")
                .parents("postulante po")
                .filter("po.id", postul);
        return this.all(sqlUtil);
    }

    @Override
    public List<PostulanteDocumento> allByPostulantes(List<Postulante> postulantes) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pd")
                .parents("postulante po")
                .filterIn("po.id", postulantes);
        return this.all(sqlUtil);
    }
}
