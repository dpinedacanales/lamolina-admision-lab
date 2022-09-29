package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante;

import pe.edu.lamolina.model.inscripcion.CarreraPostula;

public class CarreraWeb {

    private Long id;
    private String nombre;
    private String codigo;

    public CarreraWeb(CarreraPostula carrPostula) {
        this.id = carrPostula.getId();
        this.codigo = carrPostula.getCarrera().getCodigo();
        this.nombre = carrPostula.getCarrera().getNombre();
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

}
