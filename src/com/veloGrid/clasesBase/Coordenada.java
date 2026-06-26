package com.veloGrid.clasesBase;

public class Coordenada {
    /**Declaracion de Atributos*/
    private double posX;
    private double posY;
    /**Desarrollo de Constructores*/
    public Coordenada(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }
    /**Desarrollo de Getters and Setters*/
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
    /**Desarrollo de Métodos Própios de Java*/
    @Override
    public String toString() {
        return "(" + posX + "," + posY + ")";
    }
}
