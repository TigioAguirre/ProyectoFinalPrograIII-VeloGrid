package com.veloGrid.estructuras;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormateadorRutas {
    /**Declaracion de Constantes para Separadores para las bases de datos*/
    public static final String SEP_NODOS = "-";
    public static final String SEP_ARISTAS = "-";
    public static final String SEP_MIXTO = "-";
    public static final String SEP_HISTORIAL = "|";
    public static final String SEP_FECHA = ":";
    /**Desarrollo del Metodo que obtinene el momento(Tiempo)*/
    public static String generarTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd: HH mm");
        return LocalDateTime.now().format(dtf);
    }
}