package com.saceone.tfg.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.saceone.tfg.Classes.Registro;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ASUS on 10/04/2016.
 */
public class Estadisticas extends AppCompatActivity {

    MyDB db = new MyDB(Estadisticas.this);

    Button btn_view_all_entries;
    Button btn_update_reports;
    Button btn_view_report;
    Button btn_view_statistics;
    Button btn_view_shoppinglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estadisticas);

        btn_view_all_entries = (Button)findViewById(R.id.btn_estadisticas_all_entries);
        btn_update_reports = (Button)findViewById(R.id.btn_estadisticas_new_report);
        btn_view_report = (Button)findViewById(R.id.btn_estadisticas_view_report);
        btn_view_statistics = (Button)findViewById(R.id.btn_estadisticas_ver_estadisticas);
        btn_view_shoppinglist = (Button)findViewById(R.id.btn_estadisticas_ver_listacompra);

        btn_view_all_entries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Estadisticas.this,Historial_registros.class);
                startActivity(i);
            }
        });

        btn_update_reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb_last = new AlertDialog.Builder(Estadisticas.this);
                adb_last.setTitle("Generar ficheros");
                final List<String> unregistered_dates = new ArrayList<String>();
                for (Registro registro : db.getRegistroList()) {
                    if (registro.getId() > 2) {
                        if (!savedDay(registro.getTabla(), registro.getDia(), registro.getMes(), registro.getYear())) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(registro.getTabla() + "_");
                            if (registro.getDia() <= 9) sb.append("0");
                            sb.append("" + registro.getDia() + "_");
                            if (registro.getMes() <= 9) sb.append("0");
                            sb.append("" + registro.getMes() + "_");
                            sb.append(registro.getYear());
                            sb.append(".txt");
                            unregistered_dates.add(sb.toString());
                        }
                    }
                }
                adb_last.setMessage("Se sobreescribirán los ficheros ya existentes con los datos del registro actual.");
                adb_last.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb_last.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            generateFiles(unregistered_dates);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                adb_last.create().show();
            }
        });

        btn_view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int[] dia = new int[1];
                final int[] mes = new int[1];
                final int[] year = new int[1];

                Calendar now = Calendar.getInstance();
                dia[0] = now.get(Calendar.DAY_OF_MONTH);;
                mes[0] = now.get(Calendar.MONTH) + 1;;
                year[0] = now.get(Calendar.YEAR);;

                final Dialog dialog = new Dialog(Estadisticas.this);
                dialog.setTitle("Seleccionar día");
                dialog.setContentView(R.layout.dialog_calendar);
                final CalendarView calendar = (CalendarView)dialog.findViewById(R.id.dialog_calendar_calendar);
                calendar.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
                    public void onSelectedDayChange(CalendarView view, int theYear, int month, int dayOfMonth) {
                        dia[0] = dayOfMonth;
                        mes[0] = month+1; //Enero es 0
                        year[0] = theYear;
                    }
                });
                Button btn_cancel = (Button)dialog.findViewById(R.id.btn_dialog_calendar_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btn_submit = (Button)dialog.findViewById(R.id.btn_dialog_calendar_submit);
                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        StringBuilder sb_fecha = new StringBuilder();
                        if(dia[0]<=9)sb_fecha.append("0");
                        sb_fecha.append(dia[0] + "_");
                        if(mes[0]<=9)sb_fecha.append("0");
                        sb_fecha.append(mes[0] + "_");
                        sb_fecha.append(year[0]);
                        String fecha = sb_fecha.toString();
                        AlertDialog.Builder adb = new AlertDialog.Builder(Estadisticas.this);
                        adb.setTitle("Registros del " + dia[0] + "/" + mes[0] + "/" + year[0]);
                        //TODO: lista de archivos http://stackoverflow.com/a/8647397
                        String[] folders = {"Desayunos","Comidas", "Cenas"};
                        List<File> fileList = new ArrayList<File>();
                        for(String folder : folders){
                            File f = new File(Environment.getExternalStorageDirectory().toString()+"/Cafeteria RUGP/Registros/"+folder+File.separator);
                            File file[] = f.listFiles();
                            for (int i=0; i < file.length; i++){
                                fileList.add(file[i]);
                            }
                        }
                        StringBuilder msg = new StringBuilder();
                        StringBuilder cons = new StringBuilder();
                        for(File file : fileList){
                            if(file.getName().contains(fecha)){
                                //TODO: read files http://stackoverflow.com/a/12421888
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(file));
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        msg.append(line);
                                        msg.append('\n');
                                    }
                                    br.close();
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                    msg.append("Ha ocurrido un error al recopilar los registros. Disculpe las molestias.");
                                }
                                msg.append("\n");
                            }
                        }
                        if(msg.length()==0){
                            msg.append("No se han encontrado registros para la fecha seleccionada.");
                        }
                        else{
                            cons.append("*************************************\n");
                            cons.append("Número total de consumiciones: "+db.getDayList(dia[0],mes[0],year[0]).size()+"\n");
                            cons.append("*************************************\n\n");
                        }
                        adb.setMessage(cons.toString()+msg.toString());
                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.create().show();
                    }
                });
                dialog.show();
            }
        });

        btn_view_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Estadisticas.this,Graficas.class);
                startActivity(i);
            }
        });

        btn_view_shoppinglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Estadisticas.this);
                File f = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Lista de la compra", "Lista de la compra.txt");
                if(f.exists()){
                    StringBuilder msg = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String line;
                        while ((line = br.readLine()) != null) {
                            msg.append(line);
                            msg.append('\n');
                        }
                        br.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        msg.append("Ha ocurrido un error al recopilar los registros. Disculpe las molestias.");
                    }
                    adb.setMessage(msg.toString());
                }
                else {
                    adb.setMessage("No se ha encontrado el fichero de la lista de la compra");
                }
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.create().show();
            }
        });

    }

    private void generateFiles(List<String> unregistered_dates) throws IOException {
        //TODO: escribir ficheros txt en android http://stackoverflow.com/a/8738467
        for(String name : unregistered_dates){
            File newFile;
            String start;
            int consumiciones = 0;
            switch (name.substring(0,2)){
                case "de":
                    start="de";
                    newFile = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Registros/Desayunos", name);
                    break;
                case "co":
                    start="co";
                    newFile = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Registros/Comidas", name);
                    break;
                default:
                    start="ce";
                    newFile = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Registros/Cenas", name);
                    break;
            }
            if(newFile.exists()){
                newFile.delete();
            }
            newFile.createNewFile();
            if(newFile.exists()) {
                OutputStream fo = new FileOutputStream(newFile);
                String fecha_base = name.substring((name.length()-14),(name.length()-4));
                StringBuilder sb_intro = new StringBuilder();
                StringBuilder sb_data = new StringBuilder();
                switch (start){
                    case "de":
                        sb_intro.append("REGISTRO DE DESAYUNOS\n");
                        break;
                    case "co":
                        sb_intro.append("REGISTRO DE COMIDAS\n");
                        break;
                    default:
                        sb_intro.append("REGISTRO DE CENAS\n");
                        break;
                }
                for(Registro mRegistro : db.getRegistroList()){
                    if(mRegistro.getId()>2){
                        if(mRegistro.getTabla().substring(0,2).equals(start)){
                            StringBuilder sb_fecha = new StringBuilder();
                            if(mRegistro.getDia()<=9)sb_fecha.append("0");
                            sb_fecha.append("" + mRegistro.getDia() + "_");
                            if(mRegistro.getMes()<=9)sb_fecha.append("0");
                            sb_fecha.append("" + mRegistro.getMes() + "_");
                            sb_fecha.append(mRegistro.getYear());
                            if(sb_fecha.toString().equals(fecha_base)){
                                consumiciones++;
                                //sb.append(mRegistro.getTabla().substring(0,(mRegistro.getTabla().length()-1))+"\n");
                                sb_data.append("Nombre: " + mRegistro.getNombre() + " " + mRegistro.getApellidos() + "\n");
                                sb_data.append("Pensión: " + mRegistro.getPension() + "\n");
                                sb_data.append("Habitación: " + mRegistro.getRoom()+"\n");
                                StringBuilder sb_hora = new StringBuilder();
                                if(mRegistro.getHora()<=9)sb_hora.append("0");
                                sb_hora.append("" + mRegistro.getHora() + ":");
                                if(mRegistro.getMinuto()<=9)sb_hora.append("0");
                                sb_hora.append("" + mRegistro.getMinuto());
                                sb_data.append("Hora: "+sb_hora.toString()+"\n");
                                sb_data.append("-----------------------"+"\n");
                            }
                        }
                    }
                }
                sb_intro.append("Total: "+consumiciones+"\n");
                sb_intro.append("Fecha: "+fecha_base+"\n\n");
                sb_intro.append("-----------------------"+"\n");
                StringBuilder msg = new StringBuilder();
                msg.append(sb_intro.toString());
                msg.append(sb_data.toString());
                fo.write(msg.toString().getBytes());
                fo.close();
            }
        }
    }

    private boolean savedDay(String tabla, int dia, int mes, int year) {
        StringBuilder sb = new StringBuilder();
        sb.append(tabla+"_");
        if(dia<=9)sb.append("0");
        sb.append("" + dia + "_");
        if(mes<=9)sb.append("0");
        sb.append("" + mes + "_");
        sb.append(year);
        String filename = sb.toString();
        File chkFile;
        switch (tabla){
            case "desayunos":
                chkFile = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Registros/Desayunos", filename);
                break;
            case "comidas":
                chkFile = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Registros/Comidas", filename);
                break;
            default:
                chkFile = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Registros/Cenas", filename);
                break;
        }
        if (chkFile.exists()) {
            return true;
        }
        else{
            return false;
        }
    }
}
