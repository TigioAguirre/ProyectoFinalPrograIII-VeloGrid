package com.veloGrid.ventanas;

import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.estructuras.PilaAlertas;
import com.veloGrid.estructuras.Nodo;
import com.veloGrid.clasesBase.AristaRuta;
import com.veloGrid.clasesBase.Incidente;
import com.veloGrid.clasesBase.TipoIncidente;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaAlertas extends JFrame {
    private JComboBox<TipoIncidente> comboTipo;
    private JTextField txtDescripcion;
    private JButton btnReportar;
    private JTable tablaRutas;
    private DefaultTableModel modeloRutas;
    private JTable tablaAlertas;
    private DefaultTableModel modeloAlertas;
    private JButton btnSolicitarEliminar;
    private PilaAlertas pilaIncidentes;
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color INPUT_BORDER   = new Color(50, 50, 65);
    private static final Color DANGER         = new Color(220, 50, 50);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);
    /**Constructores de la Ventana*/
    public VentanaAlertas(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        setTitle("VelóGRID — Alertas Comunitarias");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setMinimumSize(new Dimension(800, 500));
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(BG_DARK);
        pilaIncidentes = new PilaAlertas();
        cargarAlertasHistoricasCSV();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        setContentPane(root);
        JPanel _topBarPanel = buildTopBar();
        root.add(_topBarPanel, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, _topBarPanel);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_DARK);
        tabs.setForeground(TEXT_MUTED);
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.setBorder(new EmptyBorder(0, 0, 0, 0));
        UIManager.put("TabbedPane.selected",          BG_CARD);
        UIManager.put("TabbedPane.background",         BG_DARK);
        UIManager.put("TabbedPane.foreground",         TEXT_MUTED);
        UIManager.put("TabbedPane.selectedForeground", ORANGE_PRIMARY);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        tabs.addTab("Reportar Incidente", buildReportTab());
        tabs.addTab("Alertas Pendientes", buildStackTab());
        root.add(tabs, BorderLayout.CENTER);
        wireEvents();
        actualizarTablaAlertas();
    }
    /**Metodos de la Ventana*/
    private void wireEvents() {
        btnReportar.addActionListener(e -> {
            int filaSeleccionada = tablaRutas.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una ruta de la tabla para reportar un incidente.", "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idOrigen = (int) tablaRutas.getValueAt(filaSeleccionada, 0);
            int idDestino = (int) tablaRutas.getValueAt(filaSeleccionada, 1);
            TipoIncidente tipo = (TipoIncidente) comboTipo.getSelectedItem();
            String descripcion = txtDescripcion.getText().trim();
            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar una descripción.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Nodo nodoA = gestorNodos.buscarNodo(idOrigen);
            AristaRuta rutaAfectada = null;
            for (AristaRuta ruta : nodoA.getRutasSalientes()) {
                if (ruta.getNodoFinal().getIdNodo() == idDestino) {
                    rutaAfectada = ruta;
                    break;
                }
            }
            if (rutaAfectada != null && rutaAfectada.getIncidenteActivo() != null && rutaAfectada.getIncidenteActivo().getEstado().equals("ACTIVO")) {
                JOptionPane.showMessageDialog(this, "Esta ruta ya tiene un incidente activo reportado.", "Ruta bloqueada", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int nuevoId = generarNuevoIdAlerta();
            Incidente nuevoIncidente = new Incidente(nuevoId, idOrigen, idDestino, tipo, "N/A", descripcion, usuarioActivo.getNombreUsuario());
            pilaIncidentes.agregarAlerta(nuevoIncidente);
            rutaAfectada.setIncidenteActivo(nuevoIncidente);
            guardarAlertaCSV(nuevoIncidente);
            float[] limites = gestorRutas.getLimitesGlobales();
            rutaAfectada.calcularPonderaciones(limites[3], limites[2], limites[1], limites[0]);
            gestorRutas.guardarRutasCSV("baseDeDatos/rutasRegistradas.csv", gestorNodos);
            actualizarTablaAlertas();
            cargarRutasEnTabla();
            JOptionPane.showMessageDialog(this, "Incidente reportado exitosamente. La ruta ha sido bloqueada.");
            txtDescripcion.setText("");
        });
        btnSolicitarEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaAlertas.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una alerta de la tabla para votar por su eliminación.", "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idAlerta = (int) tablaAlertas.getValueAt(filaSeleccionada, 0);
            Incidente[] historial = pilaIncidentes.obtenerHistorial();
            Incidente alertaSeleccionada = null;
            for (Incidente inc : historial) {
                if (inc.getIdIncidente() == idAlerta) {
                    alertaSeleccionada = inc;
                    break;
                }
            }
            if (alertaSeleccionada != null) {
                alertaSeleccionada.registrarSolicitudEliminacion();
                actualizarVotoEnCSV(idAlerta, alertaSeleccionada.getSolicitudesEliminacion());
                actualizarTablaAlertas();
                JOptionPane.showMessageDialog(this, "Has votado para que el administrador elimine esta alerta.", "Voto Registrado", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    private void cargarRutasEnTabla() {
        if(modeloRutas == null) return;
        modeloRutas.setRowCount(0);
        for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
            for (AristaRuta ruta : nodo.getRutasSalientes()) {
                String estadoStr = "Normal";
                if (ruta.getIncidenteActivo() != null && ruta.getIncidenteActivo().getEstado().equals("ACTIVO")) {
                    estadoStr = "⚠️ INCIDENTE";
                }
                modeloRutas.addRow(new Object[]{
                        ruta.getNodoInicio().getIdNodo(),
                        ruta.getNodoFinal().getIdNodo(),
                        ruta.getNombreRuta(),
                        estadoStr
                });
            }
        }
    }
    private void actualizarTablaAlertas() {
        if(modeloAlertas == null) return;
        modeloAlertas.setRowCount(0);
        Incidente[] historial = pilaIncidentes.obtenerHistorial();
        for (Incidente inc : historial) {
            Nodo nodoOrigen = gestorNodos.buscarNodo(inc.getIdNodoOrigen());
            Nodo nodoDestino = gestorNodos.buscarNodo(inc.getIdNodoDestino());
            String nombreOrigen = (nodoOrigen != null) ? nodoOrigen.getNombreNodo() : "ID " + inc.getIdNodoOrigen();
            String nombreDestino = (nodoDestino != null) ? nodoDestino.getNombreNodo() : "ID " + inc.getIdNodoDestino();
            String rutaInfo = "De " + nombreOrigen + " a " + nombreDestino;
            modeloAlertas.addRow(new Object[]{
                    inc.getIdIncidente(),
                    rutaInfo,
                    inc.getTipoIncidente().toString(),
                    inc.getSolicitudesEliminacion()
            });
        }
    }
    private void cargarAlertasHistoricasCSV() {
        File archivo = new File("baseDeDatos/alertas.csv");
        if (!archivo.exists()) return;
        float[] limites = gestorRutas.getLimitesGlobales();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] d = linea.split(",");
                if (d.length >= 10) {
                    int id = Integer.parseInt(d[0]);
                    int idOrigen = Integer.parseInt(d[1]);
                    int idDestino = Integer.parseInt(d[2]);
                    TipoIncidente tipo = TipoIncidente.valueOf(d[3]);
                    String grav = d[4];
                    String desc = d[5];
                    String usr = d[6];
                    String fecha = d[7];
                    String estado = d[8];
                    int solicitudes = Integer.parseInt(d[9]);
                    Incidente inc = new Incidente(id, idOrigen, idDestino, tipo, grav, desc, usr, fecha, estado, solicitudes);
                    if (estado.equals("ACTIVO")) {
                        pilaIncidentes.agregarAlerta(inc);
                        Nodo n = gestorNodos.buscarNodo(idOrigen);
                        if (n != null) {
                            for (AristaRuta arista : n.getRutasSalientes()) {
                                if (arista.getNodoFinal().getIdNodo() == idDestino) {
                                    arista.setIncidenteActivo(inc);
                                    arista.calcularPonderaciones(limites[3], limites[2], limites[1], limites[0]);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando historial de alertas: " + e.getMessage());
        }
    }
    private int generarNuevoIdAlerta() {
        int maxId = 0;
        File archivo = new File("baseDeDatos/alertas.csv");
        if (!archivo.exists()) return 1;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] d = linea.split(",");
                int id = Integer.parseInt(d[0]);
                if (id > maxId) maxId = id;
            }
        } catch (Exception e) {}
        return maxId + 1;
    }
    private void guardarAlertaCSV(Incidente inc) {
        File archivo = new File("baseDeDatos/alertas.csv");
        boolean nuevo = !archivo.exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, true))) {
            if (nuevo) pw.println("idAlerta,idNodoOrigen,idNodoDestino,tipoIncidente,gravedad,descripcion,usuarioReporta,fechaReporte,estado,solicitudesEliminacion");
            String descripcionSegura = inc.getDescripcion().replace(",", " -").replace("\n", " ");
            pw.println(inc.getIdIncidente() + "," + inc.getIdNodoOrigen() + "," + inc.getIdNodoDestino() + "," +
                    inc.getTipoIncidente() + "," + inc.getGravedad() + "," + descripcionSegura + "," +
                    inc.getUsuarioReporta() + "," + inc.getFechaReporte() + "," + inc.getEstado() + "," +
                    inc.getSolicitudesEliminacion());
        } catch (IOException e) { e.printStackTrace(); }
    }
    private void actualizarVotoEnCSV(int idAlerta, int nuevosVotos) {
        File archivo = new File("baseDeDatos/alertas.csv");
        if (!archivo.exists()) return;
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] d = linea.split(",");
                if (d[0].equals(String.valueOf(idAlerta))) {
                    linea = d[0]+","+d[1]+","+d[2]+","+d[3]+","+d[4]+","+d[5]+","+d[6]+","+d[7]+","+d[8]+","+nuevosVotos;
                }
                lineas.add(linea);
            }
        } catch (Exception e) { e.printStackTrace(); }

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (String l : lineas) pw.println(l);
        } catch (Exception e) { e.printStackTrace(); }
    }
    /**Diseño de la Ventana*/
    private JTable crearTablaEstandar(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TEXT_WHITE);
        tabla.setGridColor(DIVIDER);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(BG_DARK);
        tabla.getTableHeader().setForeground(ORANGE_PRIMARY);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.setSelectionBackground(ORANGE_PRIMARY);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
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
            @Override protected JButton createDecreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(BG_DARK); g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
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
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        l.setBorder(new EmptyBorder(0, 2, 4, 0));
        return l;
    }
    private JTextField styledTextField() {
        JTextField f = new JTextField();
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_WHITE);
        f.setCaretColor(ORANGE_PRIMARY);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)));
        f.setPreferredSize(new Dimension(0, 38));
        return f;
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
        btn.setPreferredSize(new Dimension(300, 40));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(210, 60, 0)); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(ORANGE_PRIMARY); btn.repaint(); }
        });
        return btn;
    }
    private JButton makeDangerButton(String text) {
        JButton btn = makeOrangeButton(text);
        btn.setBackground(DANGER);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(180, 30, 30)); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(DANGER); btn.repaint(); }
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
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(new EmptyBorder(0, 20, 0, 8));
        bar.setPreferredSize(new Dimension(0, 48));
        JLabel logo = new JLabel("VELÓGRID");
        logo.setFont(new Font("Arial Black", Font.BOLD, 15));
        logo.setForeground(ORANGE_PRIMARY);
        bar.add(logo, BorderLayout.WEST);
        JLabel subtitle = new JLabel("Módulo de Alertas Comunitarias");
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
    private JPanel buildReportTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setOpaque(false);
        panelFormulario.setBorder(new EmptyBorder(10, 10, 10, 20));
        JLabel lblInstrucciones = new JLabel("<html><body>Selecciona una ruta en la tabla<br>y describe el problema:</body></html>");
        lblInstrucciones.setFont(new Font("Arial", Font.BOLD, 14));
        lblInstrucciones.setForeground(TEXT_WHITE);
        panelFormulario.add(lblInstrucciones);
        panelFormulario.add(Box.createVerticalStrut(25));
        comboTipo = styledCombo(TipoIncidente.values());
        txtDescripcion = styledTextField();
        panelFormulario.add(makeLabel("Tipo de incidente:"));
        panelFormulario.add(comboTipo);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(makeLabel("Breve descripción del problema:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(Box.createVerticalStrut(30));
        btnReportar = makeOrangeButton("REPORTAR INCIDENTE");
        panelFormulario.add(btnReportar);
        gbcMain.gridx = 0; gbcMain.gridy = 0; gbcMain.weightx = 0.35;
        p.add(panelFormulario, gbcMain);
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setOpaque(false);
        JLabel lblRutas = new JLabel("Rutas Activas (Selecciona para reportar):");
        lblRutas.setForeground(TEXT_WHITE);
        lblRutas.setFont(new Font("Arial", Font.BOLD, 12));
        panelTabla.add(lblRutas, BorderLayout.NORTH);
        String[] columnas = {"Origen", "Destino", "Ruta", "Estado"};
        modeloRutas = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRutas = crearTablaEstandar(modeloRutas);
        tablaRutas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaRutas.getColumnModel().getColumn(1).setPreferredWidth(40);
        tablaRutas.getColumnModel().getColumn(2).setPreferredWidth(180);
        cargarRutasEnTabla();
        JScrollPane scrollRutas = new JScrollPane(tablaRutas);
        aplicarEstiloScroll(scrollRutas);
        panelTabla.add(scrollRutas, BorderLayout.CENTER);
        gbcMain.gridx = 1; gbcMain.gridy = 0; gbcMain.weightx = 0.65;
        p.add(panelTabla, gbcMain);
        return p;
    }
    private JPanel buildStackTab() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        JLabel lblTitle = new JLabel("Selecciona una alerta para votar por su eliminación:");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(TEXT_WHITE);
        p.add(lblTitle, BorderLayout.NORTH);
        String[] columnas = {"ID", "Ruta Afectada", "Problema", "Votos p/Eliminar"};
        modeloAlertas = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaAlertas = crearTablaEstandar(modeloAlertas);
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaAlertas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaAlertas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaAlertas.getColumnModel().getColumn(3).setPreferredWidth(100);
        JScrollPane scrollAlertas = new JScrollPane(tablaAlertas);
        aplicarEstiloScroll(scrollAlertas);
        p.add(scrollAlertas, BorderLayout.CENTER);
        btnSolicitarEliminar = makeDangerButton("SOLICITAR ELIMINAR ALERTA (VOTAR)");
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(btnSolicitarEliminar);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }
}