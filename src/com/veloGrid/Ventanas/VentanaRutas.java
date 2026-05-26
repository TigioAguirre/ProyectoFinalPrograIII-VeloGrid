package com.veloGrid.Ventanas;

import com.veloGrid.estructuras.ColaRuta;
import com.veloGrid.modelo.NivelExperiencia;
import com.veloGrid.modelo.ParadaCiclovia;
import com.veloGrid.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaRutas extends JFrame {

    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 45);
    private static final Color SUCCESS        = new Color(50, 200, 120);
    private static final Color DANGER         = new Color(220, 60, 60);

    private JComboBox<ParadaCiclovia> comboParadas;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private DefaultListModel<String> modeloLista;
    private JList<String> listaRutas;
    private JLabel lblContador;
    private JTextArea logArea;

    private ColaRuta miRutaQueue;
    private Usuario usuarioActual;

    public VentanaRutas() {
        setTitle("VeloGrid — Planificador de Rutas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        usuarioActual = new Usuario(1, "Ethan Branch", NivelExperiencia.EXPERTO);
        miRutaQueue = new ColaRuta();
        modeloLista = new DefaultListModel<>();

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
        body.add(buildLeftPanel(), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.58; gbc.insets = new Insets(0, 0, 0, 0);
        body.add(buildRightPanel(), gbc);

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
        dot.setForeground(ORANGE_PRIMARY);

        JLabel title = new JLabel("Planificador de Rutas");
        title.setFont(new Font("Arial Black", Font.BOLD, 16));
        title.setForeground(TEXT_WHITE);

        left.add(dot);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        lblContador = new JLabel("0 paradas en ruta");
        lblContador.setFont(new Font("Arial", Font.PLAIN, 11));
        lblContador.setForeground(TEXT_MUTED);

        JLabel usuario = new JLabel(usuarioActual.getNombreUsuario() + "  |  " + usuarioActual.getNivelExperiencia());
        usuario.setFont(new Font("Arial", Font.BOLD, 11));
        usuario.setForeground(ORANGE_PRIMARY);

        right.add(lblContador);
        right.add(usuario);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = buildCard();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel sectionLabel = new JLabel("AGREGAR PARADA");
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 10));
        sectionLabel.setForeground(ORANGE_PRIMARY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(sectionLabel, gbc);

        comboParadas = new JComboBox<>(ParadaCiclovia.values());
        styleCombo(comboParadas);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(comboParadas, gbc);

        btnAgregar = buildActionButton("+ AGREGAR A RUTA", ORANGE_PRIMARY, Color.WHITE);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(btnAgregar, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER);
        sep.setBackground(DIVIDER);
        gbc.gridy = 3; gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(sep, gbc);

        btnEliminar = buildActionButton("COMPLETAR SIGUIENTE", new Color(30, 30, 40), SUCCESS);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(btnEliminar, gbc);

        btnLimpiar = buildActionButton("LIMPIAR RUTA", new Color(30, 30, 40), DANGER);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(btnLimpiar, gbc);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        gbc.gridy = 6; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(spacer, gbc);

        JLabel fifoNote = new JLabel("<html><center>Estructura: Cola FIFO<br><span style='color:#555'>Primera en entrar, primera en salir</span></center></html>");
        fifoNote.setFont(new Font("Arial", Font.PLAIN, 10));
        fifoNote.setForeground(TEXT_MUTED);
        fifoNote.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(fifoNote, gbc);

        btnAgregar.addActionListener(e -> accionAgregar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> accionLimpiar());

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);

        JPanel queuePanel = buildCard();
        queuePanel.setLayout(new BorderLayout(0, 10));

        JLabel qLabel = new JLabel("COLA DE RECORRIDO");
        qLabel.setFont(new Font("Arial", Font.BOLD, 10));
        qLabel.setForeground(ORANGE_PRIMARY);
        queuePanel.add(qLabel, BorderLayout.NORTH);

        listaRutas = new JList<>(modeloLista);
        listaRutas.setOpaque(false);
        listaRutas.setBackground(BG_DARK);
        listaRutas.setForeground(TEXT_WHITE);
        listaRutas.setFont(new Font("Arial", Font.PLAIN, 12));
        listaRutas.setSelectionBackground(new Color(252, 76, 2, 60));
        listaRutas.setSelectionForeground(TEXT_WHITE);
        listaRutas.setFixedCellHeight(34);
        listaRutas.setCellRenderer(new RouteListCellRenderer());

        JScrollPane scroll = new JScrollPane(listaRutas);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        queuePanel.add(scroll, BorderLayout.CENTER);

        JPanel logPanel = buildCard();
        logPanel.setLayout(new BorderLayout(0, 8));
        logPanel.setPreferredSize(new Dimension(0, 140));

        JLabel logLabel = new JLabel("REGISTRO DE ACTIVIDAD");
        logLabel.setFont(new Font("Arial", Font.BOLD, 10));
        logLabel.setForeground(TEXT_MUTED);
        logPanel.add(logLabel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setBackground(BG_DARK);
        logArea.setForeground(TEXT_MUTED);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setWrapStyleWord(true);
        logArea.setLineWrap(true);
        logArea.setText("Sistema listo. Seleccione paradas para iniciar el recorrido.\n");
        logArea.setCaretColor(ORANGE_PRIMARY);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setOpaque(false);
        logScroll.getViewport().setBackground(BG_DARK);
        logScroll.setBorder(BorderFactory.createEmptyBorder());
        logPanel.add(logScroll, BorderLayout.CENTER);

        wrapper.add(queuePanel, BorderLayout.CENTER);
        wrapper.add(logPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void accionAgregar() {
        ParadaCiclovia seleccion = (ParadaCiclovia) comboParadas.getSelectedItem();
        if (seleccion == null) return;

        ParadaCiclovia[] recorrido = miRutaQueue.obtenerRecorridoCompleto();

        if (recorrido.length > 0) {
            ParadaCiclovia ultima = recorrido[recorrido.length - 1];
            if (ultima == seleccion) {
                JOptionPane.showMessageDialog(this,
                        "No se puede agregar la misma parada de forma consecutiva.\nAgregue una parada intermedia primero.",
                        "Parada consecutiva",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (ParadaCiclovia p : recorrido) {
                if (p == seleccion) {
                    int ok = JOptionPane.showConfirmDialog(this,
                            "\"" + seleccion + "\" ya existe en el recorrido.\n¿Agregar de todas formas?",
                            "Parada duplicada",
                            JOptionPane.YES_NO_OPTION);
                    if (ok != JOptionPane.YES_OPTION) return;
                    break;
                }
            }
        }

        miRutaQueue.agregarParada(seleccion);
        actualizarVista();
        log("+ " + seleccion.toString());
    }

    private void accionEliminar() {
        if (miRutaQueue.estaVacia()) {
            JOptionPane.showMessageDialog(this, "La cola esta vacia.", "Sin paradas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ParadaCiclovia completada = miRutaQueue.completarSiguienteParada();
        actualizarVista();
        log("Completado: " + completada.toString());
    }

    private void accionLimpiar() {
        if (miRutaQueue.estaVacia()) {
            JOptionPane.showMessageDialog(this, "La ruta ya esta vacia.", "Sin cambios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Se eliminaran todas las paradas. ¿Continuar?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            miRutaQueue.vaciarRuta();
            actualizarVista();
            log("Ruta limpiada.");
        }
    }

    private void actualizarVista() {
        modeloLista.clear();
        ParadaCiclovia[] recorrido = miRutaQueue.obtenerRecorridoCompleto();
        for (int i = 0; i < recorrido.length; i++) {
            modeloLista.addElement((i == 0 ? ">>  " : (i + 1) + ".  ") + recorrido[i].toString());
        }
        int n = miRutaQueue.getTamano();
        lblContador.setText(n + (n == 1 ? " parada en ruta" : " paradas en ruta"));
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
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
        combo.setBackground(new Color(30, 30, 40));
        combo.setForeground(TEXT_WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        combo.setPreferredSize(new Dimension(0, 36));
    }

    private static class RouteListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            label.setForeground(index == 0 ? new Color(252, 76, 2) : new Color(200, 200, 210));
            label.setBackground(isSelected ? new Color(40, 25, 15) : new Color(22, 22, 30));
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(4, 10, 4, 10));
            return label;
        }
    }
}
