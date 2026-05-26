package com.veloGrid.Ventanas;

import com.veloGrid.estructuras.PilaAlertas;
import com.veloGrid.modelo.Coordenada;
import com.veloGrid.modelo.Incidente;
import com.veloGrid.modelo.TipoIncidente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaAlertas extends JFrame {

    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 45);
    private static final Color DANGER         = new Color(220, 60, 60);
    private static final Color SUCCESS        = new Color(50, 200, 120);

    private JTextField txtId;
    private JComboBox<TipoIncidente> comboTipo;
    private JTextField txtX;
    private JTextField txtY;
    private JButton btnReportar;
    private JButton btnAtender;
    private JTextArea areaAlertas;
    private JLabel lblContador;

    private PilaAlertas pilaIncidentes;

    public VentanaAlertas() {
        setTitle("VeloGrid — Alertas Comunitarias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        pilaIncidentes = new PilaAlertas();

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(18, 22, 18, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.42; gbc.insets = new Insets(0, 0, 0, 12);
        body.add(buildFormPanel(), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.58; gbc.insets = new Insets(0, 0, 0, 0);
        body.add(buildStackPanel(), gbc);

        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_CARD);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(DIVIDER);
                g.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(16, 22, 16, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        JLabel dot = new JLabel("●  ");
        dot.setFont(new Font("Arial", Font.BOLD, 14));
        dot.setForeground(DANGER);

        JLabel title = new JLabel("Alertas Comunitarias");
        title.setFont(new Font("Arial Black", Font.BOLD, 16));
        title.setForeground(TEXT_WHITE);

        left.add(dot);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);

        lblContador = new JLabel("Pila vacia");
        lblContador.setFont(new Font("Arial", Font.BOLD, 11));
        lblContador.setForeground(TEXT_MUTED);

        right.add(lblContador);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildFormPanel() {
        JPanel panel = buildCard();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel sectionLabel = new JLabel("REPORTAR INCIDENTE");
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 10));
        sectionLabel.setForeground(DANGER);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 14, 0);
        panel.add(sectionLabel, gbc);

        panel.add(buildFieldLabel("ID del Incidente"), fieldGbc(gbc, 1, new Insets(0, 0, 4, 0)));
        txtId = buildTextField("Ej: 101");
        panel.add(txtId, fieldGbc(gbc, 2, new Insets(0, 0, 10, 0)));

        panel.add(buildFieldLabel("Tipo de Incidente"), fieldGbc(gbc, 3, new Insets(0, 0, 4, 0)));
        comboTipo = new JComboBox<>(TipoIncidente.values());
        styleCombo(comboTipo);
        panel.add(comboTipo, fieldGbc(gbc, 4, new Insets(0, 0, 10, 0)));

        panel.add(buildFieldLabel("Coordenada X"), fieldGbc(gbc, 5, new Insets(0, 0, 4, 0)));
        txtX = buildTextField("Ej: -0.2148");
        panel.add(txtX, fieldGbc(gbc, 6, new Insets(0, 0, 10, 0)));

        panel.add(buildFieldLabel("Coordenada Y"), fieldGbc(gbc, 7, new Insets(0, 0, 4, 0)));
        txtY = buildTextField("Ej: -78.5001");
        panel.add(txtY, fieldGbc(gbc, 8, new Insets(0, 0, 14, 0)));

        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER);
        sep.setBackground(DIVIDER);
        panel.add(sep, fieldGbc(gbc, 9, new Insets(0, 0, 10, 0)));

        btnReportar = buildActionButton("REPORTAR ALERTA", DANGER, Color.WHITE);
        panel.add(btnReportar, fieldGbc(gbc, 10, new Insets(0, 0, 8, 0)));

        btnAtender = buildActionButton("ATENDER ULTIMA ALERTA", new Color(30, 30, 40), SUCCESS);
        panel.add(btnAtender, fieldGbc(gbc, 11, new Insets(0, 0, 0, 0)));

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        gbc.gridy = 12; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(spacer, gbc);

        JLabel lifoNote = new JLabel("<html><center>Estructura: Pila LIFO<br><span style='color:#555'>Ultimo en entrar, primero en salir</span></center></html>");
        lifoNote.setFont(new Font("Arial", Font.PLAIN, 10));
        lifoNote.setForeground(TEXT_MUTED);
        lifoNote.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 13; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(lifoNote, gbc);

        btnReportar.addActionListener(e -> accionReportar());
        btnAtender.addActionListener(e -> accionAtender());

        return panel;
    }

    private GridBagConstraints fieldGbc(GridBagConstraints base, int row, Insets insets) {
        base.gridy = row;
        base.insets = insets;
        return base;
    }

    private JPanel buildStackPanel() {
        JPanel panel = buildCard();
        panel.setLayout(new BorderLayout(0, 10));

        JLabel qLabel = new JLabel("PILA DE ALERTAS ACTIVAS");
        qLabel.setFont(new Font("Arial", Font.BOLD, 10));
        qLabel.setForeground(DANGER);
        panel.add(qLabel, BorderLayout.NORTH);

        areaAlertas = new JTextArea();
        areaAlertas.setBackground(BG_DARK);
        areaAlertas.setForeground(TEXT_WHITE);
        areaAlertas.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaAlertas.setEditable(false);
        areaAlertas.setLineWrap(true);
        areaAlertas.setWrapStyleWord(true);
        areaAlertas.setCaretColor(DANGER);
        areaAlertas.setText("La pila de alertas esta vacia.\n");

        JScrollPane scroll = new JScrollPane(areaAlertas);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void accionReportar() {
        String idTexto = txtId.getText().trim();
        String xTexto  = txtX.getText().trim();
        String yTexto  = txtY.getText().trim();

        if (idTexto.isEmpty() || xTexto.isEmpty() || yTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un numero entero.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (id <= 0) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un numero positivo.", "ID invalido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double x, y;
        try {
            x = Double.parseDouble(xTexto);
            y = Double.parseDouble(yTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Las coordenadas deben ser valores numericos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Incidente[] historial = pilaIncidentes.obtenerHistorial();
        for (Incidente inc : historial) {
            if (inc.getIdIncidente() == id) {
                JOptionPane.showMessageDialog(this, "Ya existe un incidente con el ID " + id + ".", "ID duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        TipoIncidente tipo = (TipoIncidente) comboTipo.getSelectedItem();

        if (!pilaIncidentes.estaVacia() && historial.length >= 2) {
            if (historial[0].getTipoIncidente() == tipo && historial[1].getTipoIncidente() == tipo) {
                JOptionPane.showMessageDialog(this,
                        "No se pueden apilar tres incidentes consecutivos del mismo tipo (" + tipo + ").",
                        "Tipo repetido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Coordenada ubicacion = new Coordenada(x, y);
        Incidente nuevo = new Incidente(id, tipo, ubicacion);
        pilaIncidentes.agregarAlerta(nuevo);
        actualizarPila();

        txtId.setText(""); txtX.setText(""); txtY.setText("");
        JOptionPane.showMessageDialog(this, "Incidente reportado correctamente.", "Registrado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionAtender() {
        if (pilaIncidentes.estaVacia()) {
            JOptionPane.showMessageDialog(this, "No hay alertas pendientes.", "Pila vacia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Incidente atendido = pilaIncidentes.atenderUltimaAlerta();
        actualizarPila();
        JOptionPane.showMessageDialog(this,
                "Alerta atendida:\n" + atendido.toString(),
                "Alerta atendida",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarPila() {
        StringBuilder sb = new StringBuilder();
        Incidente[] historial = pilaIncidentes.obtenerHistorial();
        if (historial.length == 0) {
            sb.append("La pila de alertas esta vacia.\n");
            lblContador.setText("Pila vacia");
            lblContador.setForeground(TEXT_MUTED);
        } else {
            sb.append("ALERTAS ACTIVAS — ").append(historial.length).append(" en pila\n");
            sb.append("─────────────────────────────────────\n");
            for (int i = 0; i < historial.length; i++) {
                sb.append(i == 0 ? "[TOPE] " : "[  " + (i + 1) + "  ] ");
                sb.append(historial[i].toString()).append("\n");
            }
            lblContador.setText(historial.length + (historial.length == 1 ? " alerta activa" : " alertas activas"));
            lblContador.setForeground(DANGER);
        }
        areaAlertas.setText(sb.toString());
        areaAlertas.setCaretPosition(0);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(DIVIDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        return card;
    }

    private JLabel buildFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private JTextField buildTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(28, 28, 38));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setBackground(new Color(28, 28, 38));
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(ORANGE_PRIMARY);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(0, 34));
        return field;
    }

    private JButton buildActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 38));
        return btn;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(new Color(28, 28, 38));
        combo.setForeground(TEXT_WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        combo.setPreferredSize(new Dimension(0, 34));
    }
}
