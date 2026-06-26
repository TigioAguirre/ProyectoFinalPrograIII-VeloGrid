package com.veloGrid.clasesBase;

public class Usuario {
    /**Declaracion de Atributos*/
    private int idUsuario;
    private String nombreUsuario;
    private String contraseña;
    private Boolean usrRegistrado = false;
    private String nombreCompleto;
    private NivelExperiencia nivelExperiencia;
    private PreferenciaRuta preferenciaRuta;
    private float horasMovimiento;
    private float kmRecorridos;
    private float desnivelPosAcum;
    private TipoBicicleta tipoBicicleta;
    private boolean esAdmin = false;
    private String rutaActualNodos = "";
    private String rutaActualAristas = "";
    private String rutaActual = "";
    private String historialAuxiliar = "";
    private String historial = "";
    /**Desarrollo de Constructores*/
    public Usuario(int idUsuario, String nombreUsuario, String contraseña) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.usrRegistrado = false;
    }
    public Usuario(int idUsuario, String nombreUsuario, String contraseña, Boolean usrRegistrado,
                   String nombreCompleto, NivelExperiencia nivelExperiencia, PreferenciaRuta preferenciaRuta,
                   float horasMovimiento, float kmRecorridos, float desnivelPosAcum, TipoBicicleta tipoBicicleta) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.usrRegistrado = usrRegistrado;
        this.nombreCompleto = nombreCompleto;
        this.nivelExperiencia = nivelExperiencia;
        this.preferenciaRuta = preferenciaRuta;
        this.horasMovimiento = horasMovimiento;
        this.kmRecorridos = kmRecorridos;
        this.desnivelPosAcum = desnivelPosAcum;
        this.tipoBicicleta = tipoBicicleta;
    }
    /**Desarrollo de Getters y Setters*/
    public String getRutaActualNodos() { return rutaActualNodos; }
    public void setRutaActualNodos(String rutaActualNodos) { this.rutaActualNodos = rutaActualNodos; }

    public String getRutaActualAristas() { return rutaActualAristas; }
    public void setRutaActualAristas(String rutaActualAristas) { this.rutaActualAristas = rutaActualAristas; }

    public String getRutaActual() { return rutaActual; }
    public void setRutaActual(String rutaActual) { this.rutaActual = rutaActual; }

    public String getHistorialAuxiliar() { return historialAuxiliar; }
    public void setHistorialAuxiliar(String historialAuxiliar) { this.historialAuxiliar = historialAuxiliar; }

    public String getHistorial() { return historial; }
    public void setHistorial(String historial) { this.historial = historial; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public Boolean getUsrRegistrado() { return usrRegistrado; }
    public void setUsrRegistrado(Boolean usrRegistrado) { this.usrRegistrado = usrRegistrado; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) { this.nivelExperiencia = nivelExperiencia; }

    public PreferenciaRuta getPreferenciaRuta() { return preferenciaRuta; }
    public void setPreferenciaRuta(PreferenciaRuta preferenciaRuta) { this.preferenciaRuta = preferenciaRuta; }

    public float getHorasMovimiento() { return horasMovimiento; }
    public void setHorasMovimiento(float horasMovimiento) { this.horasMovimiento = horasMovimiento; }

    public float getKmRecorridos() { return kmRecorridos; }
    public void setKmRecorridos(float kmRecorridos) { this.kmRecorridos = kmRecorridos; }

    public float getDesnivelPosAcum() { return desnivelPosAcum; }
    public void setDesnivelPosAcum(float desnivelPosAcum) { this.desnivelPosAcum = desnivelPosAcum; }

    public TipoBicicleta getTipoBicicleta() { return tipoBicicleta; }
    public void setTipoBicicleta(TipoBicicleta tipoBicicleta) { this.tipoBicicleta = tipoBicicleta; }

    public boolean EsAdmin() { return esAdmin; }

    public void setEsAdmin(boolean esAdmin) { this.esAdmin = esAdmin; }
}