package com.saceone.tfg.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.saceone.tfg.Classes.Horario;
import com.saceone.tfg.Classes.TagRoom;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 21/04/2016.
 */
public class Ajustes extends AppCompatActivity implements View.OnClickListener{

    private static final int GO_FOR_TAG = 1;

    MyDB db;

    TextView txtStartDesayuno;
    TextView txtEndDesayuno;
    TextView txtStartComida;
    TextView txtEndComida;
    TextView txtStartCena;
    TextView txtEndCena;

    TextView txtMenus;
    TextView txtDesayunos;

    TextView txtPass;

    List<TextView> txtViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_avanzados);

        db = new MyDB(Ajustes.this);

        txtViewList = new ArrayList<TextView>();

        txtStartDesayuno = (TextView)findViewById(R.id.hora_apertura_desayunos);
        txtViewList.add(txtStartDesayuno);
        txtEndDesayuno = (TextView)findViewById(R.id.hora_cierre_desayunos);
        txtViewList.add(txtEndDesayuno);
        txtStartComida = (TextView)findViewById(R.id.hora_apertura_comida);
        txtViewList.add(txtStartComida);
        txtEndComida = (TextView)findViewById(R.id.hora_cierre_comida);
        txtViewList.add(txtEndComida);
        txtStartCena = (TextView)findViewById(R.id.hora_apertura_cena);
        txtViewList.add(txtStartCena);
        txtEndCena = (TextView)findViewById(R.id.hora_cierre_cena);
        txtViewList.add(txtEndCena);

        txtMenus = (TextView)findViewById(R.id.menus_media_pension);
        txtMenus.setOnClickListener(this);
        txtDesayunos = (TextView)findViewById(R.id.desayunos_media_pension);
        txtDesayunos.setOnClickListener(this);

        txtPass = (TextView)findViewById(R.id.admin_pass);
        txtPass.setOnClickListener(this);

        setHorariosView();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.menus_media_pension){
            final Dialog dialog = new Dialog(Ajustes.this);
            dialog.setTitle("Introducir número");
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.setContentView(R.layout.dialog_number_writer);
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
                    db.setMEDIAPENSION("menus", Integer.parseInt(editText.getText().toString()));
                    dialog.dismiss();
                    setHorariosView();
                }
            });
            dialog.show();
        }
        else if(v.getId()==R.id.desayunos_media_pension){
            final Dialog dialog = new Dialog(Ajustes.this);
            dialog.setTitle("Introducir número");
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
                    db.setMEDIAPENSION("desayunos", Integer.parseInt(editText.getText().toString()));
                    dialog.dismiss();
                    setHorariosView();
                }
            });
            dialog.show();
        }
        else if(v.getId()==R.id.admin_pass){
            final Dialog dialog = new Dialog(Ajustes.this);
            dialog.setTitle("Nueva contraseña");
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
                    db.setADMIN(Integer.parseInt(editText.getText().toString()));
                    dialog.dismiss();
                    setHorariosView();
                }
            });
            dialog.show();
        }
        else{
            openClockDialog(v.getId());
        }
    }

    private void openClockDialog(int id) {
        final int idTxt = id;
        final Dialog dialog = new Dialog(Ajustes.this);
        dialog.setTitle("Seleccionar hora:");
        dialog.setContentView(R.layout.dialog_clock);
        final TimePicker tp = (TimePicker)dialog.findViewById(R.id.clock);
        tp.setIs24HourView(true);
        Button aceptar = (Button)dialog.findViewById(R.id.btn_dialog_clock_submit);
        Button cancelar = (Button)dialog.findViewById(R.id.btn_dialog_clock_cancel);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tp.clearFocus();
                int hora = tp.getCurrentHour();
                int minuto = tp.getCurrentMinute();
                String[] tipoylimite = setTipoConsumicion(idTxt);
                String consumicion = tipoylimite[0];
                String limite = tipoylimite[1];
                db.setTimeLimit(consumicion, limite, hora, minuto);
                setHorariosView();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private String[] setTipoConsumicion(int idTxt) {
        String consumicion;
        String limite;
        switch (idTxt){
            case R.id.hora_apertura_desayunos:
                consumicion = "desayunos";
                limite = "apertura";
                break;
            case R.id.hora_cierre_desayunos:
                consumicion = "desayunos";
                limite = "cierre";
                break;
            case R.id.hora_apertura_comida:
                consumicion = "comidas";
                limite = "apertura";
                break;
            case R.id.hora_cierre_comida:
                consumicion = "comidas";
                limite = "cierre";
                break;
            case R.id.hora_apertura_cena:
                consumicion = "cenas";
                limite = "apertura";
                break;
            default:
                consumicion = "cenas";
                limite = "cierre";
                break;
        }
        String[] tipoylimite = {consumicion,limite};
        return tipoylimite;
    }

    private void setHorariosView() {
        int reg = 0;
        for(TextView txtView : txtViewList){
            Horario horario = db.getHORARIO(reg);
            StringBuilder sb = new StringBuilder();
            if(horario.getHora()<=9)sb.append("0");
            sb.append("" + horario.getHora() + ":");
            if(horario.getMinuto()<=9)sb.append("0");
            sb.append("" + horario.getMinuto());
            txtView.setText(sb.toString());
            reg++;
        }
        txtMenus.setText("" + db.getMediaPension().getMenus());
        txtDesayunos.setText("" + db.getMediaPension().getDesayunos());
        txtPass.setText("" + db.getAdmin().getPass());
    }
}
