package com.saceone.tfg.Classes;

/**
 * Created by ASUS on 22/04/2016.
 */
public class Horario {

    private int id;
    private String consumicion;
    private String limite;
    private int hora;
    private int minuto;

    public Horario(int id, String consumicion, String limite, int hora, int minuto) {
        this.id = id;
        this.consumicion = consumicion;
        this.limite = limite;
        this.hora = hora;
        this.minuto = minuto;
    }

    public int getId() {
        return id;
    }

    public String getConsumicion() {
        return consumicion;
    }

    public String getLimite() {
        return limite;
    }

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }
}
