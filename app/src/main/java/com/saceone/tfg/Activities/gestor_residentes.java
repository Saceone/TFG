package com.saceone.tfg.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.saceone.tfg.Adapters.ResidentesAdapter;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.ItemClickSupport;
import com.saceone.tfg.Utils.MyDB;
import com.saceone.tfg.Utils.ResidentesConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 14/03/2016.
 */


//TODO:https://icons8.com/web-app/for/androidL/food

public class gestor_residentes extends AppCompatActivity{

    final MyDB db = new MyDB(this);

    //para convertir la posicion (0,1,2,...) de la lista en numero de habitacion (110,111,112,...)
    List<Integer> roomList = new ArrayList<Integer>();
    int id;

    //TODO: edittext de busqueda en actionbar http://stackoverflow.com/q/26965319
    MenuItem searchItem;
    SearchView searchView;

    RecyclerView rv_residentes;
    TextView txt_lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestor_residentes);

        rv_residentes = (RecyclerView)findViewById(R.id.rv_residentes);
        rv_residentes.setHasFixedSize(true);
        LinearLayoutManager llm_tagroom = new LinearLayoutManager(this);
        llm_tagroom.setOrientation(LinearLayoutManager.VERTICAL);
        rv_residentes.setLayoutManager(llm_tagroom);

        txt_lista = (TextView)findViewById(R.id.txt_gestor_residentes);

        setResidentesList();

        ItemClickSupport.addTo(rv_residentes).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                id = roomList.get(position);
                Intent i = new Intent(gestor_residentes.this, ResidenteView.class);
                Residente residente = db.getRESIDENTE(id);
                String request;
                if (residente.exists()) {
                    i.putExtra("nombre", residente.getNombre() == null ? "no disponible" : residente.getNombre());
                    i.putExtra("apellidos", residente.getApellidos() == null ? "no disponibles" : residente.getApellidos());
                    i.putExtra("pension", residente.getTipo_pension() == null ? "no disponible" : residente.getTipo_pension());
                    i.putExtra("menus", residente.getMenus_restantes());
                    i.putExtra("desayunos", residente.getDesayunos_restantes());
                    i.putExtra("notas", residente.getNotas());
                    request = "existingRD";
                } else {
                    request = "nonExistingRD";
                }
                i.putExtra("request", request);
                i.putExtra("room", residente.getRoom());
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setResidentesList();
    }

    //TODO: resetear el searchview http://stackoverflow.com/questions/13142678/resetting-search-widget-searchview-value
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        clearSearchView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_residente, menu);
        searchItem = menu.findItem(R.id.menu_residente_buscar_habitacion);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Buscar...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String input) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                List<Residente> listResidente = new ArrayList<Residente>();
                roomList.clear();
                try {
                    Cursor cursorRoom = db.getResidenteIfContainsRoom(input);
                    Cursor cursorNombre = db.getResidenteIfContainsNombre(input);
                    Cursor cursorApellidos = db.getResidenteIfContainsApellidos(input);

                    if (!cursorRoom.moveToFirst()) {
                        Log.d("LOG: ", "no hay resultados para esa habitación.");
                    } else {
                        txt_lista.setText(R.string.lista_residentes);
                        cursorRoom.moveToFirst();
                        if (cursorRoom != null) {
                            int id = cursorRoom.getColumnIndex("_id");
                            do {
                                int room = cursorRoom.getInt(id);
                                Residente residente = db.getRESIDENTE(room);
                                roomList.add(residente.getRoom());
                                listResidente.add(residente);
                            } while (cursorRoom.moveToNext());
                        }
                    }
                    if (!cursorNombre.moveToFirst()) {
                        Log.d("LOG: ", "no hay resultados para ese nombre.");
                    } else {
                        txt_lista.setText(R.string.lista_residentes);
                        cursorNombre.moveToFirst();
                        if (cursorNombre != null) {
                            int id = cursorNombre.getColumnIndex("_id");
                            do {
                                int room = cursorNombre.getInt(id);
                                Residente residente = db.getRESIDENTE(room);
                                roomList.add(residente.getRoom());
                                listResidente.add(residente);
                            } while (cursorNombre.moveToNext());
                        }
                    }
                    if (!cursorApellidos.moveToFirst()) {
                        Log.d("LOG: ", "no hay resultados para esos apellidos.");
                    } else {
                        txt_lista.setText(R.string.lista_residentes);
                        cursorApellidos.moveToFirst();
                        if (cursorApellidos != null) {
                            int id = cursorApellidos.getColumnIndex("_id");
                            do {
                                int room = cursorApellidos.getInt(id);
                                Residente residente = db.getRESIDENTE(room);
                                roomList.add(residente.getRoom());
                                listResidente.add(residente);
                            } while (cursorApellidos.moveToNext());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("LOG: ", "error al buscar residente");
                }
                if (roomList.isEmpty()) {
                    txt_lista.setVisibility(View.VISIBLE);
                    txt_lista.setText("No hay resultados para '" + input + "'.");
                }
                else{
                    txt_lista.setVisibility(View.GONE);
                }
                ResidentesAdapter mAdapter = new ResidentesAdapter(listResidente,gestor_residentes.this,ResidentesConstants.LISTA_RESIDENTES);
                rv_residentes.setAdapter(mAdapter);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_residente_deleteAll) {
            AlertDialog.Builder adb = new AlertDialog.Builder(gestor_residentes.this);
            adb.setTitle("Resetear");
            adb.setMessage("¿Desea resetear la lista de residentes?" +
                    "\n\nAdvertencia: se perderán todos los datos almacenados.");
            adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    for(Residente residente : db.getResidentesList()){
                        File photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos",
                                residente.getNombre()+residente.getApellidos()+ ".jpg");
                        if (photo.exists()) {
                            photo.delete();
                        }
                    }
                    db.resetRESIDENTES();
                    clearSearchView();
                    setResidentesList();
                }
            });
            adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
            return true;
        }
        else if (id == R.id.menu_residente_giveTickets) {
            final List<Residente> mediaPension = new ArrayList<Residente>();
            for(Residente residente : db.getResidentesList()){
                if(residente.getTipo_pension().equals("Media pensión")){
                    mediaPension.add(residente);
                }
            }
            StringBuilder sb = new StringBuilder();
            if(mediaPension.isEmpty()){
                sb.append("No se han encontrado residentes con media pensión");
            }
            else{
                sb.append("Se incrementarán los menús y desayunos de los siguientes " +
                        mediaPension.size()+" residentes:\n\n");
                for(Residente residente : mediaPension){
                    sb.append(residente.getNombre()+" "+residente.getApellidos()+"\n");
                }
            }
            final Dialog dialog = new Dialog(gestor_residentes.this);
            dialog.setTitle("Ronda de tickets");
            dialog.setContentView(R.layout.dialog_ronda_tickets);
            TextView msg = (TextView)dialog.findViewById(R.id.dialog_ronda_tickets_msg);
            msg.setText(sb.toString());
            final EditText menus = (EditText)dialog.findViewById(R.id.dialog_ronda_tickets_menus);
            final EditText desayunos = (EditText)dialog.findViewById(R.id.dialog_ronda_tickets_desayunos);
            Button btnCancel = (Button)dialog.findViewById(R.id.dialog_ronda_tickets_cancelar);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button btnSubmit = (Button)dialog.findViewById(R.id.dialog_ronda_tickets_aceptar);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(Residente residente : mediaPension){
                        int nuevos_menus = residente.getMenus_restantes() + Integer.parseInt(menus.getText().toString());
                        int nuevos_desayunos = residente.getDesayunos_restantes() + Integer.parseInt(desayunos.getText().toString());
                        db.modifyRESIDENTE(residente.getId(),residente.getNombre(),residente.getApellidos(),residente.getRoom(),
                                residente.getTipo_pension(),nuevos_desayunos,nuevos_menus,residente.getNotas());
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }
        else if(id == R.id.menu_residente_changeRoom){
            AlertDialog.Builder adb = new AlertDialog.Builder(gestor_residentes.this);
            adb.setTitle("Seleccionar tipo");
            adb.setMessage("Seleccione el tipo de cambio de habitación que desea efectuar.");
            adb.setNegativeButton("INDIVIDUAL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(gestor_residentes.this, CambioIndividual.class);
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            adb.setPositiveButton("GENERAL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(gestor_residentes.this, CambioGeneral.class);
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            adb.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setResidentesList() {
        List<Residente> listResidente = db.getResidentesList();
        roomList.clear();
        for(Residente residente : listResidente){
            roomList.add(residente.getRoom());
        }
        ResidentesAdapter mAdapter = new ResidentesAdapter(listResidente,gestor_residentes.this, ResidentesConstants.LISTA_RESIDENTES);
        rv_residentes.setAdapter(mAdapter);
    }

    private void clearSearchView() {
        searchView.setQuery("", false);
        searchView.setIconified(true);
    }
}
