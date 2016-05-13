package com.saceone.tfg.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Adapters.ListaCompraCategoriaAdapter;
import com.saceone.tfg.Adapters.ListaCompraElementosAdapter;
import com.saceone.tfg.Adapters.TagRoomAdapter;
import com.saceone.tfg.Classes.ItemCompras;
import com.saceone.tfg.Classes.TagRoom;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 24/03/2016.
 */
public class ListaCompra extends AppCompatActivity {

    final MyDB db = new MyDB(this);

    TextView txt_lista;
    RecyclerView rv_lista_categorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_compra);

        if(db.getItemComprasList()!=null){
            for(ItemCompras itemAux : db.getItemComprasList()){
                db.modifyITEM(itemAux.getId(),itemAux.getNombre(),itemAux.getCategoria(),itemAux.getCantidad(),itemAux.getUnidad(),0);
            }
        }

        txt_lista = (TextView)findViewById(R.id.txt_listacompra);

        rv_lista_categorias = (RecyclerView) findViewById(R.id.rv_listacompra_categoria);
        rv_lista_categorias.setHasFixedSize(true);
        LinearLayoutManager llm_lista_categorias = new LinearLayoutManager(this);
        llm_lista_categorias.setOrientation(LinearLayoutManager.VERTICAL);
        rv_lista_categorias.setLayoutManager(llm_lista_categorias);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_listacompra);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListaCompra.this,newItemListaCompra.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        setItemsView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listacompra, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_listacompra_borrar) {
            final List<ItemCompras> itemsBorrar = new ArrayList<>();
            if(db.getItemComprasList()!=null){
                for(ItemCompras itemAux : db.getItemComprasList()){
                    if(itemAux.getBorrar()==1){
                        itemsBorrar.add(itemAux);
                    }
                }
            }
            if(!itemsBorrar.isEmpty()){
                AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
                adb.setTitle("Eliminar");
                adb.setMessage("¿Está seguro que desea eliminar los elementos seleccionados de la lista de la compra?");
                adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteITEMS(itemsBorrar);
                        try {
                            updateListaCompra(db.getItemComprasList());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setItemsView();
                    }
                });
                adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog ad = adb.create();
                ad.show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setItemsView() {
        List<ItemCompras> itemList = db.getItemComprasList();
        List<String> categorias = new ArrayList<String>();

        if(itemList==null){
            txt_lista.setVisibility(View.VISIBLE);
            txt_lista.setText("No hay elementos añadidos a la lista de la compra.");
        }
        else{
            txt_lista.setVisibility(View.GONE);
            for(ItemCompras item : itemList){
                db.modifyITEM(item.getId(), item.getNombre(), item.getCategoria(), item.getCantidad(), item.getUnidad(), 0);
                if(!categorias.contains(item.getCategoria())){
                    categorias.add(item.getCategoria());
                }
            }
        }
        ListaCompraCategoriaAdapter mAdapter = new ListaCompraCategoriaAdapter(categorias,ListaCompra.this);
        rv_lista_categorias.setAdapter(mAdapter);
    }

    private void updateListaCompra(List<ItemCompras> itemComprasList) throws IOException {
        File newFile = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Lista de la compra", "Lista de la compra.txt");
        if(newFile.exists()){
            newFile.delete();
        }
        newFile.createNewFile();
        if(newFile.exists()) {
            OutputStream fo = new FileOutputStream(newFile);
            StringBuilder sb = new StringBuilder();
            List<String> categorias = new ArrayList<String>();
            if(itemComprasList==null){
                Log.d("LOG: ","ITEM null");
                sb.append("Lista de la compra vacia.");
            }
            else{
                sb.append("************************\n");
                sb.append("** LISTA DE LA COMPRA **\n");
                sb.append("************************\n\n");

                for(ItemCompras item : itemComprasList){
                    if(!categorias.contains(item.getCategoria())){
                        categorias.add(item.getCategoria());
                    }
                }
                for(String categoria : categorias){
                    sb.append(categoria.toUpperCase()+"\n\n");
                    for(ItemCompras item : itemComprasList){
                        if(item.getCategoria().equals(categoria)){
                            sb.append(item.getNombre()+": ");
                            if(item.getCantidad()>0)sb.append(item.getCantidad()+" ");
                            sb.append(item.getUnidad()+"\n\n");
                        }
                    }
                    sb.append("---------------------\n");
                }
            }
            fo.write(sb.toString().getBytes());
            fo.close();
        }
    }
}
