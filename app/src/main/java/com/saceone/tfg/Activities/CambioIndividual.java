package com.saceone.tfg.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Adapters.ResidentesAdapter;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.ItemClickSupport;
import com.saceone.tfg.Utils.MyDB;
import com.saceone.tfg.Utils.ResidentesConstants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 25/04/2016.
 */
public class CambioIndividual extends AppCompatActivity{

    private RecyclerView rv_residentes;

    //para convertir la posicion (0,1,2,...) de la lista en numero de habitacion (110,111,112,...)
    List<Integer> roomList = new ArrayList<Integer>();
    int oldRoom;

    final MyDB db = new MyDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambio_individual);

        rv_residentes = (RecyclerView)findViewById(R.id.rv_residentes_cambio_individual);
        rv_residentes.setHasFixedSize(true);
        LinearLayoutManager llm_tagroom = new LinearLayoutManager(this);
        llm_tagroom.setOrientation(LinearLayoutManager.VERTICAL);
        rv_residentes.setLayoutManager(llm_tagroom);

        setResidentesList();

        ItemClickSupport.addTo(rv_residentes).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                oldRoom = roomList.get(position);
                Residente residente = db.getRESIDENTE(oldRoom);
                if (residente.exists()) {
                    final Dialog dialog = new Dialog(CambioIndividual.this);
                    dialog.setTitle("Nueva habitación");
                    dialog.setContentView(R.layout.dialog_number_writer);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    final EditText editText = (EditText)dialog.findViewById(R.id.ed_number);
                    Button btnCancel = (Button)dialog.findViewById(R.id.btn_dialog_number_cancel);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button btnSubmit = (Button)dialog.findViewById(R.id.btn_dialog_number_submit);
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int newRoom;
                            try{
                                newRoom = Integer.parseInt(editText.getText().toString());
                                if(correctRoomNumber(newRoom)){
                                    if(newRoomIsEmpty(newRoom)){
                                        db.modifyRESIDENTE(newRoom,
                                                db.getRESIDENTE(oldRoom).getNombre(),
                                                db.getRESIDENTE(oldRoom).getApellidos(),
                                                newRoom,
                                                db.getRESIDENTE(oldRoom).getTipo_pension(),
                                                db.getRESIDENTE(oldRoom).getDesayunos_restantes(),
                                                db.getRESIDENTE(oldRoom).getMenus_restantes(),
                                                db.getRESIDENTE(oldRoom).getNotas());
                                        db.deleteRESIDENTE(oldRoom);
                                        setResidentesList();
                                    }
                                    else{
                                        AlertDialog.Builder adb = new AlertDialog.Builder(CambioIndividual.this);
                                        adb.setTitle("Habitación ocupada");
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("La habitación "+newRoom);
                                        sb.append(" está ocupada actualmente por el residente ");
                                        sb.append(db.getRESIDENTE(newRoom).getNombre() + " " + db.getRESIDENTE(newRoom).getApellidos());
                                        sb.append(".\n\nSi mueve al residente ");
                                        sb.append(db.getRESIDENTE(oldRoom).getNombre()+" "+db.getRESIDENTE(oldRoom).getApellidos());
                                        sb.append(" a esta habitación, el residente ");
                                        sb.append(db.getRESIDENTE(newRoom).getNombre() + " " + db.getRESIDENTE(newRoom).getApellidos());
                                        sb.append(" se eliminará.");
                                        adb.setMessage(sb.toString());
                                        adb.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface alertDialog, int which) {
                                                alertDialog.dismiss();
                                                dialog.dismiss();
                                            }
                                        });
                                        adb.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface alertDialog, int which) {
                                                db.modifyRESIDENTE(newRoom,
                                                        db.getRESIDENTE(oldRoom).getNombre(),
                                                        db.getRESIDENTE(oldRoom).getApellidos(),
                                                        newRoom,
                                                        db.getRESIDENTE(oldRoom).getTipo_pension(),
                                                        db.getRESIDENTE(oldRoom).getDesayunos_restantes(),
                                                        db.getRESIDENTE(oldRoom).getMenus_restantes(),
                                                        db.getRESIDENTE(oldRoom).getNotas());
                                                db.deleteRESIDENTE(oldRoom);
                                                setResidentesList();
                                                alertDialog.dismiss();
                                            }
                                        });
                                        adb.create().show();
                                    }
                                }
                                else{
                                    Toast.makeText(CambioIndividual.this,"Habitación no válida. Por favor, introduzca un número de habitación correcto.",Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(CambioIndividual.this, "Número inválido.", Toast.LENGTH_LONG).show();
                            };
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    //HABITACION VACIA - NO HACER NADA
                }
            }
        });
    }

    private boolean newRoomIsEmpty(int newRoom) {
        return !db.getRESIDENTE(newRoom).exists();
    }

    private boolean correctRoomNumber(int newRoom) {
        return roomList.contains(newRoom);
    }

    private void setResidentesList() {
        List<Residente> listResidente = db.getResidentesList();
        roomList.clear();
        for(Residente residente : listResidente){
            roomList.add(residente.getRoom());
        }
        ResidentesAdapter mAdapter = new ResidentesAdapter(listResidente,CambioIndividual.this, ResidentesConstants.CAMBIO_INDIVIUDAL);
        rv_residentes.setAdapter(mAdapter);
    }

}
