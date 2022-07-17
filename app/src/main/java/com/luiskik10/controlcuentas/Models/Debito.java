package com.luiskik10.controlcuentas.Models;

public class Debito {
    private String iddebito;
    private double montod;
    private String descripciond;
    private String fechad;
    private String idcategoria;
    private String categoria;
    private String idcuenta;
    private String numero;
    private String entidad;
    private double saldo;
    private String iduser;


    public Debito() {
    }

    public Debito(String iddebito, double montod, String descripciond, String fechad, String idcategoria, String categoria, String idcuenta, String numero, String entidad, double saldo, String iduser) {
        this.iddebito = iddebito;
        this.montod = montod;
        this.descripciond = descripciond;
        this.fechad = fechad;
        this.idcategoria = idcategoria;
        this.categoria = categoria;
        this.idcuenta = idcuenta;
        this.numero = numero;
        this.entidad = entidad;
        this.saldo = saldo;
        this.iduser = iduser;
    }

    public String getIddebito() {
        return iddebito;
    }

    public void setIddebito(String iddebito) {
        this.iddebito = iddebito;
    }

    public double getMontod() {
        return montod;
    }

    public void setMontod(double montod) {
        this.montod = montod;
    }

    public String getDescripciond() {
        return descripciond;
    }

    public void setDescripciond(String descripciond) {
        this.descripciond = descripciond;
    }

    public String getFechad() {
        return fechad;
    }

    public void setFechad(String fechad) {
        this.fechad = fechad;
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

    public String getIdcuenta() {
        return idcuenta;
    }

    public void setIdcuenta(String idcuenta) {
        this.idcuenta = idcuenta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    @Override
    public String toString() {
        return "Descripción: "+descripciond+"\nmonto Q. "+montod+"\nfecha: "+fechad+"\nNo. "+numero+"\nentidad:"+entidad+"\nSaldo: Q."+saldo;
    }
}



