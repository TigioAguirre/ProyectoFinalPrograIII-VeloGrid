package gui;

import javax.swing.*;

public class VentanaRutas extends JFrame {

    private JPanel panel1;
    private JTabbedPane tabbedPane1;

    private JComboBox<Ciclovia> comboParadas;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JList<Ciclovia> listaRutas;
    private JTextArea textArea1;

    private DefaultListModel<Ciclovia> modeloLista;

    private Usuario usuarioActual;

    public VentanaRutas() {

        setTitle("Planificador de Rutas");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        usuarioActual = new Usuario(1, "Ethan Branch", NivelExperiencia.EXPERTO);

        modeloLista = new DefaultListModel<>();
        listaRutas.setModel(modeloLista);

        cargarCicloviasDePrueba();

        btnAgregar.addActionListener(e -> {
            Ciclovia rutaSeleccionada = (Ciclovia) comboParadas.getSelectedItem();

            if (rutaSeleccionada != null) {
                modeloLista.addElement(rutaSeleccionada);

                textArea1.append(">>> Añadiendo parada: Ciclovía #" + rutaSeleccionada.getIdCiclovia() + "\n");

                String evaluacion = evaluarRutaParaUsuario(rutaSeleccionada);
                textArea1.append(evaluacion + "\n\n");
            }
        });

        btnEliminar.addActionListener(e -> {
            int index = listaRutas.getSelectedIndex();

            if(index != -1){
                Ciclovia eliminada = modeloLista.get(index);
                modeloLista.remove(index);

                textArea1.append("--- Ruta removida del plan: Ciclovía #" + eliminada.getIdCiclovia() + "\n\n");
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona una ruta de la lista inferior para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
    }


    private void cargarCicloviasDePrueba() {
        comboParadas.addItem(new Ciclovia(101, EstadoVia.OPTIMO));
        comboParadas.addItem(new Ciclovia(102, EstadoVia.REGULAR));
        comboParadas.addItem(new Ciclovia(103, EstadoVia.DETERIORADA));
        comboParadas.addItem(new Ciclovia(104, EstadoVia.EN_MANTENIMIENTO));
        comboParadas.addItem(new Ciclovia(105, EstadoVia.CERRADA));
    }


    private String evaluarRutaParaUsuario(Ciclovia ciclovia) {
        String mensaje = "[Evaluación] " + usuarioActual.getNombreUsuario() + " evaluando...\n";

        if (ciclovia.getEstadoVia().equals("CERRADA")) {
            mensaje += "Ruta rechazada: La vía está completamente cerrada.";
        } else if (usuarioActual.getNivelExperiencia() == NivelExperiencia.NOVATO && ciclovia.getEstadoVia().equals("DETERIORADA")) {
            mensaje += "Sugerencia: Esta vía está deteriorada. No recomendada para novatos.";
        } else {
            mensaje += "¡Ruta aprobada! Disfruta del viaje.";
        }
        return mensaje;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaRutas().setVisible(true);
        });
    }
}