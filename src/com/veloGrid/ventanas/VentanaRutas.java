package com.veloGrid.ventanas;

import com.veloGrid.clasesBase.AristaRuta;
import com.veloGrid.clasesBase.Usuario;
import com.veloGrid.estructuras.*;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class VentanaRutas extends JFrame {
    private JComboBox<Nodo> comboNodos;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnGenerarRuta;
    private JTextArea rutaTextArea;
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;
    private JList<Nodo> listaNodos;
    private DefaultListModel<Nodo> modeloLista;
    private List<Nodo> paradasSeleccionadas;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color INPUT_BORDER   = new Color(50, 50, 65);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);
    /**Constructores de la Ventana*/
    public VentanaRutas(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        setTitle("VelóGRID — Planificador de Rutas");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setMinimumSize(new Dimension(700, 500));
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(BG_DARK);
        paradasSeleccionadas = new ArrayList<>();
        modeloLista = new DefaultListModel<>();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        setContentPane(root);
        JPanel _topBarPanel = buildTopBar();
        root.add(_topBarPanel, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, _topBarPanel);
        root.add(buildPlannerTab(), BorderLayout.CENTER);
        String nodosActivos = usuarioActivo.getRutaActualNodos();
        if (nodosActivos != null && !nodosActivos.trim().isEmpty()) {
            String[] ids = nodosActivos.split(FormateadorRutas.SEP_NODOS);
            for (String idStr : ids) {
                try {
                    Nodo n = gestorNodos.buscarNodo(Integer.parseInt(idStr.trim()));
                    if (n != null) {
                        paradasSeleccionadas.add(n);
                        modeloLista.addElement(n);
                    }
                } catch (Exception ex) {}
            }
            actualizarVisualizacionRuta();
        }

        wireEvents();
    }
    /**Metodos de la Ventana*/
    private void wireEvents() {
        btnAgregar.addActionListener(e -> {
            Nodo seleccion = (Nodo) comboNodos.getSelectedItem();
            if (seleccion == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un lugar.", "Selección requerida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!paradasSeleccionadas.isEmpty()) {
                Nodo ultimaParada = paradasSeleccionadas.get(paradasSeleccionadas.size() - 1);
                if (ultimaParada.getIdNodo() == seleccion.getIdNodo()) {
                    JOptionPane.showMessageDialog(this, "No se puede agregar el mismo lugar consecutivamente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            paradasSeleccionadas.add(seleccion);
            modeloLista.addElement(seleccion);
            actualizarVisualizacionRuta();
        });
        btnEliminar.addActionListener(e -> {
            int indiceSeleccionado = listaNodos.getSelectedIndex();
            if (indiceSeleccionado != -1) {
                paradasSeleccionadas.remove(indiceSeleccionado);
                modeloLista.remove(indiceSeleccionado);
                actualizarVisualizacionRuta();
            } else {
                if (paradasSeleccionadas.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La ruta está vacía.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, seleccione un lugar de la lista para eliminarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        btnLimpiar.addActionListener(e -> {
            if (!paradasSeleccionadas.isEmpty()) {
                paradasSeleccionadas.clear();
                modeloLista.clear();
                actualizarVisualizacionRuta();
            }
        });
        btnGenerarRuta.addActionListener(e -> {
            if (paradasSeleccionadas.size() < 2) {
                JOptionPane.showMessageDialog(this, "Debes agregar al menos 2 puntos para generar una ruta.", "Ruta Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MotorDijkstra motor = new MotorDijkstra(gestorNodos);
            List<AristaRuta> rutaOptima = motor.calcularRutaCompleta(paradasSeleccionadas, usuarioActivo.getPreferenciaRuta());
            if (rutaOptima == null || rutaOptima.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No existe conexión vial entre algunos de los puntos seleccionados.", "Error de Ruteo", JOptionPane.ERROR_MESSAGE);
                return;
            }
            StringBuilder sbNodos = new StringBuilder();
            StringBuilder sbAristas = new StringBuilder();
            StringBuilder sbMixto = new StringBuilder();
            Nodo nodoInicial = rutaOptima.get(0).getNodoInicio();
            sbNodos.append(nodoInicial.getIdNodo());
            sbMixto.append(nodoInicial.getIdNodo());
            for (int i = 0; i < rutaOptima.size(); i++) {
                AristaRuta arista = rutaOptima.get(i);
                Nodo siguienteNodo = arista.getNodoFinal();
                String nombreAristaSeguro = arista.getNombreRuta().replaceAll("[-|:,]", " ").trim();
                sbAristas.append(nombreAristaSeguro);
                if (i < rutaOptima.size() - 1) {
                    sbAristas.append(FormateadorRutas.SEP_ARISTAS);
                }
                sbNodos.append(FormateadorRutas.SEP_NODOS).append(siguienteNodo.getIdNodo());
                sbMixto.append(FormateadorRutas.SEP_MIXTO).append(nombreAristaSeguro)
                        .append(FormateadorRutas.SEP_MIXTO).append(siguienteNodo.getIdNodo());
            }
            usuarioActivo.setRutaActualNodos(sbNodos.toString());
            usuarioActivo.setRutaActualAristas(sbAristas.toString());
            usuarioActivo.setRutaActual(sbMixto.toString());
            String histAux = usuarioActivo.getHistorialAuxiliar();
            if (histAux == null) histAux = "";
            if (histAux.isEmpty()) {
                histAux = sbAristas.toString();
            } else {
                histAux += FormateadorRutas.SEP_HISTORIAL + sbAristas.toString();
            }
            usuarioActivo.setHistorialAuxiliar(histAux);
            gestor.guardarUsuarios();
            GestorVentanas.cambiarVentana(this, new VentanaRutaActual(usuarioActivo, gestor, gestorNodos, gestorRutas));
        });
    }
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(new EmptyBorder(0, 20, 0, 8));
        bar.setPreferredSize(new Dimension(0, 48));
        JLabel logo = new JLabel("VELÓGRID");
        logo.setFont(new Font("Arial Black", Font.BOLD, 15));
        logo.setForeground(ORANGE_PRIMARY);
        bar.add(logo, BorderLayout.WEST);
        JLabel subtitle = new JLabel("Planificador de Rutas");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setBorder(new EmptyBorder(0, 14, 0, 0));
        bar.add(subtitle, BorderLayout.CENTER);
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        topRight.setOpaque(false);
        JButton btnVolver = makeIconButton("media/icon_back.png", 16, "Volver");
        btnVolver.addActionListener(e -> {
            GestorVentanas.cambiarVentana(this, new VentanaPrincipal(usuarioActivo, gestor, gestorNodos, gestorRutas));
        });
        topRight.add(btnVolver);
        GestorVentanas.agregarBotonesVentana(this, topRight);
        bar.add(topRight, BorderLayout.EAST);
        return bar;
    }
    private JPanel buildPlannerTab() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        JPanel selectorRow = new JPanel(new BorderLayout(12, 0));
        selectorRow.setOpaque(false);
        List<Nodo> listaNodosExtraidos = new ArrayList<>(gestorNodos.getMapaNodos().values());
        listaNodosExtraidos.sort((n1, n2) -> Integer.compare(n1.getIdNodo(), n2.getIdNodo()));
        Nodo[] arregloNodos = listaNodosExtraidos.toArray(new Nodo[0]);
        comboNodos = styledCombo(arregloNodos);
        selectorRow.add(comboNodos, BorderLayout.CENTER);
        btnAgregar = makeOrangeButton("+ Agregar parada");
        selectorRow.add(btnAgregar, BorderLayout.EAST);
        p.add(selectorRow, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);
        listaNodos = new JList<>(modeloLista);
        listaNodos.setBackground(BG_CARD);
        listaNodos.setForeground(TEXT_WHITE);
        listaNodos.setFont(new Font("Arial", Font.PLAIN, 13));
        listaNodos.setSelectionBackground(ORANGE_PRIMARY);
        listaNodos.setSelectionForeground(Color.WHITE);
        listaNodos.setBorder(new EmptyBorder(8, 12, 8, 12));
        listaNodos.setFixedCellHeight(32);
        JScrollPane scrollLista = new JScrollPane(listaNodos);
        aplicarEstiloScroll(scrollLista);
        centerPanel.add(scrollLista, BorderLayout.CENTER);
        rutaTextArea = new JTextArea();
        rutaTextArea.setEditable(false);
        rutaTextArea.setBackground(BG_CARD);
        rutaTextArea.setForeground(TEXT_WHITE);
        rutaTextArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        rutaTextArea.setCaretColor(ORANGE_PRIMARY);
        rutaTextArea.setBorder(new EmptyBorder(14, 16, 14, 16));
        rutaTextArea.setLineWrap(true);
        rutaTextArea.setWrapStyleWord(true);
        rutaTextArea.setText("Esperando puntos para la ruta...\n");
        JScrollPane scrollText = new JScrollPane(rutaTextArea);
        aplicarEstiloScroll(scrollText);
        scrollText.setPreferredSize(new Dimension(0, 90));
        centerPanel.add(scrollText, BorderLayout.SOUTH);
        p.add(centerPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 12));
        bottomPanel.setOpaque(false);
        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        btnEliminar = makeGhostButton("Eliminar selección");
        btnLimpiar  = makeGhostButton("Limpiar ruta");
        actions.add(btnEliminar);
        actions.add(btnLimpiar);
        btnGenerarRuta = makeOrangeButton("Generar Ruta Óptima");
        btnGenerarRuta.setPreferredSize(new Dimension(0, 45));
        bottomPanel.add(actions, BorderLayout.NORTH);
        bottomPanel.add(btnGenerarRuta, BorderLayout.SOUTH);
        p.add(bottomPanel, BorderLayout.SOUTH);
        return p;
    }
    private void actualizarVisualizacionRuta() {
        if (paradasSeleccionadas.isEmpty()) {
            rutaTextArea.setText("Recorrido limpiado.\n\nEsperando puntos...");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paradasSeleccionadas.size(); i++) {
            sb.append(paradasSeleccionadas.get(i).getNombreNodo());
            if (i < paradasSeleccionadas.size() - 1) {
                sb.append(" --> ");
            }
        }
        rutaTextArea.setText("Ruta planificada actual:\n\n");
        rutaTextArea.append(sb.toString() + "\n\n");
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
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
                g2.dispose();
            }
        };
    }
    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_WHITE);
        cb.setFont(new Font("Arial", Font.PLAIN, 13));
        cb.setBorder(BorderFactory.createLineBorder(INPUT_BORDER, 1));
        cb.setPreferredSize(new Dimension(0, 38));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ORANGE_PRIMARY : BG_CARD);
                setForeground(TEXT_WHITE);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        return cb;
    }
    private JButton makeOrangeButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(TEXT_WHITE);
        btn.setBackground(ORANGE_PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(210, 60, 0)); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(ORANGE_PRIMARY); btn.repaint(); }
        });
        return btn;
    }
    private JButton makeGhostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(INPUT_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(BG_CARD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(ORANGE_PRIMARY); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(TEXT_MUTED); btn.repaint(); }
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
            btn.setText(tooltip.equals("Volver") ? "<" : "X");
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
}