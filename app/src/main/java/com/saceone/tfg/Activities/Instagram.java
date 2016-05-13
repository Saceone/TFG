package com.saceone.tfg.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.saceone.tfg.R;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ASUS on 16/04/2016.
 */
public class Instagram extends AppCompatActivity {

    private static final int TAKE_PICTURE = 1;
    Uri imageUri;
    String photoname;

    ImageView iv_pic;
    FloatingActionButton fab_takePic;
    FloatingActionButton fab_rePic;
    FloatingActionButton fab_instagram;
    FloatingActionButton fab_share;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instagram);

        iv_pic = (ImageView)findViewById(R.id.iv_instagram);
        fab_takePic = (FloatingActionButton)findViewById(R.id.fab_instagram_take_pic);
        fab_rePic = (FloatingActionButton)findViewById(R.id.fab_instagram_rePic);
        fab_instagram = (FloatingActionButton)findViewById(R.id.fab_instagram_instagram);
        fab_share = (FloatingActionButton)findViewById(R.id.fab_instagram_share);

        fab_rePic.setVisibility(View.GONE);
        fab_instagram.setVisibility(View.GONE);
        fab_share.setVisibility(View.GONE);

        fab_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPic();
            }
        });
        fab_rePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPic();
            }
        });
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: compartir cosas via intents https://guides.codepath.com/android/Sharing-Content-with-Intents
                //TODO: lista tipos MIME http://www.sitepoint.com/web-foundations/mime-types-complete-list/
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                File photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Instagram", photoname+".jpg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photo));
                startActivity(Intent.createChooser(shareIntent, "Compartir imagen..."));
            }
        });
        fab_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instagramIntent = new Intent(Intent.ACTION_SEND);
                instagramIntent.setType("image/jpg");
                File photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Instagram", photoname+".jpg");
                Uri uri = Uri.fromFile(photo);
                instagramIntent.putExtra(Intent.EXTRA_STREAM, uri);
                //A partir de ahora viene lo nuevo
                //TODO: compartirlo con la app de instagram http://stackoverflow.com/a/32742394
                instagramIntent.setPackage("com.instagram.android");
                PackageManager packManager = getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(instagramIntent,  PackageManager.MATCH_DEFAULT_ONLY);
                boolean resolved = false;
                for(ResolveInfo resolveInfo: resolvedInfoList){
                    if(resolveInfo.activityInfo.packageName.startsWith("com.instagram.android")){
                        instagramIntent.setClassName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }
                if(resolved){
                    startActivity(instagramIntent);
                }else{
                    Toast.makeText(Instagram.this, "La aplicación de Instagram no está instalada en el dispositivo.", Toast.LENGTH_LONG).show();
                    //TODO: llevar al market http://stackoverflow.com/a/16299999
                    instagramIntent = new Intent(Intent.ACTION_VIEW);
                    instagramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    instagramIntent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
                    startActivity(instagramIntent);
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
                    fab_takePic.setVisibility(View.GONE);
                    fab_rePic.setVisibility(View.VISIBLE);
                    fab_instagram.setVisibility(View.VISIBLE);
                    fab_share.setVisibility(View.VISIBLE);
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView = (ImageView) findViewById(R.id.iv_instagram);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr,selectedImage);
                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    private void newPic() {
        photoname = generatePicName();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory() + "/Cafeteria RUGP/Instagram", photoname+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private String generatePicName() {
        StringBuilder sb = new StringBuilder();
        Calendar now = Calendar.getInstance();
        int dia = now.get(Calendar.DAY_OF_MONTH);
        int mes = now.get(Calendar.MONTH) + 1;;
        int year = now.get(Calendar.YEAR);
        int hora = now.get(Calendar.HOUR);
        int minuto = now.get(Calendar.MINUTE);
        int segundo = now.get(Calendar.SECOND);
        sb.append("PIC_");
        sb.append(String.valueOf(dia));
        sb.append(String.valueOf(mes));
        sb.append(String.valueOf(year));
        sb.append("_");
        sb.append(String.valueOf(hora));
        sb.append(String.valueOf(minuto));
        sb.append(String.valueOf(segundo));
        return sb.toString();
    }
}
