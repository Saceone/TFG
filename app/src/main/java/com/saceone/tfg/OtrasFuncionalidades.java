package com.saceone.tfg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


/**
 * Created by ASUS on 25/02/2016.
 */
public class OtrasFuncionalidades extends AppCompatActivity {
    Button btn_gestion_tags;
    Button btn_gestion_residentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otras_funcionalidades);

        btn_gestion_residentes = (Button)findViewById(R.id.btn_gestion_residentes);
        btn_gestion_residentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_gestion_tags = (Button)findViewById(R.id.btn_gestion_tags);
        btn_gestion_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
