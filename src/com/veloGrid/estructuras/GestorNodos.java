package com.veloGrid.estructuras;


import com.veloGrid.clasesBase.Coordenada;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GestorNodos {
    /**Declaracion de Atributos*/
    private Map<Integer, Nodo> mapaNodos;//Usamos un HashMap de tipo Nodo para iniciar los datos de los csv en la memoria y acceder a ellos rapidamente
    /**Desarrollo de Constructores*/
    public GestorNodos() {
        this.mapaNodos = new HashMap<>();
    }
    /**Desarrollo de Métodos Propios del Desarrollador*/
    public Nodo buscarNodo(int idNodo) {
        return mapaNodos.get(idNodo); // Retorna el nodo, o null si no existe
    }
    public Map<Integer, Nodo> getMapaNodos() {
        return mapaNodos;
    }
    //Cargar desde el CSV en el HashMap para persistencia
    public void cargarNodosCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false; // Saltamos los encabezados
                    continue;
                }
                String[] datos = linea.split(",");
                if (datos.length >= 7) {
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    double posX = Double.parseDouble(datos[2]);
                    double posY = Double.parseDouble(datos[3]);
                    boolean esCiclovia = Boolean.parseBoolean(datos[4]);
                    String creador = datos[5];
                    boolean esVerificado = Boolean.parseBoolean(datos[6]);
                    Coordenada coord = new Coordenada(posX, posY);
                    Nodo nuevoNodo = new Nodo(id, nombre, coord, esCiclovia, creador, esVerificado);
                    mapaNodos.put(id, nuevoNodo);
                }
            }
            System.out.println("Nodos regulares cargados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al leer nodos: " + e.getMessage());
        }
    }
    public void cargarPuntosInteresCSV(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                String[] datos = linea.split(",");
                if (datos.length >= 10) {
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    double posX = Double.parseDouble(datos[2]);
                    double posY = Double.parseDouble(datos[3]);
                    boolean esCiclovia = Boolean.parseBoolean(datos[4]);
                    String creador = datos[5];
                    boolean esVerificado = Boolean.parseBoolean(datos[6]);
                    String descripcion = datos[7];
                    String horario = datos[8];
                    String ultimoEditor = datos[9];
                    Coordenada coord = new Coordenada(posX, posY);
                    PuntoInteres nuevoPOI = new PuntoInteres(id, nombre, coord, esCiclovia, creador, esVerificado, descripcion, horario, ultimoEditor);
                    mapaNodos.put(id, nuevoPOI);
                }
            }
            System.out.println("Puntos de Interés cargados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al leer Puntos de Interés: " + e.getMessage());
        }
    }
    //Guardar los datos al final en el CSV para persistencia
    public void guardarNodosCSV(String rutaArchivo) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(rutaArchivo))) {
            // Cabecera
            pw.println("id,nombre,posX,posY,esCiclovia,creador,esVerificado");
            //Datos
            for (Nodo nodo : mapaNodos.values()) {
                if (!(nodo instanceof PuntoInteres)) {// Solo Nodos, no puntos de Interes
                    String linea = nodo.getIdNodo() + "," +
                            nodo.getNombreNodo() + "," +
                            nodo.getCoordenada().getPosX() + "," +
                            nodo.getCoordenada().getPosY() + "," +
                            nodo.isEsCiclovia() + "," +
                            nodo.getQuienAgrego() + "," +
                            nodo.isEsVerificado();
                    pw.println(linea);
                }
            }
            System.out.println("CSV de Nodos actualizado correctamente.");
        } catch (java.io.IOException e) {
            System.err.println("Error al escribir el archivo CSV de nodos: " + e.getMessage());
        }
    }

    public void guardarPuntosInteresCSV(String rutaArchivo) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(rutaArchivo))) {
            // Cabecera
            pw.println("id,nombre,posX,posY,esCiclovia,creador,esVerificado,descripcion,horario,ultimoEditor");
            //Datos
            for (Nodo nodo : mapaNodos.values()) {
                if (nodo instanceof PuntoInteres) {//Solo Puntos de Interes
                    PuntoInteres poi = (PuntoInteres) nodo;
                    String linea = poi.getIdNodo() + "," +
                            poi.getNombreNodo() + "," +
                            poi.getCoordenada().getPosX() + "," +
                            poi.getCoordenada().getPosY() + "," +
                            poi.isEsCiclovia() + "," +
                            poi.getQuienAgrego() + "," +
                            poi.isEsVerificado() + "," +
                            poi.getDescripcion() + "," +
                            poi.getHorario() + "," +
                            poi.getUltimoEditor();
                    pw.println(linea);
                }
            }
            System.out.println("CSV de Puntos de Interés actualizado correctamente.");
        } catch (java.io.IOException e) {
            System.err.println("Error al escribir el archivo CSV de puntos de interés: " + e.getMessage());
        }
    }
}