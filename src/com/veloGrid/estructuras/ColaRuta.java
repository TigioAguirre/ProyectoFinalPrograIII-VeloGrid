package com.veloGrid.estructuras;

import com.veloGrid.modelo.ParadaCiclovia;

public class ColaRuta {

    private NodoPila frente;
    private NodoPila fin;
    private int tamano;

    public ColaRuta() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    public void agregarParada(ParadaCiclovia parada) {
        NodoPila nuevoNodo = new NodoPila(parada);

        if (estaVacia()) {
            frente = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
        }
        fin = nuevoNodo;
        tamano++;
    }

    public ParadaCiclovia completarSiguienteParada() {
        if (estaVacia()) {
            return null;
        }

        ParadaCiclovia paradaCompletada = frente.getParada();

        frente = frente.getSiguiente();

        if (frente == null) {
            fin = null;
        }

        tamano--;
        return paradaCompletada;
    }

    public ParadaCiclovia verProximaParada() {
        if (estaVacia()) {
            return null;
        }
        return frente.getParada();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public void vaciarRuta() {
        frente = null;
        fin = null;
        tamano = 0;
    }

    public int getTamano() {
        return tamano;
    }

    public ParadaCiclovia[] obtenerRecorridoCompleto() {
        ParadaCiclovia[] arreglo = new ParadaCiclovia[tamano];
        NodoPila actual = frente;
        int indice = 0;

        while (actual != null) {
            arreglo[indice] = actual.getParada();
            actual = actual.getSiguiente();
            indice++;
        }

        return arreglo;
    }
}