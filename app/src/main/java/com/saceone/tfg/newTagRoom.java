package com.saceone.tfg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Exceptions.BluetoothNotAvaliableException;
import com.saceone.tfg.Exceptions.BluetoothNotEnabledException;
import com.saceone.tfg.Utils.BluetoothHelper;

import java.io.IOException;

/**
 * Created by ASUS on 25/02/2016.
 */
public class newTagRoom extends Activity {

    BluetoothHelper bt = BluetoothHelper.getInstance(this);

    EditText edtxtRoom;
    TextView txtTag;
    Button cancel;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_tag_room);

        edtxtRoom = (EditText)findViewById(R.id.edtxt_room_new_tag);
        txtTag = (TextView)findViewById(R.id.txt_tag_new_tag);

        cancel = (Button)findViewById(R.id.btn_cancel_new_tag);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bt.sendData("EXIT_NEWTAG");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        submit = (Button)findViewById(R.id.btn_submit_new_tag);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomSTRING = edtxtRoom.getText().toString();
                String tagKEY = txtTag.getText().toString();
                try {
                    int roomNUMBER = Integer.parseInt(roomSTRING);
                } catch (NumberFormatException e) {
                    Toast.makeText(getBaseContext(),"Número no válido.",Toast.LENGTH_SHORT).show();
                }
                if (tagKEY.length() < 10) {
                    Toast.makeText(getBaseContext(),"HAB: " + roomSTRING + ", TAG: " + tagKEY,Toast.LENGTH_SHORT);
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        try {
            bt.sendData("e");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            bt.connect();
        } catch (BluetoothNotEnabledException e) {
            e.printStackTrace();
        } catch (BluetoothNotAvaliableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            bt.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void call(){
        finish();
    }
}
