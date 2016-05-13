package com.saceone.tfg.Classes;

/**
 * Created by ASUS on 01/04/2016.
 */
public class Registro {

    private final String tabla;
    private final int year;
    private final int mes;
    private final int dia;
    private final int segundo;
    private final int minuto;
    private final int hora;
    private final int room;
    private final String pension;
    private final String apellidos;
    private final String nombre;
    private final int id;

    public Registro(String tabla, int id, String nombre, String apellidos, String pension, int hora, int minuto, int segundo, int dia, int mes, int year, int room){
        this.tabla=tabla;
        this.nombre=nombre;
        this.apellidos=apellidos;
        this.pension=pension;
        this.hora=hora;
        this.minuto=minuto;
        this.segundo=segundo;
        this.dia=dia;
        this.mes=mes;
        this.year=year;
        this.id = id;
        this.room = room;
    }

    public String getTabla() {
        return tabla;
    }

    public int getId(){
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMes() {
        return mes;
    }

    public int getDia() {
        return dia;
    }

    public int getSegundo() {
        return segundo;
    }

    public int getMinuto() {
        return minuto;
    }

    public int getHora() {
        return hora;
    }

    public int getRoom() {
        return room;
    }

    public String getPension() {
        return pension;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombre() {
        return nombre;
    }
}
