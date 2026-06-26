package com.veloGrid.clasesBase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Incidente {

    /**Declaracion de Atributos*/
    private int idIncidente;
    private TipoIncidente tipoIncidente;
    private int idNodoOrigen;
    private int idNodoDestino;
    private String gravedad;
    private String descripcion;
    private String usuarioReporta;
    private String fechaReporte;
    private String estado;
    private int solicitudesEliminacion;
    /**Desarrollo de Constructores */
    public Incidente(int idIncidente, int idNodoOrigen, int idNodoDestino, TipoIncidente tipoIncidente,
                     String gravedad, String descripcion, String usuarioReporta,
                     String fechaReporte, String estado, int solicitudesEliminacion) {
        this.idIncidente = idIncidente;
        this.idNodoOrigen = idNodoOrigen;
        this.idNodoDestino = idNodoDestino;
        this.tipoIncidente = tipoIncidente;
        this.gravedad = gravedad;
        this.descripcion = descripcion;
        this.usuarioReporta = usuarioReporta;
        this.fechaReporte = fechaReporte;
        this.estado = estado;
        this.solicitudesEliminacion = solicitudesEliminacion;
    }
    public Incidente(int idIncidente, int idNodoOrigen, int idNodoDestino, TipoIncidente tipoIncidente,
                     String gravedad, String descripcion, String usuarioReporta) {
        this.idIncidente = idIncidente;
        this.idNodoOrigen = idNodoOrigen;
        this.idNodoDestino = idNodoDestino;
        this.tipoIncidente = tipoIncidente;
        this.gravedad = gravedad;
        this.descripcion = descripcion;
        this.usuarioReporta = usuarioReporta;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");//Registro de cuando se levantó el reporte del incidente
        this.fechaReporte = LocalDateTime.now().format(formato);
        this.estado = "ACTIVO";
        this.solicitudesEliminacion = 0;
    }
    /**Desarrollo de Getters y Setters */
    public int getIdIncidente() { return idIncidente; }
    public void setIdIncidente(int idIncidente) { this.idIncidente = idIncidente; }

    public TipoIncidente getTipoIncidente() { return tipoIncidente; }
    public void setTipoIncidente(TipoIncidente tipoIncidente) { this.tipoIncidente = tipoIncidente; }

    public int getIdNodoOrigen() { return idNodoOrigen; }
    public void setIdNodoOrigen(int idNodoOrigen) { this.idNodoOrigen = idNodoOrigen; }

    public int getIdNodoDestino() { return idNodoDestino; }
    public void setIdNodoDestino(int idNodoDestino) { this.idNodoDestino = idNodoDestino; }

    public String getGravedad() { return gravedad; }
    public void setGravedad(String gravedad) { this.gravedad = gravedad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUsuarioReporta() { return usuarioReporta; }
    public void setUsuarioReporta(String usuarioReporta) { this.usuarioReporta = usuarioReporta; }

    public String getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(String fechaReporte) { this.fechaReporte = fechaReporte; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getSolicitudesEliminacion() { return solicitudesEliminacion; }
    public void setSolicitudesEliminacion(int solicitudesEliminacion) { this.solicitudesEliminacion = solicitudesEliminacion; }
    /**Desarrollo de Métodos Propios de Java*/
    @Override
    public String toString() {
        return "Incidente #" + idIncidente + " [" + gravedad + "] - " + tipoIncidente +
                " en Ruta (" + idNodoOrigen + " -> " + idNodoDestino + ") - Votos p/eliminar: " + solicitudesEliminacion;
    }
    /**Desarrollo de Métodos Propios del desarrollador */
    public void registrarSolicitudEliminacion() {
        this.solicitudesEliminacion++;
    }
    public void atenderIncidente() {
        this.estado = "ATENDIDO";
    }
}