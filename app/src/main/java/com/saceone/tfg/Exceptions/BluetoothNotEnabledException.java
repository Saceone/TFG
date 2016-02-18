package com.saceone.tfg.Exceptions;

public class BluetoothNotEnabledException extends Exception {
    public BluetoothNotEnabledException() {
        super();
    }

    public BluetoothNotEnabledException(String detailMessage) {
        super(detailMessage);
    }

    public BluetoothNotEnabledException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BluetoothNotEnabledException(Throwable throwable) {
        super(throwable);
    }
}
