package edu.pe.lamolina.admision.zelper.model;

public class ItemBean {

    private String codigo;
    private Integer codigo2;
    private String descripcion;

    public ItemBean(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public ItemBean(Integer codigo2, String descripcion) {
        this.codigo2 = codigo2;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCodigo2() {
        return codigo2;
    }

    public void setCodigo2(Integer codigo2) {
        this.codigo2 = codigo2;
    }

}
