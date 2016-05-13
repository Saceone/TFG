package com.saceone.tfg.Adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.saceone.tfg.Classes.ItemCompras;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import java.util.List;

/**
 * Created by ASUS on 24/03/2016.
 */
public class ListaCompraElementosAdapter extends RecyclerView.Adapter<ListaCompraElementosAdapter.ListaCompraElemenotsAdapterViewHolder>{

    private List<ItemCompras> itemList;
    private static Context context;

    public ListaCompraElementosAdapter(List<ItemCompras> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(final ListaCompraElemenotsAdapterViewHolder listaCompraElementosAdapterViewHolder, int i) {

        final ItemCompras item = itemList.get(i);

        listaCompraElementosAdapterViewHolder.txt_nombre.setText(item.getNombre());

        StringBuilder sb = new StringBuilder();
        if(item.getCantidad()!=-1) sb.append(item.getCantidad()+" ");
        if((item.getUnidad()!=null)&&(item.getCantidad()!=-1)) sb.append(item.getUnidad());

        if(sb.toString().isEmpty()){
            listaCompraElementosAdapterViewHolder.txt_info.setVisibility(View.INVISIBLE);
        }
        else{
            listaCompraElementosAdapterViewHolder.txt_info.setVisibility(View.VISIBLE);
            listaCompraElementosAdapterViewHolder.txt_info.setText(sb.toString());
        }

        listaCompraElementosAdapterViewHolder.chk_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyDB db = new MyDB(context);
                int borrar = isChecked?1:0;
                db.modifyITEM(item.getId(),item.getNombre(),item.getCategoria(),item.getCantidad(),item.getUnidad(),borrar);
            }
        });

    }


    @Override
    public ListaCompraElemenotsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_listacompra_elemento, viewGroup, false);
        return new ListaCompraElemenotsAdapterViewHolder(itemView);
    }

    public static class ListaCompraElemenotsAdapterViewHolder extends RecyclerView.ViewHolder{

        protected TextView txt_nombre;
        protected TextView txt_info;
        protected CheckBox chk_item;

        public ListaCompraElemenotsAdapterViewHolder(View v) {
            super(v);
            txt_nombre = (TextView)v.findViewById(R.id.txt_listacompra_nombre);
            txt_info = (TextView)v.findViewById(R.id.txt_listacompra_info);
            chk_item = (CheckBox)v.findViewById(R.id.chk_listacompra_elemento);
        }
    }
}
