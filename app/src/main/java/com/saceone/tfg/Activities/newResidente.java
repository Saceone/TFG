package com.saceone.tfg.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

/**
 * Created by ASUS on 16/03/2016.
 */
public class newResidente extends AppCompatActivity {

    private final static int MEDIA_PENSION = 1;
    private final static int PENSION_COMPLETA = 2;
    private static final int TAKE_PICTURE = 3;
    private Uri imageUri;

    LinearLayout lv_media_pension;

    ImageView iv_residente;
    ImageView iv_pension;

    EditText edtxt_nombre;
    EditText edtxt_apellidos;
    TextView txt_room;
    TextView txt_pension;
    EditText edtxt_menus;
    EditText edtxt_desayunos;
    EditText edtxt_notas;

    CheckBox chk_full;

    Button btn_submit;
    Button btn_cancel;

    MyDB db;

    String request;

    File photo;
    String photoname;
    String photonameOld;

    String nombre;
    String apellidos;
    String notas;
    int pension=-1;
    int room;
    int menus=-1;
    int desayunos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: iconos en la plantilla de nuevo residente https://icons8.com/web-app/for/androidL/
        setContentView(R.layout.new_residente);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new MyDB(newResidente.this);

        lv_media_pension = (LinearLayout)findViewById(R.id.lv_media_pension);
        iv_residente = (ImageView)findViewById(R.id.new_residente_image);
        iv_pension = (ImageView)findViewById(R.id.iv_tipo_pension_desplegable);
        edtxt_nombre = (EditText)findViewById(R.id.ed_new_residente_nombre);
        edtxt_nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nombre = s.toString();
                photoname = nombre+apellidos;
            }
        });
        edtxt_apellidos = (EditText)findViewById(R.id.ed_new_residente_apellidos);
        edtxt_apellidos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                apellidos = s.toString();
                photoname = nombre+apellidos;
            }
        });
        txt_room = (TextView)findViewById(R.id.txt_new_residente_room);

        room=getIntent().getIntExtra("room", -1);
        txt_room.setText(String.valueOf(room));

        txt_pension = (TextView)findViewById(R.id.txt_new_residente_pension);
        edtxt_menus = (EditText)findViewById(R.id.ed_menus_new_residente);
        edtxt_desayunos = (EditText)findViewById(R.id.ed_desayunos_new_residente);
        edtxt_notas = (EditText)findViewById(R.id.ed_new_residente_nota);

        chk_full = (CheckBox)findViewById(R.id.chk_new_residente_full);

        request=getIntent().getStringExtra("request");
        switch (request){
            case "modify":
                nombre = getIntent().getStringExtra("nombre");
                edtxt_nombre.setText(nombre);
                apellidos = getIntent().getStringExtra("apellidos");
                edtxt_apellidos.setText(apellidos);
                pension = getIntent().getStringExtra("pension").equals("Media pensión")?MEDIA_PENSION:PENSION_COMPLETA;
                txt_pension.setText(getIntent().getStringExtra("pension"));
                txt_room.setText(String.valueOf(getIntent().getIntExtra("room", -1)));
                edtxt_menus.setText(String.valueOf(getIntent().getIntExtra("menus",-1)));
                edtxt_desayunos.setText(String.valueOf(getIntent().getIntExtra("desayunos",-1)));
                edtxt_notas.setText(getIntent().getStringExtra("notas"));
                switch (pension){
                    case MEDIA_PENSION:
                        lv_media_pension.setVisibility(View.VISIBLE);
                        break;
                    case PENSION_COMPLETA:
                        chk_full.setVisibility(View.GONE);
                        lv_media_pension.setVisibility(View.GONE);
                }
                photoname = nombre+apellidos;
                photonameOld = photoname;
                photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos",photoname+".jpg");
                if(photo.exists()){
                    iv_residente.setImageBitmap(BitmapFactory.decodeFile(Environment
                            .getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos/" + photoname + ".jpg"));
                }
                else{
                    iv_residente.setImageResource(R.drawable.random_user);
                }
                break;
            case "new":
                iv_residente.setImageResource(R.drawable.random_user);
                lv_media_pension.setVisibility(View.GONE);
                chk_full.setVisibility(View.GONE);
                chk_full.setChecked(false);
                break;
            default:
                iv_residente.setImageResource(R.drawable.random_user);
                break;
        }

        iv_pension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_tipo_pension, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.new_residente_media_pension) {
                            if(request.equals("new")){
                                //Activamos la opcion para asignar lo correspondiente a un trimestre solo si es un residente nuevo
                                //Para cambios de tipo de pension en residentes existentes, el numero de menus/desayunos se pone a mano
                                chk_full.setVisibility(View.VISIBLE);
                            }
                            lv_media_pension.setVisibility(View.VISIBLE);
                            txt_pension.setText("Media pensión");
                            pension=MEDIA_PENSION;
                        } else if (item.getItemId() == R.id.new_residente_pension_completa) {
                            txt_pension.setText("Pensión completa");
                            chk_full.setVisibility(View.GONE);
                            lv_media_pension.setVisibility(View.GONE);
                            pension=PENSION_COMPLETA;
                        } else {
                            Log.d("LOG: ", "Opción inválida");
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        chk_full.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    menus = db.getMediaPension().getMenus();
                    edtxt_menus.setText(String.valueOf(menus));
                    desayunos = db.getMediaPension().getDesayunos();
                    edtxt_desayunos.setText(String.valueOf(desayunos));
                } else {
                    menus = -1;
                    edtxt_menus.setText("");
                    desayunos = -1;
                    edtxt_desayunos.setText("");
                }
            }
        });

        btn_submit = (Button)findViewById(R.id.btn_submit_new_residente);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();

                StringBuilder sb = new StringBuilder();

                nombre = edtxt_nombre.getText().toString();
                apellidos = edtxt_apellidos.getText().toString();
                notas = edtxt_notas.getText().toString();
                try {
                    room = Integer.parseInt(txt_room.getText().toString());
                } catch (NumberFormatException e) {
                   sb.append("Habitación no válida");
                }
                if(pension==MEDIA_PENSION){
                    try {
                        menus = Integer.parseInt(edtxt_menus.getText().toString());
                    } catch (NumberFormatException e) {
                        sb.append("\nNúmero de menús no válido");
                    }
                    try {
                        desayunos = Integer.parseInt(edtxt_desayunos.getText().toString());
                    } catch (NumberFormatException e) {
                        sb.append("\nNúmero de desayunos no válido");
                    }
                }
                String aux = sb.toString();
                StringBuilder sb2 = new StringBuilder();
                if (nombre.length()==0)
                    sb2.append("\nNombre no válido");
                if (apellidos.length()==0)
                    sb2.append("\nApellidos no válidos");
                if (((pension != MEDIA_PENSION) && (pension != PENSION_COMPLETA))||(txt_pension.getText().equals("no disponible")))
                    sb2.append("\nTipo de pensión no válida");
                if (!goodRoom(room))
                    sb2.append("\nHabitación no válida");
                 if(pension==MEDIA_PENSION){
                    if (menus < 0)
                        if(!aux.contains("menús"))
                            sb2.append("\nNúmero de menús no válido");
                    if (desayunos < 0)
                        if(!aux.contains("desayunos"))
                            sb2.append("\nNúmero de desayunos no válido");
                }
                String aux2 = aux.concat(sb2.toString());
                if(aux2.length()>0){
                    AlertDialog.Builder adb = new AlertDialog.Builder(newResidente.this);
                    adb.setTitle("Datos inválidos");
                    adb.setMessage("Se encontraron los siguientes problemas:" +
                            "\n" + aux2);
                    adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog ad = adb.create();
                    ad.show();
                }
                else {
                    //Si modifico el nombre, actualizo el nombre del archivo
                    if(!photoname.equals(photonameOld)){
                        photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos",photonameOld+".jpg");
                        File photoNew = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos",photoname+".jpg");
                        photo.renameTo(photoNew);
                    }
                    i.putExtra("nombre", nombre);
                    i.putExtra("apellidos", apellidos);
                    i.putExtra("pension", pension==MEDIA_PENSION?"Media pensión":"Pensión completa");
                    i.putExtra("notas", notas);
                    i.putExtra("room", room);
                    i.putExtra("menus", menus);
                    i.putExtra("desayunos", desayunos);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });

        btn_cancel = (Button)findViewById(R.id.btn_cancel_new_residente);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(newResidente.this);
                adb.setTitle("Cancelar");
                adb.setMessage("¿Desea descartar los cambios realizados?");
                adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                AlertDialog ad = adb.create();
                ad.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_residente_take_picture_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombre==null){
                    Toast.makeText(newResidente.this, "Por favor, introduzca un nombre.",Toast.LENGTH_SHORT).show();
                }
                else if(apellidos==null){
                    Toast.makeText(newResidente.this, "Por favor, introduza unos apellidos.",Toast.LENGTH_SHORT).show();
                }
                else{
                    //TODO: control de la camara http://stackoverflow.com/a/2737770
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Fotos", photoname+".jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photo));
                    imageUri = Uri.fromFile(photo);
                    startActivityForResult(intent, TAKE_PICTURE);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView = (ImageView) findViewById(R.id.new_residente_image);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr,selectedImage);
                        imageView.setImageBitmap(bitmap);
                        //Toast.makeText(this, "imagen guardada: "+selectedImage.toString(),Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
        }
    }

    private boolean goodRoom(int room) {
        boolean goodRoom=false;
        int[] roomList = {110, 111, 112, 114, 115, 116, 117, 119, 120, 121, 122,
                201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222,
                301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322,
                401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422,
                501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522,
                601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622};
        for(int i : roomList){
            if(i==room) goodRoom=true;
        }
        return goodRoom;
    }

}