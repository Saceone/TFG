package com.saceone.tfg.Utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.saceone.tfg.Exceptions.BluetoothNotAvaliableException;
import com.saceone.tfg.Exceptions.BluetoothNotEnabledException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothHelper {
    // Instancia única de la clase (patrón Singleton)
    private static BluetoothHelper mBluetoothHelper;
    // BluetoothSocket
    private BluetoothSocket mBluetoothSocket;
    // Output y Input streams
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    // Constantes
    public static final String BT_CONNECT = "0";
    public static final String WAKEREADER = "1";
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
        if (mBluetoothHelper == null) {
            // Necesitamos la activity actual para cuando tengamos que iniciar diálogos (newMenu)
            mBluetoothHelper = new BluetoothHelper(activity);
        }
        return mBluetoothHelper;
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

    public boolean checkBluetoothState() throws BluetoothNotEnabledException,
                                                BluetoothNotAvaliableException {
        if (getBluetoothAdapter() == null) {
            return false;
        } else {
            return getBluetoothAdapter().isEnabled();
        }
    }

    public void connect() throws BluetoothNotEnabledException, BluetoothNotAvaliableException,
                                 IOException {
        Set<BluetoothDevice> pairedDevices = getBluetoothAdapter().getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mBluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                mBluetoothSocket.connect();
                mOutputStream = mBluetoothSocket.getOutputStream();
                mInputStream = mBluetoothSocket.getInputStream();
                break;
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
        mOutputStream.write(data.getBytes());
    }

    private void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10;

        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !mStopWorker) {
                    try {
                        int bytesAvailable = mInputStream.available();

                        if (bytesAvailable > 0) {
                            byte[] packedBytes = new byte[bytesAvailable];
                            mInputStream.read(packedBytes);

                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packedBytes[i];

                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[mReadBufferPosition];
                                    System.arraycopy(mReadBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    mReadBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch(data){
                                                case "E9A05D35":
                                                case "481C3BE":
                                                    // Llama al método call() de MainActivity
                                                    // (o cualquier activity actual que se haya
                                                    // pasado a getInstance)
                                                    mCallback.call();
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    });
                                } else {
                                    mReadBuffer[mReadBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException e) {
                        mStopWorker = true;
                    }
                }
            }
        });

        mWorkerThread.start();
    }
}
