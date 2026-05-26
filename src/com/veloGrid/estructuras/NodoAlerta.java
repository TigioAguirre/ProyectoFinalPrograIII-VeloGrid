package com.veloGrid.estructuras;

import com.veloGrid.modelo.Incidente;

/**
 * Clase externa NodoAlerta.
 * Funciona como el eslabón para la estructura Pila de incidentes.
 */
public class NodoAlerta {
    private Incidente incidente; // El dato a guardar
    private NodoAlerta abajo;    // Puntero al elemento que quedó debajo en la pila

    public NodoAlerta(Incidente incidente) {
        this.incidente = incidente;
        this.abajo = null;
    }

    // Getters y Setters
    public Incidente getIncidente() {
        return incidente;
    }

    public void setIncidente(Incidente incidente) {
        this.incidente = incidente;
    }

    public NodoAlerta getAbajo() {
        return abajo;
    }

    public void setAbajo(NodoAlerta abajo) {
        this.abajo = abajo;
    }
}