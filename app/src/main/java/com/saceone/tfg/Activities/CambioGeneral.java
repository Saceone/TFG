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
import android.widget.Toast;

import com.saceone.tfg.Adapters.ResidentesAdapter;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;
import com.saceone.tfg.Utils.ResidentesConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Ramón on 11/05/2016.
 */

public class CambioGeneral extends AppCompatActivity {

    RecyclerView rv_residentes;
    Button btn_cancel;
    Button btn_submit;

    //para convertir la posicion (0,1,2,...) de la lista en numero de habitacion (110,111,112,...)
    List<Integer> roomList = new ArrayList<Integer>();

    final MyDB db = new MyDB(this);

    ResidentesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambio_general);

        rv_residentes = (RecyclerView) findViewById(R.id.rv_residentes_cambio_general);
        btn_cancel = (Button) findViewById(R.id.btn_cambio_general_cancelar);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog adb = new AlertDialog.Builder(CambioGeneral.this)
                        .setTitle("Salir")
                        .setMessage("Se descartarán los cambios realizados. ¿Desea continuar?")
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .create();
                        adb.show();
            }
        });
        btn_submit = (Button) findViewById(R.id.btn_cambio_general_aceptar);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] newRoomList = mAdapter.getNewRooms();
                if(everyRoomIsCorrect(newRoomList)&&notRepeatedRooms(newRoomList)){
                    AlertDialog adb = new AlertDialog.Builder(CambioGeneral.this)
                            .setTitle("Guardar cambios")
                            .setMessage("Se modificarán las habitaciones de los residentes. ¿Desea continuar?")
                            .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    modifyRooms(newRoomList);
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .create();
                    adb.show();
                }

            }
        });

        rv_residentes.setHasFixedSize(true);
        LinearLayoutManager llm_tagroom = new LinearLayoutManager(this);
        llm_tagroom.setOrientation(LinearLayoutManager.VERTICAL);
        rv_residentes.setLayoutManager(llm_tagroom);

        setResidentesList();
    }

    private void modifyRooms(int[] newRoomList) {
        List<Residente> residenteList = db.getResidentesList();
        int i = 0;
        for(Residente residente : residenteList){
            db.modifyRESIDENTE(newRoomList[i],
                    residente.getNombre(),
                    residente.getApellidos(),
                    newRoomList[i],
                    residente.getTipo_pension(),
                    residente.getDesayunos_restantes(),
                    residente.getMenus_restantes(),
                    residente.getNotas());
            i++;
        }
    }

    private boolean notRepeatedRooms(int[] newRoomList) {
        boolean result = true;
        List<Integer> newRooms = new ArrayList<>();
        for(int newRoom : newRoomList){
            if(newRooms.isEmpty()) newRooms.add(newRoom);
            else{
                if(newRooms.contains(newRoom)) {
                    result = false;
                    Toast.makeText(CambioGeneral.this,"La habitación "+newRoom+" está repetida.",Toast.LENGTH_LONG).show();
                }
                else newRooms.add(newRoom);
            }
        }
        return result;
    }

    private boolean everyRoomIsCorrect(int[] newRoomList) {
        boolean result = true;
        int[] roomList = {110, 111, 112, 114, 115, 116, 117, 119, 120, 121, 122,
                201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222,
                301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322,
                401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422,
                501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522,
                601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622};
        List<Integer> newRooms = new ArrayList<>();
        for(int room : roomList){
            newRooms.add(room);
        }
        for(int newRoom : newRoomList){
            if(!newRooms.contains(newRoom)){
                result = false;
                Toast.makeText(CambioGeneral.this,"La habitación "+newRoom+" no es válida.",Toast.LENGTH_LONG).show();
            }
        }
        return result;
    }

    private void setResidentesList() {
        List<Residente> listResidente = db.getResidentesList();
        roomList.clear();
        for(Residente residente : listResidente){
            roomList.add(residente.getRoom());
        }
        mAdapter = new ResidentesAdapter(listResidente,CambioGeneral.this, ResidentesConstants.CAMBIO_GENERAL);
        rv_residentes.setAdapter(mAdapter);
    }
}
