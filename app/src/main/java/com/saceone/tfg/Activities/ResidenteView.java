package com.saceone.tfg.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Classes.Registro;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import java.io.File;

/**
 * Created by ASUS on 16/03/2016.
 */
public class ResidenteView extends AppCompatActivity {

    private static final int EDIT_RESIDENTE = 1;
    private static final String NEW = "new";
    private static final String MODIFY = "modify";

    final MyDB db = new MyDB(this);

    ImageView iv_pic;
    TextView txt_nombre;
    TextView txt_apellidos;
    TextView txt_room;
    TextView txt_pension;
    TextView txt_menus;
    TextView txt_desayunos;
    TextView txt_notas;

    String request;

    File photo;
    String nombre;
    String apellidos;
    int room;
    String pension;
    int menus;
    int desayunos;
    String notas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.residente_view);

        iv_pic = (ImageView)findViewById(R.id.img_residente_view);

        txt_nombre = (TextView)findViewById(R.id.txt_residente_view_nombre);
        txt_apellidos = (TextView)findViewById(R.id.txt_residente_view_apellidos);
        txt_room = (TextView)findViewById(R.id.txt_residente_view_habitacion);
        txt_pension = (TextView)findViewById(R.id.txt_residente_view_pension);
        txt_menus = (TextView)findViewById(R.id.txt_residente_view_menus);
        txt_desayunos = (TextView)findViewById(R.id.txt_residente_view_desayunos);
        txt_notas = (TextView)findViewById(R.id.txt_residente_view_notas);

        request=getIntent().getStringExtra("request");
    }

    @Override
    protected void onResume(){
        super.onResume();
        //seteamos la View en onResume para actualizar cambios que puedan provenir de una modificacion de la ficha
        setResidenteView(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (request){
            case "existingRD":
                getMenuInflater().inflate(R.menu.menu_residente_view_existing, menu);
                break;
            case "nonExistingRD":
                getMenuInflater().inflate(R.menu.menu_residente_view_new, menu);
                break;
            case "updateRD":
                getMenuInflater().inflate(R.menu.menu_residente_view_existing, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.menu_residente_view_new, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_residente_view_add) {
            newResidente();
            return true;
        }
        if (id == R.id.menu_residente_view_modify) {
            modifyResidente();
            return true;
        }
        if (id == R.id.menu_residente_view_delete) {
            deleteResidente();
            return true;
        }
        if (id == R.id.menu_residente_view_history){
            viewHistory();
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewHistory() {
        if(request.equals("existingRD")) {
            final Residente residente = db.getRESIDENTE(getIntent().getIntExtra("room",-1));
            int desayunos=0, comidas=0, cenas=0;
            String tabla;
            StringBuilder sb = new StringBuilder();
            AlertDialog.Builder adb = new AlertDialog.Builder(ResidenteView.this);
            adb.setTitle("Historial de " + residente.getNombre() + " " + residente.getApellidos());
            for(Registro registro : db.getRegistroList()){
                if((registro.getNombre().equals(residente.getNombre()))&&(registro.getApellidos().equals(registro.getApellidos()))){
                    switch (registro.getTabla()){
                        case "desayunos":
                            tabla="Desayuno";
                            desayunos++;
                            break;
                        case "comidas":
                            tabla="Comida";
                            comidas++;
                            break;
                        case "cenas":
                            tabla="Cena";
                            cenas++;
                            break;
                        default:
                            tabla="Otra consumición";
                            break;
                    }
                    sb.append(tabla+"\n");
                    if(registro.getDia()<=9)sb.append("0");
                    sb.append("" + registro.getDia() + "/");
                    if(registro.getMes()<=9)sb.append("0");
                    sb.append("" + registro.getMes() + "/");
                    sb.append(registro.getYear()+"    ");

                    if(registro.getHora()<=9)sb.append("0");
                    sb.append("" + registro.getHora() + ":");
                    if(registro.getMinuto()<=9)sb.append("0");
                    sb.append("" + registro.getMinuto()+"\n");

                    sb.append("--------------------------------\n");
                }
            }
            StringBuilder msg = new StringBuilder();
            msg.append("Desayunos: "+desayunos+"\n");
            msg.append("Comidas: "+comidas+"\n");
            msg.append("Cenas: "+cenas+"\n\n");
            msg.append("--------------------------------\n\n");
            msg.append(sb.toString());
            adb.setMessage(msg.toString());
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            adb.create().show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_RESIDENTE) {
            if (resultCode == RESULT_OK) {
                nombre = data.getStringExtra("nombre");
                apellidos = data.getStringExtra("apellidos");
                room = data.getIntExtra("room", -1);
                pension = data.getStringExtra("pension");
                menus = data.getIntExtra("menus", -1);
                desayunos = data.getIntExtra("desayunos",-1);
                notas = data.getStringExtra("notas");
                db.modifyRESIDENTE(room, nombre, apellidos, room, pension, desayunos, menus, notas);
                request="updateRD";
                setResidenteView(request);
            }
        }
    }

    private void setResidenteView(String request) {
        //cuando seteamos la view tambien hay que actualizar el menu
        //así que invalidamos el menu, lo cual hace que se llame de nuevo al onCreateOptionsMenu()
        invalidateOptionsMenu();
        switch (request){
            case "existingRD":
                txt_nombre.setVisibility(View.VISIBLE);
                nombre = getIntent().getStringExtra("nombre");
                txt_nombre.setText("Nombre: " + nombre);
                txt_apellidos.setVisibility(View.VISIBLE);
                apellidos = getIntent().getStringExtra("apellidos");
                txt_apellidos.setText("Apellidos: " + apellidos);
                txt_pension.setVisibility(View.VISIBLE);
                pension = getIntent().getStringExtra("pension");
                txt_pension.setText("Tipo de pensión: "+pension);
                if(pension.equals("Media pensión")){
                    txt_menus.setVisibility(View.VISIBLE);
                    menus = getIntent().getIntExtra("menus", -1);
                    txt_menus.setText("Menús restantes: "+menus);
                    txt_desayunos.setVisibility(View.VISIBLE);
                    desayunos = getIntent().getIntExtra("desayunos",-1);
                    txt_desayunos.setText("Desayunos restantes: " + desayunos);
                }
                else {
                    txt_menus.setVisibility(View.GONE);
                    txt_desayunos.setVisibility(View.GONE);
                }
                room = getIntent().getIntExtra("room", -1);
                txt_room.setText("Habitación: " + room);
                if(notas==null){
                    txt_notas.setVisibility(View.GONE);
                }
                txt_notas.setVisibility(View.VISIBLE);
                notas = getIntent().getStringExtra("notas");
                if((notas==null)||notas.equals("")||notas.equals("no disponible")){
                    txt_notas.setVisibility(View.GONE);
                }
                else{
                    txt_notas.setVisibility(View.VISIBLE);
                    txt_notas.setText("Notas: " + notas);
                }
                photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos",nombre+apellidos+".jpg");
                if(photo.exists()){
                    //TODO: cargar imagenes desde archivo http://stackoverflow.com/a/2690030
                    iv_pic.setImageBitmap(BitmapFactory.decodeFile(Environment
                            .getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos/" + nombre + apellidos + ".jpg"));
                }
                else{
                    iv_pic.setImageResource(R.drawable.random_user);
                }
                break;
            case "nonExistingRD":
                iv_pic.setImageResource(R.drawable.random_user);
                txt_nombre.setVisibility(View.VISIBLE);
                txt_nombre.setText("Ficha vacía");
                txt_apellidos.setVisibility(View.GONE);
                txt_pension.setVisibility(View.GONE);
                txt_room.setVisibility(View.VISIBLE);
                room = getIntent().getIntExtra("room", -1);
                txt_room.setText("Habitación: " + room);
                txt_menus.setVisibility(View.GONE);
                txt_desayunos.setVisibility(View.GONE);
                txt_notas.setVisibility(View.GONE);
                break;
            case "updateRD":
                txt_nombre.setVisibility(View.VISIBLE);
                txt_nombre.setText("Nombre: " + nombre);
                txt_apellidos.setVisibility(View.VISIBLE);
                txt_apellidos.setText("Apellidos: " + apellidos);
                txt_room.setVisibility(View.VISIBLE);
                txt_room.setText("Habitación: " + room);
                txt_pension.setVisibility(View.VISIBLE);
                txt_pension.setText(pension);
                if(pension.equals("Media pensión")){
                    txt_menus.setVisibility(View.VISIBLE);
                    txt_menus.setText("Menús restantes: " + menus);
                    txt_desayunos.setVisibility(View.VISIBLE);
                    txt_desayunos.setText("Desayunos restantes: " + desayunos);
                }
                else{
                    txt_menus.setVisibility(View.GONE);
                    txt_desayunos.setVisibility(View.GONE);
                }
                if((notas==null)||notas.equals("")||notas.equals("no disponible")){
                    txt_notas.setVisibility(View.GONE);
                }
                else{
                    txt_notas.setVisibility(View.VISIBLE);
                    txt_notas.setText("Notas: " + notas);
                }
                photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos",nombre+apellidos+".jpg");
                if(photo.exists()){
                    iv_pic.setImageBitmap(BitmapFactory.decodeFile(Environment
                            .getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos/" + nombre + apellidos + ".jpg"));
                }
                else{
                    iv_pic.setImageResource(R.drawable.random_user);
                }
                break;
            default:
                txt_nombre.setVisibility(View.GONE);
                txt_apellidos.setVisibility(View.GONE);
                txt_pension.setVisibility(View.GONE);
                txt_menus.setVisibility(View.GONE);
                txt_desayunos.setVisibility(View.GONE);
                txt_notas.setVisibility(View.GONE);
                break;
        }
    }

    private void newResidente() {
        Intent i = new Intent(ResidenteView.this, newResidente.class);
        i.putExtra("room",room);
        i.putExtra("request",NEW);
        startActivityForResult(i, EDIT_RESIDENTE);
    }

    private void modifyResidente() {
        Intent i = new Intent(ResidenteView.this, newResidente.class);
        Residente residente = db.getRESIDENTE(room);
        i.putExtra("request",MODIFY);
        i.putExtra("nombre",residente.getNombre());
        i.putExtra("apellidos",residente.getApellidos());
        i.putExtra("pension",residente.getTipo_pension());
        i.putExtra("room",room);
        i.putExtra("menus",residente.getMenus_restantes());
        i.putExtra("desayunos",residente.getDesayunos_restantes());
        i.putExtra("notas",residente.getNotas());
        startActivityForResult(i, EDIT_RESIDENTE);
    }

    private void deleteResidente() {
        AlertDialog.Builder adb = new AlertDialog.Builder(ResidenteView.this);
        adb.setTitle("Eliminar ficha");
        adb.setMessage("¿Está seguro que desea borrar la ficha de este residente? \n\nAdvertencia: se perderán todos sus datos.");
        adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos", nombre + apellidos + ".jpg");
                if (photo.exists()) {
                    photo.delete();
                }
                iv_pic.setImageResource(R.drawable.random_user);
                nombre = "no disponible";
                apellidos = "no disponibles";
                pension = "no disponible";
                desayunos = 0;
                menus = 0;
                notas = null;
                db.modifyRESIDENTE(room, nombre, apellidos, room, pension, desayunos, menus, notas);
                request = "nonExistingRD";
                setResidenteView(request);
            }
        });
        adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }
}
