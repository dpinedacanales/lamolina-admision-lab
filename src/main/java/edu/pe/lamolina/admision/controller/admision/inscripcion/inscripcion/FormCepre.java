package edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion;

import pe.edu.lamolina.model.general.TipoDocIdentidad;

public class FormCepre {

    private TipoDocIdentidad tipoDocumento;
    private String numeroDocIdentidad;
    private String codigo;

    public TipoDocIdentidad getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocIdentidad tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocIdentidad() {
        return numeroDocIdentidad;
    }

    public void setNumeroDocIdentidad(String numeroDocIdentidad) {
        this.numeroDocIdentidad = numeroDocIdentidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
