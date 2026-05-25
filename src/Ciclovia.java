public class Ciclovia {
    /** Definición de Atributos de Clase*/
    private int idCiclovia;
    private EstadoVia estadoVia;
    /** Creación de Constructores*/
    public Ciclovia(int idCiclovia, EstadoVia estadoVia) {
        this.idCiclovia = idCiclovia;
        this.estadoVia = estadoVia;
    }
    /** Getters and Setters*/
    public int getIdCiclovia() {
        return idCiclovia;
    }
    public void setIdCiclovia(int idCiclovia) {
        this.idCiclovia = idCiclovia;
    }
    public EstadoVia getEstadoVia() {
        return estadoVia;
    }
    public void setEstadoVia(EstadoVia estadoVia) {
        this.estadoVia = estadoVia;
    }
    /** Métodos Própios de Java*/
    @Override
    public String toString() {
        return "Ciclovia #" + idCiclovia + " - Estado de la Via: " + estadoVia ;
    }
    /** Métodos Própios del desarrollador*/
    public void verificarEstado(){}
}
