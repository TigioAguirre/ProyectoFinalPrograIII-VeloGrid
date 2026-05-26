package com.veloGrid.Ventanas;

import com.veloGrid.estructuras.ColaRuta;
import com.veloGrid.modelo.NivelExperiencia;
import com.veloGrid.modelo.ParadaCiclovia;
import com.veloGrid.modelo.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaRutas extends JFrame {

    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JComboBox<ParadaCiclovia> comboParadas;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JList<ParadaCiclovia> listaRutas;
    private JTextArea rutaTextArea;
    private DefaultListModel<ParadaCiclovia> modeloLista;
    private ColaRuta miRutaQueue;
    private Usuario usuarioActual;

    public VentanaRutas() {
        setTitle("Planificador de Rutas VeloGRID");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        usuarioActual = new Usuario(1, "veloGrid Tester", NivelExperiencia.EXPERTO);
        miRutaQueue = new ColaRuta();
        modeloLista = new DefaultListModel<>();
        listaRutas.setModel(modeloLista);
        comboParadas.setModel(new DefaultComboBoxModel<>(ParadaCiclovia.values()));

        rutaTextArea.setText("Bienvenido al planificador, " + usuarioActual.getNombreUsuario() + ".\n");
        rutaTextArea.append("Seleccione paradas para armar su recorrido.\n\n");

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ParadaCiclovia seleccion = (ParadaCiclovia) comboParadas.getSelectedItem();

                if (seleccion == null) {
                    JOptionPane.showMessageDialog(panel1,
                            "Debe seleccionar una parada.",
                            "Seleccion requerida",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ParadaCiclovia[] recorrido = miRutaQueue.obtenerRecorridoCompleto();

                if (recorrido.length > 0) {
                    ParadaCiclovia ultima = recorrido[recorrido.length - 1];
                    if (ultima == seleccion) {
                        JOptionPane.showMessageDialog(panel1,
                                "No se puede agregar la misma parada de forma consecutiva.\nDebe agregar al menos una parada intermedia antes de repetir \"" + seleccion + "\".",
                                "Parada duplicada consecutiva",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    for (ParadaCiclovia p : recorrido) {
                        if (p == seleccion) {
                            int confirmacion = JOptionPane.showConfirmDialog(panel1,
                                    "La parada \"" + seleccion + "\" ya existe en el recorrido.\n¿Desea agregarla de todas formas?",
                                    "Parada duplicada",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                            if (confirmacion != JOptionPane.YES_OPTION) {
                                return;
                            }
                            break;
                        }
                    }
                }

                miRutaQueue.agregarParada(seleccion);
                actualizarListaVisual();
                rutaTextArea.append("Agregado: " + seleccion.toString() + "\n");
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!miRutaQueue.estaVacia()) {
                    ParadaCiclovia paradaCompletada = miRutaQueue.completarSiguienteParada();
                    actualizarListaVisual();
                    rutaTextArea.append("Removido: " + paradaCompletada.toString() + "\n");
                } else {
                    JOptionPane.showMessageDialog(panel1,
                            "La ruta esta vacia. No hay paradas por eliminar.",
                            "Cola vacia",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (miRutaQueue.estaVacia()) {
                    JOptionPane.showMessageDialog(panel1,
                            "La ruta ya esta vacia.",
                            "Sin cambios",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int confirmacion = JOptionPane.showConfirmDialog(panel1,
                        "Se eliminaran todas las paradas del recorrido. ¿Desea continuar?",
                        "Confirmar limpieza",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    miRutaQueue.vaciarRuta();
                    actualizarListaVisual();
                    rutaTextArea.append("Recorrido limpiado.\n");
                }
            }
        });
    }

    private void actualizarListaVisual() {
        modeloLista.clear();
        ParadaCiclovia[] recorrido = miRutaQueue.obtenerRecorridoCompleto();
        for (ParadaCiclovia parada : recorrido) {
            modeloLista.addElement(parada);
        }
    }
}
