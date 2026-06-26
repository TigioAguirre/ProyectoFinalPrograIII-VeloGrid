package com.veloGrid.ventanas;

import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaEliminacionAdmin extends JFrame {
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;
    private JTable tablaSolicitudes;
    private DefaultTableModel modeloTabla;
    private List<String> lineasCSV;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color DANGER         = new Color(220, 50, 50);
    private static final Color SUCCESS        = new Color(46, 204, 113);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);
    /**Constructores de la Ventana*/
    public VentanaEliminacionAdmin(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        this.lineasCSV = new ArrayList<>();
        initUI();
        cargarSolicitudes();
    }
    /**Metodos de la Ventana*/
    private void initUI() {
        setTitle("VelóGRID — Atender Solicitudes");
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
        JLabel logo = new JLabel("ATENDER ELIMINACIONES");
        logo.setFont(new Font("Arial Black", Font.BOLD, 14));
        logo.setForeground(ORANGE_PRIMARY);
        topBar.add(logo, BorderLayout.WEST);
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        topRight.setOpaque(false);
        JButton btnVolver = makeIconButton("media/icon_back.png", 16, "Volver");
        btnVolver.addActionListener(e -> {
            GestorVentanas.cambiarVentana(this, new VentanaAdmin(usuarioActivo, gestor, gestorNodos, gestorRutas));
        });
        topRight.add(btnVolver);
        GestorVentanas.agregarBotonesVentana(this, topRight);
        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, topBar);
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(30, 40, 30, 40));
        JLabel lblTitle = new JLabel("Tickets Pendientes");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_WHITE);
        body.add(lblTitle, BorderLayout.NORTH);
        String[] columnas = {"Línea CSV", "ID Nodo", "Nombre", "Motivo", "Solicitante", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaSolicitudes = new JTable(modeloTabla);
        tablaSolicitudes.setBackground(BG_CARD);
        tablaSolicitudes.setForeground(TEXT_WHITE);
        tablaSolicitudes.setGridColor(DIVIDER);
        tablaSolicitudes.setRowHeight(28);
        tablaSolicitudes.getTableHeader().setBackground(BG_DARK);
        tablaSolicitudes.getTableHeader().setForeground(ORANGE_PRIMARY);
        tablaSolicitudes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaSolicitudes.setSelectionBackground(DIVIDER);
        tablaSolicitudes.setSelectionForeground(Color.WHITE);
        tablaSolicitudes.getColumnModel().getColumn(0).setMinWidth(0);
        tablaSolicitudes.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaSolicitudes.getColumnModel().getColumn(0).setWidth(0);
        JScrollPane scrollTabla = new JScrollPane(tablaSolicitudes);
        aplicarEstiloScroll(scrollTabla);
        body.add(scrollTabla, BorderLayout.CENTER);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setOpaque(false);
        JButton btnRechazar = makeActionButton("Rechazar Solicitud", BG_CARD, TEXT_MUTED);
        btnRechazar.addActionListener(e -> procesarAccion("RECHAZADO"));
        JButton btnAprobar = makeActionButton("Aprobar y Eliminar", DANGER, TEXT_WHITE);
        btnAprobar.addActionListener(e -> procesarAccion("APROBADO"));
        panelBotones.add(btnRechazar);
        panelBotones.add(btnAprobar);
        body.add(panelBotones, BorderLayout.SOUTH);
        root.add(body, BorderLayout.CENTER);
    }
    private void cargarSolicitudes() {
        lineasCSV.clear();
        modeloTabla.setRowCount(0);
        File archivo = new File("baseDeDatos/solicitudesEliminacion.csv");
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int indiceLinea = 0;
            while ((linea = br.readLine()) != null) {
                lineasCSV.add(linea);
                if (indiceLinea > 0) {
                    String[] datos = linea.split(",");
                    if (datos.length >= 6 && datos[5].trim().equals("PENDIENTE")) {
                        modeloTabla.addRow(new Object[]{
                                indiceLinea,
                                datos[0],
                                datos[1],
                                datos[4],
                                datos[2],
                                datos[5]
                        });
                    }
                }
                indiceLinea++;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer solicitudes.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void procesarAccion(String nuevoEstado) {
        int filaSeleccionada = tablaSolicitudes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una solicitud de la tabla primero.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int indiceCSV = (int) tablaSolicitudes.getValueAt(filaSeleccionada, 0);
        int idNodo = Integer.parseInt(tablaSolicitudes.getValueAt(filaSeleccionada, 1).toString());
        if (nuevoEstado.equals("APROBADO")) {
            if (gestorNodos.buscarNodo(idNodo) != null) {
                gestorNodos.getMapaNodos().remove(idNodo);
                gestorNodos.guardarNodosCSV("baseDeDatos/nodosRegistrados.csv");
                gestorNodos.guardarPuntosInteresCSV("baseDeDatos/puntosInteres.csv");
            }
        }
        String lineaActual = lineasCSV.get(indiceCSV);
        String lineaActualizada = lineaActual.replace("PENDIENTE", nuevoEstado);
        lineasCSV.set(indiceCSV, lineaActualizada);
        try (PrintWriter pw = new PrintWriter(new FileWriter("baseDeDatos/solicitudesEliminacion.csv"))) {
            for (String linea : lineasCSV) {
                pw.println(linea);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el archivo CSV.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(this, "Solicitud marcada como " + nuevoEstado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cargarSolicitudes();
    }
    /**Diseño de la Ventana*/
    private JButton makeActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial Black", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }
    private JButton makeIconButton(String iconPath, int size, String tooltip) {
        JButton btn = new JButton();
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            btn.setText("<");
            btn.setForeground(TEXT_MUTED);
        }
        btn.setToolTipText(tooltip);
        btn.setBackground(BG_CARD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(35, 35));
        return btn;
    }
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
                int arc = 8;
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, arc, arc);
                g2.dispose();
            }
        };
    }
}