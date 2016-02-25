package com.saceone.tfg;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saceone.tfg.Exceptions.BluetoothNotAvaliableException;
import com.saceone.tfg.Exceptions.BluetoothNotEnabledException;
import com.saceone.tfg.Utils.BluetoothHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    BluetoothHelper bt = BluetoothHelper.getInstance(getActivity());

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        CardView cv1 = (CardView)view.findViewById(R.id.cv1);
        CardView cv2 = (CardView)view.findViewById(R.id.cv2);
        CardView cv3 = (CardView)view.findViewById(R.id.cv3);

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date today = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String folderName = formatter.format(today);
                    bt.sendData("SYNC_BT" + folderName);
                    String s = "SYNC_BT"+folderName;
                    Log.d("SENDATA",s.substring(0,7));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), OtrasFuncionalidades.class);
                startActivity(i);
            }
        });


        return view;
    }

}
