package com.saceone.tfg.Activities;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Adapters.RegistrosAdapter;
import com.saceone.tfg.Classes.Registro;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.Exceptions.BluetoothNotAvaliableException;
import com.saceone.tfg.Exceptions.BluetoothNotEnabledException;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.BluetoothHelper;
import com.saceone.tfg.Utils.ICallback;
import com.saceone.tfg.Utils.MyDB;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/*
 * Created by ASUS on 31/03/2016.
 */
public class Servicio extends AppCompatActivity implements ICallback {

    BluetoothHelper bt = BluetoothHelper.getInstance(this);
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean btconnected = false;

    AlertDialog waitingAlertDialog;

    MyDB db = new MyDB(Servicio.this);
    LinkedList<String> fifoDialogStack = new LinkedList<String>();

    RecyclerView rv_registro;

    ImageView ivLed;
    TextView txtBTstate;
    Button btnEnableBT;
    Button btnReconnect;
    FloatingActionButton btnPedir;
    Switch swAutoMode;

    String tabla;
    int num_consumiciones=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicio);

        waitingAlertDialog = new AlertDialog.Builder(Servicio.this)
                .setTitle("Esperando TAG")
                .setMessage("El tiempo de espera es de 10 segundos.")
                .setCancelable(false)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(btconnected){
                            try {
                                bt.sendData("EXIT_NEWTAG");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    }
                })
                .create();

        ivLed = (ImageView) findViewById(R.id.led);
        txtBTstate = (TextView) findViewById(R.id.txt_btstate);
        btnEnableBT = (Button) findViewById(R.id.btn_enable_bt);
        btnReconnect = (Button) findViewById(R.id.btn_connect_bt);
        btnPedir = (FloatingActionButton) findViewById(R.id.fav_pedir_tag);
        swAutoMode = (Switch) findViewById(R.id.sw_automatic_mode);

        btnEnableBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.enable();
                btnEnableBT.setVisibility(View.GONE);
                txtBTstate.setText("Conexión no establecida.");
                btnReconnect.setVisibility(View.VISIBLE);
            }
        });

        btnReconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtBTstate.setText("Conectando...");
                btnReconnect.setVisibility(View.GONE);
                connect();
            }
        });

        btnPedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btconnected){
                    try {
                        bt.sendData("WAKE_READER");
                        waitingAlertDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        swAutoMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(btconnected){
                    try{
                        if(isChecked){
                            bt.sendData("WAKE_READER_FOREVER");
                            btnPedir.setVisibility(View.GONE);
                        }
                        else{
                            bt.sendData("WAKE_READER");
                            btnPedir.setVisibility(View.VISIBLE);
                            fifoDialogStack.clear();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        rv_registro = (RecyclerView)findViewById(R.id.rv_registro);
        rv_registro.setFadingEdgeLength(50);
        rv_registro.setHasFixedSize(true);
        LinearLayoutManager llm_tagroom = new LinearLayoutManager(this);
        llm_tagroom.setOrientation(LinearLayoutManager.VERTICAL);
        rv_registro.setLayoutManager(llm_tagroom);

    }

    @Override
    public void onResume(){
        super.onResume();
        connect();
        swAutoMode.setChecked(false);
        setRegistroList();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(btconnected){
            try {
                bt.sendData("EXIT_NEWTAG");
                bt.disconnect();
                //Pongo "Reconectando..." porque es lo que se verá cuando se resuma la activity
                txtBTstate.setText("Conexión no establecida. Reconectando...");
                btnPedir.setVisibility(View.GONE);
                ivLed.setImageResource(R.drawable.ledred);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generate_entries, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_generate_entries) {
            Intent i = new Intent(Servicio.this,GenerateEntries.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void connect() {
        try {
            ivLed.setImageResource(R.drawable.ledred);
            txtBTstate.setText("Conectando...");
            bt.connect();
            btconnected=true;
            Date today = Calendar.getInstance().getTime();
            //Es mejor pasarlo como un string
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String folderName = formatter.format(today);
            bt.sendData("SYNC_BT" + folderName);
            txtBTstate.setText("Conexión con el lector establecida.");
            ivLed.setImageResource(R.drawable.ledgreen);
            btnPedir.setVisibility(View.VISIBLE);
            btnEnableBT.setVisibility(View.GONE);
            btnReconnect.setVisibility(View.GONE);
        } catch (BluetoothNotEnabledException e) {
            txtBTstate.setText("Bluetooth no activado.");
            btnPedir.setVisibility(View.GONE);
            btnEnableBT.setVisibility(View.VISIBLE);
            btnReconnect.setVisibility(View.GONE);
        } catch (BluetoothNotAvaliableException e) {
            txtBTstate.setText("Bluetooth no disponible.");
            btnPedir.setVisibility(View.GONE);
            btnEnableBT.setVisibility(View.GONE);
            btnReconnect.setVisibility(View.GONE);
        } catch (IOException e) {
            txtBTstate.setText("Lector no detectado.");
            btnPedir.setVisibility(View.GONE);
            btnReconnect.setVisibility(View.VISIBLE);
            btnEnableBT.setVisibility(View.GONE);
        }
    }

    public void call(String s) {
        Log.e("LOG: ",s);
        switch (s) {
            case "SYNC_OK":
                AlertDialog.Builder adb_sync = new AlertDialog.Builder(Servicio.this);
                adb_sync.setTitle("Información")
                        .setMessage("La conexión con el lector ha sido realizada con éxito.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .create()
                        .show();
                break;
            case "TAG_TIMEOUT":
                waitingAlertDialog.dismiss();
                break;
            default:
                waitingAlertDialog.dismiss();
                if(fifoDialogStack.isEmpty()){
                    fifoDialogStack.add(s);
                    deQueue();
                }
                else{
                    fifoDialogStack.add(s);
                }
                break;
        }
    }

    private void deQueue(){
        if(!fifoDialogStack.isEmpty()){

            String s = fifoDialogStack.get(0);

            if (db.getTAGROOMwithTAG(s).getRoom() != -1) {

                int room = db.getTAGROOMwithTAG(s).getRoom();
                final Residente residente = db.getRESIDENTE(room);

                if (residente.exists()) {
                    tabla = getCorrectTable();
                    if (tabla == null) {
                        AlertDialog.Builder adb_select = new AlertDialog.Builder(Servicio.this);
                        adb_select.setTitle("Nueva consumición")
                                .setMessage("La consumición se ha efectuado fuera de los horarios oficiales de servicio. " +
                                        "\nPor favor, seleccione el tipo de consumición del que se trata.")
                                .setPositiveButton("Cena", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        tabla = "cenas";
                                        setDialogView(tabla, residente);
                                    }
                                })
                                .setNegativeButton("Comida", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        tabla = "comidas";
                                        setDialogView(tabla, residente);
                                    }
                                })
                                .setNeutralButton("Desayuno", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        tabla = "desayunos";
                                        setDialogView(tabla, residente);
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        setDialogView(tabla, residente);
                    }
                } else {
                    AlertDialog.Builder adb_empty = new AlertDialog.Builder(Servicio.this);
                    adb_empty.setTitle("Habitación vacía")
                            .setMessage("No hay residente asociado a la habitación " + room + ".")
                            .setNegativeButton("Ver lista de residentes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(Servicio.this, gestor_residentes.class);
                                    startActivity(i);
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    fifoDialogStack.remove();
                                    deQueue();
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            } else {
                if(!s.equals("TAG_TIMEOUT")){
                    AlertDialog.Builder adb_unknown = new AlertDialog.Builder(Servicio.this);
                    adb_unknown.setTitle("TAG no reconocido")
                            .setMessage("El TAG recibido no está asociado a ninguna habitación.")
                            .setNegativeButton("Ver TAGs guardados", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(Servicio.this, gestor_tags.class);
                                    startActivity(i);
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    fifoDialogStack.remove();
                                    deQueue();
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
    }

    private void setDialogView(String tabla, final Residente residente) {
        final String auxTabla = tabla;

        final Dialog dialog = new Dialog(Servicio.this);
        dialog.setContentView(R.layout.dialog_consumicion);
        dialog.setCancelable(false);

        ImageView image = (ImageView) dialog.findViewById(R.id.img_dialog_consumicion);
        TextView txtNombre = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_nombre);
        TextView txtRoom = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_room);
        TextView txtPension = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_pension);
        LinearLayout lv_menus = (LinearLayout) dialog.findViewById(R.id.lv_menus);
        TextView txtMenusRestantes = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_menus_restantes);
        final TextView txtMenusNuevos = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_menus_nuevos);
        LinearLayout lv_desyaunos = (LinearLayout) dialog.findViewById(R.id.lv_desayunos);
        TextView txtDesayunosRestantes = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_desayunos_restantes);
        final TextView txtDesayunosNuevos = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_desayunos_nuevos);
        TextView txtConsumicionesMsg = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_numero_msg);
        LinearLayout lv_number_picker = (LinearLayout) dialog.findViewById(R.id.lv_number_picker);
        Button btnMenos = (Button) dialog.findViewById(R.id.btn_dialog_consumicion_menos);
        final TextView txtConsumiciones = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_numero);
        Button btnMas = (Button) dialog.findViewById(R.id.btn_dialog_consumicion_mas);
        TextView txtDate = (TextView) dialog.findViewById(R.id.txt_dialog_consumicion_fecha);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_dialog_consumicion_cancel);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_dialog_consumicion_submit);
        Button btnChange = (Button) dialog.findViewById(R.id.btn_dialog_consumicion_change);
        switch (tabla){
            case "desayunos":
                dialog.setTitle("Nuevo desayuno");
                lv_menus.setVisibility(View.GONE);
                break;
            case "comidas":
                dialog.setTitle("Nueva comida");
                lv_desyaunos.setVisibility(View.GONE);
                break;
            case "cenas":
                dialog.setTitle("Nueva cena");
                lv_desyaunos.setVisibility(View.GONE);
                break;
            default:
                dialog.setTitle("Nueva consumición");
                lv_desyaunos.setVisibility(View.GONE);
                lv_menus.setVisibility(View.GONE);
                break;
        }

        //Datos de la fecha conseguida como String
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String folderName = formatter.format(today);
        String strFecha = folderName.substring(0, 10);
        String strHora = folderName.substring(11, 16);

        //TODO: obtengo como int hora, minutos... http://stackoverflow.com/questions/2654025/how-to-get-year-month-day-hours-minutes-seconds-and-milliseconds-of-the-cur
        Calendar now = Calendar.getInstance();
        final int year = now.get(Calendar.YEAR);
        final int mes = now.get(Calendar.MONTH) + 1; // Enero = 0
        final int dia = now.get(Calendar.DAY_OF_MONTH);
        final int hora = now.get(Calendar.HOUR_OF_DAY);
        final int minuto = now.get(Calendar.MINUTE);
        final int segundo = now.get(Calendar.SECOND);

        String nombre = residente.getNombre();
        String apellidos = residente.getApellidos();
        int room = residente.getRoom();
        String pension = residente.getTipo_pension();

        txtNombre.setText(nombre + " " + apellidos);
        txtRoom.setText("Habitación: "+room);
        txtPension.setText(pension);

        if(pension.equals("Media pensión")){
            int menus = residente.getMenus_restantes();
            int menusNuevos = menus-1;
            int desayunos = residente.getDesayunos_restantes();
            int desayunosNuevos = desayunos-1;
            txtMenusRestantes.setText("Menús restantes: "+menus);
            txtMenusNuevos.setText(""+menusNuevos);
            if(menusNuevos<=0){
                txtMenusNuevos.setTextColor(Color.RED);
            }
            else{
                txtMenusNuevos.setTextColor(Color.DKGRAY);
            }
            txtDesayunosRestantes.setText("Desayunos restantes: " + desayunos);
            txtDesayunosNuevos.setText(""+desayunosNuevos);
            if(desayunosNuevos<=0){
                txtDesayunosNuevos.setTextColor(Color.RED);
            }
            else{
                txtDesayunosNuevos.setTextColor(Color.DKGRAY);
            }
        }
        else{
            txtConsumicionesMsg.setVisibility(View.GONE);
            lv_menus.setVisibility(View.GONE);
            lv_desyaunos.setVisibility(View.GONE);
            lv_number_picker.setVisibility(View.GONE);
        }

        txtDate.setText(strFecha+"   "+ strHora);

        File photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos",nombre+apellidos+".jpg");
        if(photo.exists()){
            image.setImageBitmap(BitmapFactory.decodeFile(Environment
                    .getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos/" + nombre + apellidos + ".jpg"));
        }
        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_consumiciones++;
                txtConsumiciones.setText(""+num_consumiciones);
                switch (auxTabla){
                    case "desayunos":
                        int desayunosNuevos = residente.getDesayunos_restantes()-num_consumiciones;
                        txtDesayunosNuevos.setText(""+desayunosNuevos);
                        if(desayunosNuevos<=0){
                            txtDesayunosNuevos.setTextColor(Color.RED);
                        }
                        else{
                            txtDesayunosNuevos.setTextColor(Color.DKGRAY);
                        }
                        break;
                    default:
                        int menusNuevos = residente.getMenus_restantes()-num_consumiciones;
                        txtMenusNuevos.setText(""+menusNuevos);
                        if(menusNuevos<=0){
                            txtMenusNuevos.setTextColor(Color.RED);
                        }
                        else{
                            txtMenusNuevos.setTextColor(Color.DKGRAY);
                        }
                        break;
                }
            }
        });
        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num_consumiciones>1){
                    num_consumiciones--;
                    txtConsumiciones.setText(""+num_consumiciones);
                    switch (auxTabla){
                        case "desayunos":
                            int desayunosNuevos = residente.getDesayunos_restantes()-num_consumiciones;
                            txtDesayunosNuevos.setText(""+desayunosNuevos);
                            if(desayunosNuevos<=0){
                                txtDesayunosNuevos.setTextColor(Color.RED);
                            }
                            else{
                                txtDesayunosNuevos.setTextColor(Color.DKGRAY);
                            }
                            break;
                        default:
                            int menusNuevos = residente.getMenus_restantes()-num_consumiciones;
                            txtMenusNuevos.setText(""+menusNuevos);
                            if(menusNuevos<=0){
                                txtMenusNuevos.setTextColor(Color.RED);
                            }
                            else{
                                txtMenusNuevos.setTextColor(Color.DKGRAY);
                            }
                            break;
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_consumiciones=1;
                fifoDialogStack.remove();
                deQueue();
                dialog.dismiss();
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AlertDialog.Builder adb_select = new AlertDialog.Builder(Servicio.this);
                adb_select.setTitle("Cambiar tipo de consumición")
                        .setMessage("Por favor, seleccione el tipo de consumición del que se trata.")
                        .setPositiveButton("Cena", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newtabla = "cenas";
                                setDialogView(newtabla, residente);
                            }
                        })
                        .setNegativeButton("Comida", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newtabla = "comidas";
                                setDialogView(newtabla, residente);
                            }
                        })
                        .setNeutralButton("Desayuno", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newtabla = "desayunos";
                                setDialogView(newtabla, residente);
                            }
                        })
                        .create()
                        .show();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //restamos tickets a los Media
                switch (residente.getTipo_pension()){
                    case "Media pensión":
                        while(num_consumiciones>0){
                            //la id de la nueva entrada sera la longitud de la lista (empieza en 0, asi que no hay que hacer size()+1)
                            int id = db.getRegistroList().size();
                            //Insertamos el registro de media pension
                            db.insertREG(auxTabla, id,
                                    residente.getNombre(),
                                    residente.getApellidos(),
                                    residente.getRoom(),
                                    residente.getTipo_pension(),
                                    hora,minuto,segundo,dia,mes,year);
                            num_consumiciones--;
                        }
                        switch (auxTabla){
                            case "desayunos":
                                db.modifyRESIDENTE(residente.getId(), residente.getNombre(), residente.getApellidos(), residente.getRoom(),
                                        residente.getTipo_pension(), Integer.parseInt(txtDesayunosNuevos.getText().toString()),
                                        residente.getMenus_restantes(), residente.getNotas());
                                Toast.makeText(Servicio.this,"Desayuno validado.",Toast.LENGTH_SHORT).show();
                                send(residente,"aceptado",auxTabla,Integer.parseInt(txtDesayunosNuevos.getText().toString()));
                                play("aceptado");
                                break;
                            default:
                                db.modifyRESIDENTE(residente.getId(), residente.getNombre(), residente.getApellidos(), residente.getRoom(),
                                        residente.getTipo_pension(), residente.getDesayunos_restantes(),
                                        Integer.parseInt(txtMenusNuevos.getText().toString()), residente.getNotas());
                                Toast.makeText(Servicio.this,"Menú validado.",Toast.LENGTH_SHORT).show();
                                send(residente,"aceptado",auxTabla,Integer.parseInt(txtMenusNuevos.getText().toString()));
                                play("aceptado");
                                break;
                        }
                        break;
                    case "Pensión completa":
                        if(db.pensionCompletaValidadaPreviamente(auxTabla,residente.getRoom(),dia,mes,year)){
                            Toast.makeText(Servicio.this,"Consumición no aceptada.\nMotivo: pensión completa ya validada.",Toast.LENGTH_LONG).show();
                            send(residente,"denegado",auxTabla,-1);
                            play("denegado");
                        }
                        else {
                            int id = db.getRegistroList().size();
                            //Insertamos el registro de pension completa
                            db.insertREG(auxTabla, id,
                                    residente.getNombre(),
                                    residente.getApellidos(),
                                    residente.getRoom(),
                                    residente.getTipo_pension(),
                                    hora,minuto,segundo,dia,mes,year);
                            switch (auxTabla){
                                case "desayunos":
                                    Toast.makeText(Servicio.this,"Desayuno validado.",Toast.LENGTH_SHORT).show();
                                    send(residente,"aceptado",auxTabla,-1);
                                    play("aceptado");
                                    break;
                                default:
                                    Toast.makeText(Servicio.this,"Menú validado.",Toast.LENGTH_SHORT).show();
                                    send(residente,"aceptado",auxTabla,-1);
                                    play("aceptado");
                                    break;
                            }
                        }
                        break;
                }
                //reseteamos el numero de consumiciones por defecto para la siguiente vez
                num_consumiciones=1;
                setRegistroList();
                fifoDialogStack.remove();
                deQueue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void send(Residente residente, String consumicion, String tabla, int newConsumiciones) {
        StringBuilder sb = new StringBuilder();
        sb.append("CONS");
        sb.append(String.valueOf(residente.getRoom()));
        if(consumicion.equals("aceptado")) {
            if (residente.getTipo_pension().equals("Media pensión")){
                if (tabla.equals("desayunos")) {
                    sb.append("Desayunos: ");
                    sb.append(newConsumiciones+"");
                }
                else{
                    sb.append("Menus: ");
                    sb.append(newConsumiciones+"");
                }
            }
            else{
                if (tabla.equals("desayunos")) {
                    sb.append("Desay. validado");
                }
                else{
                    sb.append("Menu validado");
                }
            }
        }
        else{
            sb.append("Cons no validada");
        }
        try {
            bt.sendData(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play(String sound) {
        MediaPlayer mp = new MediaPlayer();
        switch (sound){
            case "aceptado":
                mp.create(Servicio.this, R.raw.aceptado).start();
                break;
            case "denegado":
                mp.create(Servicio.this, R.raw.denegado).start();
                break;
            default:
                Toast.makeText(Servicio.this, "Ha ocurrido un error reproduciendo el audio.",Toast.LENGTH_LONG).show();
                break;
        }
    }

    private String getCorrectTable() {
        Calendar now = Calendar.getInstance();
        final int hora = now.get(Calendar.HOUR_OF_DAY);
        final int minuto = now.get(Calendar.MINUTE);
        float adaptedTime = hora + ((float)minuto)/60;

        String tabla = db.getTablaFromTime(adaptedTime);
        if(tabla==null) return null;
        else return tabla;
    }

    private void setRegistroList() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int mes = now.get(Calendar.MONTH) + 1; // Enero = 0
        int dia = now.get(Calendar.DAY_OF_MONTH);
        List<Registro> todayList = db.getDayList(dia,mes,year);
        //No hace falta elminar dummies porque getDayList() solo me da los de hoy (los dummies son de la fecha 0/0/0)
        RegistrosAdapter mAdapter = new RegistrosAdapter(todayList,Servicio.this,"DAY");
        rv_registro.setAdapter(mAdapter);
    }

}
