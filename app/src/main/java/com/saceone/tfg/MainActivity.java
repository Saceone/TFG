package com.saceone.tfg;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.saceone.tfg.Utils.BluetoothHelper;
import com.saceone.tfg.Utils.ICallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ICallback {

    BluetoothHelper bt = BluetoothHelper.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    bt.sendData("POLE");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void call() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String folderName = formatter.format(today);
        String day = folderName.substring(0,10);
        String hour = folderName.substring(11, 16);

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.tag_detected_dialog);
        dialog.setTitle("Nuevo menu");

        TextView txtName = (TextView) dialog.findViewById(R.id.txt_name_tag_detected_dialog);
        TextView txtRoom = (TextView) dialog.findViewById(R.id.txt_room_tag_detected_dialog);
        TextView txtBoard = (TextView) dialog.findViewById(R.id.txt_board_tag_detected_dialog);
        TextView txtLeftMenus = (TextView)dialog.findViewById(R.id.txt_left_menus_tag_detected_dialog);
        TextView txtDate = (TextView) dialog.findViewById(R.id.txt_date_tag_detected_dialog);
        TextView txtHour = (TextView) dialog.findViewById(R.id.txt_hour_tag_detected_dialog);

        txtName.setText("Ramon Arteaga");
        txtRoom.setText("Habitacion: 303");
        txtBoard.setText(Html.fromHtml("Tipo de pension: media pension"));
        txtLeftMenus.setText("Menus restantes: 43");
        txtDate.setText("Fecha: " + day);
        txtHour.setText("Hora: " + hour);

        ImageView image = (ImageView) dialog.findViewById(R.id.img_tag_detected_dialog);
        image.setImageResource(R.drawable.random_user);

        dialog.show();

        Button declineButton = (Button) dialog.findViewById(R.id.btn_cancel_tag_detected_dialog);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button submitButton = (Button) dialog.findViewById(R.id.btn_submit_tag_detected_dialog);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
