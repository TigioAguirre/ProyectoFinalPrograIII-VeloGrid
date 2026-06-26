package com.veloGrid.estructuras;

import com.veloGrid.clasesBase.Coordenada;
import com.veloGrid.clasesBase.AristaRuta;
import java.util.ArrayList;
import java.util.List;

public class Nodo {
    /**Declaracion de Atributos*/
    private int idNodo;
    private String nombreNodo;
    private Coordenada coordenada;
    private boolean esCiclovia;
    private List<AristaRuta> rutasSalientes;
    private boolean esVerificado;
    private String quienAgrego;
    /**Desarrollo de Constructores*/
    public Nodo(int idNodo, String nombreNodo, Coordenada coordenada, boolean esCiclovia, String quienAgrego, boolean esVerificado) {
        this.idNodo = idNodo;
        this.nombreNodo = nombreNodo;
        this.coordenada = coordenada;
        this.esCiclovia = esCiclovia;
        this.quienAgrego = quienAgrego;
        this.esVerificado = esVerificado;
        this.rutasSalientes = new ArrayList<>();
    }
    public Nodo(int idNodo, String nombreNodo, Coordenada coordenada, boolean esCiclovia, String quienAgrego) {
        this.idNodo = idNodo;
        this.nombreNodo = nombreNodo;
        this.coordenada = coordenada;
        this.esCiclovia = esCiclovia;
        this.quienAgrego = quienAgrego;
        this.esVerificado = false;
        this.rutasSalientes = new ArrayList<>();
    }
    /**Desarrollo de Getters y Setters*/
    public int getIdNodo() { return idNodo; }
    public void setIdNodo(int idNodo) { this.idNodo = idNodo; }

    public String getNombreNodo() { return nombreNodo; }
    public void setNombreNodo(String nombreNodo) { this.nombreNodo = nombreNodo; }

    public Coordenada getCoordenada() { return coordenada; }
    public void setCoordenada(Coordenada coordenada) { this.coordenada = coordenada; }

    public boolean isEsCiclovia() { return esCiclovia; }
    public void setEsCiclovia(boolean esCiclovia) { this.esCiclovia = esCiclovia; }

    public String getQuienAgrego() { return quienAgrego; }
    public void setQuienAgrego(String quienAgrego) { this.quienAgrego = quienAgrego; }

    public boolean isEsVerificado() { return esVerificado; }
    public void setEsVerificado(boolean esVerificado) { this.esVerificado = esVerificado; }

    public List<AristaRuta> getRutasSalientes() { return rutasSalientes; }
    /**Desarrollo de Metodos propios de Java*/
    @Override
    public String toString() {
        return this.nombreNodo;
    }
    /**Desarrollo de Metodos Propios del Desarrollador*/
    public void agregarRuta(Nodo destino, float pendiente, String agregadaPor, boolean esVerificada) {
        AristaRuta nuevaRuta = new AristaRuta(this, destino, pendiente, agregadaPor, esVerificada);
        this.rutasSalientes.add(nuevaRuta);
    }
}