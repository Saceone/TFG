package com.saceone.tfg.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Adapters.TagRoomAdapter;
import com.saceone.tfg.Classes.TagRoom;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.ItemClickSupport;
import com.saceone.tfg.Utils.MyDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 25/02/2016.
 */
public class gestor_tags extends AppCompatActivity {

    private static final int GO_FOR_TAG = 1;

    final MyDB db = new MyDB(this);

    MenuItem searchItem;
    SearchView searchView;

    List<Integer> roomList = new ArrayList<Integer>();
    int id;

    TextView txt_lista;
    RecyclerView rv_tagroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestor_tag);

        //TODO: fundamentos de recyclerview y cardview: https://www.binpress.com/tutorial/android-l-recyclerview-and-cardview-tutorial/156
        rv_tagroom = (RecyclerView) findViewById(R.id.rv_tagroom);
        rv_tagroom.setHasFixedSize(true);
        LinearLayoutManager llm_tagroom = new LinearLayoutManager(this);
        llm_tagroom.setOrientation(LinearLayoutManager.VERTICAL);
        rv_tagroom.setLayoutManager(llm_tagroom);

        ItemClickSupport.addTo(rv_tagroom).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView rv, int position, View v) {
                id = roomList.get(position);
                //TODO: popup menu http://www.javatpoint.com/android-popup-menu-example
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                if (db.getTAGROOMwithID(id).getTag() == null) {
                    inflater.inflate(R.menu.menu_tagroom_popup_new, popup.getMenu());
                } else {
                    inflater.inflate(R.menu.menu_tagroom_popup_existing, popup.getMenu());
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_tagroom_modificarTAG) {
                            goForTag();
                        } else if (item.getItemId() == R.id.menu_tagroom_eliminarTAG) {
                            deleteTag();
                        } else if (item.getItemId() == R.id.menu_tagroom_addTAG) {
                            goForTag();
                        } else {
                            Log.d("LOG: ", "Opción inválida");
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        setTagRoomList();

        txt_lista = (TextView)findViewById(R.id.txt_gestor_tags);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        clearSearchView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GO_FOR_TAG) {
            if (resultCode == RESULT_OK) {
                txt_lista.setText("Relación Habitaciones - TAGs");
                int room = data.getIntExtra("ROOM",-1);
                if(room!=-1){
                    int id = room;
                    String tag = data.getStringExtra("TAG");
                    boolean newTAG = true;
                    int existingROOM = -1;
                    List<TagRoom> tagRoomList = db.getTagRoomList();
                    for (TagRoom tagRoom : tagRoomList){
                        if(tagRoom.getTag()!=null){
                            if(tagRoom.getTag().equals(tag)){
                                newTAG=false;
                                existingROOM = tagRoom.getRoom();
                            }
                        }
                    }
                    if(newTAG){
                        db.modifyTAG(id, tag, room);
                        Toast.makeText(gestor_tags.this,"TAG "+tag+" asociado correctamente a la habitación "+room+".",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(gestor_tags.this,"El TAG "+tag+" ya está asociado a la habitación "+existingROOM
                                +". Por favor, introduzca un TAG único a cada habitación.",Toast.LENGTH_LONG).show();
                    }
                }
                setTagRoomList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tagroom, menu);
        searchItem = menu.findItem(R.id.menu_tag_buscar_habitacion);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Buscar...");
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String input) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                List<TagRoom> tagRoomList = new ArrayList<TagRoom>();
                roomList.clear();
                try {
                    Cursor cursorRoom = db.getTagRoomIfContainsRoom(input);
                    if (!cursorRoom.moveToFirst()) {
                        Log.d("LOG: ", "no hay resultados para esa habitación.");
                    } else {
                        txt_lista.setText(R.string.lista_residentes);
                        cursorRoom.moveToFirst();
                        if (cursorRoom != null) {
                            int id = cursorRoom.getColumnIndex("_id");
                            do {
                                int room = cursorRoom.getInt(id);
                                TagRoom tagRoom = db.getTAGROOMwithID(room);
                                roomList.add(tagRoom.getRoom());
                                tagRoomList.add(tagRoom);
                            } while (cursorRoom.moveToNext());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("LOG: ", "error al buscar residente");
                }
                if (roomList.isEmpty()) {
                    txt_lista.setVisibility(View.VISIBLE);
                    txt_lista.setText("No hay resultados para '" + input + "'.");
                } else {
                    txt_lista.setVisibility(View.GONE);
                }
                TagRoomAdapter mAdapter = new TagRoomAdapter(tagRoomList,gestor_tags.this);
                rv_tagroom.setAdapter(mAdapter);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_tagroom_reset) {
            resetTagRoomAll();
            return true;
        }
        else if (id == R.id.menu_tagroom_checkTAG){
            checkTag();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private void setTagRoomList() {
        List<TagRoom> listTagRoom = db.getTagRoomList();
        roomList.clear();
        for(TagRoom tagroom : listTagRoom){
            roomList.add(tagroom.getRoom());
        }
        TagRoomAdapter mAdapter = new TagRoomAdapter(listTagRoom,gestor_tags.this);
        rv_tagroom.setAdapter(mAdapter);
    }

    private void resetTagRoomAll() {
        AlertDialog.Builder adb = new AlertDialog.Builder(gestor_tags.this);
        adb.setTitle("Resetear");
        adb.setMessage("¿Desea resetear la lista de habitaciones?" +
                "\n\nAdvertencia: se perderán todos los TAGs almacenados.");
        adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.resetTAGROOM();
                setTagRoomList();
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

    private void goForTag() {
        Intent i = new Intent(gestor_tags.this, newTagRoom.class);
        i.putExtra("id", id);
        i.putExtra("request", "GOFORTAG");
        startActivityForResult(i, GO_FOR_TAG);
    }

    private void checkTag() {
        Intent i = new Intent(gestor_tags.this, newTagRoom.class);
        i.putExtra("id", id);
        i.putExtra("request", "CHECKTAG");
        Log.d("LOG: ", "id:" + id);
        startActivity(i);
    }

    private void deleteTag() {
        AlertDialog.Builder adb = new AlertDialog.Builder(gestor_tags.this);
        adb.setTitle("Eliminar");
        adb.setMessage("¿Desea eliminar el código TAG de la habitación " + id + "?");
        adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteTAG(id);
                setTagRoomList();
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

    private void clearSearchView() {
        searchView.setQuery("", false);
        searchView.setIconified(true);
    }

}
