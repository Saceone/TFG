package com.saceone.tfg.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
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
import java.util.Queue;

/**
 * Created by ASUS on 03/03/2016.
 */
public class HomeActivity extends AppCompatActivity{

    ImageView ivLogo;
    Button btnService;
    Button btnSettings;

    MyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        db = new MyDB(HomeActivity.this);

        //Carpetas para la app
        String[] folders = {"Fotos", "Registros/Desayunos", "Registros/Comidas", "Registros/Cenas", "Lista de la compra", "Instagram", "Sync"};
        for (String folder : folders){
            File checkFolder = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/" + folder);
            if(!checkFolder.isDirectory()) {
                File createFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "Cafeteria RUGP/" + folder);
                createFolder.mkdirs();
            }
        }

        ivLogo = (ImageView) findViewById(R.id.logo_rugp);
        Ion.with(ivLogo).animateGif(AnimateGifMode.ANIMATE).load("http://i.imgur.com/1cqcVg3.gif");

        btnService = (Button) findViewById(R.id.btn_food);
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, Servicio.class);
                startActivity(i);
            }
        });

        btnSettings = (Button) findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog (HomeActivity.this);
                //TODO: abrir dialog con teclado http://stackoverflow.com/a/19573049
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.setTitle("Autorización");
                dialog.setContentView(R.layout.dialog_number_writer);
                final EditText editText = (EditText)dialog.findViewById(R.id.ed_number);
                //TODO: cambiar input type programaticamente http://stackoverflow.com/a/2588112
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
                        if(!editText.getText().toString().equals("")){
                            if(Integer.parseInt(editText.getText().toString())==db.getAdmin().getPass()){
                                Intent i = new Intent(HomeActivity.this,OtrasFuncionalidades.class);
                                startActivity(i);
                            }
                            else {
                                Toast.makeText(HomeActivity.this,"Contraseña incorrecta.",Toast.LENGTH_LONG).show();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        if(!db.isInitialized()){
            initializeDB();
        }
    }

    private void initializeDB() {
        AlertDialog.Builder adb = new AlertDialog.Builder(HomeActivity.this);
        adb.setTitle("Inicialización");
        adb.setMessage("Es la primera vez que ejecuta la aplicación en este dispositivo. Es necesario que inicialice la base de datos." +
                "\nEsto no será necesario en el futuro.");
        adb.setCancelable(false);
        adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.resetREGISTROS();
                db.resetTAGROOM();
                db.resetRESIDENTES();
                db.resetTimeLimits();
                db.resetADMIN();
                db.resetMEDIAPENSION();
                dialog.dismiss();
            }
        });
        adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }
}
