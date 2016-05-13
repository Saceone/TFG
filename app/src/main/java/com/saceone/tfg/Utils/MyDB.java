package com.saceone.tfg.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.saceone.tfg.Classes.Admin;
import com.saceone.tfg.Classes.Horario;
import com.saceone.tfg.Classes.ItemCompras;
import com.saceone.tfg.Classes.MediaPension;
import com.saceone.tfg.Classes.Registro;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.Classes.TagRoom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/*
 * Created by Ramón on 25/02/2016
 */

//TODO: tutorial bases de datos con sqlite android http://elbauldeandroid.blogspot.com.es/2013/02/base-de-datos-sqlite.html

public class MyDB extends SQLiteOpenHelper {

    private static final int VERSION_BASEDATOS = 1;

    //NOMBRE DE LA BASE DE DATOS
    private static final String NOMBRE_BASEDATOS = "mibasedatos.db";

    //NOMBRES DE LAS TABLAS
    private static final String TAGROOM = "tagroom";
    private static final String RESIDENTES = "residentes";
    private static final String LISTACOMPRA = "listacompra";
    private static final String DESAYUNOS = "desayunos";
    private static final String COMIDAS = "comidas";
    private static final String CENAS = "cenas";
    private static final String HORARIOS = "horarios";
    private static final String ADMIN = "admin";
    private static final String MEDIAPENSION = "mediapension";

    //SENTENCIAS SQL DE CREACION DE TABLAS
    private static final String TABLA_TAGROOM = "CREATE TABLE IF NOT EXISTS " + TAGROOM +
            "(_id INT PRIMARY KEY, tag TEXT, room INT)";
    private static final String TABLA_RESIDENTES = "CREATE TABLE IF NOT EXISTS " + RESIDENTES +
            "(_id INT PRIMARY KEY, nombre TEXT, apellidos TEXT, room INT, pension TEXT, desayunos INT, menus INT, notas TEXT)";
    private static final String TABLA_LISTACOMPRA = "CREATE TABLE IF NOT EXISTS " + LISTACOMPRA +
            "(_id INT PRIMARY KEY, nombre TEXT, categoria TEXT, cantidad INT, unidad TEXT, borrar INT)";
    private static final String TABLA_DESAYUNOS = "CREATE TABLE IF NOT EXISTS " + DESAYUNOS +
            "(_id INT PRIMARY KEY, tabla TEXT, nombre TEXT, apellidos TEXT, room INT, pension TEXT, hora INT, minuto INT, segundo INT, dia INT, mes INT, year INT)";
    private static final String TABLA_COMIDAS = "CREATE TABLE IF NOT EXISTS " + COMIDAS +
            "(_id INT PRIMARY KEY, tabla TEXT, nombre TEXT, apellidos TEXT, room INT, pension TEXT, hora INT, minuto INT, segundo INT, dia INT, mes INT, year INT)";
    private static final String TABLA_CENAS = "CREATE TABLE IF NOT EXISTS " + CENAS +
            "(_id INT PRIMARY KEY, tabla TEXT, nombre TEXT, apellidos TEXT, room INT, pension TEXT, hora INT, minuto INT, segundo INT, dia INT, mes INT, year INT)";
    private static final String TABLA_HORARIOS = "CREATE TABLE IF NOT EXISTS " + HORARIOS +
            "(_id INT PRIMARY KEY, consumicion TEXT, limite TEXT, hora INT, minuto INT)";
    private static final String TABLA_ADMIN = "CREATE TABLE IF NOT EXISTS " + ADMIN +
            "(_id INT PRIMARY KEY, pass INT)";
    private static final String TABLA_MEDIAPENSION = "CREATE TABLE IF NOT EXISTS " + MEDIAPENSION +
            "(_id INT PRIMARY KEY, menus INT, desayunos INT)";

    // CONSTRUCTOR
    public MyDB(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    //CREACIÓN
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_TAGROOM);
        db.execSQL(TABLA_RESIDENTES);
        db.execSQL(TABLA_LISTACOMPRA);
        db.execSQL(TABLA_DESAYUNOS);
        db.execSQL(TABLA_COMIDAS);
        db.execSQL(TABLA_CENAS);
        db.execSQL(TABLA_HORARIOS);
        db.execSQL(TABLA_ADMIN);
        db.execSQL(TABLA_MEDIAPENSION);
    }

    //ACTUALIZACIÓN
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_TAGROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_RESIDENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_LISTACOMPRA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_DESAYUNOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_COMIDAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_CENAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_HORARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_MEDIAPENSION);
        onCreate(db);
    }





    /*------------TABLA TAG-ROOM---------------------------*/
    //MODIFICAR TAG
    public void modifyTAG(int id, String tag, int room){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("tag", tag);
        valores.put("room", room);
        db.update(TAGROOM, valores, "_id=" + id, null);
        db.close();
    }

    //LEER TAGROOM POR ID/ROOM
    public TagRoom getTAGROOMwithID(int id) {
        SQLiteDatabase db = getReadableDatabase();
        TagRoom tagroom = null;
        String[] valores_recuperar = {"_id", "room", "tag"};
        Cursor c = db.query(TAGROOM, valores_recuperar, "_id=" + id, null, null, null, null, null);
        if(c != null) {
            c.moveToFirst();
            tagroom = new TagRoom(c.getInt(0), c.getInt(1), c.getString(2));
            c.close();
        }
        db.close();
        return tagroom;
    }

    //LEER TAGROOM POR TAG
    public TagRoom getTAGROOMwithTAG(String tag) {
        TagRoom tagroom;
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id", "room", "tag"};
        Cursor c = db.query(TAGROOM, valores_recuperar, "tag='" + tag + "'", null, null, null, null, null);
        if(!c.moveToFirst()) {
            tagroom = new TagRoom(-1,-1,"UNKNOWN_TAG");
        }
        else{
            c.moveToFirst();
            tagroom = new TagRoom(c.getInt(0), c.getInt(1), c.getString(2));
        }
        db.close();
        c.close();
        return tagroom;
    }

    //BUSCAR TAGROOM POR ROOM
    public Cursor getTagRoomIfContainsRoom(String input) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TAGROOM + " WHERE CAST(room AS TEXT) LIKE  '"
                +input+"%';";
        return db.rawQuery(selectQuery, null);
    }

    //LISTA COMPLETA TAG-ROOM
    public List<TagRoom> getTagRoomList() {
        SQLiteDatabase db = getReadableDatabase();
        List<TagRoom> tagRooms = new ArrayList<>();
        String[] values = {"_id", "room", "tag"};
        Cursor c = db.query(TAGROOM, values, null, null, null, null, "_id ASC"); //Cursor ordenado ascendentemente
        if(!c.moveToFirst()){
            Log.d("LOG: ", "No se han encontrado coincidencias en getTagRoomList().");
            return null;
        }
        else {
            c.moveToFirst();
            do {
                TagRoom tagRoom = new TagRoom(c.getInt(0), c.getInt(1),c.getString(2));
                tagRooms.add(tagRoom);
            } while (c.moveToNext());
            db.close();
            c.close();
            return tagRooms;
        }
    }

    //BORRAR CÓDIGO TAG DE UNA ENTRADA TAGROOM
    public void deleteTAG(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("tag",(String)null);
        valores.put("room", id);
        db.update(TAGROOM, valores, "_id=" + id, null);
        db.close();
    }
    /*------------------------------------------------------*/







    /*------------TABLA RESIDENTES--------------------------*/
    //MODIFICAR RESIDENTE
    public void modifyRESIDENTE(int id, String nombre, String apellidos, int room, String pension, int desayunos, int menus, String notas){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("nombre", nombre);
        valores.put("apellidos", apellidos);
        valores.put("room",room);
        valores.put("pension",pension);
        valores.put("desayunos",desayunos);
        valores.put("menus",menus);
        valores.put("notas",notas);
        db.update(RESIDENTES, valores, "_id=" + id, null);
        db.close();
    }

    //LEER UN RESIDENTE
    public Residente getRESIDENTE(int id) {
        Residente residente = null;
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id", "nombre", "apellidos", "room", "pension", "desayunos", "menus", "notas"};
        Cursor c = db.query(RESIDENTES, valores_recuperar, "_id=" + id,null, null, null, null, null);
        if(c != null) {
            c.moveToFirst();
            residente = new Residente(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getString(4), c.getInt(5), c.getInt(6), c.getString(7));
            c.close();
        }
        db.close();
        return residente;
    }

    //LISTA COMPLETA RESIDENTES
    public List<Residente> getResidentesList() {
        SQLiteDatabase db = getReadableDatabase();
        List<Residente> residentes = new ArrayList<>();
        String[] values = {"_id", "nombre", "apellidos", "room", "pension", "desayunos", "menus", "notas"};
        Cursor c = db.query(RESIDENTES, values, null, null, null, null, "_id ASC"); //Cursor ordenado descendientemente
        if(!c.moveToFirst()){
            Log.d("LOG: ", "No se han encontrado coincidencias en getResidentesList.");
            return null;
        }
        else {
            c.moveToFirst();
            do {
                Residente residente = new Residente(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getString(4), c.getInt(5), c.getInt(6), c.getString(7));
                residentes.add(residente);
            } while (c.moveToNext());
            db.close();
            c.close();
            return residentes;
        }
    }

    //ELIMINAR RESIDENTE
    public void deleteRESIDENTE(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("nombre","no disponible");
        valores.put("apellidos","no disponibles");
        valores.put("room", id);
        valores.put("pension","no disponible");
        valores.put("desayunos", 0);
        valores.put("menus", 0);
        valores.put("notas", "no disponible");
        db.update(RESIDENTES, valores, "_id=" + id, null);
        db.close();
    }

    //BUSCAR RESIDENTE POR NOMBRE
    public Cursor getResidenteIfContainsNombre(String input) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + RESIDENTES + " WHERE nombre LIKE  '"
                + "%"+input+"%';";
        return db.rawQuery(selectQuery, null);
    }

    //BUSCAR RESIDENTE POR APELLIDO
    public Cursor getResidenteIfContainsApellidos(String input) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + RESIDENTES + " WHERE apellidos LIKE  '"
                + "%"+input+"%';";
        return db.rawQuery(selectQuery, null);
    }

    //BUSCAR RESIDENTE POR HABITACION
    public Cursor getResidenteIfContainsRoom(String input) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + RESIDENTES + " WHERE CAST(room AS TEXT) LIKE  '"
                +input+"%';";
        return db.rawQuery(selectQuery, null);
    }
    /*------------------------------------------------------*/






    /*------------TABLA LISTA DE LA COMPRA------------------*/
    //INSERTAR TAG
    public void insertITEM(int id, String nombre, String categoria, int cantidad, String unidad, int borrar) {
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("_id", id);
            valores.put("nombre", nombre);
            valores.put("categoria", categoria);
            valores.put("cantidad", cantidad);
            valores.put("unidad", unidad);
            valores.put("borrar", borrar);
            db.insert(LISTACOMPRA, null, valores);
            db.close();
        }
    }

    //LISTA COMPLETA DE ITEMS DE LA LISTA DE COMPRA
    public List<ItemCompras> getItemComprasList() {
        SQLiteDatabase db = getReadableDatabase();
        List<ItemCompras> itemList = new ArrayList<>();
        String[] values = {"_id", "nombre", "categoria", "cantidad", "unidad", "borrar"};
        Cursor c = db.query(LISTACOMPRA, values, null, null, null, null, "_id ASC"); //Cursor ordenado ascendentemente
        if(!c.moveToFirst()){
            Log.d("LOG: ", "No se han encontrado coincidencias en getResidentesList.");
            return null;
        }
        else {
            c.moveToFirst();
            do {
                ItemCompras item = new ItemCompras(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getString(4),c.getInt(5));
                itemList.add(item);
            } while (c.moveToNext());
            db.close();
            c.close();
            return itemList;
        }
    }

    //MODIFICAR ITEM DE LA LISTA DE COMPRAS
    public void modifyITEM(int id, String nombre, String categoria, int cantidad, String unidad, int borrar) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("nombre", nombre);
        valores.put("categoria",categoria);
        valores.put("cantidad",cantidad);
        valores.put("unidad",unidad);
        valores.put("borrar",borrar);
        db.update(LISTACOMPRA, valores, "_id=" + id, null);
        db.close();
    }

    //ELIMINAR VARIOS ITEMS DE LA LISTA DE COMPRAS
    public void deleteITEMS(List<ItemCompras> itemList) {
        SQLiteDatabase db = getWritableDatabase();
        for (ItemCompras item : itemList) {
            db.delete(LISTACOMPRA, "_id=" + item.getId(), null);
        }
        db.close();
    }
    /*-------------------------------------------------------*/







    /*------------------TABLA REGISTROS----------------------*/
    //INSERTAR REGISTRO
    public void insertREG(String tabla, int id, String nombre, String apellidos, int room, String pension, int hora, int minuto, int segundo, int dia, int mes, int year){
        SQLiteDatabase db = getWritableDatabase();
        String tabla_necesaria;
        switch (tabla){
            case "desayunos":
                tabla_necesaria = DESAYUNOS;
                break;
            case "comidas":
                tabla_necesaria = COMIDAS;
                break;
            case "cenas":
                tabla_necesaria = CENAS;
                break;
            default:
                tabla_necesaria = null;
                break;
        }
        if((db != null)&&(tabla_necesaria != null)){
            ContentValues valores = new ContentValues();
            valores.put("tabla",tabla);
            valores.put("_id",id);
            valores.put("nombre", nombre);
            valores.put("apellidos", apellidos);
            valores.put("room", room);
            valores.put("pension", pension);
            valores.put("hora", hora);
            valores.put("minuto", minuto);
            valores.put("segundo", segundo);
            valores.put("dia", dia);
            valores.put("mes", mes);
            valores.put("year", year);
            db.insert(tabla_necesaria, null, valores);
            db.close();
        }
    }

    //LEER LISTA COMPLETA DE REGISTROS
    public List<Registro> getRegistroList() {
        SQLiteDatabase db = getReadableDatabase();
        List<Registro> registros = new ArrayList<>();
        String[] tablas = {DESAYUNOS,COMIDAS,CENAS};
        for(String tabla : tablas){
            String[] values = {"_id", "nombre", "apellidos", "pension", "hora", "minuto", "segundo", "dia", "mes", "year", "room"};
            Cursor c = db.query(tabla, values, null, null, null, null, null);
            if(!c.moveToFirst()){
                Log.d("LOG: ", "No se han encontrado coincidencias en getRegistroList.");
                return null;
            }
            else {
                c.moveToFirst();
                do {
                    Registro registro = new Registro(tabla, c.getInt(0), c.getString(1), c.getString(2),
                            c.getString(3), c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getInt(9), c.getInt(10));
                    registros.add(registro);
                } while (c.moveToNext());
                c.close();
            }
        }
        db.close();
        //TODO: ordenar una lista en funcion de uno de los campos de los objetos que contiene
        Collections.sort(registros, new Comparator<Registro>() {
            public int compare(Registro reg1, Registro reg2) {
                if (reg1.getId() == reg2.getId())
                    return 0;
                return reg1.getId() < reg2.getId() ? 1 : -1;
            }
        });
        return registros;
    }

    //COMPROBAR CONSUMICIONES DE PENSIÓN COMPLETA
    public boolean pensionCompletaValidadaPreviamente(String tabla, int room, int dia, int mes, int year) {
        SQLiteDatabase db = getReadableDatabase();
        String tabla_necesaria;
        switch (tabla){
            case "desayunos":
                tabla_necesaria = DESAYUNOS;
                break;
            case "comidas":
                tabla_necesaria = COMIDAS;
                break;
            case "cenas":
                tabla_necesaria = CENAS;
                break;
            default:
                tabla_necesaria = null;
                break;
        }
        if (db != null){
            String selectQuery = "SELECT * FROM " + tabla_necesaria + " WHERE " +
                    "room = '"+room+"' AND " +
                    "dia = '"+dia+"' AND " +
                    "mes = '"+mes+"' AND " +
                    "year = '"+year+"' AND " +
                    "tabla = '"+tabla+"';";
            Cursor cursor = db.rawQuery(selectQuery, null);
            return cursor.moveToFirst();
        }
        return false;
    }

    //ELIMINAR UN REGISTRO
    public void deleteREGISTRO(int id, String tabla) {
        SQLiteDatabase db = getWritableDatabase();
        String tabla_necesaria;
        switch (tabla){
            case "desayunos":
                tabla_necesaria=DESAYUNOS;
                break;
            case "comidas":
                tabla_necesaria=COMIDAS;
                break;
            case "cenas":
                tabla_necesaria=CENAS;
                break;
            default:
                tabla_necesaria=COMIDAS;
                break;
        }
        db.delete(tabla_necesaria, "_id=" + id, null);
        db.close();
    }

    //ELIMINAR TODOS LOS REGISTROS
    public void deleteREGS(List<Registro> registroList) {
        SQLiteDatabase db = getWritableDatabase();
        for (Registro registro : registroList) {
            db.delete(registro.getTabla(), "_id=" + registro.getId(), null);
        }
        db.close();
    }

    //LEER LOS REGISTROS DEL DIA
    public List<Registro> getDayList(int dia, int mes, int year) {
        SQLiteDatabase db = getReadableDatabase();
        List<Registro> registros = new ArrayList<>();
        String[] tablas = {DESAYUNOS,COMIDAS,CENAS};
        for(String tabla : tablas){
            String[] values = {"_id", "nombre", "apellidos", "pension", "hora", "minuto", "segundo", "dia", "mes", "year", "room"};
            Cursor c = db.query(tabla, values, null, null, null, null, null);
            if(!c.moveToFirst()){
                Log.d("LOG: ", "No se han encontrado coincidencias en getTodayList().");
                return null;
            }
            else {
                c.moveToFirst();
                do {
                    Registro registro = new Registro(tabla, c.getInt(0), c.getString(1), c.getString(2),
                            c.getString(3), c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getInt(9), c.getInt(10));
                    if((c.getInt(7)==dia)&&(c.getInt(8)==mes)&&(c.getInt(9)==year)){
                        registros.add(registro);
                    }
                } while (c.moveToNext());
                c.close();
            }
        }
        db.close();
        //TODO: ordenar una lista en funcion de uno de los campos de los objetos que contiene
        Collections.sort(registros, new Comparator<Registro>() {
            public int compare(Registro reg1, Registro reg2) {
                if (reg1.getId() == reg2.getId())
                    return 0;
                return reg1.getId() < reg2.getId() ? 1 : -1;
            }
        });
        return registros;
    }

    //OBTENER NUMEROS DE LOS REGISTROS NECESARIOS PARA LA GRAFICA
    public Integer getPeriodRegistros(String s, int dia_mes, int dia_semana, int mes, int year, String table) {
        SQLiteDatabase db = getReadableDatabase();
        List<Registro> registros = new ArrayList<>();
        String[] tablas = {DESAYUNOS,COMIDAS,CENAS};
        for(String tabla : tablas){
            String[] values = {"_id", "nombre", "apellidos", "pension", "hora", "minuto", "segundo", "dia", "mes", "year", "room"};
            Cursor c = db.query(tabla, values, null, null, null, null, null);
            if(!c.moveToFirst()){
                Log.d("LOG: ", "No se han encontrado coincidencias en getPeriodRegistros().");
                return 0;
            }
            else {
                c.moveToFirst();
                do {
                    Registro registro = new Registro(tabla, c.getInt(0), c.getString(1), c.getString(2),
                            c.getString(3), c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getInt(9), c.getInt(10));
                    switch (s) {
                        case "Desayunos":
                            if ((registro.getDia() == dia_mes) && (registro.getMes() == mes) && (registro.getYear() == year)) {
                                if (registro.getTabla().equals("desayunos")) {
                                    registros.add(registro);
                                }
                            }
                            break;
                        case "Comidas":
                            if ((registro.getDia() == dia_mes) && (registro.getMes() == mes) && (registro.getYear() == year)) {
                                if (registro.getTabla().equals("comidas")) {
                                    registros.add(registro);
                                }
                            }
                            break;
                        case "Cenas":
                            if ((registro.getDia() == dia_mes) && (registro.getMes() == mes) && (registro.getYear() == year)) {
                                if (registro.getTabla().equals("cenas")) {
                                    registros.add(registro);
                                }
                            }
                            break;
                        default:
                            try {
                                //Gráfica del último mes
                                if ((Integer.parseInt(s) >= 1) && (Integer.parseInt(s) <= 31)) {
                                    if ((registro.getMes() == mes) && (registro.getDia() == Integer.parseInt(s))) {
                                        if (registro.getTabla().equals(table)) {
                                            registros.add(registro);
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                if (s.contains(":")) {
                                    //RECIBIDA UNA HORA
                                    //intervalo del numero generado por la hora (n-15, n+14) en desayunos y (n-8, n+7) en menús
                                    int minutos_antes;
                                    int minutos_despues;
                                    if (table.equals("desayunos")) {
                                        minutos_antes = 15;
                                        minutos_despues = 14;
                                    } else {
                                        minutos_antes = 8;
                                        minutos_despues = 7;
                                    }
                                    boolean hourInPeriod = false;
                                    try {
                                        Date check = new Date(year, mes, dia_mes, registro.getHora(), registro.getMinuto());
                                        Log.d("LOG: ", "CHECK:        " + check.toString());
                                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                                        Date hora_central = sdf.parse(s);
                                        hora_central.setDate(dia_mes);
                                        hora_central.setMonth(mes);
                                        hora_central.setYear(year);
                                        Log.d("LOG: ", "HORA CENTRAL: " + hora_central.toString());
                                        Date before = new Date(hora_central.getTime() - (minutos_antes * 60000));
                                        Log.d("LOG: ", "BEFORE:       " + before.toString());
                                        Date after = new Date(hora_central.getTime() + (minutos_despues * 60000));
                                        Log.d("LOG: ", "AFTER:        " + after.toString());
                                        hourInPeriod = (before.getTime() < check.getTime()) && (after.getTime() > check.getTime());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if ((c.getInt(7) == dia_mes) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                        if (hourInPeriod) {
                                            registros.add(registro);
                                        }
                                    }
                                } else {
                                    //RECIBIDO UN MES
                                    if (s.length() > 1) {
                                        int mes_necesario = -1;
                                        switch (s) {
                                            case "Ene":
                                                mes_necesario = 1;
                                                break;
                                            case "Feb":
                                                mes_necesario = 2;
                                                break;
                                            case "Mar":
                                                mes_necesario = 3;
                                                break;
                                            case "Abr":
                                                mes_necesario = 4;
                                                break;
                                            case "May":
                                                mes_necesario = 5;
                                                break;
                                            case "Jun":
                                                mes_necesario = 6;
                                                break;
                                            case "Jul":
                                                mes_necesario = 7;
                                                break;
                                            case "Ago":
                                                mes_necesario = 8;
                                                break;
                                            case "Sep":
                                                mes_necesario = 8;
                                                break;
                                            case "Oct":
                                                mes_necesario = 10;
                                                break;
                                            case "Nov":
                                                mes_necesario = 11;
                                                break;
                                            case "Dic":
                                                mes_necesario = 12;
                                                break;
                                        }
                                        if ((c.getInt(8) == mes_necesario) && (c.getInt(9) == year)) {
                                            if (table.equals("todo")) {
                                                registros.add(registro);
                                            } else {
                                                if (registro.getTabla().equals(table)) {
                                                    registros.add(registro);
                                                }
                                            }
                                        }
                                    }
                                    //RECIBIDO UN DIA
                                    else {
                                        switch (s) {
                                            case "L":
                                                switch (dia_semana) {
                                                    case Calendar.MONDAY:
                                                        if ((c.getInt(7) == (dia_mes)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.TUESDAY:
                                                        if ((c.getInt(7) == (dia_mes - 1)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.WEDNESDAY:
                                                        if ((c.getInt(7) == (dia_mes - 2)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.THURSDAY:
                                                        if ((c.getInt(7) == (dia_mes - 3)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.FRIDAY:
                                                        if ((c.getInt(7) == (dia_mes - 4)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SATURDAY:
                                                        if ((c.getInt(7) == (dia_mes - 5)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SUNDAY:
                                                        if ((c.getInt(7) == (dia_mes - 6)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                }
                                                break;
                                            case "M":
                                                switch (dia_semana) {
                                                    case Calendar.TUESDAY:
                                                        if ((c.getInt(7) == (dia_mes)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.WEDNESDAY:
                                                        if ((c.getInt(7) == (dia_mes - 1)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.THURSDAY:
                                                        if ((c.getInt(7) == (dia_mes - 2)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.FRIDAY:
                                                        if ((c.getInt(7) == (dia_mes - 3)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SATURDAY:
                                                        if ((c.getInt(7) == (dia_mes - 4)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SUNDAY:
                                                        if ((c.getInt(7) == (dia_mes - 5)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case "X":
                                                switch (dia_semana) {
                                                    case Calendar.WEDNESDAY:
                                                        if ((c.getInt(7) == (dia_mes)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.THURSDAY:
                                                        if ((c.getInt(7) == (dia_mes - 1)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.FRIDAY:
                                                        if ((c.getInt(7) == (dia_mes - 2)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SATURDAY:
                                                        if ((c.getInt(7) == (dia_mes - 3)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SUNDAY:
                                                        if ((c.getInt(7) == (dia_mes - 4)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case "J":
                                                switch (dia_semana) {
                                                    case Calendar.THURSDAY:
                                                        if ((c.getInt(7) == (dia_mes)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.FRIDAY:
                                                        if ((c.getInt(7) == (dia_mes - 1)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SATURDAY:
                                                        if ((c.getInt(7) == (dia_mes - 2)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SUNDAY:
                                                        if ((c.getInt(7) == (dia_mes - 3)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case "V":
                                                switch (dia_semana) {
                                                    case Calendar.FRIDAY:
                                                        if ((c.getInt(7) == (dia_mes)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SATURDAY:
                                                        if ((c.getInt(7) == (dia_mes - 1)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SUNDAY:
                                                        if ((c.getInt(7) == (dia_mes - 2)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case "S":
                                                switch (dia_semana) {
                                                    case Calendar.SATURDAY:
                                                        if ((c.getInt(7) == (dia_mes)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Calendar.SUNDAY:
                                                        if ((c.getInt(7) == (dia_mes - 1)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                            case "D":
                                                switch (dia_semana) {
                                                    case Calendar.SUNDAY:
                                                        if ((c.getInt(7) == (dia_mes)) && (c.getInt(8) == mes) && (c.getInt(9) == year)) {
                                                            if (table.equals("todo")) {
                                                                registros.add(registro);
                                                            } else {
                                                                if (registro.getTabla().equals(table)) {
                                                                    registros.add(registro);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                            break;
                    }
                } while (c.moveToNext());
                c.close();
            }
        }
        db.close();
        return registros.size();
    }
    /*-------------------------------------------------------*/






    /*---------------------HORARIOS--------------------------*/
    //MODIFICAR HORARIOS
    public void setTimeLimit(String consumicion, String limite, int hora, int minuto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        int id;
        if(consumicion.equals("desayunos")&&limite.equals("apertura")) id=0;
        else if(consumicion.equals("desayunos")&&limite.equals("cierre")) id=1;
        else if(consumicion.equals("comidas")&&limite.equals("apertura")) id=2;
        else if(consumicion.equals("comidas")&&limite.equals("cierre")) id=3;
        else if(consumicion.equals("cenas")&&limite.equals("apertura")) id=4;
        else id=5;
        valores.put("hora",hora);
        valores.put("minuto",minuto);
        db.update(HORARIOS, valores, "_id=" + id, null);
        db.close();
    }

    //LEER UN HORARIO
    public Horario getHORARIO(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id", "consumicion", "limite", "hora", "minuto"};
        Cursor c = db.query(HORARIOS, valores_recuperar, "_id=" + id, null, null, null, null, null);
        if(!c.moveToFirst()){
            Log.d("LOG: ", "No se ha podido introducir el horario.");
            return null;
        }
        else{
            return new Horario(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getInt(4));
        }
    }

    //TIPO DE CONSUMICIÓN SEGÚN HORARIO
    public String getTablaFromTime(float adaptedTime) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id", "consumicion", "limite", "hora", "minuto"};
        float[] limites = new float[6];
        Cursor c = db.query(HORARIOS, valores_recuperar, null, null, null, null, "_id ASC"); //Cursor ordenado ascendentemente
        if(!c.moveToFirst()){
            Log.d("LOG: ", "No se han encontrado coincidencias en getTablaFromTime().");
            return null;
        }
        else {
            c.moveToFirst();
            int i = 0;
            do {
                Horario horario = new Horario(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getInt(4));
                limites[i] = horario.getHora() + ((float)horario.getMinuto()/60);
                i++;
            } while (c.moveToNext());
            db.close();
            c.close();
            if((adaptedTime>=limites[0])&&(adaptedTime<=limites[1])){
                return "desayunos";
            }
            else if((adaptedTime>=limites[2])&&(adaptedTime<=limites[3])){
                return "comidas";
            }
            else if((adaptedTime>=limites[4])&&(adaptedTime<=limites[5])){
                return "cenas";
            }
            else{
                return null;
            }
        }
    }
    /*-------------------------------------------------------*/






    /*-----------INICIALIZACIÓN DE LA BASE DE DATOS----------*/
    //RESETEAR LA LISTA TAG-ROOM
    public void resetTAGROOM(){
        int[] roomList = {110,111,112,114,115,116,117,119,120,121,122,
                201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,
                301,302,303,304,305,306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,
                401,402,403,404,405,406,407,408,409,410,411,412,413,414,415,416,417,418,419,420,421,422,
                501,502,503,504,505,506,507,508,509,510,511,512,513,514,515,516,517,518,519,520,521,522,
                601,602,603,604,605,606, 607, 608, 609, 610, 611,612,613,614,615,616,617,618, 619, 620, 621, 622};
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            db.delete(TAGROOM, null, null);
            for(int i=0;i<roomList.length-1;i++){
                ContentValues valores = new ContentValues();
                valores.put("_id", roomList[i]);
                valores.put("room", roomList[i]);
                db.insert("tagroom", null, valores);
            }
            db.close();
        }
    }

    //RESETEAR LA LISTA RESIDENTES
    public void resetRESIDENTES() {
        int[] roomList = {110, 111, 112, 114, 115, 116, 117, 119, 120, 121, 122,
                201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222,
                301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322,
                401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422,
                501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522,
                601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622};
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.delete(RESIDENTES, null, null);
            for (int i = 0; i < roomList.length-1; i++) {
                ContentValues valores = new ContentValues();
                valores.put("_id", roomList[i]);
                valores.put("nombre","no disponible");
                valores.put("apellidos","no disponibles");
                valores.put("room", roomList[i]);
                valores.put("pension","no disponible");
                valores.put("desayunos", 0);
                valores.put("menus", 0);
                valores.put("notas", "no disponible");
                db.insert(RESIDENTES, null, valores);
            }
            db.close();
        }
    }

    //INICIALIZAR REGISTROS
    public void resetREGISTROS() {
        //generamos 3 registros dummy que luego no tendremos en cuenta
        //pero sirven para inicializar la lista de registros
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            String[] tablas = {"desayunos","comidas","cenas"};
            int id = 0;
            for(String tabla : tablas){
                db.delete(tabla, null, null);
                ContentValues valores = new ContentValues();
                valores.put("nombre","dummy_nombre");
                valores.put("apellidos", "dummy_apellidos");
                valores.put("room",0);
                valores.put("pension","dummy_pension");
                valores.put("hora",0);
                valores.put("minuto",0);
                valores.put("segundo",0);
                valores.put("dia",0);
                valores.put("mes",0);
                valores.put("year",0);
                valores.put("_id",id);
                id++;
                db.insert(tabla, null, valores);
            }
            db.close();
        }
    }

    //INICIALIZAR HORARIOS
    public void resetTimeLimits() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.delete(HORARIOS, null, null);
            String[] consumicion = {"desayunos","desayunos","comidas","comidas","cenas","cenas"};
            String[] limite = {"apertura","cierre","apertura","cierre","apertura","cierre"};
            int[] hora = {7,12,13,15,20,22};
            int[] minuto = {0,0,0,45,30,45};
            ContentValues valores = new ContentValues();
            for(int i = 0; i<6; i++){
                valores.put("_id",i);
                valores.put("consumicion",consumicion[i]);
                valores.put("limite",limite[i]);
                valores.put("hora",hora[i]);
                valores.put("minuto",minuto[i]);
                db.insert(HORARIOS, null, valores);
            }
            db.close();
        }
    }

    //INICIALIZAR DATOS ADMIN
    public void resetADMIN() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.delete(ADMIN, null, null);
            ContentValues valores = new ContentValues();
            valores.put("_id",0);
            valores.put("pass",1234);
            db.insert(ADMIN, null, valores);
            db.close();
        }
    }

    //INICIALIZAR DATOS MEDIA PENSION
    public void resetMEDIAPENSION() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.delete(MEDIAPENSION, null, null);
            ContentValues valores = new ContentValues();
            valores.put("_id",0);
            valores.put("menus", 120);
            valores.put("desayunos",90);
            db.insert(MEDIAPENSION, null, valores);
            db.close();
        }
    }
    /*--------------------------------------------------------*/






    /*------------------AJUSTES AVANZADOS---------------------*/
    //COMPROBAR SI LA BASE DE DATOS ESTÁ INICIALIZADA
    public boolean isInitialized() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TAGROOM, null);
        return !(c.getCount()==0);
    }

    //DATOS DE MEDIA PENSION
    public MediaPension getMediaPension() {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id", "menus", "desayunos"};
        Cursor c = db.query(MEDIAPENSION, valores_recuperar, "_id=0", null, null, null, null, null);
        if(!c.moveToFirst()){
            Log.d("LOG: ", "No se ha podido recuperar los datos de media pensión.");
            return null;
        }
        else{
            return new MediaPension(c.getInt(1),c.getInt(2));
        }
    }

    //DATOS DEL ADMIN
    public Admin getAdmin() {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id", "pass"};
        Cursor c = db.query(ADMIN, valores_recuperar, "_id=0", null, null, null, null, null);
        if(!c.moveToFirst()){
            Log.d("LOG: ", "No se ha podido recuperar los datos de administrador.");
            return null;
        }
        else{
            return new Admin(c.getInt(1));
        }
    }

    //MODIFICAR DATOS DEFAULT DE MEDIA PENSION
    public void setMEDIAPENSION(String tipo, int number) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        switch (tipo){
            case "menus":
                valores.put("menus",number);
                break;
            default:
                valores.put("desayunos",number);
                break;
        }
        db.update(MEDIAPENSION, valores, "_id=0", null);
        db.close();
    }

    //MODIFICAR LA CONTRASEÑA
    public void setADMIN(int pass) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("pass",pass);
        db.update(ADMIN, valores, "_id=0", null);
        db.close();
    }
    /*---------------------------------------------------------*/
}
