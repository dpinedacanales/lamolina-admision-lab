package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface SolicitudCambioInfoDAO extends EasyDAO<SolicitudCambioInfo> {

    List<SolicitudCambioInfo> allByPostulante(Postulante postulante);

    List<SolicitudCambioInfo> allByPostulanteEstado(Postulante postulante, EstadoEnum estadoEnum);

    List<SolicitudCambioInfo> allByPostulanteEstado(Postulante postulante, SolicitudCambioInfoEstadoEnum estado);

    List<SolicitudCambioInfo> allByPostulanteEstados(Postulante postulante, List<SolicitudCambioInfoEstadoEnum> estados);

}
