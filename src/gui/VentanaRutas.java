package gui;

import javax.swing.*;

public class VentanaRutas extends JFrame {

    private JPanel panel1;
    private JTabbedPane tabbedPane1;

    // TAB RUTAS
    private JComboBox<String> comboParadas;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JList<String> listaRutas;

    // TAB ALERTAS
    private JTextArea textArea1;

    // Modelo para el JList
    private DefaultListModel<String> modeloLista;

    public VentanaRutas() {

        // CONFIGURACIÓN DE LA VENTANA
        setTitle("Velogrind - Planificador de Rutas");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);


        modeloLista = new DefaultListModel<>();
        listaRutas.setModel(modeloLista);


        cargarParadas();


        textArea1.append("=== SISTEMA DE ALERTAS ===\n");
        textArea1.append("Sistema iniciado correctamente.\n\n");


        btnAgregar.addActionListener(e -> agregarParada());


        btnEliminar.addActionListener(e -> eliminarParada());


        btnLimpiar.addActionListener(e -> limpiarRuta());
    }


    private void cargarParadas() {

        comboParadas.addItem("Terminal Norte");
        comboParadas.addItem("Centro Histórico");
        comboParadas.addItem("Universidad Central");
        comboParadas.addItem("Aeropuerto");
        comboParadas.addItem("Hospital General");
        comboParadas.addItem("Mall del Río");
        comboParadas.addItem("Parque Central");
        comboParadas.addItem("Estación Sur");
    }


    private void agregarParada() {

        String parada = comboParadas.getSelectedItem().toString();

        modeloLista.addElement(parada);

        textArea1.append("✓ Parada agregada: " + parada + "\n");
    }


    private void eliminarParada() {

        int index = listaRutas.getSelectedIndex();

        if(index != -1){

            String eliminada = modeloLista.get(index);

            modeloLista.remove(index);

            textArea1.append("⚠ Parada eliminada: " + eliminada + "\n");

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Seleccione una parada para eliminar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }


    private void limpiarRuta() {

        modeloLista.clear();

        textArea1.append("🗑 Ruta limpiada correctamente.\n");
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new VentanaRutas().setVisible(true);

        });
    }
}