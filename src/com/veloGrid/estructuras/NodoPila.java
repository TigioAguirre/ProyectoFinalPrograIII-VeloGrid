package com.veloGrid.estructuras;

import com.veloGrid.modelo.ParadaCiclovia;

public class NodoPila {
    private ParadaCiclovia parada;
    private NodoPila siguiente;

    public NodoPila(ParadaCiclovia parada) {
        this.parada = parada;
        this.siguiente = null;
    }

    public ParadaCiclovia getParada() {
        return parada;
    }

    public void setParada(ParadaCiclovia parada) {
        this.parada = parada;
    }

    public NodoPila getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
}