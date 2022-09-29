package edu.pe.lamolina.admision.controller.admision.inscripcion.token;

import java.util.Date;
import pe.edu.lamolina.model.inscripcion.Postulante;

public class TokenPostulante {

    private Postulante postulante;
    private Date fecha;
    private String token;

    public Postulante getPostulante() {
        return postulante;
    }

    public void setPostulante(Postulante postulante) {
        this.postulante = postulante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
