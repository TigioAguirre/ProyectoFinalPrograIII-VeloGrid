package com.veloGrid.Ventanas;

import com.veloGrid.estructuras.PilaAlertas;
import com.veloGrid.modelo.Coordenada;
import com.veloGrid.modelo.Incidente;
import com.veloGrid.modelo.TipoIncidente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaAlertas extends JFrame {
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane1;
    private JTextField txtId;
    private JComboBox<TipoIncidente> comboTipo;
    private JTextField txtX;
    private JTextField txtY;
    private JButton btnReportar;
    private JTextArea areaAlertas;
    private JButton btnAtender;

    private PilaAlertas pilaIncidentes;

    public VentanaAlertas() {
        setTitle("Modulo de Alertas Comunitarias");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);

        pilaIncidentes = new PilaAlertas();
        comboTipo.setModel(new DefaultComboBoxModel<>(TipoIncidente.values()));

        btnReportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idTexto = txtId.getText().trim();
                String xTexto = txtX.getText().trim();
                String yTexto = txtY.getText().trim();

                if (idTexto.isEmpty() || xTexto.isEmpty() || yTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Todos los campos son obligatorios.",
                            "Campos incompletos",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int id;
                double x, y;

                try {
                    id = Integer.parseInt(idTexto);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "El ID del incidente debe ser un numero entero.",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (id <= 0) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "El ID del incidente debe ser un numero positivo.",
                            "ID invalido",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    x = Double.parseDouble(xTexto);
                    y = Double.parseDouble(yTexto);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Las coordenadas deben ser valores numericos.",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Incidente[] historial = pilaIncidentes.obtenerHistorial();
                for (Incidente inc : historial) {
                    if (inc.getIdIncidente() == id) {
                        JOptionPane.showMessageDialog(panelPrincipal,
                                "Ya existe un incidente con el ID " + id + " en la pila.",
                                "ID duplicado",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                TipoIncidente tipo = (TipoIncidente) comboTipo.getSelectedItem();

                if (!pilaIncidentes.estaVacia()) {
                    Incidente[] hist = pilaIncidentes.obtenerHistorial();
                    if (hist.length >= 2) {
                        TipoIncidente tipoTope = hist[0].getTipoIncidente();
                        TipoIncidente tipoBajo = hist[1].getTipoIncidente();
                        if (tipoTope == tipo && tipoBajo == tipo) {
                            JOptionPane.showMessageDialog(panelPrincipal,
                                    "No se pueden apilar tres incidentes consecutivos del mismo tipo (" + tipo + ").\nReporte un tipo diferente primero.",
                                    "Tipo repetido consecutivamente",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }

                Coordenada ubicacion = new Coordenada(x, y);
                Incidente nuevoIncidente = new Incidente(id, tipo, ubicacion);

                pilaIncidentes.agregarAlerta(nuevoIncidente);
                actualizarAreaAlertas();
                JOptionPane.showMessageDialog(panelPrincipal, "Incidente reportado correctamente.");

                txtId.setText("");
                txtX.setText("");
                txtY.setText("");
            }
        });

        btnAtender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pilaIncidentes.estaVacia()) {
                    Incidente atendido = pilaIncidentes.atenderUltimaAlerta();
                    actualizarAreaAlertas();
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "Alerta atendida y removida:\n" + atendido.toString(),
                            "Alerta atendida",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "No hay alertas pendientes en la pila.",
                            "Pila vacia",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void actualizarAreaAlertas() {
        StringBuilder sb = new StringBuilder();
        if (pilaIncidentes.estaVacia()) {
            sb.append("La pila de alertas esta vacia.\n");
        } else {
            sb.append("ALERTAS PENDIENTES (LIFO)\n\n");
            Incidente[] historial = pilaIncidentes.obtenerHistorial();
            for (int i = 0; i < historial.length; i++) {
                sb.append("[Posicion ").append(i).append("] ")
                        .append(historial[i].toString())
                        .append("\n");
            }
        }
        areaAlertas.setText(sb.toString());
    }
}
