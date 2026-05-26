package com.veloGrid.modelo;

public class Incidente {
    /** Definición de Atributos de Clase*/
    private int idIncidente;
    private TipoIncidente tipoIncidente;
    private Coordenada ubicacion;
    /** Creación de Constructores*/
    public Incidente(int idIncidente, TipoIncidente tipoIncidente, Coordenada ubicacion) {
        this.idIncidente = idIncidente;
        this.tipoIncidente = tipoIncidente;
        this.ubicacion = ubicacion;
    }
    /** Getters and Setters*/
    public int getIdIncidente() {
        return idIncidente;
    }
    public void setIdIncidente(int idIncidente) {
        this.idIncidente = idIncidente;
    }
    public TipoIncidente getTipoIncidente() {
        return tipoIncidente;
    }
    public void setTipoIncidente(TipoIncidente tipoIncidente) {
        this.tipoIncidente = tipoIncidente;
    }
    public Coordenada getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(Coordenada ubicacion) {
        this.ubicacion = ubicacion;
    }
    /** Métodos Própios de Java*/
    @Override
    public String toString() {
        return "Incidente #" + idIncidente + " - Tipo de Incidente: " + tipoIncidente + " - Ubicacion [X,Y]: (" + ubicacion.getPosX() + "," + ubicacion.getPosY() + ")";}
    /** Métodos Própios del desarrollador*/
    public void actualizarEstado(){}
    public void notificarUsuario(){}

}
