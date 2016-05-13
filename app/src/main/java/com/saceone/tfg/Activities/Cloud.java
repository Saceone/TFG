package com.saceone.tfg.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cloud extends AppCompatActivity implements OnConnectionFailedListener, ConnectionCallbacks {

    private static final int REQUEST_CODE_CREATOR = 1;
    private static final int REQUEST_CODE_RESOLUTION = 2;

    private static final String DATABASE_NAME = "database.db";

    private GoogleApiClient mGoogleApiClient;
    private File mDbFile;

    private static final int BUF_SZ = 4096;

    MyDB db = new MyDB(Cloud.this);

    Button btn_upload;
    Button btn_download;
    TextView txt_date_local;
    TextView txt_date_drive;

    Date dt_local;
    Date dt_drive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud);

        db = new MyDB(this);
        mDbFile = new File("/data/data/com.saceone.tfg/databases/mibasedatos.db");

        txt_date_local = (TextView)findViewById(R.id.txt_clould_local);
        txt_date_drive = (TextView)findViewById(R.id.txt_cloud_drive);

        btn_upload = (Button)findViewById(R.id.btn_cloud_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog adb = new AlertDialog.Builder(Cloud.this)
                        .setTitle("Subir base de datos")
                        .setMessage("Se actualizará la base de datos en la nube con los datos de la base de datos actual en su dispositivo. ¿Desea proceder?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDb();
                                updateDriveDb(mDbFile);
                                setTextViews();
                                dialog.dismiss();
                            }
                        }).create();
                adb.show();
            }
        });
        btn_download = (Button)findViewById(R.id.btn_cloud_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog adb = new AlertDialog.Builder(Cloud.this)
                        .setTitle("Descargar base de datos")
                        .setMessage("Se actualizará la base de datos de su dispositivo con los datos de la base de datos en la nube. ¿Desea proceder?")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateLocalDb();
                                setTextViews();
                                dialog.dismiss();
                            }
                        }).create();
                adb.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
        setTextViews();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cloud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_cloud_clearLogin) {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CREATOR:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(Cloud.this,"Base de datos subida correctamente.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            Toast.makeText(Cloud.this,"Inicie sesión con Google Drive.",Toast.LENGTH_LONG).show();
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Toast.makeText(Cloud.this,"Error en la conexión con Google Drive.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
       // Toast.makeText(Cloud.this,"Conexión con Google Drive establecida.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Toast.makeText(Cloud.this, "Conexión con Google Drive suspendida", Toast.LENGTH_LONG).show();
    }

    private void setTextViews() {
        dt_local = new Date(mDbFile.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
        txt_date_local.setText("Actualizada a " + sdf.format(dt_local));

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, DATABASE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if (!result.getStatus().isSuccess()) {
                            return;
                        }
                        if (result.getMetadataBuffer().getCount() > 0) {
                            DriveId driveId = result.getMetadataBuffer().get(0).getDriveId();
                            DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, driveId);
                            driveFile.getMetadata(mGoogleApiClient).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                @Override
                                public void onResult(DriveResource.MetadataResult metadataResult) {
                                    dt_drive = metadataResult.getMetadata().getCreatedDate();
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
                                    txt_date_drive.setText("Actualizada a " + sdf.format(dt_drive));
                                }
                            });
                        } else
                            txt_date_drive.setText("No existe una copia de seguridad de la base de datos en la nube.");
                        result.release();
                    }
                });
    }

    private void deleteDb(){
        final String[] driveId = new String[1];
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, DATABASE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if (!result.getStatus().isSuccess()) {
                            return;
                        }
                        if(result.getMetadataBuffer().getCount()>0){
                            driveId[0] = result.getMetadataBuffer().get(0).getDriveId().encodeToString();
                            DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, DriveId.decodeFromString(driveId[0]));
                            driveFile.delete(mGoogleApiClient);
                        }
                        else Toast.makeText(Cloud.this,"No se ha encontrado base de datos que borrar",Toast.LENGTH_LONG).show();
                        result.release();
                    }
                });
        }

    private void updateLocalDb() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, DATABASE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if (!result.getStatus().isSuccess()) {
                            return;
                        }
                        if(result.getMetadataBuffer().getCount()>0){
                            DriveId driveId = result.getMetadataBuffer().get(0).getDriveId();
                            DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, driveId);
                            driveFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                        @Override
                                        public void onResult(DriveApi.DriveContentsResult result) {
                                            if (!result.getStatus().isSuccess()) {
                                                return;
                                            }
                                            DriveContents contents = result.getDriveContents();
                                            InputStream is = contents.getInputStream();
                                            byte[] buffer;
                                            try {
                                                buffer = new byte[is.available()];
                                                int bytesRead;
                                                ByteArrayOutputStream output = new ByteArrayOutputStream();
                                                while ((bytesRead = is.read(buffer)) != -1){
                                                    output.write(buffer, 0, bytesRead);
                                                }
                                                byte[] byteArray = output.toByteArray();
                                                FileOutputStream fos = new FileOutputStream(mDbFile,false); //true = append, false = overwrite
                                                fos.write(byteArray);
                                                fos.close();
                                                Toast.makeText(Cloud.this,"Base de datos descargada correctamente.",Toast.LENGTH_LONG).show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            contents.discard(mGoogleApiClient);
                                        }
                                    });
                            result.release();
                        }
                        else Toast.makeText(Cloud.this,"No se ha encontrado una copia de seguridad en la nube para descargar.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateDriveDb(final File fileToSave) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    Toast.makeText(Cloud.this, "Ha ocurrido un error al preparar los archivos para subir.", Toast.LENGTH_LONG).show();
                    return;
                }
                OutputStream outputStream = result.getDriveContents().getOutputStream();
                byte[] bArray = file2Bytes(fileToSave);
                try {
                    outputStream.write(bArray);
                } catch (IOException e1) {
                    Toast.makeText(Cloud.this, "No ha sido posible subir los archivos.", Toast.LENGTH_LONG).show();
                }
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("application/x-sqlite3")
                        .setTitle("database.db")
                        .build();
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(result.getDriveContents())
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static byte[] file2Bytes(File file) {
        if (file != null) try {
            return is2Bytes(new FileInputStream(file));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return null;
    }

    static byte[] is2Bytes(InputStream is) {
        byte[] buf = null;
        BufferedInputStream bufIS = null;
        if (is != null) try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            bufIS = new BufferedInputStream(is);
            //buf = new byte[BUF_SZ];
            buf = new byte[is.available()];
            int cnt;
            while ((cnt = bufIS.read(buf)) >= 0) {
                byteBuffer.write(buf, 0, cnt);
            }
            buf = byteBuffer.size() > 0 ? byteBuffer.toByteArray() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (bufIS != null) bufIS.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buf;
    }
//*/
}
