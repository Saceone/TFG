package com.saceone.tfg.Classes;

/**
 * Created by ASUS on 28/04/2016.
 */
public class MyDrive {
    private String database;
    private String fotos;
    private String compra;
    private String instagram;
    private String registros;
    private String desayunos;
    private String comidas;
    private String cenas;

    public MyDrive(String database, String fotos, String compra, String instagram, String registros, String desayunos, String comidas, String cenas) {
        this.database = database;
        this.fotos = fotos;
        this.compra = compra;
        this.instagram = instagram;
        this.registros = registros;
        this.desayunos = desayunos;
        this.comidas = comidas;
        this.cenas = cenas;
    }

    public String getDatabase() {
        return database;
    }

    public String getFotos() {
        return fotos;
    }

    public String getCompra() {
        return compra;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getRegistros() {
        return registros;
    }

    public String getDesayunos() {
        return desayunos;
    }

    public String getComidas() {
        return comidas;
    }

    public String getCenas() {
        return cenas;
    }
}
