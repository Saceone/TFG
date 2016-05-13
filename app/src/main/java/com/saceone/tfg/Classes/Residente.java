package com.saceone.tfg.Classes;

/**
 * Created by ASUS on 14/03/2016.
 */
public class Residente {

    private int id;
    private String nombre;
    private String apellidos;
    private int room;
    private String tipo_pension;
    private int desayunos_restantes;
    private int menus_restantes;
    private String notas;
    private boolean consumir;

    public Residente(int id, String nombre, String apellidos, int room, String tipo_pension, int desayunos_restantes, int menus_restantes, String notas){
        this.id=id;
        this.nombre=nombre;
        this.apellidos=apellidos;
        this.room=room;
        this.tipo_pension=tipo_pension;
        this.desayunos_restantes=desayunos_restantes;
        this.menus_restantes=menus_restantes;
        this.notas=notas;
        this.consumir = false;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public String getTipo_pension() {
        return tipo_pension;
    }

    public void setTipo_pension(String tipo_pension) {
        this.tipo_pension = tipo_pension;
    }

    public int getDesayunos_restantes() {
        return desayunos_restantes;
    }

    public void setDesayunos_restantes(int desayunos_restantes) {
        this.desayunos_restantes = desayunos_restantes;
    }

    public int getMenus_restantes() {
        return menus_restantes;
    }

    public void setMenus_restantes(int menus_restantes) {
        this.menus_restantes = menus_restantes;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public boolean exists() {
        if((this.nombre==null)&&(this.apellidos==null)&&(this.getTipo_pension()==null)){
            return false;
        }
        if((this.nombre.equals("no disponible"))&&(this.apellidos.equals("no disponibles"))&&(this.getTipo_pension().equals("no disponible"))){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean isConsumir() {
        return consumir;
    }

    public void setConsumir(boolean consumir) {
        this.consumir = consumir;
    }
}
