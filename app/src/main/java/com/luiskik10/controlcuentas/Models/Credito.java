package com.luiskik10.controlcuentas.Models;

public class Credito {
    private String idcredito;
    private double montoc;
    private String descripcionc;
    private String fechac;
    private String idcuenta;
    private String numero;
    private String entidad;
    private double saldo;
    private String iduser;

    public Credito() {
    }

    public Credito(String idcredito, double montoc, String descripcionc, String fechac, String idcuenta, String numero, String entidad, double saldo, String iduser) {
        this.idcredito = idcredito;
        this.montoc = montoc;
        this.descripcionc = descripcionc;
        this.fechac = fechac;
        this.idcuenta = idcuenta;
        this.numero = numero;
        this.entidad = entidad;
        this.saldo = saldo;
        this.iduser = iduser;
    }

    public String getIdcredito() {
        return idcredito;
    }

    public void setIdcredito(String idcredito) {
        this.idcredito = idcredito;
    }

    public double getMontoc() {
        return montoc;
    }

    public void setMontoc(double montoc) {
        this.montoc = montoc;
    }

    public String getDescripcionc() {
        return descripcionc;
    }

    public void setDescripcionc(String descripcionc) {
        this.descripcionc = descripcionc;
    }

    public String getFechac() {
        return fechac;
    }

    public void setFechac(String fechac) {
        this.fechac = fechac;
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
        return "Descripción: "+descripcionc+"\nMonto: Q."+montoc+"\nfecha: "+fechac+"\nNo."+numero+"\nentidad: "+entidad+"\nSaldo Q."+saldo;
    }
}
