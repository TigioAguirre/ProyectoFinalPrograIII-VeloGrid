package com.veloGrid.estructuras;

import com.veloGrid.clasesBase.Coordenada;

public class PuntoInteres extends Nodo {
    /**Declaración de atributos*/
    private String descripcion;
    private String horario;
    private String ultimoEditor;
    /**Desarrollo de Constructores*/
    public PuntoInteres(int idNodo, String nombreNodo, Coordenada coordenada, boolean esCiclovia,
                        String quienAgrego, boolean esVerificado, String descripcion,
                        String horario, String ultimoEditor) {
        // Iniciamos los atributos de la clase padre
        super(idNodo, nombreNodo, coordenada, esCiclovia, quienAgrego, esVerificado);
        //Iniciamos los atributos de la clase
        this.descripcion = descripcion;
        this.horario = horario;
        this.ultimoEditor = ultimoEditor;
    }
    public PuntoInteres(int idNodo, String nombreNodo, Coordenada coordenada, boolean esCiclovia,
                        String quienAgrego, String descripcion, String horario) {
        // Iniciamos los atributos de la clase padre
        super(idNodo, nombreNodo, coordenada, esCiclovia, quienAgrego);
        //Iniciamos los atributos de la clase
        this.descripcion = descripcion;
        this.horario = horario;
        this.ultimoEditor = quienAgrego;
    }
    /**Desarrollo de Getters y Setters*/
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public String getUltimoEditor() { return ultimoEditor; }
    public void setUltimoEditor(String ultimoEditor) { this.ultimoEditor = ultimoEditor; }
    @Override
    public String toString() {
        return "(Punto de Interés) " + getNombreNodo();
    }
}