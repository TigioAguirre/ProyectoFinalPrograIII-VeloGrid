import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class AlertaComunitaria extends JFrame {


    private Stack<Incidente> pilaIncidentes;


    private JTextArea areaAlertas;
    private JComboBox<TipoIncidente> comboTipo;
    private JTextField txtId, txtX, txtY;

    public AlertaComunitaria() {
        pilaIncidentes = new Stack<>();

        setTitle("Módulo de Alertas Comunitarias");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Reportar Nueva Alerta", crearPanelReportar());
        tabbedPane.addTab("Gestión de Alertas", crearPanelGestion());

        add(tabbedPane);
    }

    private JPanel crearPanelReportar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("ID del Incidente:"));
        txtId = new JTextField();
        formPanel.add(txtId);

        formPanel.add(new JLabel("Tipo de Incidente:"));
        comboTipo = new JComboBox<>(TipoIncidente.values());
        formPanel.add(comboTipo);

        formPanel.add(new JLabel("Coordenada X:"));
        txtX = new JTextField();
        formPanel.add(txtX);

        formPanel.add(new JLabel("Coordenada Y:"));
        txtY = new JTextField();
        formPanel.add(txtY);

        JButton btnReportar = new JButton("Reportar Incidente");
        btnReportar.setBackground(new Color(46, 204, 113));
        btnReportar.setForeground(Color.WHITE);
        btnReportar.addActionListener(e -> agregarIncidente());

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(btnReportar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelGestion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        areaAlertas = new JTextArea();
        areaAlertas.setEditable(false);
        areaAlertas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaAlertas);

        JButton btnAtender = new JButton("Atender Última Alerta");
        btnAtender.setBackground(new Color(231, 76, 60));
        btnAtender.setForeground(Color.WHITE);
        btnAtender.addActionListener(e -> atenderIncidente());

        panel.add(new JLabel("Alertas Activas:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnAtender, BorderLayout.SOUTH);

        return panel;
    }

    private void agregarIncidente() {
        try {
            int id = Integer.parseInt(txtId.getText());
            double x = Double.parseDouble(txtX.getText());
            double y = Double.parseDouble(txtY.getText());
            TipoIncidente tipo = (TipoIncidente) comboTipo.getSelectedItem();

            Coordenada ubicacion = new Coordenada(x, y);
            Incidente nuevoIncidente = new Incidente(id, tipo, ubicacion);


            pilaIncidentes.push(nuevoIncidente);
            actualizarAreaAlertas();

            JOptionPane.showMessageDialog(this, "Incidente reportado y añadido a la pila.");


            txtId.setText(""); txtX.setText(""); txtY.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa valores numéricos válidos en ID y Coordenadas.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atenderIncidente() {
        if (!pilaIncidentes.isEmpty()) {

            Incidente atendido = pilaIncidentes.pop();
            atendido.actualizarEstado();
            actualizarAreaAlertas();
            JOptionPane.showMessageDialog(this, "Alerta atendida y removida:\n" + atendido.toString(), "Alerta Atendida", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No hay alertas pendientes en la pila.", "Pila Vacía", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarAreaAlertas() {
        StringBuilder sb = new StringBuilder();
        if (pilaIncidentes.isEmpty()) {
            sb.append("La pila de alertas está vacía.");
        } else {

            for (int i = pilaIncidentes.size() - 1; i >= 0; i--) {
                sb.append("[Tope - Índice ").append(i).append("] ")
                        .append(pilaIncidentes.get(i).toString())
                        .append("\n");
            }
        }
        areaAlertas.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AlertaComunitaria().setVisible(true);
        });
    }
}

