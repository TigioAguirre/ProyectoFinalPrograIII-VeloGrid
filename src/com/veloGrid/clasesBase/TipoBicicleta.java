package com.veloGrid.clasesBase;

public enum TipoBicicleta {
    RUTA("media/Ruta.png"),
    GRAVEL("media/Gravel.png"),
    MONTAÑA("media/MTB.png"),
    BMX("media/BMX.png"),
    FIXED_GEAR("media/Fixed.png");
    /**Declaracion de Atributo*///Lo que se desarrolla a continuacion es para poder devolver la ruta de la imagen del tipo de bicicleta
    private String rutaImagen;
    /**Desarrollo de Constructores*/
    TipoBicicleta(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
    /**Desarrollo de Getter*/
    public String getRutaImagen() {
        return rutaImagen;
    }
}