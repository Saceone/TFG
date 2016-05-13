package com.saceone.tfg.Utils;

/**
 * Esta interfaz representa un objeto "callback" que nos permite llamar métodos de la activity
 *
 * No es estrictamente necesario, pues en el run() de la clase BluetoothHelper podría hacer:
 * (ClaseQueRecibeDatos1)getActivity().call(data);
 * (ClaseQueREcibeDatos2)getActivity().call(data);
 * ...
 *
 * pero usando la interfaz no hay que incluir estos cast's a cada activity, vale para todas
 *
 * Para usarla, es necesario declarar en la clase un "implements ICallback"
 * e incluir el método public void call (String s){
 *     //do something with data received
 * }
 *
 */
public interface ICallback {
    void call(String s);
}
