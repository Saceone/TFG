package com.saceone.tfg.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.saceone.tfg.Adapters.RegistrosAdapter;
import com.saceone.tfg.Classes.Registro;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 12/04/2016.
 */
public class Historial_registros extends AppCompatActivity {

    RecyclerView rv_registros;
    MyDB db = new MyDB(Historial_registros.this);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_registros);

        rv_registros = (RecyclerView)findViewById(R.id.rv_todos_los_registros);
        rv_registros.setFadingEdgeLength(50);
        rv_registros.setHasFixedSize(true);
        LinearLayoutManager llm_tagroom = new LinearLayoutManager(this);
        llm_tagroom.setOrientation(LinearLayoutManager.VERTICAL);
        rv_registros.setLayoutManager(llm_tagroom);

        List<Registro> listaCompleta = db.getRegistroList();
        List<Registro> registros_mostrables = new ArrayList<Registro>();
        for(Registro registro : listaCompleta){
            if(registro.getId()>2) registros_mostrables.add(registro);
        }
        RegistrosAdapter mAdapter = new RegistrosAdapter(registros_mostrables,Historial_registros.this,"ALL");
        rv_registros.setAdapter(mAdapter);

    }
}
