package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.DocumentoRequisitoDAO;
import pe.edu.lamolina.model.inscripcion.DocumentoRequisito;

@Repository
public class DocumentoRequisitoDAOH extends AbstractDAO<DocumentoRequisito> implements DocumentoRequisitoDAO {

    public DocumentoRequisitoDAOH() {
        super();
        setClazz(DocumentoRequisito.class);
    }
}
