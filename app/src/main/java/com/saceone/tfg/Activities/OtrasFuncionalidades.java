package com.saceone.tfg.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.saceone.tfg.R;


/**
 * Created by ASUS on 25/02/2016.
 */
public class OtrasFuncionalidades extends AppCompatActivity {

    Button btnTags;
    Button btnResidentes;
    Button btnEstadisticas;
    Button btnCompras;
    Button btnInstagram;
    Button btnCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otras_funcionalidades);

        btnTags = (Button) findViewById(R.id.btn_gestion_tags);
        btnTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtrasFuncionalidades.this, gestor_tags.class);
                startActivity(i);
            }
        });

        btnResidentes = (Button) findViewById(R.id.btn_gestion_residentes);
        btnResidentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtrasFuncionalidades.this, gestor_residentes.class);
                startActivity(i);
            }
        });

        btnEstadisticas = (Button) findViewById(R.id.btn_estadisticas);
        btnEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtrasFuncionalidades.this, Estadisticas.class);
                startActivity(i);
            }
        });

        btnCompras = (Button) findViewById(R.id.btn_compras);
        btnCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtrasFuncionalidades.this, ListaCompra.class);
                startActivity(i);
            }
        });


        btnInstagram = (Button) findViewById(R.id.btn_instagram);
        btnInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtrasFuncionalidades.this, Instagram.class);
                startActivity(i);
            }
        });

        btnCloud = (Button) findViewById(R.id.btn_website);
        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtrasFuncionalidades.this, Cloud.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ajustes_datos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ajustes_avanzados) {
            Intent i = new Intent(OtrasFuncionalidades.this,Ajustes.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
