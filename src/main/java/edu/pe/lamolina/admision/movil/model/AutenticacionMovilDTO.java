package edu.pe.lamolina.admision.movil.model;

public class AutenticacionMovilDTO {

    private String email;
    private String clave;
    private String token;

    private Long idPersona;

    private Long idInteresado;

    private Long idCicloPostula;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
