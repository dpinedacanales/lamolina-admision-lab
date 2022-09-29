package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante;

import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

public class ModalidadWeb {

    private Long id;
    private String nombre;
    private String codigo;
    private String tipo;

    public ModalidadWeb(ModalidadIngreso modalidad) {
        id = modalidad.getId();
        nombre = modalidad.getNombreInscripcion();
        codigo = modalidad.getCodigo();
        tipo = modalidad.getTipo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
