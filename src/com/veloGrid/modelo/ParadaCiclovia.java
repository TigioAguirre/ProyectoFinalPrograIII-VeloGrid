package com.veloGrid.modelo;

public enum ParadaCiclovia {
    OFELIA("Terminal La Ofelia"),
    FERNANDEZ_SALVADOR("Fernández Salvador / Parque Inglés"),
    BICENTENARIO("Parque Bicentenario"),
    EL_LABRADOR("Estación El Labrador"),
    ISLA_TORTUGA("Isla Tortuga"),
    ATAHUALPA("Avenida Atahualpa"),
    LA_CAROLINA("Parque La Carolina"),
    LA_PRADERA("La Pradera"),
    GABRIELA_MISTRAL("Plaza Gabriela Mistral"),
    UNIVERSIDAD_CENTRAL("Universidad Central"),
    EL_EJIDO("Parque El Ejido"),
    LA_MAGDALENA("Estación La Magdalena"),
    VILLAFLORA("Redondel de La Villaflora"),
    CARDENAL_DE_LA_TORRE("Parque Cardenal de la Torre"),
    SOLANDA("Solanda"),
    MORAN_VALVERDE("Estación Morán Valverde"),
    QUICENTRO_SUR("Quicentro Sur"),
    PLATAFORMA_SUR("Plataforma Gubernamental Sur"),
    QUITUMBE("Terminal Terrestre Quitumbe");
    private final String nombreVisible;
    ParadaCiclovia(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }
    @Override
    public String toString() {
        return nombreVisible;
    }
}
