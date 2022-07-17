package com.luiskik10.controlcuentas.Models;

public class Categoria {
    private String idcategoria;
    private String categoria;
    private String iduser;

    public Categoria() {
    }

    public Categoria(String idcategoria, String categoria, String iduser) {
        this.idcategoria = idcategoria;
        this.categoria = categoria;
        this.iduser = iduser;
    }

    public String getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(String idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    @Override
    public String toString() {
        return categoria;
    }

}
