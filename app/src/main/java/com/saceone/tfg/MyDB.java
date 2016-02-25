package com.saceone.tfg;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ASUS on 25/02/2016.
 */
public class MyDB extends SQLiteOpenHelper {

    private static final int VERSION_BASEDATOS = 1;

    // Nombre de nuestro archivo de base de datos
    private static final String NOMBRE_BASEDATOS = "mibasedatos.db";

    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_CONTACTOS = "CREATE TABLE tag" +
            "(_id INT PRIMARY KEY, tag TEXT, room INT)";

    // CONSTRUCTOR de la clase
    public MyDB(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_CONTACTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_CONTACTOS);
        onCreate(db);
    }

    public void insertTAG(int id, String tag, int room) {
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("_id", id);
            valores.put("tag", tag);
            valores.put("room", room);
            db.insert("tag", null, valores);
            db.close();
        }
    }
}
