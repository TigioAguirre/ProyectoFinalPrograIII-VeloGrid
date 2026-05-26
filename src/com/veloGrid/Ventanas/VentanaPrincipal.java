package com.veloGrid.Ventanas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {
    private JPanel panelMenu;
    private JButton btnAbrirRutas;
    private JButton btnAbrirAlertas;

    public VentanaPrincipal() {
        setTitle("VelóGRID - Menú Principal");
        setContentPane(panelMenu);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Si cierras el menú, se apaga el programa
        setSize(400, 300);
        setLocationRelativeTo(null); // Centrar en la pantalla

        // Acción para abrir el módulo de Rutas
        btnAbrirRutas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VentanaRutas ventanaRutas = new VentanaRutas();
                // Cambiamos el comportamiento para que no cierre todo el programa al cerrar esta ventana
                ventanaRutas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventanaRutas.setVisible(true);
            }
        });

        // Acción para abrir el módulo de Alertas
        btnAbrirAlertas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VentanaAlertas ventanaAlertas = new VentanaAlertas();
                // Lo mismo aquí: DISPOSE_ON_CLOSE solo cierra esta ventana y te devuelve al menú
                ventanaAlertas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventanaAlertas.setVisible(true);
            }
        });
    }
}
