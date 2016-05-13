package com.saceone.tfg.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Classes.ItemCompras;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;
import com.saceone.tfg.Utils.WrappingRecyclerViewLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 24/03/2016.
 */
public class ListaCompraCategoriaAdapter extends RecyclerView.Adapter<ListaCompraCategoriaAdapter.ListaCompraCategoriaAdapterViewHolder>{

    private List<String> categorias;
    private static Context context;

    public ListaCompraCategoriaAdapter(List<String> categorias, Context context) {
        this.categorias = categorias;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    @Override
    public void onBindViewHolder(ListaCompraCategoriaAdapterViewHolder listaCompraCategoriaAdapterViewHolder, int i) {

        final String categoria = categorias.get(i);

        listaCompraCategoriaAdapterViewHolder.txt_categoria.setText(categoria);

        MyDB db = new MyDB(context);

        List<ItemCompras> itemList = db.getItemComprasList();
        List<ItemCompras> selectedItems = new ArrayList<ItemCompras>();

        for(ItemCompras item : itemList){
            if(item.getCategoria().equals(categoria)){
                selectedItems.add(item);
            }
        }

        listaCompraCategoriaAdapterViewHolder.rv_elementos.setHasFixedSize(true);
        WrappingRecyclerViewLayoutManager llm_lista_elementos = new WrappingRecyclerViewLayoutManager(context);
        llm_lista_elementos.setOrientation(WrappingRecyclerViewLayoutManager.VERTICAL);
        listaCompraCategoriaAdapterViewHolder.rv_elementos.setLayoutManager(llm_lista_elementos);

        ListaCompraElementosAdapter mAdapter = new ListaCompraElementosAdapter(selectedItems,context);
        listaCompraCategoriaAdapterViewHolder.rv_elementos.setAdapter(mAdapter);
    }

    @Override
    public ListaCompraCategoriaAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_listacompra_categoria, viewGroup, false);
        return new ListaCompraCategoriaAdapterViewHolder(itemView);
    }

    public static class ListaCompraCategoriaAdapterViewHolder extends RecyclerView.ViewHolder{

        protected TextView txt_categoria;
        protected RecyclerView rv_elementos;

        public ListaCompraCategoriaAdapterViewHolder(View v) {
            super(v);
            txt_categoria = (TextView)v.findViewById(R.id.txt_listacompra_categoria);
            rv_elementos = (RecyclerView)v.findViewById(R.id.rv_listacompra_elementos);
        }
    }
}
