package edu.pe.lamolina.admision.movil.model;

import java.math.BigDecimal;

public class InscripcionMovilDTO {

    private String codigo;
    private String email;
    private String clave;
    private String direccionDomicilio;
    private String ubigeoDistrito;
    private String fechaNacimiento;
    private String paterno;
    private String materno;
    private String nombres;
    private String sexo;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String celular;

    private String codigoModular;
    private String colegio;
    private String paisColegio;
    private String gestion;
    private Integer modalidad;
    private BigDecimal costo;
    private String codigoCepre;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCodigoModular() {
        return codigoModular;
    }

    public void setCodigoModular(String codigoModular) {
        this.codigoModular = codigoModular;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public String getGestion() {
        return gestion;
    }

    public void setGestion(String gestion) {
        this.gestion = gestion;
    }

    public Integer getModalidad() {
        return modalidad;
    }

    public void setModalidad(Integer modalidad) {
        this.modalidad = modalidad;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getDireccionDomicilio() {
        return direccionDomicilio;
    }

    public void setDireccionDomicilio(String direccionDomicilio) {
        this.direccionDomicilio = direccionDomicilio;
    }

    public String getUbigeoDistrito() {
        return ubigeoDistrito;
    }

    public void setUbigeoDistrito(String ubigeoDistrito) {
        this.ubigeoDistrito = ubigeoDistrito;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaisColegio() {
        return paisColegio;
    }

    public void setPaisColegio(String paisColegio) {
        this.paisColegio = paisColegio;
    }

    public String getCodigoCepre() {
        return codigoCepre;
    }

    public void setCodigoCepre(String codigoCepre) {
        this.codigoCepre = codigoCepre;
    }

}
