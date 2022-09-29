package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.DocumentoModalidadDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.DocumentoModalidad;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class DocumentoModalidadDAOH extends AbstractEasyDAO<DocumentoModalidad> implements DocumentoModalidadDAO {

    public DocumentoModalidadDAOH() {
        super();
        setClazz(DocumentoModalidad.class);
    }

    @Override
    public List<DocumentoModalidad> allRequistosByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(DocumentoModalidad.class, "dm")
                .join("documentoRequisito dr", "modalidad m", "cicloPostula cp")
                .filter("m.id", postulante.getModalidadIngreso())
                .filter("cp.id", postulante.getCicloPostula());

        return all(sql);
    }

    @Override
    public List<DocumentoModalidad> allByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(DocumentoModalidad.class, "dm")
                .join("documentoRequisito dr", "modalidad m", "cicloPostula cp")
                .filter("m.id", modalidad)
                .filter("cp.id", ciclo)
                .orderBy("dr.nombre");

        return all(sql);
    }

}
