package edu.pe.lamolina.admision.zelper.model;

import java.io.Serializable;
import java.util.Map;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;
import pe.edu.lamolina.model.seguridad.Usuario;

public class DataSessionAdmision implements Serializable {

    private Interesado interesado;

    private CicloPostula cicloPostula;

    private Postulante postulante;

    private ModalidadIngreso modalidad;

    private Persona persona;

    private Alumno alumno;

    private Usuario usuario;

    private Prelamolina prelamolina;

    private boolean errorCodigo;

    private String mensajeError;

    private Integer pasoInscripcion;

    private Integer pasoEncuesta;

    private Integer returnAfterPreInsc;

    private Long timeEvaluacion;

    private Map<Long, RespuestaInteresado> respuestaInteresado;

    private ExamenInteresado examenInteresado;

    public DataSessionAdmision() {
        this.pasoInscripcion = 0;
        this.pasoEncuesta = 1;
        this.returnAfterPreInsc = 0;
        this.timeEvaluacion = 0l;

    }

    public CicloPostula getCicloPostula() {
        return cicloPostula;
    }

    public void setCicloPostula(CicloPostula cicloPostula) {
        this.cicloPostula = cicloPostula;
    }

    public Interesado getInteresado() {
        return interesado;
    }

    public void setInteresado(Interesado interesado) {
        this.interesado = interesado;
    }

    public Postulante getPostulante() {
        return postulante;
    }

    public void setPostulante(Postulante postulante) {
        this.postulante = postulante;
    }

    public ModalidadIngreso getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadIngreso modalidad) {
        this.modalidad = modalidad;
    }

    public Integer getPasoInscripcion() {
        return pasoInscripcion;
    }

    public void setPasoInscripcion(Integer pasoInscripcion) {
        this.pasoInscripcion = pasoInscripcion;
    }

    public Integer getPasoEncuesta() {
        return pasoEncuesta;
    }

    public void setPasoEncuesta(Integer pasoEncuesta) {
        this.pasoEncuesta = pasoEncuesta;
    }

    public Integer getReturnAfterPreInsc() {
        return returnAfterPreInsc;
    }

    public void setReturnAfterPreInsc(Integer returnAfterPreInsc) {
        this.returnAfterPreInsc = returnAfterPreInsc;
    }

    public Long getTimeEvaluacion() {
        return timeEvaluacion;
    }

    public void setTimeEvaluacion(Long timeEvaluacion) {
        this.timeEvaluacion = timeEvaluacion;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Prelamolina getPrelamolina() {
        return prelamolina;
    }

    public void setPrelamolina(Prelamolina prelamolina) {
        this.prelamolina = prelamolina;
    }

    public boolean isErrorCodigo() {
        return errorCodigo;
    }

    public void setErrorCodigo(boolean errorCodigo) {
        this.errorCodigo = errorCodigo;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public ExamenInteresado getExamenInteresado() {
        return examenInteresado;
    }

    public void setExamenInteresado(ExamenInteresado examenInteresado) {
        this.examenInteresado = examenInteresado;
    }

    public Map<Long, RespuestaInteresado> getRespuestaInteresado() {
        return respuestaInteresado;
    }

    public void setRespuestaInteresado(Map<Long, RespuestaInteresado> respuestaInteresado) {
        this.respuestaInteresado = respuestaInteresado;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
