public class Coordenada {
    /** Definición de Atributos de Clase*/
    private double posX;
    private double posY;
    /** Creación de Constructores*/
    public Coordenada(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }
    /** Getters and Setters*/
    public double getPosX() {
        return posX;
    }
    public void setPosX(double posX) {
        this.posX = posX;
    }
    public double getPosY() {
        return posY;
    }
    public void setPosY(double posY) {
        this.posY = posY;
    }
    /** Métodos Própios de Java*/
    @Override
    public String toString() {
        return "Coordenada: ("+ posX + "," + posY+ ")"  ;
    }
    /** Métodos Própios del desarrollador*/
    public double distanciaHacia(Coordenada coordenada){
        double difY = this.posY - coordenada.posY;
        double difX = this.posX - coordenada.posX;
        return Math.sqrt((difX * difX) + (difY * difY));
    }

}
