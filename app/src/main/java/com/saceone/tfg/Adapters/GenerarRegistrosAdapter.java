package com.saceone.tfg.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.R;
import com.saceone.tfg.Classes.Residente;

import java.util.List;

/**
 * Created by ASUS on 14/03/2016.
 */
public class GenerarRegistrosAdapter extends RecyclerView.Adapter<GenerarRegistrosAdapter.GenerarRegistrosViewHolder>{

    private List<Residente> residenteList;
    private static Context context;

    public GenerarRegistrosAdapter(List<Residente> residenteList, Context context) {
        this.residenteList = residenteList;
        this.context = context;
        for(Residente residente : residenteList){
            residente.setConsumir(false);
        }
    }

    @Override
    public int getItemCount() {
        return residenteList.size();
    }

    @Override
    public void onBindViewHolder(GenerarRegistrosViewHolder generarRegistrosViewHolder, int i) {

        final Residente mResidente = residenteList.get(i);

        if(mResidente.exists()){
            generarRegistrosViewHolder.txt_nombre.setText(mResidente.getNombre() + " " + mResidente.getApellidos());
            generarRegistrosViewHolder.txt_tipo_pension.setText(mResidente.getTipo_pension());
        }
        else{
            generarRegistrosViewHolder.txt_nombre.setText("Nombre no disponible");
            generarRegistrosViewHolder.txt_tipo_pension.setText("Pensión no disponible");
        }
        
        generarRegistrosViewHolder.txt_room.setText(String.valueOf(mResidente.getRoom()));
    }

    @Override
    public GenerarRegistrosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_residente, viewGroup, false);
        return new GenerarRegistrosViewHolder(itemView);
    }


    public static class GenerarRegistrosViewHolder extends RecyclerView.ViewHolder{

        protected TextView txt_nombre;
        protected TextView txt_room;
        protected TextView txt_tipo_pension;
        
        public GenerarRegistrosViewHolder(View v) {
            super(v);
            txt_nombre =  (TextView) v.findViewById(R.id.txt_cv_residente_nombre);
            txt_room = (TextView) v.findViewById(R.id.txt_cv_residente_room);
            txt_tipo_pension = (TextView) v.findViewById(R.id.txt_cv_residente_tipo_pension);
        }
    }
}