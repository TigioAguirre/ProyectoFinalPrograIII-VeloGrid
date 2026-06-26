package com.veloGrid.estructuras;

import com.veloGrid.clasesBase.Incidente;
import java.util.LinkedList;


public class PilaAlertas {
    /**Declaracion de Atributos*/
    private LinkedList<Incidente> pila;
    /**Desarrollo de Constructores*/
    public PilaAlertas() {
        this.pila = new LinkedList<>();
    }
    /**Metodos Propios del Desarrollador*/
    public void agregarAlerta(Incidente incidente) {
        pila.push(incidente);
    }
    public Incidente[] obtenerHistorial() {
        return pila.toArray(new Incidente[0]);//Lo transformamos a Array para la interfaz grafica
    }
}