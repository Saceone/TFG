package com.saceone.tfg.Exceptions;

public class BluetoothNotAvaliableException extends Exception {
    public BluetoothNotAvaliableException() {
        super();
    }

    public BluetoothNotAvaliableException(String detailMessage) {
        super(detailMessage);
    }

    public BluetoothNotAvaliableException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BluetoothNotAvaliableException(Throwable throwable) {
        super(throwable);
    }
}
