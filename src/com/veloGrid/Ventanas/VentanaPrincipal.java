package com.veloGrid.Ventanas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaPrincipal extends JFrame {

    private static final Color BG_DARK       = new Color(15, 15, 20);
    private static final Color BG_CARD       = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color ORANGE_HOVER  = new Color(255, 100, 30);
    private static final Color TEXT_WHITE    = new Color(240, 240, 240);
    private static final Color TEXT_MUTED    = new Color(140, 140, 155);
    private static final Color DIVIDER       = new Color(35, 35, 45);

    private JButton btnAbrirRutas;
    private JButton btnAbrirAlertas;

    public VentanaPrincipal() {
        setTitle("VeloGrid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint gp = new GradientPaint(0, 0, new Color(252, 76, 2, 30), getWidth(), getHeight(), new Color(0, 0, 0, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(40, 36, 20, 36));

        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoArea.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Arial", Font.BOLD, 22));
        dot.setForeground(ORANGE_PRIMARY);

        JLabel title = new JLabel(" VeloGrid");
        title.setFont(new Font("Arial Black", Font.BOLD, 26));
        title.setForeground(TEXT_WHITE);

        logoArea.add(dot);
        logoArea.add(title);
        header.add(logoArea, BorderLayout.WEST);

        JLabel version = new JLabel("v2.0");
        version.setFont(new Font("Arial", Font.PLAIN, 11));
        version.setForeground(TEXT_MUTED);
        header.add(version, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(header, BorderLayout.NORTH);

        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setOpaque(false);
        heroPanel.setBorder(new EmptyBorder(10, 36, 30, 36));

        JLabel heroTitle = new JLabel("<html><span style='color:#FC4C02;'>Planifica.</span> Pedalea.<br>Conquista.</html>");
        heroTitle.setFont(new Font("Arial Black", Font.BOLD, 28));
        heroTitle.setForeground(TEXT_WHITE);
        heroPanel.add(heroTitle, BorderLayout.CENTER);

        JLabel subtitle = new JLabel("Ciclovia Quito — Sistema de Navegacion Urbana");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setBorder(new EmptyBorder(10, 0, 0, 0));
        heroPanel.add(subtitle, BorderLayout.SOUTH);

        wrapper.add(heroPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 28, 0, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JPanel statsRow = buildStatsRow();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        center.add(statsRow, gbc);

        btnAbrirRutas = buildModuleCard(
                "Planificador de Rutas",
                "Cola FIFO  •  19 paradas disponibles",
                "ABRIR MODULO",
                false
        );
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 14, 0);
        center.add(btnAbrirRutas, gbc);

        btnAbrirAlertas = buildModuleCard(
                "Alertas Comunitarias",
                "Pila LIFO  •  Incidentes en tiempo real",
                "ABRIR MODULO",
                true
        );
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        center.add(btnAbrirAlertas, gbc);

        btnAbrirRutas.addActionListener(e -> {
            VentanaRutas vr = new VentanaRutas();
            vr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            vr.setVisible(true);
        });

        btnAbrirAlertas.addActionListener(e -> {
            VentanaAlertas va = new VentanaAlertas();
            va.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            va.setVisible(true);
        });

        return center;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
        row.setOpaque(false);
        row.add(buildStatCard("19", "Paradas"));
        row.add(buildStatCard("~22km", "Recorrido"));
        row.add(buildStatCard("7", "Alertas"));
        return row;
    }

    private JPanel buildStatCard(String value, String label) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(DIVIDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 10, 14, 10));

        JLabel valLabel = new JLabel(value, SwingConstants.CENTER);
        valLabel.setFont(new Font("Arial Black", Font.BOLD, 20));
        valLabel.setForeground(ORANGE_PRIMARY);

        JLabel lblLabel = new JLabel(label, SwingConstants.CENTER);
        lblLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        lblLabel.setForeground(TEXT_MUTED);

        card.add(valLabel, BorderLayout.CENTER);
        card.add(lblLabel, BorderLayout.SOUTH);
        return card;
    }

    private JButton buildModuleCard(String title, String subtitle, String btnText, boolean muted) {
        JButton card = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(28, 28, 38) : BG_CARD;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                Color border = getModel().isRollover() ? ORANGE_PRIMARY : DIVIDER;
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 18, 18));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setContentAreaFilled(false);
        card.setBorderPainted(false);
        card.setFocusPainted(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 15));
        titleLabel.setForeground(TEXT_WHITE);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subLabel.setForeground(TEXT_MUTED);

        text.add(titleLabel, BorderLayout.NORTH);
        text.add(subLabel, BorderLayout.CENTER);

        JLabel arrow = new JLabel(btnText + "  →");
        arrow.setFont(new Font("Arial", Font.BOLD, 10));
        arrow.setForeground(muted ? TEXT_MUTED : ORANGE_PRIMARY);

        card.add(text, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);
        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 24, 0));

        JLabel foot = new JLabel("VeloGrid  •  Ciclovia Quito  •  2025");
        foot.setFont(new Font("Arial", Font.PLAIN, 10));
        foot.setForeground(new Color(70, 70, 85));
        footer.add(foot);
        return footer;
    }
}
