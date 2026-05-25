//Remigio Estuvo Aqui//Test//Test2
public class Usuario {
    /** Definición de Atributos de Clase*/
    private int idUsuario;
    private String nombreUsuario;
    private NivelExperiencia nivelExperiencia;
    /** Creación de Constructores*/
    public Usuario(int idUsuario, String nombreUsuario, NivelExperiencia nivelExperiencia) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.nivelExperiencia = nivelExperiencia;
    }
    /** Getters and Setters*/
    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    public NivelExperiencia getNivelExperiencia() {
        return nivelExperiencia;
    }
    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }
    /** Métodos Própios de Java*/
    @Override
    public String toString() {
        return "Usuario "+ idUsuario + " - Nombre de Usuario: " + nombreUsuario + " - Nivel de Experiencia: " + nivelExperiencia ;
    }
    /** Métodos Própios del desarrollador*/
    public void planificarRuta(){}
    public void reportarIncidente(){}

}
