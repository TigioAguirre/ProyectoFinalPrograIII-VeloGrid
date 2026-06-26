package com.veloGrid.ventanas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GestorVentanas {

    /**
     * Permite arrastrar la ventana desde cualquier panel (ej: topBar).
     * Solo funciona cuando la ventana NO está maximizada.
     */
    public static void habilitarArrastre(JFrame ventana, JPanel panel) {
        final int[] dragStart = new int[2];

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart[0] = e.getX();
                dragStart[1] = e.getY();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Doble clic en la topBar: alternar maximizar/restaurar
                if (e.getClickCount() == 2) {
                    if (ventana.getExtendedState() == JFrame.NORMAL) {
                        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    } else {
                        ventana.setExtendedState(JFrame.NORMAL);
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Solo arrastrar si la ventana no está maximizada
                if (ventana.getExtendedState() != JFrame.NORMAL) return;
                Point loc = ventana.getLocation();
                ventana.setLocation(
                    loc.x + e.getX() - dragStart[0],
                    loc.y + e.getY() - dragStart[1]
                );
            }
        });
    }

    /**
     * Cierra la ventana actual, abre la nueva, PERO le transfiere
     * el tamaño exacto, la posición y el estado (Maximizado/Normal).
     */
    public static void cambiarVentana(JFrame ventanaActual, JFrame ventanaNueva) {
        // Copiamos el estado (Maximizada o Normal)
        int estadoActual = ventanaActual.getExtendedState();
        ventanaNueva.setExtendedState(estadoActual);

        // Si la ventana NO estaba maximizada, copiamos sus dimensiones y ubicación en pantalla
        if (estadoActual == JFrame.NORMAL) {
            ventanaNueva.setBounds(ventanaActual.getBounds());
        }

        // Mostramos la nueva y destruimos la anterior
        ventanaNueva.setVisible(true);
        ventanaActual.dispose();
    }

    /**
     * Agrega los botones estándar (Minimizar, Maximizar, Cerrar) a cualquier panel derecho.
     */
    public static void agregarBotonesVentana(JFrame ventana, JPanel panelDerecho) {
        Color TEXT_MUTED = new Color(140, 140, 155);
        Color ORANGE_PRIMARY = new Color(252, 76, 2);
        Color DANGER = new Color(220, 50, 50);

        JButton minButton = crearBotonBarra("—", TEXT_MUTED, ORANGE_PRIMARY);
        JButton maxButton = crearBotonBarra("◻", TEXT_MUTED, ORANGE_PRIMARY);
        JButton closeButton = crearBotonBarra("✕", TEXT_MUTED, DANGER);

        // Acciones reales de la ventana
        minButton.addActionListener(e -> ventana.setExtendedState(JFrame.ICONIFIED));
        maxButton.addActionListener(e -> {
            if (ventana.getExtendedState() == JFrame.NORMAL) {
                ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                ventana.setExtendedState(JFrame.NORMAL);
            }
        });
        closeButton.addActionListener(e -> System.exit(0));

        // Los agregamos al panel que recibe por parámetro
        panelDerecho.add(minButton);
        panelDerecho.add(maxButton);
        panelDerecho.add(closeButton);
    }

    // Diseño estándar para los botones de la barra superior
    private static JButton crearBotonBarra(String texto, Color textoNormal, Color hoverText) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("sansserif", Font.BOLD, 16));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setForeground(textoNormal);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(40, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(hoverText); }
            @Override public void mouseExited(MouseEvent e) { btn.setForeground(textoNormal); }
        });
        return btn;
    }
}