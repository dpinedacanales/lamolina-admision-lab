package edu.pe.lamolina.admision.movil.model;

import java.util.Date;

public class EventoCalendarioMovilDTO {

    public enum TipoEnum {
        EVENTO, TALLER;
    }

    private String titulo;

    private Date fecha;

    private String campusWebsite;
    
    private String ubicacion;

    private String carrera;

    private TipoEnum tipo;

    private boolean importante;

    private boolean examenAdmision;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isImportante() {
        return importante;
    }

    public void setImportante(boolean importante) {
        this.importante = importante;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public TipoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnum tipo) {
        this.tipo = tipo;
    }

    public boolean isExamenAdmision() {
        return examenAdmision;
    }

    public void setExamenAdmision(boolean examenAdmision) {
        this.examenAdmision = examenAdmision;
    }

    public String getCampusWebsite() {
        return campusWebsite;
    }

    public void setCampusWebsite(String campusWebsite) {
        this.campusWebsite = campusWebsite;
    }
    
    
    
    

}
