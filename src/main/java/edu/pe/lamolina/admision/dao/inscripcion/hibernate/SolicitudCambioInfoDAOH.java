package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioInfoDAO;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCambioInfoEnum;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class SolicitudCambioInfoDAOH extends AbstractEasyDAO<SolicitudCambioInfo> implements SolicitudCambioInfoDAO {

    public SolicitudCambioInfoDAOH() {
        super();
        setClazz(SolicitudCambioInfo.class);
    }

    @Override
    public List<SolicitudCambioInfo> allByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query(SolicitudCambioInfo.class, "sci")
                .join("postulante po")
                .leftJoin("tipoCambioInfo tci", "modalidadIngresoNueva mi", "colegioNuevo cn", "universidadNueva un")
                .filter("po.id", postulante);
        return all(sql);
    }

    @Override
    public List<SolicitudCambioInfo> allByPostulanteEstado(Postulante postulante, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query(SolicitudCambioInfo.class, "sci")
                .join("postulante po")
                .leftJoin("tipoCambioInfo tci", "modalidadIngresoNueva mi", "colegioNuevo cn", "universidadNueva un")
                .filter("po.id", postulante)
                .filter("sci.estado", estadoEnum);
        return all(sql);
    }

    @Override
    public List<SolicitudCambioInfo> allByPostulanteEstado(Postulante postulante, SolicitudCambioInfoEstadoEnum estado) {
        Octavia sql = Octavia.query(SolicitudCambioInfo.class, "sci")
                .join("postulante po", "tipoCambioInfo tci")
                .leftJoin("modalidadIngresoNueva mi", "colegioNuevo cn", "universidadNueva un")
                .filter("po.id", postulante)
                .filter("sci.estado", estado);
        return all(sql);
    }

    @Override
    public List<SolicitudCambioInfo> allByPostulanteEstados(Postulante postulante, List<SolicitudCambioInfoEstadoEnum> estados) {
        Octavia sql = Octavia.query(SolicitudCambioInfo.class, "sci")
                .join("postulante po", "tipoCambioInfo tci")
                .leftJoin("modalidadIngresoNueva mi", "colegioNuevo cn", "universidadNueva un")
                .filter("po.id", postulante)
                .in("sci.estado", estados)
                .notIn("tci.codigo", Arrays.asList(TipoCambioInfoEnum.CDIFEMOD.name()));
        return all(sql);
    }

}
