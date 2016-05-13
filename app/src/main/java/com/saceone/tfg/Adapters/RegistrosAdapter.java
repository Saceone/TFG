package com.saceone.tfg.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Classes.Registro;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by ASUS on 07/04/2016.
 */
public class RegistrosAdapter extends RecyclerView.Adapter<RegistrosAdapter.RegistrosViewHolder>{

    private List<Registro> registroList;
    private static Context context;
    private MyDB db;
    private String request;

    public RegistrosAdapter(List<Registro> registroList, Context context, String request) {
        this.registroList = registroList;
        this.context = context;
        this.db = new MyDB(context);
        this.request = request;
    }

    @Override
    public int getItemCount() {
        return registroList.size();
    }

    @Override
    public void onBindViewHolder(final RegistrosViewHolder registrosViewHolder, int i) {

        final Registro mRegistro = registroList.get(i);

        final StringBuilder sb_fecha = new StringBuilder();
        if(mRegistro.getDia()<=9)sb_fecha.append("0");
        sb_fecha.append("" + mRegistro.getDia() + "/");
        if(mRegistro.getMes()<=9)sb_fecha.append("0");
        sb_fecha.append("" + mRegistro.getMes() + "/");
        sb_fecha.append(mRegistro.getYear());
        registrosViewHolder.txt_fecha.setText(sb_fecha.toString());

        final StringBuilder sb_hora = new StringBuilder();
        if(mRegistro.getHora()<=9)sb_hora.append("0");
        sb_hora.append("" + mRegistro.getHora() + ":");
        if(mRegistro.getMinuto()<=9)sb_hora.append("0");
        sb_hora.append("" + mRegistro.getMinuto() + "      ");
        registrosViewHolder.txt_hora.setText(sb_hora.toString());

        registrosViewHolder.txt_tipo.setText(mRegistro.getTabla().toUpperCase().substring(0, mRegistro.getTabla().length() - 1));
        registrosViewHolder.txt_nombre.setText(mRegistro.getNombre() + " " + mRegistro.getApellidos());
        registrosViewHolder.txt_pension.setText(mRegistro.getPension());

        registrosViewHolder.rl_entrada.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_registro_context_menu);
                dialog.setTitle("Opciones");

                Button btn_ver = (Button) dialog.findViewById(R.id.btn_dialog_registro_ver);
                btn_ver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(context);
                        adb.setTitle("Registro");
                        StringBuilder sb = new StringBuilder();
                        sb.append("Información detallada del registro seleccionado:\n\n");
                        sb.append("Tipo: " + mRegistro.getTabla() + "\n");
                        sb.append("Nombre: " + mRegistro.getNombre() + " " + mRegistro.getApellidos() + "\n");
                        sb.append("Pensión: " + mRegistro.getPension() + "\n\n");
                        sb.append("Habitación: " + mRegistro.getRoom()+"\n\n");
                        sb.append("Fecha: " + sb_fecha.toString()+"\n");
                        sb.append("Hora: "+sb_hora.toString()+"\n\n");
                        //ID para mostrar al usuario (desfase de 3 por los dummies)
                        int id = mRegistro.getId()-2; //-2 para que saltar los dummies 0, 1 y 2 de modo que el ID3 sea el ID1
                        sb.append("Identificación: ID-"+id);
                        adb.setMessage(sb.toString());
                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.create().show();
                        dialog.dismiss();
                    }
                });
                Button btn_ver_todos = (Button) dialog.findViewById(R.id.btn_dialog_registro_ver_todo);
                btn_ver_todos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Residente residente = db.getRESIDENTE(mRegistro.getRoom());
                        int desayunos=0, comidas=0, cenas=0;
                        String tabla;
                        StringBuilder sb = new StringBuilder();
                        AlertDialog.Builder adb = new AlertDialog.Builder(context);

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
                        dialog.dismiss();
                    }
                });
                final Button btn_eliminar = (Button) dialog.findViewById(R.id.btn_dialog_registro_eliminar);
                btn_eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Residente residente = db.getRESIDENTE(mRegistro.getRoom());
                        switch (residente.getTipo_pension()){
                            case "Media pensión":
                                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                                adb.setTitle("Eliminar registro");
                                StringBuilder sb = new StringBuilder();
                                sb.append("Si elimina este registro, al residente ");
                                sb.append(residente.getNombre()+" "+residente.getApellidos());
                                sb.append(", de media pensión, se le añadirá una consumición a su lista de ");
                                sb.append(mRegistro.getTabla()+".");
                                adb.setMessage(sb.toString());
                                adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int nuevos_desayunos = residente.getDesayunos_restantes();
                                        int nuevos_menus = residente.getMenus_restantes();
                                        switch (mRegistro.getTabla()) {
                                            case "desayunos":
                                                nuevos_desayunos++;
                                                break;
                                            default:
                                                nuevos_menus++;
                                                break;
                                        }
                                        db.modifyRESIDENTE(residente.getId(),
                                                residente.getNombre(), residente.getApellidos(),
                                                residente.getRoom(), residente.getTipo_pension(),
                                                nuevos_desayunos, nuevos_menus, residente.getNotas());
                                        deleteRegistro(mRegistro.getId(), mRegistro.getTabla());
                                        dialog.dismiss();
                                    }
                                });
                                adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                adb.create().show();
                                break;
                            case "Pensión completa":
                                AlertDialog.Builder adb_completa = new AlertDialog.Builder(context);
                                adb_completa.setTitle("Eliminar registro");
                                StringBuilder sb_completa = new StringBuilder();
                                sb_completa.append("Si elimina este registro, al residente ");
                                sb_completa.append(residente.getNombre()+" "+residente.getApellidos());
                                sb_completa.append(", de pensión completa, se le anulará la consumición validada de su lista de ");
                                sb_completa.append(mRegistro.getTabla()+".");
                                adb_completa.setMessage(sb_completa.toString());
                                adb_completa.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int nuevos_desayunos = residente.getDesayunos_restantes();
                                        int nuevos_menus = residente.getMenus_restantes();
                                        switch (mRegistro.getTabla()) {
                                            case "desayunos":
                                                nuevos_desayunos++;
                                                break;
                                            default:
                                                nuevos_menus++;
                                                break;
                                        }
                                        db.modifyRESIDENTE(residente.getId(),
                                                residente.getNombre(), residente.getApellidos(),
                                                residente.getRoom(), residente.getTipo_pension(),
                                                nuevos_desayunos, nuevos_menus, residente.getNotas());
                                        deleteRegistro(mRegistro.getId(), mRegistro.getTabla());
                                        dialog.dismiss();
                                    }
                                });
                                adb_completa.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                adb_completa.create().show();
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return false;
            }
        });
    }

    @Override
    public RegistrosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_registro, viewGroup, false);
        return new RegistrosViewHolder(itemView);
    }

    private void deleteRegistro(int id, String tabla) {
        db.deleteREGISTRO(id,tabla);

        updateIDsRegistro();

        List<Registro> updatedList = getListToShow(request);

        swap(updatedList);
    }

    private List<Registro> getListToShow(String request) {
        List<Registro> listaNecesaria = new ArrayList<Registro>();
        List<Registro> registros_mostrables = new ArrayList<Registro>();
        switch (request){
            case "ALL":
                //Si estamos en Estadisticas -> Ver todos los registros, necesito la lista completa
                listaNecesaria = db.getRegistroList();
                break;
            case "DAY":
                //Si estamos en Servicio -> Solo necesito los registros del día
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int mes = now.get(Calendar.MONTH) + 1; // Enero = 0
                int dia = now.get(Calendar.DAY_OF_MONTH);
                listaNecesaria = db.getDayList(dia,mes,year);
                break;
            default:
                //En otro caso, no se mostará nada (caso de error)
                break;
        }
        if(listaNecesaria!=null){
            for(Registro registro : listaNecesaria){
                if(registro.getId()>2) registros_mostrables.add(registro);
            }
        }
        return registros_mostrables;
    }

    private void updateIDsRegistro() {
        List<Registro> oldRegistroList = db.getRegistroList();
        List<Registro> newRegistroList = new ArrayList<>();
        if(oldRegistroList!=null){
            for(Registro registro : oldRegistroList){
                newRegistroList.add(registro);
            }
            Collections.reverse(newRegistroList);
            db.deleteREGS(oldRegistroList);
            int i=0;
            for(Registro registro : newRegistroList){
                db.insertREG(registro.getTabla(),i,registro.getNombre(),registro.getApellidos(),registro.getRoom(),registro.getPension(),
                        registro.getHora(),registro.getMinuto(),registro.getSegundo(),registro.getDia(),registro.getMes(),registro.getYear());
                i++;
            }
        }

    }

    //TODO: actualizar el recyclerview desde dentro del propio adapter (sin setAlgoView()) http://stackoverflow.com/a/30057015
    public void swap(List list){
        if (registroList != null) {
            registroList.clear();
            registroList.addAll(list);
        }
        else {
            registroList = list;
        }
        notifyDataSetChanged();
    }

    public static class RegistrosViewHolder extends RecyclerView.ViewHolder{

        protected RelativeLayout rl_entrada;
        protected TextView txt_tipo;
        protected TextView txt_nombre;
        protected TextView txt_pension;
        protected TextView txt_fecha;
        protected TextView txt_hora;

        public RegistrosViewHolder(View v) {
            super(v);
            rl_entrada = (RelativeLayout)v.findViewById(R.id.registro_entry);
            txt_tipo = (TextView)v.findViewById(R.id.cv_registro_tipo);
            txt_nombre = (TextView)v.findViewById(R.id.cv_registro_nombre);
            txt_pension = (TextView)v.findViewById(R.id.cv_registro_pension);
            txt_fecha = (TextView)v.findViewById(R.id.cv_registro_fecha);
            txt_hora = (TextView)v.findViewById(R.id.cv_registro_hora);
        }
    }

}
