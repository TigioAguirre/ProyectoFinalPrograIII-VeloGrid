package com.veloGrid.clasesBase;

import com.veloGrid.estructuras.Nodo;

public class AristaRuta {
    /**Declaracion de Atributos*/
    private boolean esVerificada = false;
    private String agregadaPor;
    private String nombreRuta;
    private Incidente incidenteActivo;
    private Nodo nodoInicio;
    private Nodo nodoFinal;
    private boolean esCiclovia;
    private float pendienteMedia;
    private double distancia;
    private float ponderacionRapidez;
    private float ponderacionCiclovia;
    private float ponderacionPendiente;
    /**Desarrollo de Constructores*/
    public AristaRuta(Nodo nodoInicio, Nodo nodoFinal, float pendienteMedia, String agregadaPor) {
        this(nodoInicio, nodoFinal, pendienteMedia, agregadaPor, false);
    }
    public AristaRuta(Nodo nodoInicio, Nodo nodoFinal, float pendienteMedia, String agregadaPor, boolean esVerificada) {
        this.nodoInicio = nodoInicio;
        this.nodoFinal = nodoFinal;
        this.pendienteMedia = pendienteMedia;
        this.agregadaPor = agregadaPor;
        this.esVerificada = esVerificada;
        this.esCiclovia = nodoInicio.isEsCiclovia() && nodoFinal.isEsCiclovia();
        this.nombreRuta = "De " + nodoInicio.getNombreNodo() + " a " + nodoFinal.getNombreNodo();
        this.incidenteActivo = null;
        double deltaX = nodoFinal.getCoordenada().getPosX() - nodoInicio.getCoordenada().getPosX();
        double deltaY = nodoFinal.getCoordenada().getPosY() - nodoInicio.getCoordenada().getPosY();
        double distanciaCalculada = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        this.distancia = (float) distanciaCalculada;
    }
    /**Desarrollo de Getters y Setters*/
    public String getNombreRuta() { return nombreRuta; }
    public void setNombreRuta(String nombreRuta) { this.nombreRuta = nombreRuta; }
    public Incidente getIncidenteActivo() { return incidenteActivo; }
    public void setIncidenteActivo(Incidente incidenteActivo) {
        this.incidenteActivo = incidenteActivo;
    }
    public float getPonderacionRapidez() { return ponderacionRapidez; }
    public float getPonderacionCiclovia() { return ponderacionCiclovia; }
    public float getPonderacionPendiente() { return ponderacionPendiente; }
    public Nodo getNodoInicio() { return nodoInicio; }
    public Nodo getNodoFinal() { return nodoFinal; }
    public double getDistancia() { return distancia; }
    public float getPendienteMedia() { return pendienteMedia; }
    public boolean isEsCiclovia() { return esCiclovia; }
    public boolean isEsVerificada() { return esVerificada; }
    public void setEsVerificada(boolean esVerificada) { this.esVerificada = esVerificada; }
    public String getAgregadaPor() { return agregadaPor; }
    /**Desarrollo de Metodos Propios del Desarrollador*/
    public void calcularPonderaciones(float distMin, float distMax, float pendMin, float pendMax) {
        double distNormalizada = 0;
        if (distMax - distMin > 0) {
            distNormalizada = (this.distancia - distMin) / (distMax - distMin);
        }
        double pendNormalizada = 0;
        if (pendMax - pendMin > 0) {
            pendNormalizada = (this.pendienteMedia - pendMin) / (pendMax - pendMin);
        }

        int penalizacionCiclovia = this.esCiclovia ? 0 : 1;
        this.ponderacionRapidez = (float) ((distNormalizada * 0.60) + (penalizacionCiclovia * 0.10) + (pendNormalizada * 0.30));
        this.ponderacionCiclovia = (float) ((penalizacionCiclovia * 0.80) + (distNormalizada * 0.20));
        this.ponderacionPendiente = (float) ((distNormalizada * 0.15) + (pendNormalizada * 0.80) + (penalizacionCiclovia * 0.05));
        actualizarPenalizacionIncidente();
    }
    public void actualizarPenalizacionIncidente() {
        boolean hayIncidente = (this.incidenteActivo != null && this.incidenteActivo.getEstado().equals("ACTIVO"));
        if (hayIncidente && this.ponderacionRapidez < 100) {
            this.ponderacionRapidez += 1000;
            this.ponderacionCiclovia += 1000;
            this.ponderacionPendiente += 1000;
        }
        else if (!hayIncidente && this.ponderacionRapidez >= 1000) {
            this.ponderacionRapidez -= 1000;
            this.ponderacionCiclovia -= 1000;
            this.ponderacionPendiente -= 1000;
        }
    }
    public boolean verificarYActualizarLimites(float[] limites) {
        boolean requiereActualizarCSV = false;
        if (this.pendienteMedia > limites[0]) {
            limites[0] = this.pendienteMedia;
            requiereActualizarCSV = true;
        }
        if (this.pendienteMedia < limites[1]) {
            limites[1] = this.pendienteMedia;
            requiereActualizarCSV = true;
        }
        if (this.distancia > limites[2]) {
            limites[2] = (float) this.distancia;
            requiereActualizarCSV = true;
        }
        if (this.distancia < limites[3]) {
            limites[3] = (float) this.distancia;
            requiereActualizarCSV = true;
        }
        return requiereActualizarCSV;
    }
}