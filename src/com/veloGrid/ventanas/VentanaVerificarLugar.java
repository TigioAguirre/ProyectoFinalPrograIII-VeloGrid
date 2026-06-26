package com.veloGrid.ventanas;

import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.estructuras.Nodo;
import com.veloGrid.estructuras.PuntoInteres;
import com.veloGrid.clasesBase.AristaRuta;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaVerificarLugar extends JFrame {
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;
    private JTable tablaNodos;
    private DefaultTableModel modeloNodos;
    private JTable tablaRutas;
    private DefaultTableModel modeloRutas;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color SUCCESS        = new Color(46, 204, 113); // Verde para "Verificado"
    private static final Color DANGER         = new Color(220, 50, 50);  // Rojo para "Revocado"
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);
    /**Constructores de la Ventana*/
    public VentanaVerificarLugar(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        initUI();
        cargarDatosTablas();
    }
    /**Metodos de la Ventana*/
    private void initUI() {
        setTitle("VelóGRID — Verificación de Lugares y Rutas");
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
        JLabel logo = new JLabel("VERIFICAR LUGARES Y RUTAS");
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
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setOpaque(false);
        panelControles.setBorder(new EmptyBorder(10, 10, 10, 20));
        JLabel lblTitulo = new JLabel("Panel de Aprobación");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(TEXT_WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelControles.add(lblTitulo);
        panelControles.add(Box.createVerticalStrut(20));
        JLabel lblInfo = new JLabel("<html><body>Selecciona un Nodo o una<br>Ruta en las tablas de la derecha para gestionar su estado oficial.</body></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setForeground(TEXT_MUTED);
        panelControles.add(lblInfo);
        panelControles.add(Box.createVerticalStrut(30));
        JButton btnAprobar = makeActionButton("Verificar (Aprobar)", SUCCESS, TEXT_WHITE);
        btnAprobar.addActionListener(e -> procesarVerificacion(true));
        panelControles.add(btnAprobar);
        panelControles.add(Box.createVerticalStrut(15));
        JButton btnRevocar = makeActionButton("Quitar Verificación", DANGER, TEXT_WHITE);
        btnRevocar.addActionListener(e -> procesarVerificacion(false));
        panelControles.add(btnRevocar);
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.30; // 30% del ancho
        body.add(panelControles, gbcMain);
        JPanel panelTablas = new JPanel(new GridLayout(2, 1, 0, 15));
        panelTablas.setOpaque(false);
        JPanel panelNodos = new JPanel(new BorderLayout(0, 5));
        panelNodos.setOpaque(false);
        JLabel lblNodos = new JLabel("Nodos y Puntos de Interés:");
        lblNodos.setForeground(TEXT_WHITE);
        lblNodos.setFont(new Font("Arial", Font.BOLD, 12));
        panelNodos.add(lblNodos, BorderLayout.NORTH);
        String[] colNodos = {"ID", "Nombre", "Tipo", "Creador", "Verificado"};
        modeloNodos = new DefaultTableModel(colNodos, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaNodos = crearTablaEstandar(modeloNodos);
        JScrollPane scrollNodos = new JScrollPane(tablaNodos);
        aplicarEstiloScroll(scrollNodos);
        panelNodos.add(scrollNodos, BorderLayout.CENTER);
        JPanel panelRutas = new JPanel(new BorderLayout(0, 5));
        panelRutas.setOpaque(false);
        JLabel lblRutas = new JLabel("Rutas y Tramos:");
        lblRutas.setForeground(TEXT_WHITE);
        lblRutas.setFont(new Font("Arial", Font.BOLD, 12));
        panelRutas.add(lblRutas, BorderLayout.NORTH);
        String[] colRutas = {"Origen (ID)", "Destino (ID)", "Creador", "Verificada"};
        modeloRutas = new DefaultTableModel(colRutas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRutas = crearTablaEstandar(modeloRutas);
        JScrollPane scrollRutas = new JScrollPane(tablaRutas);
        aplicarEstiloScroll(scrollRutas);
        panelRutas.add(scrollRutas, BorderLayout.CENTER);
        panelTablas.add(panelNodos);
        panelTablas.add(panelRutas);
        tablaNodos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaNodos.getSelectedRow() != -1) {
                tablaRutas.clearSelection();
            }
        });
        tablaRutas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaRutas.getSelectedRow() != -1) {
                tablaNodos.clearSelection();
            }
        });
        gbcMain.gridx = 1;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.70;
        body.add(panelTablas, gbcMain);
        root.add(body, BorderLayout.CENTER);
    }
    private void cargarDatosTablas() {
        modeloNodos.setRowCount(0);
        modeloRutas.setRowCount(0);
        for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
            // Llenar tabla de Nodos
            String tipo = (nodo instanceof PuntoInteres) ? "POI" : "Nodo";
            modeloNodos.addRow(new Object[]{
                    nodo.getIdNodo(),
                    nodo.getNombreNodo(),
                    tipo,
                    nodo.getQuienAgrego(),
                    nodo.isEsVerificado() ? "Sí" : "No"
            });
            for (AristaRuta ruta : nodo.getRutasSalientes()) {
                modeloRutas.addRow(new Object[]{
                        ruta.getNodoInicio().getIdNodo(),
                        ruta.getNodoFinal().getIdNodo(),
                        ruta.getAgregadaPor(),
                        ruta.isEsVerificada() ? "Sí" : "No"
                });
            }
        }
    }
    private void procesarVerificacion(boolean nuevoEstado) {
        int filaNodos = tablaNodos.getSelectedRow();
        int filaRutas = tablaRutas.getSelectedRow();

        if (filaNodos == -1 && filaRutas == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un elemento de la tabla primero.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (filaNodos != -1) {
                int idNodo = Integer.parseInt(tablaNodos.getValueAt(filaNodos, 0).toString());
                Nodo nodo = gestorNodos.buscarNodo(idNodo);
                if (nodo != null) {
                    nodo.setEsVerificado(nuevoEstado);
                    if (nodo instanceof PuntoInteres) {
                        gestorNodos.guardarPuntosInteresCSV("baseDeDatos/puntosInteres.csv");
                    } else {
                        gestorNodos.guardarNodosCSV("baseDeDatos/nodosRegistrados.csv");
                    }
                    JOptionPane.showMessageDialog(this, "Estado del Nodo actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else if (filaRutas != -1) {
                int idOrigen = Integer.parseInt(tablaRutas.getValueAt(filaRutas, 0).toString());
                int idDestino = Integer.parseInt(tablaRutas.getValueAt(filaRutas, 1).toString());
                Nodo nodoOrigen = gestorNodos.buscarNodo(idOrigen);
                if (nodoOrigen != null) {
                    for (AristaRuta ruta : nodoOrigen.getRutasSalientes()) {
                        if (ruta.getNodoFinal().getIdNodo() == idDestino) {
                            ruta.setEsVerificada(nuevoEstado);
                            gestorRutas.guardarRutasCSV("baseDeDatos/rutasRegistradas.csv", gestorNodos);
                            JOptionPane.showMessageDialog(this, "Estado de la Ruta actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                }
            }
            cargarDatosTablas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al procesar la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private JTable crearTablaEstandar(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TEXT_WHITE);
        tabla.setGridColor(DIVIDER);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setBackground(BG_DARK);
        tabla.getTableHeader().setForeground(ORANGE_PRIMARY);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tabla.setSelectionBackground(ORANGE_PRIMARY);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);
        return tabla;
    }
    /**Diseño de la Ventana*/
    private JButton makeActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial Black", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setMaximumSize(new Dimension(180, 40));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
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