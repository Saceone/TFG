package com.saceone.tfg.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Classes.TagRoom;
import com.saceone.tfg.Exceptions.BluetoothNotAvaliableException;
import com.saceone.tfg.Exceptions.BluetoothNotEnabledException;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.BluetoothHelper;
import com.saceone.tfg.Utils.ICallback;
import com.saceone.tfg.Utils.MyDB;

import java.io.IOException;
import java.util.List;

/**
 * Created by ASUS on 25/02/2016.
 */
public class newTagRoom extends Activity implements ICallback{

    BluetoothHelper bt = BluetoothHelper.getInstance(this);
    boolean conectado = false;

    MyDB db = new MyDB(newTagRoom.this);

    TextView txtTag;
    TextView txtRoom;
    Button cancel;
    Button submit;

    String request;

    int room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tag_room);

        txtTag = (TextView)findViewById(R.id.txt_tag_new_tag);
        txtRoom = (TextView)findViewById(R.id.txt_room_new_tagroom);
        txtRoom.setVisibility(View.VISIBLE);

        request = getIntent().getStringExtra("request");

        cancel = (Button)findViewById(R.id.btn_cancel_new_tag);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bt.sendData("EXIT_NEWTAG");
                } catch (IOException e) {
                    Log.d("LOG: ", "No hay conexión establecida.");
                }
                finish();
            }
        });

        submit = (Button)findViewById(R.id.btn_submit_new_tag);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagKEY = txtTag.getText().toString();
                Intent i=new Intent();
                i.putExtra("ROOM",room);
                i.putExtra("TAG", tagKEY);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        switch (request){
            case "GOFORTAG":
                room=getIntent().getIntExtra("id",-1);
                txtRoom.setText(String.valueOf(room));
                break;
            case "CHECKTAG":
                submit.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                txtRoom.setText("Esperando TAG...");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed(){
        try {
            bt.sendData("EXIT_NEWTAG");
        } catch (IOException e) {
            Log.d("LOG: ", "No hay conexión establecida que desconectar.");
        }
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            bt.connect();
            conectado=true;
            bt.sendData("WAKE_READER");
        } catch (BluetoothNotEnabledException e) {
            Toast.makeText(this,"Bluetooth no activado. Por favor, actívelo para poder establecer la comunicación con el lector.",Toast.LENGTH_LONG).show();
            finish();
        } catch (BluetoothNotAvaliableException e) {
            Log.d("LOG: ","Bluetooth no disponible");
            finish();
        } catch (IOException e) {
            Toast.makeText(this,"Lector no detectado. Por favor, asegúrese de que está encendido.",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(conectado){
            try {
                bt.disconnect();
            } catch (IOException e) {
                Log.d("LOG: ","No hay conexión establecida que desconectar.");
            }
        }
    }

    public void call(String data){
        if(data.equals("TAG_TIMEOUT")){
            Intent i = new Intent();
            i.putExtra("ROOM",-1);
            i.putExtra("TAG","tag_no_valida");
            finish();
        }
        else {
            switch (request) {
                case "GOFORTAG":
                    txtTag.setText(data);
                    break;
                case "CHECKTAG":
                    txtTag.setText(data);
                    List<TagRoom> tagRoomList = db.getTagRoomList();
                    boolean existingTag = false;
                    for (TagRoom tagRoom : tagRoomList){
                        if(tagRoom.getTag()!=null){
                            if(tagRoom.getTag().equals(data)){
                                txtRoom.setText(String.valueOf(tagRoom.getRoom()));
                                existingTag=true;
                            }
                        }
                    }
                    if(!existingTag){
                        txtRoom.setText("TAG no asociado a ninguna habitación.");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
