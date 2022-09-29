package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioCarreraDAO;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.finanzas.SolicitudCambioCarrera;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;

@Repository
public class SolicitudCambioCarreraDAOH extends AbstractEasyDAO<SolicitudCambioCarrera> implements SolicitudCambioCarreraDAO {

    public SolicitudCambioCarreraDAOH() {
        super();
        setClazz(SolicitudCambioCarrera.class);
    }

    @Override
    public List<SolicitudCambioCarrera> allBySolicitudes(List<SolicitudCambioInfo> solicitudes) {
        Octavia sql = Octavia.query(SolicitudCambioCarrera.class)
                .join("solicitudCambioInfo si", "carreraPostula cp")
                .in("si.id", solicitudes);
        return all(sql);
    }

}
