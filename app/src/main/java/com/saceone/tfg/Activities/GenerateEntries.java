package com.saceone.tfg.Activities;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.saceone.tfg.Adapters.ResidentesAdapter;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.ItemClickSupport;
import com.saceone.tfg.Utils.MyDB;
import com.saceone.tfg.Utils.ResidentesConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ASUS on 27/04/2016.
 */
public class GenerateEntries extends AppCompatActivity {

    final MyDB db = new MyDB(this);

    //para convertir la posicion (0,1,2,...) de la lista en numero de habitacion (110,111,112,...)
    List<Integer> roomList = new ArrayList<Integer>();
    int id;

    RecyclerView rv_residentes;

    String tabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestor_residentes);

        rv_residentes = (RecyclerView)findViewById(R.id.rv_residentes);
        rv_residentes.setHasFixedSize(true);
        LinearLayoutManager llm_tagroom = new LinearLayoutManager(this);
        llm_tagroom.setOrientation(LinearLayoutManager.VERTICAL);
        rv_residentes.setLayoutManager(llm_tagroom);

        setResidentesList();

        ItemClickSupport.addTo(rv_residentes).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                int id = roomList.get(position);
                final Residente residente = db.getRESIDENTE(id);
                if (residente.exists()) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(GenerateEntries.this);
                    adb.setTitle("Generar consumición");
                    adb.setMessage("Seleccione el tipo de consumición a efectuar");
                    adb.setPositiveButton("Cena", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            tabla = "cenas";
                            generateEntry(tabla, residente);
                            dialog.dismiss();
                        }
                    });
                    adb.setNegativeButton("Comida", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            tabla = "comidas";
                            generateEntry(tabla, residente);
                            dialog.dismiss();
                        }
                    });
                    adb.setNeutralButton("Desayuno", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            tabla = "desayunos";
                            generateEntry(tabla, residente);
                            dialog.dismiss();
                        }
                    });
                    adb.create().show();
                } else {
                    //HABITACION VACIA - NO HACER NADA
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generate_entries, menu);
        return true;
    }

    private void generateEntry(String tabla, Residente residente) {
        Calendar now = Calendar.getInstance();
        final int year = now.get(Calendar.YEAR);
        final int mes = now.get(Calendar.MONTH) + 1; // Enero = 0
        final int dia = now.get(Calendar.DAY_OF_MONTH);
        final int hora = now.get(Calendar.HOUR_OF_DAY);
        final int minuto = now.get(Calendar.MINUTE);
        final int segundo = now.get(Calendar.SECOND);
        if(residente.getTipo_pension().equals("Media pensión")){
            switch (tabla){
                case "desayunos":
                    db.modifyRESIDENTE(residente.getId(),
                            residente.getNombre(),
                            residente.getApellidos(),
                            residente.getRoom(),
                            residente.getTipo_pension(),
                            residente.getDesayunos_restantes()-1,
                            residente.getMenus_restantes(),
                            residente.getNotas());
                    residente.setDesayunos_restantes(residente.getDesayunos_restantes() - 1);
                    break;
                default:
                    db.modifyRESIDENTE(residente.getId(),
                            residente.getNombre(),
                            residente.getApellidos(),
                            residente.getRoom(),
                            residente.getTipo_pension(),
                            residente.getDesayunos_restantes(),
                            residente.getMenus_restantes()-1,
                            residente.getNotas());
                    break;
            }
            int id = db.getRegistroList().size();
            db.insertREG(GenerateEntries.this.tabla, id,
                    residente.getNombre(),
                    residente.getApellidos(),
                    residente.getRoom(),
                    residente.getTipo_pension(),
                    hora,minuto,segundo,dia,mes,year);
            play("aceptado");
        }
        else{
            if(!db.pensionCompletaValidadaPreviamente(tabla,residente.getRoom(),dia,mes,year)){
                int id = db.getRegistroList().size();
                db.insertREG(GenerateEntries.this.tabla, id,
                        residente.getNombre(),
                        residente.getApellidos(),
                        residente.getRoom(),
                        residente.getTipo_pension(),
                        hora,minuto,segundo,dia,mes,year);
                play("aceptado");
            }
            else{
                Toast.makeText(GenerateEntries.this,"Consumición no aceptada.\nMotivo: pensión completa ya validada.",Toast.LENGTH_LONG).show();
                play("denegado");            }
        }
        finish();
    }

    private void setResidentesList() {
        List<Residente> listResidente = db.getResidentesList();
        roomList.clear();
        for(Residente residente : listResidente){
            roomList.add(residente.getRoom());
        }
        ResidentesAdapter mAdapter = new ResidentesAdapter(listResidente,GenerateEntries.this, ResidentesConstants.LISTA_RESIDENTES);
        rv_residentes.setAdapter(mAdapter);
    }

    private void play(String sound) {
        MediaPlayer mp = new MediaPlayer();
        switch (sound){
            case "aceptado":
                mp.create(GenerateEntries.this, R.raw.aceptado).start();
                break;
            case "denegado":
                mp.create(GenerateEntries.this, R.raw.denegado).start();
                break;
            default:
                Toast.makeText(GenerateEntries.this, "Ha ocurrido un error reproduciendo el audio.",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
