package com.veloGrid.main;

import com.veloGrid.Ventanas.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // Ejecutamos la interfaz gráfica en el hilo de eventos de Swing (Buena práctica)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VentanaPrincipal menu = new VentanaPrincipal();
                menu.setVisible(true);
            }
        });

    }
}