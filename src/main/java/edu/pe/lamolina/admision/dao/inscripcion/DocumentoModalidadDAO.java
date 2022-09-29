package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.DocumentoModalidad;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface DocumentoModalidadDAO extends EasyDAO<DocumentoModalidad> {

    List<DocumentoModalidad> allRequistosByPostulante(Postulante postulante);

    List<DocumentoModalidad> allByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula ciclo);

}
