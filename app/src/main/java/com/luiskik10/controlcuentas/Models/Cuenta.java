package com.luiskik10.controlcuentas.Models;

public class Cuenta {
    private String  idcuenta;
    private String  numero;
    private String  entidad;
    private Double  saldo;
    private String  iduser;

    public Cuenta() {
    }

    public Cuenta(String idcuenta, String numero, String entidad, Double saldo, String iduser) {
        this.idcuenta = idcuenta;
        this.numero = numero;
        this.entidad = entidad;
        this.saldo = saldo;
        this.iduser = iduser;
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

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
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
        return "No."+numero+" Entidad: "+entidad+" Saldo Q."+saldo;
    }
}
