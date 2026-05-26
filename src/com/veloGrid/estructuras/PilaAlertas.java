package com.veloGrid.estructuras;

import com.veloGrid.modelo.Incidente;

/**
 * Estructura de Datos Dinámica: Pila (Stack)
 * Funciona bajo el principio LIFO (Last In, First Out).
 */
public class PilaAlertas {

    private NodoAlerta tope; // El elemento más reciente (arriba de la pila)
    private int tamano;

    public PilaAlertas() {
        this.tope = null;
        this.tamano = 0;
    }

    // --- OPERACIONES DE LA PILA ---

    // PUSH: Agrega un nuevo incidente a la cima de la pila
    public void agregarAlerta(Incidente incidente) {
        NodoAlerta nuevoNodo = new NodoAlerta(incidente);

        if (!estaVacia()) {
            // Usamos el Setter: El nodo debajo del nuevo será el que antes era el tope
            nuevoNodo.setAbajo(tope);
        }
        tope = nuevoNodo;
        tamano++;
    }

    // POP: Atiende (elimina y devuelve) el incidente más reciente
    public Incidente atenderUltimaAlerta() {
        if (estaVacia()) {
            return null;
        }

        // Usamos el Getter para obtener el dato
        Incidente alertaAtendida = tope.getIncidente();

        // El nuevo tope es el que estaba debajo
        tope = tope.getAbajo();
        tamano--;

        return alertaAtendida;
    }

    public boolean estaVacia() {
        return tope == null;
    }

    // Método para la interfaz gráfica: devuelve un arreglo en orden de la cima a la base
    public Incidente[] obtenerHistorial() {
        Incidente[] arreglo = new Incidente[tamano];
        NodoAlerta actual = tope;
        int indice = 0;

        while (actual != null) {
            arreglo[indice] = actual.getIncidente();
            actual = actual.getAbajo(); // Avanzamos al nodo de abajo usando el Getter
            indice++;
        }
        return arreglo;
    }
}