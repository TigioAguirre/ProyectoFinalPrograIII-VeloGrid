package com.veloGrid.ventanas;

import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.estructuras.Nodo;
import com.veloGrid.clasesBase.AristaRuta;
import com.veloGrid.clasesBase.Incidente;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaAdmin extends JFrame {
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;
    private JTable tablaRutas;
    private DefaultTableModel modeloRutas;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color DANGER         = new Color(220, 50, 50);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);
    /**Constructores de la Ventana*/
    public VentanaAdmin(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        initUI();
        cargarDatosTablaPrioridad();
    }
    /**Metodos de la Ventana*/
    private void initUI() {
        setTitle("VelóGRID — Panel de Administrador");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setMinimumSize(new Dimension(800, 600));
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JLabel logo = new JLabel("VELÓGRID - PANEL DE ADMINISTRADOR");
        logo.setFont(new Font("Arial Black", Font.BOLD, 14));
        logo.setForeground(ORANGE_PRIMARY);
        topBar.add(logo, BorderLayout.WEST);
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        topRight.setOpaque(false);
        JButton btnVolver = makeIconButton("media/icon_back.png", 16, "Volver");
        btnVolver.addActionListener(e -> {
            GestorVentanas.cambiarVentana(this, new VentanaPrincipal(usuarioActivo, gestor, gestorNodos, gestorRutas));
        });
        topRight.add(btnVolver);
        GestorVentanas.agregarBotonesVentana(this, topRight);
        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, topBar);
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(25, 40, 25, 40));
        JLabel lblTitle = new JLabel("Monitor de Rutas e Incidentes Críticos");
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(lblTitle);
        body.add(Box.createVerticalStrut(15));
        String[] columnas = {"Origen", "Destino", "Ruta", "Estado / Incidente", "Votos p/Eliminar"};
        modeloRutas = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRutas = new JTable(modeloRutas);
        tablaRutas.setBackground(BG_CARD);
        tablaRutas.setForeground(TEXT_WHITE);
        tablaRutas.setGridColor(DIVIDER);
        tablaRutas.setRowHeight(25);
        tablaRutas.getTableHeader().setBackground(BG_DARK);
        tablaRutas.getTableHeader().setForeground(ORANGE_PRIMARY);
        tablaRutas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaRutas.setSelectionBackground(ORANGE_PRIMARY);
        tablaRutas.setSelectionForeground(Color.WHITE);
        tablaRutas.getTableHeader().setReorderingAllowed(false);
        tablaRutas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaRutas.getColumnModel().getColumn(1).setPreferredWidth(40);
        tablaRutas.getColumnModel().getColumn(2).setPreferredWidth(200);
        tablaRutas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaRutas.getColumnModel().getColumn(4).setPreferredWidth(100);
        JScrollPane scrollTabla = new JScrollPane(tablaRutas);
        aplicarEstiloScroll(scrollTabla);
        scrollTabla.setPreferredSize(new Dimension(700, 150)); // Altura controlada
        scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        body.add(scrollTabla);
        body.add(Box.createVerticalStrut(10));
        JButton btnGestionar = new JButton("ATENDER / ELIMINAR INCIDENTE");
        btnGestionar.setFont(new Font("Arial Black", Font.BOLD, 12));
        btnGestionar.setForeground(TEXT_WHITE);
        btnGestionar.setBackground(DANGER);
        btnGestionar.setFocusPainted(false);
        btnGestionar.setBorderPainted(false);
        btnGestionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGestionar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGestionar.setMaximumSize(new Dimension(300, 35));
        btnGestionar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnGestionar.setBackground(DANGER.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { btnGestionar.setBackground(DANGER); }
        });

        btnGestionar.addActionListener(e -> procesarAtencionIncidente());
        body.add(btnGestionar);
        body.add(Box.createVerticalStrut(30));
        JPanel cards = new JPanel(new GridLayout(2, 2, 20, 20));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(800, 200)); // Limitar tamaño de las tarjetas
        cards.add(makeAdminCard(
                "ELIMINAR LUGARES",
                "Revisar solicitudes de eliminación de Nodos o POIs por parte de los usuarios.",
                "GESTIONAR",
                e -> {
                    GestorVentanas.cambiarVentana(this, new VentanaEliminacionAdmin(usuarioActivo, gestor, gestorNodos, gestorRutas));
                }
        ));
        cards.add(makeAdminCard(
                "VERIFICAR LUGARES",
                "Aprobar o rechazar nuevos Nodos y Rutas ingresados por la comunidad.",
                "GESTIONAR",
                e -> {
                    GestorVentanas.cambiarVentana(this, new VentanaVerificarLugar(usuarioActivo, gestor, gestorNodos, gestorRutas));
                }
        ));
        cards.add(makeAdminCard("GESTIÓN DE USUARIOS", "Administrar cuentas y asignar roles.", "PRÓXIMAMENTE", null));
        cards.add(makeAdminCard("SISTEMA", "Configuraciones globales de la base de datos.", "PRÓXIMAMENTE", null));
        body.add(cards);
        root.add(body, BorderLayout.CENTER);
    }
    private void cargarDatosTablaPrioridad() {
        modeloRutas.setRowCount(0);
        List<AristaRuta> todasLasRutas = new ArrayList<>();
        for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
            todasLasRutas.addAll(nodo.getRutasSalientes());
        }
        todasLasRutas.sort((r1, r2) -> {
            int votos1 = (r1.getIncidenteActivo() != null && r1.getIncidenteActivo().getEstado().equals("ACTIVO"))
                    ? r1.getIncidenteActivo().getSolicitudesEliminacion() : -1;
            int votos2 = (r2.getIncidenteActivo() != null && r2.getIncidenteActivo().getEstado().equals("ACTIVO"))
                    ? r2.getIncidenteActivo().getSolicitudesEliminacion() : -1;
            return Integer.compare(votos2, votos1); // Descendente
        });
        for (AristaRuta ruta : todasLasRutas) {
            String estadoStr = "✅ Normal";
            String votosStr = "-";
            if (ruta.getIncidenteActivo() != null && ruta.getIncidenteActivo().getEstado().equals("ACTIVO")) {
                estadoStr = "⚠️ " + ruta.getIncidenteActivo().getTipoIncidente();
                votosStr = String.valueOf(ruta.getIncidenteActivo().getSolicitudesEliminacion());
            }
            modeloRutas.addRow(new Object[]{
                    ruta.getNodoInicio().getIdNodo(),
                    ruta.getNodoFinal().getIdNodo(),
                    ruta.getNombreRuta(),
                    estadoStr,
                    votosStr
            });
        }
    }
    private void procesarAtencionIncidente() {
        int filaSeleccionada = tablaRutas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una ruta de la tabla para atender su incidente.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idOrigen = (int) tablaRutas.getValueAt(filaSeleccionada, 0);
        int idDestino = (int) tablaRutas.getValueAt(filaSeleccionada, 1);
        Nodo nodoA = gestorNodos.buscarNodo(idOrigen);
        AristaRuta rutaAfectada = null;
        for (AristaRuta ruta : nodoA.getRutasSalientes()) {
            if (ruta.getNodoFinal().getIdNodo() == idDestino) {
                rutaAfectada = ruta;
                break;
            }
        }
        if (rutaAfectada != null && rutaAfectada.getIncidenteActivo() != null && rutaAfectada.getIncidenteActivo().getEstado().equals("ACTIVO")) {
            Incidente inc = rutaAfectada.getIncidenteActivo();
            inc.atenderIncidente();
            float[] limites = gestorRutas.getLimitesGlobales();
            rutaAfectada.calcularPonderaciones(limites[3], limites[2], limites[1], limites[0]);
            actualizarEstadoAlertaCSV(inc.getIdIncidente(), "ATENDIDO");
            gestorRutas.guardarRutasCSV("baseDeDatos/rutasRegistradas.csv", gestorNodos);
            cargarDatosTablaPrioridad();
            JOptionPane.showMessageDialog(this, "El incidente ha sido marcado como ATENDIDO. La ruta vuelve a estar operativa.", "Incidente Resuelto", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "La ruta seleccionada no tiene ningún incidente activo en este momento.", "Ruta Operativa", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void actualizarEstadoAlertaCSV(int idAlerta, String nuevoEstado) {
        File archivo = new File("baseDeDatos/alertas.csv");
        if (!archivo.exists()) return;
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] d = linea.split(",");
                if (d.length >= 10 && d[0].equals(String.valueOf(idAlerta))) {
                    d[8] = nuevoEstado;
                    linea = String.join(",", d);
                }
                lineas.add(linea);
            }
        } catch (Exception e) { e.printStackTrace(); }
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (String l : lineas) pw.println(l);
        } catch (Exception e) { e.printStackTrace(); }
    }
    /**Diseño de la Ventana*/
    private JPanel makeAdminCard(String title, String desc, String btnText, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblTitle.setForeground(ORANGE_PRIMARY);
        JLabel lblDesc = new JLabel("<html><body>" + desc + "</body></html>");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDesc.setForeground(TEXT_MUTED);
        lblDesc.setBorder(new EmptyBorder(5, 0, 0, 0));
        textPanel.add(lblTitle);
        textPanel.add(lblDesc);
        card.add(textPanel, BorderLayout.CENTER);
        JButton btnAction = new JButton(btnText);
        btnAction.setFont(new Font("Arial Black", Font.BOLD, 10));
        btnAction.setForeground(TEXT_WHITE);
        btnAction.setBackground(action == null ? DIVIDER : ORANGE_PRIMARY);
        btnAction.setFocusPainted(false);
        btnAction.setBorderPainted(false);
        btnAction.setCursor(action == null ? new Cursor(Cursor.DEFAULT_CURSOR) : new Cursor(Cursor.HAND_CURSOR));
        btnAction.setPreferredSize(new Dimension(0, 30));
        if (action != null) {
            btnAction.addActionListener(action);
            btnAction.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btnAction.setBackground(ORANGE_PRIMARY.brighter()); }
                @Override public void mouseExited(MouseEvent e)  { btnAction.setBackground(ORANGE_PRIMARY); }
            });
        }
        card.add(btnAction, BorderLayout.SOUTH);
        return card;
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
}