package com.veloGrid.ventanas;

import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.estructuras.Nodo;
import com.veloGrid.estructuras.PuntoInteres;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VentanaEliminacionLugar extends JFrame {
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;
    private JTextField txtIdNodo;
    private JTextArea txtMotivo;
    private JTable tablaNodos;
    private DefaultTableModel modeloTabla;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color DANGER         = new Color(220, 50, 50); // Rojo para alertas de eliminación
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);
    /**Constructores de la Ventana*/
    public VentanaEliminacionLugar(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        initUI();
        cargarDatosTabla();
    }
    /**Metodos de la Ventana*/
    private void initUI() {
        setTitle("VelóGRID — Solicitar Eliminación");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(BG_DARK);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        setContentPane(root);
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_CARD);
        topBar.setBorder(new EmptyBorder(0, 20, 0, 8));
        topBar.setPreferredSize(new Dimension(0, 48));
        JLabel logo = new JLabel("SOLICITAR ELIMINACIÓN DE LUGAR");
        logo.setFont(new Font("Arial Black", Font.BOLD, 14));
        logo.setForeground(DANGER); // Usamos rojo para denotar una acción destructiva
        topBar.add(logo, BorderLayout.WEST);
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        topRight.setOpaque(false);
        JButton btnVolver = new JButton("<");
        btnVolver.setForeground(TEXT_MUTED);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            GestorVentanas.cambiarVentana(this, new VentanaPrincipal(usuarioActivo, gestor, gestorNodos, gestorRutas));
        });
        topRight.add(btnVolver);
        GestorVentanas.agregarBotonesVentana(this, topRight);
        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, topBar);
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setOpaque(false);
        panelFormulario.setBorder(new EmptyBorder(0, 0, 0, 20));
        JLabel lblInstrucciones = new JLabel("<html>Genera un ticket para que un<br>Administrador revise el caso:</html>");
        lblInstrucciones.setFont(new Font("Arial", Font.BOLD, 14));
        lblInstrucciones.setForeground(TEXT_WHITE);
        panelFormulario.add(lblInstrucciones);
        panelFormulario.add(Box.createVerticalStrut(25));
        txtIdNodo = makeTextField();
        panelFormulario.add(makeFormRow("ID del Lugar:", txtIdNodo));
        panelFormulario.add(Box.createVerticalStrut(15));
        JLabel lblMotivo = new JLabel("Motivo de la eliminación:");
        lblMotivo.setForeground(TEXT_MUTED);
        lblMotivo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMotivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(lblMotivo);
        panelFormulario.add(Box.createVerticalStrut(5));
        txtMotivo = new JTextArea();
        txtMotivo.setBackground(BG_CARD);
        txtMotivo.setForeground(TEXT_WHITE);
        txtMotivo.setCaretColor(ORANGE_PRIMARY);
        txtMotivo.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        aplicarEstiloScroll(scrollMotivo);
        scrollMotivo.setPreferredSize(new Dimension(300, 120));
        scrollMotivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFormulario.add(scrollMotivo);
        panelFormulario.add(Box.createVerticalStrut(30));
        JButton btnEnviar = new JButton("Enviar Solicitud");
        btnEnviar.setBackground(DANGER); // Botón rojo
        btnEnviar.setForeground(TEXT_WHITE);
        btnEnviar.setFont(new Font("Arial Black", Font.BOLD, 12));
        btnEnviar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnviar.setMaximumSize(new Dimension(300, 40));
        btnEnviar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnEnviar.setBackground(DANGER.brighter()); }
            @Override public void mouseExited(MouseEvent e) { btnEnviar.setBackground(DANGER); }
        });
        btnEnviar.addActionListener(e -> procesarSolicitud());
        panelFormulario.add(btnEnviar);
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.35; // 35% del ancho
        body.add(panelFormulario, gbcMain);
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setOpaque(false);
        JLabel lblTituloTabla = new JLabel("Lugares Registrados (Referencia):");
        lblTituloTabla.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloTabla.setForeground(TEXT_WHITE);
        panelTabla.add(lblTituloTabla, BorderLayout.NORTH);
        String[] columnas = {"ID", "Nombre", "Tipo", "Creador"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaNodos = new JTable(modeloTabla);
        tablaNodos.setBackground(BG_CARD);
        tablaNodos.setForeground(TEXT_WHITE);
        tablaNodos.setGridColor(DIVIDER);
        tablaNodos.setRowHeight(25);
        tablaNodos.getTableHeader().setBackground(BG_DARK);
        tablaNodos.getTableHeader().setForeground(ORANGE_PRIMARY);
        tablaNodos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaNodos.setFillsViewportHeight(true);
        JScrollPane scrollTabla = new JScrollPane(tablaNodos);
        aplicarEstiloScroll(scrollTabla);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        gbcMain.gridx = 1;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.65; // 65% del ancho
        body.add(panelTabla, gbcMain);
        root.add(body, BorderLayout.CENTER);
    }
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
            String tipo = (nodo instanceof PuntoInteres) ? "Punto de Interés" : "Intersección";
            modeloTabla.addRow(new Object[]{
                    nodo.getIdNodo(),
                    nodo.getNombreNodo(),
                    tipo,
                    nodo.getQuienAgrego()
            });
        }
    }
    private void procesarSolicitud() {
        try {
            int id = Integer.parseInt(txtIdNodo.getText().trim());
            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe especificar un motivo para la eliminación.", "Motivo Vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Nodo nodoObjetivo = gestorNodos.buscarNodo(id);
            if (nodoObjetivo == null) {
                JOptionPane.showMessageDialog(this, "El ID ingresado no corresponde a ningún lugar registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaActual = ahora.format(formato);
            String motivoSeguro = motivo.replace(",", " -").replace("\n", " ");
            guardarSolicitudCSV(id, nodoObjetivo.getNombreNodo(), usuarioActivo.getNombreUsuario(), fechaActual, motivoSeguro);
            JOptionPane.showMessageDialog(this, "Tu solicitud ha sido enviada al Administrador exitosamente.", "Ticket Generado", JOptionPane.INFORMATION_MESSAGE);
            txtIdNodo.setText("");
            txtMotivo.setText("");
            txtIdNodo.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un valor numérico.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void guardarSolicitudCSV(int idNodo, String nombreNodo, String solicitante, String fecha, String motivo) {
        File archivo = new File("baseDeDatos/solicitudesEliminacion.csv");
        boolean archivoNuevo = !archivo.exists();
        try (FileWriter fw = new FileWriter(archivo, true);
             PrintWriter pw = new PrintWriter(fw)) {
            if (archivoNuevo) {
                pw.println("idNodo,nombreNodo,usuarioSolicitante,fecha,motivo,estado");
            }
            pw.println(idNodo + "," + nombreNodo + "," + solicitante + "," + fecha + "," + motivo + ",PENDIENTE");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar la solicitud en el sistema: " + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**Diseño de la Ventana*/
    private void aplicarEstiloScroll(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(10, 0));
        verticalBar.setUI(crearScrollBarUI());

        JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
        horizontalBar.setPreferredSize(new Dimension(0, 10));
        horizontalBar.setUI(crearScrollBarUI());
    }
    private BasicScrollBarUI crearScrollBarUI() {
        return new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = SCROLL_THUMB; this.trackColor = BG_DARK; }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(BG_DARK);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isThumbRollover() ? SCROLL_HOVER : SCROLL_THUMB);
                int arc = 8, x = thumbBounds.x + 2, y = thumbBounds.y + 2, width = thumbBounds.width - 4, height = thumbBounds.height - 4;
                g2.fillRoundRect(x, y, width, height, arc, arc);
                g2.dispose();
            }
        };
    }
    private JPanel makeFormRow(String labelText, JTextField textField) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(400, 35));
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(TEXT_MUTED);
        lbl.setPreferredSize(new Dimension(100, 35));
        row.add(lbl, BorderLayout.WEST);
        row.add(textField, BorderLayout.CENTER);
        return row;
    }
    private JTextField makeTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(BG_CARD);
        txt.setForeground(TEXT_WHITE);
        txt.setCaretColor(ORANGE_PRIMARY);
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DIVIDER, 1), new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }
}