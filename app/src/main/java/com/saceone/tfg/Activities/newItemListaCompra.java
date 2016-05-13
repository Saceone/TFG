package com.saceone.tfg.Activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.saceone.tfg.Classes.ItemCompras;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 25/03/2016.
 */
public class newItemListaCompra extends AppCompatActivity {

    MyDB db = new MyDB(newItemListaCompra.this);

    boolean writtenNombre = false;
    String categoria;

    EditText ed_nombre;
    EditText ed_cantidad;
    EditText ed_unidad;

    Spinner sp_categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_item_listacompra);

        ed_nombre = (EditText)findViewById(R.id.ed_new_item_nombre);
        ed_nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                writtenNombre = (s.length()>0)? true : false;
                invalidateOptionsMenu();
            }
        });
        ed_cantidad = (EditText)findViewById(R.id.ed_new_item_cantidad);
        ed_unidad = (EditText)findViewById(R.id.ed_new_item_unidad);

        //TODO: spinner http://developer.android.com/intl/es/guide/topics/ui/controls/spinner.html
        sp_categoria = (Spinner) findViewById(R.id.spinner_listacompra_categoria);
        ArrayAdapter<CharSequence> mAdapter =
                ArrayAdapter.createFromResource(newItemListaCompra.this,
                R.array.categorias_compra, R.layout.sp_18dp_text);
        // Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sp_categoria.setAdapter(mAdapter);
        sp_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(writtenNombre){
            getMenuInflater().inflate(R.menu.menu_new_item, menu);
        }
        else{
            menu.clear();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if(itemId == R.id.menu_new_item_accept){
            int id = itemID();
            String nombre = ed_nombre.getText().toString();
            int cantidad;
            try {
                cantidad=Integer.parseInt(ed_cantidad.getText().toString());
            }
            catch (NumberFormatException e){
                cantidad=-1;
            }
            String unidad = ed_unidad.getText().toString();
            db.insertITEM(id, nombre, categoria, cantidad, unidad, 0);
            try {
                updateListaCompra(db.getItemComprasList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
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

    private int itemID() {
        int id;
        List<ItemCompras> oldItemList = db.getItemComprasList();
        List<ItemCompras> newItemList = new ArrayList<>();
        if(oldItemList==null){
            id=0;
        }
        else{
            //reseteamos las id's de 0 en adelante
            for(ItemCompras item : oldItemList){
                newItemList.add(item);
            }
            db.deleteITEMS(oldItemList);
            int i=0;
            for(ItemCompras item : newItemList){
                db.insertITEM(i,item.getNombre(),item.getCategoria(),item.getCantidad(),item.getUnidad(),0);
                i++;
            }
            id=i;
        }
        return id;
    }
}