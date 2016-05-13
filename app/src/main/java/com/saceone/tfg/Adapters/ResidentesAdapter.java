package com.saceone.tfg.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.Activities.CambioGeneral;
import com.saceone.tfg.Activities.OtrasFuncionalidades;
import com.saceone.tfg.R;
import com.saceone.tfg.Classes.Residente;
import com.saceone.tfg.Utils.MyDB;
import com.saceone.tfg.Utils.ResidentesConstants;

import java.util.List;

/**
 * Created by ASUS on 14/03/2016.
 */
public class ResidentesAdapter extends RecyclerView.Adapter<ResidentesAdapter.ResidentesViewHolder>{

    private List<Residente> residenteList;
    private Context context;
    private int request;
    int[] roomList = {110, 111, 112, 114, 115, 116, 117, 119, 120, 121, 122,
            201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222,
            301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322,
            401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422,
            501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522,
            601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622};

    public ResidentesAdapter(List<Residente> residenteList, Context context, int request) {
        this.residenteList = residenteList;
        this.context = context;
        this.request = request;
    }

    @Override
    public int getItemCount() {
        return residenteList.size();
    }

    @Override
        public void onBindViewHolder(final ResidentesViewHolder residentesViewHolder, final int i) {

        final Residente mResidente = residenteList.get(i);

        if(request == ResidentesConstants.CAMBIO_GENERAL){
            residentesViewHolder.txt_room.setText(String.valueOf(roomList[i]));
            residentesViewHolder.txt_room.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    dialog.setTitle("Introducir habitación");
                    dialog.setContentView(R.layout.dialog_number_writer);
                    final EditText editText = (EditText)dialog.findViewById(R.id.ed_number);
                    Button btnCancel = (Button)dialog.findViewById(R.id.btn_dialog_number_cancel);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button btnSubmit = (Button)dialog.findViewById(R.id.btn_dialog_number_submit);
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            roomList[i] = Integer.parseInt(editText.getText().toString());
                            residentesViewHolder.txt_room.setText(String.valueOf(roomList[i]));
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }
        else{
            residentesViewHolder.txt_room.setText(String.valueOf(mResidente.getRoom()));
        }

        if(mResidente.exists()){
            residentesViewHolder.iv_warning.setVisibility(View.GONE);
            residentesViewHolder.txt_nombre.setText(mResidente.getNombre()+" "+
                                                    mResidente.getApellidos());
            residentesViewHolder.txt_tipo_pension.setText(mResidente.getTipo_pension());
        }
        else{
            residentesViewHolder.iv_warning.setVisibility(View.VISIBLE);
            residentesViewHolder.iv_warning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "No hay residente asociado a la habitación " + mResidente.getRoom() + ".", Toast.LENGTH_SHORT).show();
                }
            });
            residentesViewHolder.txt_nombre.setText("Nombre no disponible");
            residentesViewHolder.txt_tipo_pension.setText("Pensión no disponible");
        }
    }

    @Override
    public ResidentesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_residente, viewGroup, false);
        return new ResidentesViewHolder(itemView);
    }

    public int[] getNewRooms() {
        return roomList;
    }


    public static class ResidentesViewHolder extends RecyclerView.ViewHolder{

        protected ImageView iv_warning;
        protected TextView txt_nombre;
        protected TextView txt_room;
        protected TextView txt_tipo_pension;

        public ResidentesViewHolder(View v) {
            super(v);
            iv_warning = (ImageView) v.findViewById(R.id.iv_residente_cv_warning);
            txt_nombre =  (TextView) v.findViewById(R.id.txt_cv_residente_nombre);
            txt_room = (TextView) v.findViewById(R.id.txt_cv_residente_room);
            txt_tipo_pension = (TextView) v.findViewById(R.id.txt_cv_residente_tipo_pension);
        }
    }
}