package com.saceone.tfg.Classes;

/**
 * Created by ASUS on 24/03/2016.
 */
public class ItemCompras {

    private int id;
    private String nombre;
    private String categoria;
    private int cantidad;
    private String unidad;
    private int borrar;


    public ItemCompras(int id, String nombre, String categoria, int cantidad, String unidad, int borrar) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.borrar = borrar;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public int getBorrar() {
        return borrar;
    }

}
