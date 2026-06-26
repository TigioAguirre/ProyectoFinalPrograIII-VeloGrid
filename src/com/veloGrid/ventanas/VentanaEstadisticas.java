package com.veloGrid.ventanas;

import com.veloGrid.estructuras.*;
import com.veloGrid.clasesBase.TipoBicicleta;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class VentanaEstadisticas extends JFrame {
    private final Usuario usuarioActivo;
    private final GestorUsuarios gestor;
    private JLabel lblImagenBicicleta;
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);
    /**Constructores de la Ventana*/
    public VentanaEstadisticas(Usuario usuarioActivo, GestorUsuarios gestor) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        initUI();
    }
    /**Metodos de la Ventana*/
    private void initUI() {
        setTitle("VelóGRID — Estadísticas e Historial");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setMinimumSize(new Dimension(750, 550));
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
        JLabel logo = new JLabel("VELÓGRID - MIS ESTADÍSTICAS");
        logo.setFont(new Font("Arial Black", Font.BOLD, 14));
        logo.setForeground(ORANGE_PRIMARY);
        topBar.add(logo, BorderLayout.WEST);
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        topRight.setOpaque(false);
        JButton btnVolver = makeIconButton("media/icon_back.png", 16, "Volver");
        btnVolver.addActionListener(e -> {
            GestorVentanas.cambiarVentana(this, new VentanaPrincipal(usuarioActivo, gestor));
        });
        topRight.add(btnVolver);
        GestorVentanas.agregarBotonesVentana(this, topRight);
        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, topBar);
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));
        JPanel topContent = new JPanel(new BorderLayout(20, 0));
        topContent.setOpaque(false);
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);
        statsPanel.add(makeStatCard("Km Recorridos", String.format("%.1f", usuarioActivo.getKmRecorridos()) + " km"));
        statsPanel.add(makeStatCard("Horas Movimiento", String.format("%.1f", usuarioActivo.getHorasMovimiento()) + " h"));
        statsPanel.add(makeStatCard("Desnivel Positivo", String.format("%.0f", usuarioActivo.getDesnivelPosAcum()) + " m"));
        String nivel = usuarioActivo.getNivelExperiencia() != null ? usuarioActivo.getNivelExperiencia().name() : "N/A";
        statsPanel.add(makeStatCard("Nivel", nivel));
        topContent.add(statsPanel, BorderLayout.CENTER);
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setOpaque(false);
        imageContainer.setPreferredSize(new Dimension(200, 0));
        JLabel lblBikeTitle = new JLabel("MI BICICLETA");
        lblBikeTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblBikeTitle.setForeground(TEXT_MUTED);
        lblBikeTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblBikeTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        imageContainer.add(lblBikeTitle, BorderLayout.NORTH);
        JPanel imageBox = new JPanel(new BorderLayout());
        imageBox.setBackground(BG_CARD);
        imageBox.setBorder(BorderFactory.createLineBorder(DIVIDER, 2));
        lblImagenBicicleta = new JLabel("", SwingConstants.CENTER);
        lblImagenBicicleta.setForeground(TEXT_MUTED);
        lblImagenBicicleta.setFont(new Font("Arial", Font.ITALIC, 12));
        imageBox.add(lblImagenBicicleta, BorderLayout.CENTER);
        imageContainer.add(imageBox, BorderLayout.CENTER);
        String tipoBiciStr = usuarioActivo.getTipoBicicleta() != null ? usuarioActivo.getTipoBicicleta().name() : "Sin definir";
        JLabel lblTipoBici = new JLabel(tipoBiciStr);
        lblTipoBici.setFont(new Font("Arial", Font.BOLD, 14));
        lblTipoBici.setForeground(ORANGE_PRIMARY);
        lblTipoBici.setHorizontalAlignment(SwingConstants.CENTER);
        lblTipoBici.setBorder(new EmptyBorder(5, 0, 0, 0));
        imageContainer.add(lblTipoBici, BorderLayout.SOUTH);
        topContent.add(imageContainer, BorderLayout.EAST);
        body.add(topContent, BorderLayout.NORTH);
        JPanel bottomContent = new JPanel(new BorderLayout(0, 10));
        bottomContent.setOpaque(false);
        JLabel lblHistorial = new JLabel("HISTORIAL DE VIAJES");
        lblHistorial.setFont(new Font("Arial", Font.BOLD, 14));
        lblHistorial.setForeground(TEXT_MUTED);
        bottomContent.add(lblHistorial, BorderLayout.NORTH);
        String[] columnas = {"Fecha y Hora", "Ruta (Calles Transitadas)"};
        modeloHistorial = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setBackground(BG_CARD);
        tablaHistorial.setForeground(TEXT_WHITE);
        tablaHistorial.setGridColor(DIVIDER);
        tablaHistorial.setRowHeight(32);
        tablaHistorial.getTableHeader().setBackground(BG_DARK);
        tablaHistorial.getTableHeader().setForeground(ORANGE_PRIMARY);
        tablaHistorial.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaHistorial.getTableHeader().setReorderingAllowed(false);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(140);
        tablaHistorial.getColumnModel().getColumn(0).setMaxWidth(160);
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        aplicarEstiloScroll(scrollHistorial);
        bottomContent.add(scrollHistorial, BorderLayout.CENTER);
        body.add(bottomContent, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        actualizarImagenBicicleta(usuarioActivo.getTipoBicicleta());
        cargarHistorialRutas();
    }
    private void cargarHistorialRutas() {
        String historialGlobal = usuarioActivo.getHistorial();

        if (historialGlobal == null || historialGlobal.trim().isEmpty()) {
            modeloHistorial.addRow(new Object[]{"-", "Aún no has completado ninguna ruta."});
            return;
        }
        String[] viajes = historialGlobal.split("\\|");
        for (String viaje : viajes) {
            String[] partes = viaje.split(":", 3);
            if (partes.length == 3) {
                String fechaHora = partes[0].trim() + ":" + partes[1];
                String ruta = partes[2].trim();
                modeloHistorial.addRow(new Object[]{fechaHora, ruta});
            } else {
                modeloHistorial.addRow(new Object[]{"N/A", viaje});
            }
        }
    }
    private void actualizarImagenBicicleta(TipoBicicleta tipo) {
        if (tipo == null) {
            lblImagenBicicleta.setText("Configura tu perfil");
            return;
        }
        String rutaImagen = tipo.getRutaImagen();
        File imgFile = new File(rutaImagen);
        if (imgFile.exists()) {
            ImageIcon iconoOriginal = new ImageIcon(rutaImagen);
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            lblImagenBicicleta.setIcon(new ImageIcon(imagenEscalada));
            lblImagenBicicleta.setText("");
        } else {
            lblImagenBicicleta.setText("<html><center>Imagen no encontrada:<br>" + rutaImagen + "</center></html>");
        }
    }
    /**Diseño de la Ventana*/
    private JPanel makeStatCard(String title, String value) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(TEXT_MUTED);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial Black", Font.BOLD, 22));
        lblValue.setForeground(TEXT_WHITE);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);
        return card;
    }
    private JButton makeIconButton(String iconPath, int size, String tooltip) {
        JButton btn = new JButton();
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        } catch(Exception e) {
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
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = SCROLL_THUMB; this.trackColor = BG_DARK; }
            @Override protected JButton createDecreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
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
        });
    }
}