package com.saceone.tfg.Utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.saceone.tfg.Exceptions.BluetoothNotAvaliableException;
import com.saceone.tfg.Exceptions.BluetoothNotEnabledException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothHelper {
    // BluetoothSocket
    private BluetoothSocket mBluetoothSocket;
    // Output y Input streams
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    // Constantes
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String ADDRESS = "98:D3:31:F4:11:80";
    // Variables del handler
    Thread mWorkerThread;
    byte[] mReadBuffer = new byte[1024];
    int mReadBufferPosition = 0;
    volatile boolean mStopWorker = false;
    // Variable de callback para poder llamar al método de la activity
    private ICallback mCallback;


    private BluetoothHelper(Activity activity) {
        // TODO: Inicializar la clase
        mCallback = (ICallback) activity;
    }

    public static BluetoothHelper getInstance(Activity activity) {
        return new BluetoothHelper(activity);
    }

    public BluetoothAdapter getBluetoothAdapter() throws BluetoothNotEnabledException,
                                                         BluetoothNotAvaliableException {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                return bluetoothAdapter;
            } else {
                throw new BluetoothNotEnabledException("El Bluetooth no está activado");
            }
        } else {
            throw new BluetoothNotAvaliableException("El dispositivo Bluetooth no está disponible.");
        }
    }

    public void connect() throws BluetoothNotEnabledException, BluetoothNotAvaliableException,
                                 IOException {

        Set<BluetoothDevice> pairedDevices = getBluetoothAdapter().getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if(device.getAddress().equals(ADDRESS))
                {
                    mBluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    try{
                        mBluetoothSocket.connect();

                    } catch (IOException e) {
                        //TODO: el motivo de este catch (por qué no conecta la primera) está sacado de aquí: http://stackoverflow.com/a/25647197
                        //Viene a decir que <4.2 entra por el .connect() primario, pero a partir de 4.2 el stack cambia y hay que hacerlo de este metodo
                        Log.d("LOG: ", "Error en la conexión primaria: "+e.getMessage());
                        try {
                            Log.d("LOG: ", "Intentando conexión secundaria...");
                            mBluetoothSocket.connect();
                            Log.d("LOG: ", "Conectado");
                        } catch (Exception e2) {
                            Log.d("LOG: ", "No se ha podido establecer la conexión al bluetooh");
                        }
                    break;
                    }
                }
            }
        }
        beginListenForData();
    }

    public void disconnect() throws IOException {
        if (mOutputStream != null) mOutputStream.flush();
        if (mInputStream != null) mInputStream.close();
        mBluetoothSocket.close();
    }

    public void sendData(String message) throws IOException{
        String data = message.concat("$");
        mOutputStream = mBluetoothSocket.getOutputStream();
        mOutputStream.write(data.getBytes());
    }

    private void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        mStopWorker = false;
        mReadBufferPosition = 0;
        mReadBuffer = new byte[1024];
        mWorkerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !mStopWorker)
                {
                    try
                    {
                        mInputStream = mBluetoothSocket.getInputStream();
                        int bytesAvailable = mInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[mReadBufferPosition];
                                    System.arraycopy(mReadBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    mReadBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            mCallback.call(data);
                                        }
                                    });
                                }
                                else
                                {
                                    mReadBuffer[mReadBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        mStopWorker = true;
                    }
                }
            }
        });

        mWorkerThread.start();
    }

}
