package edu.pe.lamolina.admision.controller.admision.inscripcion.postpago;

import java.util.List;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioCarrera;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Ingresante;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.seguridad.TokenIngresante;

public interface PostPagoService {

    CicloPostula findCiclo(CicloPostula ciclo);

    Postulante findPostulante(Postulante postulante);

    void postulanteFinalizar(Postulante postulante, CicloPostula ciclo);

    Boolean verificarPagoGuiaPostulante(Postulante postulante);

    Ingresante findIngresanteByPostulante(Postulante postulanteSession);

    TurnoEntrevistaObuae findTurnoPostulante(Postulante postulante, CicloPostula ciclo);

    List<OpcionCarrera> allOpcionCarreraByPostulante(Postulante postulante);

    EventoCiclo getEventoAula(CicloPostula ciclo);

    ModalidadIngresoCiclo findModalidadCiclo(ModalidadIngreso modalidadIngreso, CicloPostula ciclo);

    ContenidoCarta findContenidoCartaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum);

    TokenIngresante findToken(Postulante postulante, CicloPostula ciclo);

    TokenIngresante findByPersona(Persona persona);

    List<SolicitudCambioInfo> allSolicitudCambioInfoByPostulante(Postulante postulante, List<SolicitudCambioInfoEstadoEnum> asList);

    List<SolicitudCambioCarrera> allSolicitudCambioCarreraBySolicitud(SolicitudCambioInfo carreras);

    List<DeudaInteresado> allDeudasByPostulante(Postulante postulante, CicloPostula ciclo);

    RecorridoIngresante findRecorridoIngresante(Postulante psotuPostulante, CicloPostula cicloPostula);

}
