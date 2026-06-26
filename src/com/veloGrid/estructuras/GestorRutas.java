package com.veloGrid.estructuras;

import java.io.*;
import com.veloGrid.clasesBase.AristaRuta;
import com.veloGrid.clasesBase.Incidente;
import com.veloGrid.clasesBase.TipoIncidente;

public class GestorRutas {
    /**Declaracion de Atributos*/
    private float pMax, pMin, dMax, dMin;
    /**Desarrollo de Constructores*/
    public GestorRutas() {
    }
    /**Desarrollo de Metodos propios del desarrollador*/
    public float[] getLimitesGlobales() {
        return new float[]{pMax, pMin, dMax, dMin};
    }
    public void actualizarLimitesInternos(float pMax, float pMin, float dMax, float dMin) {
        this.pMax = pMax;
        this.pMin = pMin;
        this.dMax = dMax;
        this.dMin = dMin;
    }
    public void sincronizarAlertasConRutas(GestorNodos gestorNodos) {
        File archivo = new File("baseDeDatos/alertas.csv");//Ubicacion de el archivo de persistencia
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) { primeraLinea = false; continue; }
                String[] d = linea.split(",");
                if (d.length >= 10) {
                    int idOrigen = Integer.parseInt(d[1]);
                    int idDestino = Integer.parseInt(d[2]);
                    String estado = d[8];
                    if (estado.equals("ACTIVO")) {
                        Nodo n = gestorNodos.buscarNodo(idOrigen);
                        if (n != null) {
                            for (AristaRuta arista : n.getRutasSalientes()) {
                                if (arista.getNodoFinal().getIdNodo() == idDestino) {
                                    Incidente inc = new Incidente(
                                            Integer.parseInt(d[0]), idOrigen, idDestino,
                                            TipoIncidente.valueOf(d[3]),
                                            d[4], d[5], d[6], d[7], estado, Integer.parseInt(d[9])
                                    );
                                    arista.setIncidenteActivo(inc);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error sincronizando alertas con mapa: " + e.getMessage());
        }
        for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
            for (AristaRuta arista : nodo.getRutasSalientes()) {
                arista.actualizarPenalizacionIncidente();
            }
        }
        guardarRutasCSV("baseDeDatos/rutasRegistradas.csv", gestorNodos);
        System.out.println("Alertas sincronizadas. Archivo de Rutas actualizado con penalizaciones.");
    }
    //Cargar desde el CSV para persistencia
    public void cargarLimitesCSV(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            this.pMax = 0; this.pMin = 0; this.dMax = 0; this.dMin = 0;
            guardarLimitesCSV(rutaArchivo);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            br.readLine();
            String linea = br.readLine();
            if (linea != null) {
                String[] datos = linea.split(",");
                this.pMax = Float.parseFloat(datos[0]);
                this.pMin = Float.parseFloat(datos[1]);
                this.dMax = Float.parseFloat(datos[2]);
                this.dMin = Float.parseFloat(datos[3]);
            }
            System.out.println("Límites globales cargados: pMax=" + pMax + ", dMax=" + dMax);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer límites globales: " + e.getMessage());
        }
    }
    public void cargarRutasDesdeCSV(String rutaArchivo, GestorNodos gestorNodos) {
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
                    int idInicio = Integer.parseInt(datos[0].trim());
                    int idFinal = Integer.parseInt(datos[1].trim());
                    float pendiente = Float.parseFloat(datos[2].trim());
                    String agregadaPor = datos[3].trim();
                    boolean esVerificada = Boolean.parseBoolean(datos[4].trim());
                    Nodo nodoA = gestorNodos.buscarNodo(idInicio);
                    Nodo nodoB = gestorNodos.buscarNodo(idFinal);
                    if (nodoA != null && nodoB != null) {
                        nodoA.agregarRuta(nodoB, pendiente, agregadaPor, esVerificada);
                    }
                }
            }
            for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
                for (AristaRuta arista : nodo.getRutasSalientes()) {
                    arista.calcularPonderaciones(dMin, dMax, pMin, pMax);
                }
            }
            System.out.println("Rutas cargadas en memoria.");
            sincronizarAlertasConRutas(gestorNodos);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar rutas desde CSV: " + e.getMessage());
        }
    }
    //Guardar los datos al final en el CSV para persistencia
    public void guardarLimitesCSV(String rutaArchivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {
            pw.println("pMax,pMin,dMax,dMin");
            pw.println(this.pMax + "," + this.pMin + "," + this.dMax + "," + this.dMin);
        } catch (IOException e) {
            System.err.println("Error al guardar archivo de límites: " + e.getMessage());
        }
    }
    public void guardarRutasCSV(String rutaArchivo, GestorNodos gestorNodos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {
            pw.println("idInicio,idFinal,pendiente,agregadaPor,esVerificada,esCiclovia,distancia,ponderacionRapidez,ponderacionCiclovia,ponderacionPendiente,nombreRuta,tieneIncidente");
            for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
                for (AristaRuta arista : nodo.getRutasSalientes()) {
                    boolean tieneIncidente = (arista.getIncidenteActivo() != null && arista.getIncidenteActivo().getEstado().equals("ACTIVO"));
                    String linea = arista.getNodoInicio().getIdNodo() + "," +
                            arista.getNodoFinal().getIdNodo() + "," +
                            arista.getPendienteMedia() + "," +
                            arista.getAgregadaPor() + "," +
                            arista.isEsVerificada() + "," +
                            arista.isEsCiclovia() + "," +
                            arista.getDistancia() + "," +
                            arista.getPonderacionRapidez() + "," +
                            arista.getPonderacionCiclovia() + "," +
                            arista.getPonderacionPendiente() + "," +
                            arista.getNombreRuta() + "," +
                            tieneIncidente;
                    pw.println(linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo de rutas: " + e.getMessage());
        }
    }
}