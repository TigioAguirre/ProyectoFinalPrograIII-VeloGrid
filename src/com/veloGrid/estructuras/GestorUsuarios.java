package com.veloGrid.estructuras;

import com.veloGrid.clasesBase.NivelExperiencia;
import com.veloGrid.clasesBase.PreferenciaRuta;
import com.veloGrid.clasesBase.TipoBicicleta;
import com.veloGrid.clasesBase.Usuario;

import java.io.*;
import java.util.ArrayList;

public class GestorUsuarios {
    /**Declaración de atributos*/
    private ArrayList<Usuario> listaUsuarios;
    //Ruta para persistencia en csv
    private final String RUTA_ARCHIVO = "baseDeDatos/usuariosRegistrados.csv";
    /**Desarrollo de Constructores*/
    public GestorUsuarios() {
        listaUsuarios = new ArrayList<>();
        verificarArchivo();
        cargarUsuarios();
    }
    /**Desarrollo de los Metodos propios del desarrollador*/
    private void verificarArchivo() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
                guardarUsuarios();
            } catch (IOException e) {
                System.out.println("Error al crear la base de datos: " + e.getMessage());
            }
        }
    }
    //Cargar desde el CSV para persistencia
    private void cargarUsuarios() {
        listaUsuarios.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            br.readLine();
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",", -1);
                if (datos.length >= 4) {
                    int id = Integer.parseInt(datos[0].trim());
                    String user = datos[1].trim();
                    String pass = datos[2].trim();
                    boolean registrado = Boolean.parseBoolean(datos[3].trim());
                    Usuario usuario = new Usuario(id, user, pass);
                    usuario.setUsrRegistrado(registrado);
                    if (registrado && datos.length >= 17) {
                        if (!datos[4].trim().isEmpty()) usuario.setNombreCompleto(datos[4].trim());
                        if (!datos[5].trim().isEmpty()) usuario.setNivelExperiencia(NivelExperiencia.valueOf(datos[5].trim()));
                        if (!datos[6].trim().isEmpty()) usuario.setPreferenciaRuta(PreferenciaRuta.valueOf(datos[6].trim()));
                        if (!datos[7].trim().isEmpty()) usuario.setHorasMovimiento(Float.parseFloat(datos[7].trim()));
                        if (!datos[8].trim().isEmpty()) usuario.setKmRecorridos(Float.parseFloat(datos[8].trim()));
                        if (!datos[9].trim().isEmpty()) usuario.setDesnivelPosAcum(Float.parseFloat(datos[9].trim()));
                        if (!datos[10].trim().isEmpty()) usuario.setTipoBicicleta(TipoBicicleta.valueOf(datos[10].trim()));
                        if (!datos[11].trim().isEmpty()) usuario.setEsAdmin(Boolean.parseBoolean(datos[11].trim()));
                        if (!datos[12].trim().isEmpty()) usuario.setRutaActualNodos(datos[12].trim());
                        if (!datos[13].trim().isEmpty()) usuario.setRutaActualAristas(datos[13].trim());
                        if (!datos[14].trim().isEmpty()) usuario.setRutaActual(datos[14].trim());
                        if (!datos[15].trim().isEmpty()) usuario.setHistorialAuxiliar(datos[15].trim());
                        if (!datos[16].trim().isEmpty()) usuario.setHistorial(datos[16].trim());
                    }
                    listaUsuarios.add(usuario);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar usuarios o formato numérico incorrecto: " + e.getMessage());
        }
    }
    //Guardar los datos al final en el CSV para persistencia
    public void guardarUsuarios() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RUTA_ARCHIVO))) {
            pw.println("idUsuario,nombreUsuario,contraseña,usrRegistrado,nombreCompleto,nivelExperiencia,preferenciaRuta,horasMovimiento,kmRecorridos,desnivelAcum,tipoBicicleta,esAdmin,rutaActualNodos,rutaActualAristas,rutaActual,historialAuxiliar,historial");
            for (Usuario usuario : listaUsuarios) {
                String linea = usuario.getIdUsuario() + "," +
                        usuario.getNombreUsuario() + "," +
                        usuario.getContraseña() + "," +
                        usuario.getUsrRegistrado() + "," +
                        (usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : "") + "," +
                        (usuario.getNivelExperiencia() != null ? usuario.getNivelExperiencia().name() : "") + "," +
                        (usuario.getPreferenciaRuta() != null ? usuario.getPreferenciaRuta().name() : "") + "," +
                        usuario.getHorasMovimiento() + "," +
                        usuario.getKmRecorridos() + "," +
                        usuario.getDesnivelPosAcum() + "," +
                        (usuario.getTipoBicicleta() != null ? usuario.getTipoBicicleta().name() : "") + "," +
                        usuario.EsAdmin() + "," +
                        (usuario.getRutaActualNodos() != null ? usuario.getRutaActualNodos() : "") + "," +
                        (usuario.getRutaActualAristas() != null ? usuario.getRutaActualAristas() : "") + "," +
                        (usuario.getRutaActual() != null ? usuario.getRutaActual() : "") + "," +
                        (usuario.getHistorialAuxiliar() != null ? usuario.getHistorialAuxiliar() : "") + "," +
                        (usuario.getHistorial() != null ? usuario.getHistorial() : "");
                pw.println(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }
    public Usuario autenticarUsuario(String user, String pass) {
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(user) && usuario.getContraseña().equals(pass)) {
                return usuario;
            }
        }
        return null;
    }
    public boolean eliminarUsuario(Usuario usuario) {
        boolean eliminado = listaUsuarios.removeIf(user -> user.getIdUsuario() == usuario.getIdUsuario());
        if (eliminado) {
            guardarUsuarios();
        }
        return eliminado;
    }
    public boolean registrarNuevoUsuario(String user, String pass) {
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(user)) {
                return false; // El usuario ya existe
            }
        }
        int nuevoId = listaUsuarios.isEmpty() ? 1 : listaUsuarios.get(listaUsuarios.size() - 1).getIdUsuario() + 1;
        Usuario nuevo = new Usuario(nuevoId, user, pass);
        listaUsuarios.add(nuevo);
        guardarUsuarios();
        return true;
    }
}