package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.SolicitudCambioCarrera;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;

public interface SolicitudCambioCarreraDAO extends EasyDAO<SolicitudCambioCarrera> {

    List<SolicitudCambioCarrera> allBySolicitudes(List<SolicitudCambioInfo> solicitudes);

}
