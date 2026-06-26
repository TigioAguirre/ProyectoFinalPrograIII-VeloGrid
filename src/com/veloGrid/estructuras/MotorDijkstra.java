package com.veloGrid.estructuras;

import com.veloGrid.clasesBase.AristaRuta;
import com.veloGrid.clasesBase.PreferenciaRuta;
import java.util.*;

public class MotorDijkstra {
    /**Declaracion de Atributos(Clase GestorNodos a usar)*/
    private GestorNodos gestorNodos;
    /**Desarrollo de Constructores*/
    public MotorDijkstra(GestorNodos gestorNodos) {
        this.gestorNodos = gestorNodos;
    }
    /**Auxiliares*/
    private static class NodoDistancia {
        Nodo nodo;
        float distancia;
        NodoDistancia(Nodo n, float d) {
            this.nodo = n;
            this.distancia = d;
        }
    }
    /**Desarrollo de Metodos Propios del Desarrollador*/
    //Dijkstra
    private List<AristaRuta> Dijkstra(Nodo origen, Nodo destino, PreferenciaRuta preferencia) {
        Map<Integer, Float> distancias = new HashMap<>(); //Historial de Distancias para la mas corta
        Map<Integer, AristaRuta> aristaPrevia = new HashMap<>(); //Arista por la que llegamos al camino mas corto
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distancia));// Cola de prioridad que ordena automáticamente por la distancia acumulada más baja
        for (Integer id : gestorNodos.getMapaNodos().keySet()) {// Inicializar distancias al infinito
            distancias.put(id, Float.MAX_VALUE);
        }
        distancias.put(origen.getIdNodo(), 0f);
        cola.add(new NodoDistancia(origen, 0f));
        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            Nodo nodoActual = actual.nodo;
            if (nodoActual.getIdNodo() == destino.getIdNodo()) {// Si llegamos al destino, detenemos la búsqueda de esta arista
                break;
            }
            if (actual.distancia > distancias.get(nodoActual.getIdNodo())) {// Si procesamos un nodo que ya encontramos por un camino más corto, lo ignoramos
                continue;
            }
            for (AristaRuta arista : nodoActual.getRutasSalientes()) {// Explorar todos los nodos adyacentes
                Nodo vecino = arista.getNodoFinal();
                float peso = obtenerPeso(arista, preferencia);// Se lee la ponderación de la rutaArista según las preferencias del perfil del usuario
                float nuevaDistancia = distancias.get(nodoActual.getIdNodo()) + peso;
                if (nuevaDistancia < distancias.get(vecino.getIdNodo())) {
                    distancias.put(vecino.getIdNodo(), nuevaDistancia);
                    aristaPrevia.put(vecino.getIdNodo(), arista);
                    cola.add(new NodoDistancia(vecino, nuevaDistancia));
                }
            }
        }
        //Creamos el orden de la ruta a seguir
        List<AristaRuta> camino = new ArrayList<>();
        Integer actualId = destino.getIdNodo();
        while (aristaPrevia.containsKey(actualId)) {//Añadimos la nueva arista a seguir
            AristaRuta arista = aristaPrevia.get(actualId);
            camino.add(arista);
            actualId = arista.getNodoInicio().getIdNodo();
        }
        Collections.reverse(camino);// Lo invertimos porque lo construimos de destino a origen
        if (camino.isEmpty() && origen.getIdNodo() != destino.getIdNodo()) {// Si no hay una rutaArista entre los nodos de origen y destino devolvemos null
            return null;
        }
        return camino;
    }
    private float obtenerPeso(AristaRuta arista, PreferenciaRuta preferencia) {
        if (preferencia == null) return arista.getPonderacionRapidez(); //Retornamos la preferencia de rapidez en caso que algo haya salido mal(evitando NUllPointerException)
        switch (preferencia) {
            case CICLOVIAS:
                return arista.getPonderacionCiclovia();
            case EVITAR_PENDIENTES:
                return arista.getPonderacionPendiente();
            case RAPIDEZ:
            default:
                return arista.getPonderacionRapidez();
        }
    }
    public List<AristaRuta> calcularRutaCompleta(List<Nodo> paradasSeleccionadas, PreferenciaRuta preferencia) {
        if (paradasSeleccionadas == null || paradasSeleccionadas.size() < 2) {//Validacion para que se seleccionen dos paradas como minimo
            return new ArrayList<>(); // Retorna vacío si no hay suficientes paradas
        }
        List<AristaRuta> rutaFinal = new ArrayList<>();//Creamos la lista donde se va a guardar ruta completa
        for (int i = 0; i < paradasSeleccionadas.size() - 1; i++) {// En vez de A-->D, hacemos A->B|B->C|C->D
            Nodo origen = paradasSeleccionadas.get(i);
            Nodo destino = paradasSeleccionadas.get(i + 1);
            List<AristaRuta> arista = Dijkstra(origen, destino, preferencia);
            if (arista == null || arista.isEmpty()) {
                return null; // No hay aristas que conecten esos nodos
            }
            rutaFinal.addAll(arista);
        }
        return rutaFinal;
    }

}