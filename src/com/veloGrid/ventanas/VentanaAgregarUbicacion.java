package com.veloGrid.ventanas;

import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class VentanaAgregarUbicacion extends JFrame {
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color BG_CARD_HOVER  = new Color(30, 30, 42);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    /**Constructores de la Ventana*/
    public VentanaAgregarUbicacion(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        initUI();
    }
    /**Metodos de la Ventana*/
    private void initUI() {
        setTitle("VelóGRID — Seleccionar Tipo de Ubicación");
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
        JLabel logo = new JLabel("AGREGAR NUEVA UBICACIÓN");
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
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(40, 60, 50, 60));
        JLabel lblInstruccion = new JLabel("¿Qué tipo de ubicación deseas registrar en el mapa?");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 18));
        lblInstruccion.setForeground(TEXT_WHITE);
        lblInstruccion.setHorizontalAlignment(SwingConstants.CENTER);
        body.add(lblInstruccion, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, 2, 20, 0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(30, 0, 0, 0));
        cards.add(makeModuleCard("INTERSECCIÓN / NODO", "Punto de paso común o cruce de calles en la ruta.", "→", e -> {
            GestorVentanas.cambiarVentana(this, new VentanaAgregarNodo(usuarioActivo, gestor, gestorNodos, gestorRutas));
        }));
        cards.add(makeModuleCard("PUNTO DE INTERÉS", "Taller, tienda o lugar relevante con horarios y descripción.", "→", e -> {
            GestorVentanas.cambiarVentana(this, new VentanaAgregarPuntoInteres(usuarioActivo, gestor, gestorNodos, gestorRutas));
        }));
        body.add(cards, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }
    /**Diseño de la Ventana*/
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
    private JPanel makeModuleCard(String title, String desc, String arrow, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout()) {
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
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 13));
        lblTitle.setForeground(ORANGE_PRIMARY);
        JLabel lblDesc = new JLabel("<html><body>" + desc + "</body></html>");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDesc.setForeground(TEXT_MUTED);
        lblDesc.setBorder(new EmptyBorder(8, 0, 0, 0));
        JLabel lblArrow = new JLabel(arrow);
        lblArrow.setFont(new Font("Arial", Font.BOLD, 18));
        lblArrow.setForeground(ORANGE_PRIMARY);
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(lblTitle);
        textPanel.add(lblDesc);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblArrow, BorderLayout.EAST);
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(BG_CARD_HOVER); card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(BG_CARD); card.repaint(); }
            @Override public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
        });
        return card;
    }
}