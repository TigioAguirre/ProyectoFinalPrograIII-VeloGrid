package com.veloGrid.ventanas;

import com.veloGrid.estructuras.*;
import com.veloGrid.clasesBase.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaConfiguracion extends JFrame {
    private Usuario usuario;
    private GestorUsuarios gestor;
    private JTextField txtNombreCompleto;
    private JTextField txtHorasMovimiento;
    private JTextField txtKmRecorridos;
    private JTextField txtDesnivelPosAcum;
    private JComboBox<NivelExperiencia> cbExperiencia;
    private JComboBox<PreferenciaRuta> cbPreferencia;
    private JComboBox<TipoBicicleta> cbTipoBicicleta;
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
    public VentanaConfiguracion(Usuario usuario, GestorUsuarios gestor) {
        this.usuario = usuario;
        this.gestor = gestor;
        setTitle("VelóGRID — Configuración de Perfil");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setMinimumSize(new Dimension(700, 450));
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
        JLabel logo = new JLabel("VELÓGRID");
        logo.setFont(new Font("Arial Black", Font.BOLD, 15));
        logo.setForeground(ORANGE_PRIMARY);
        topBar.add(logo, BorderLayout.WEST);
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        topRight.setOpaque(false);
        GestorVentanas.agregarBotonesVentana(this, topRight);
        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, topBar);
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(32, 48, 40, 48));
        JLabel lblTitle = new JLabel("Configura tu perfil");
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_WHITE);
        JLabel lblSub = new JLabel("Cuéntanos un poco sobre ti para personalizar tu experiencia.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setBorder(new EmptyBorder(6, 0, 24, 0));
        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.add(lblTitle);
        headingPanel.add(lblSub);
        body.add(headingPanel, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 14, 0);
        gbc.gridx = 0;
        txtNombreCompleto = styledTextField();
        if (usuario.getNombreCompleto() != null) txtNombreCompleto.setText(usuario.getNombreCompleto());
        cbExperiencia = styledCombo(NivelExperiencia.values());
        if (usuario.getNivelExperiencia() != null) cbExperiencia.setSelectedItem(usuario.getNivelExperiencia());
        cbPreferencia = styledCombo(PreferenciaRuta.values());
        if (usuario.getPreferenciaRuta() != null) cbPreferencia.setSelectedItem(usuario.getPreferenciaRuta());
        cbTipoBicicleta = styledCombo(TipoBicicleta.values());
        if (usuario.getTipoBicicleta() != null) cbTipoBicicleta.setSelectedItem(usuario.getTipoBicicleta());
        txtHorasMovimiento = styledTextField();
        txtHorasMovimiento.setText(String.valueOf(usuario.getHorasMovimiento()));
        txtKmRecorridos = styledTextField();
        txtKmRecorridos.setText(String.valueOf(usuario.getKmRecorridos()));
        txtDesnivelPosAcum = styledTextField();
        txtDesnivelPosAcum.setText(String.valueOf(usuario.getDesnivelPosAcum()));
        int row = 0;
        gbc.gridy = row++; form.add(makeLabel("Nombre completo"), gbc);
        gbc.gridy = row++; form.add(txtNombreCompleto, gbc);
        gbc.gridy = row++; form.add(makeLabel("Tipo de bicicleta"), gbc);
        gbc.gridy = row++; form.add(cbTipoBicicleta, gbc);
        gbc.gridy = row++; form.add(makeLabel("Nivel de experiencia"), gbc);
        gbc.gridy = row++; form.add(cbExperiencia, gbc);
        gbc.gridy = row++; form.add(makeLabel("Preferencia de ruta"), gbc);
        gbc.gridy = row++; form.add(cbPreferencia, gbc);
        gbc.gridy = row++; form.add(makeLabel("Horas en movimiento (Total)"), gbc);
        gbc.gridy = row++; form.add(txtHorasMovimiento, gbc);
        gbc.gridy = row++; form.add(makeLabel("Kilómetros recorridos (Total)"), gbc);
        gbc.gridy = row++; form.add(txtKmRecorridos, gbc);
        gbc.gridy = row++; form.add(makeLabel("Desnivel positivo acumulado (m)"), gbc);
        gbc.gridy = row++; form.add(txtDesnivelPosAcum, gbc);
        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(10, 0)); // Barra más delgada
        verticalBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = SCROLL_THUMB;
                this.trackColor = BG_DARK;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton(); // Sin flecha superior
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton(); // Sin flecha inferior
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(BG_DARK);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isThumbRollover() ? SCROLL_HOVER : SCROLL_THUMB);
                int arc = 8;
                int x = thumbBounds.x + 2;
                int y = thumbBounds.y + 2;
                int width = thumbBounds.width - 4;
                int height = thumbBounds.height - 4;
                g2.fillRoundRect(x, y, width, height, arc, arc);
                g2.dispose();
            }
        });
        body.add(scrollPane, BorderLayout.CENTER);
        JButton btnGuardar = new JButton("GUARDAR CAMBIOS") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.setForeground(TEXT_WHITE);
        btnGuardar.setBackground(ORANGE_PRIMARY);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setOpaque(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(160, 40));
        btnGuardar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnGuardar.setBackground(new Color(210, 60, 0)); btnGuardar.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btnGuardar.setBackground(ORANGE_PRIMARY); btnGuardar.repaint(); }
        });
        btnGuardar.addActionListener(e -> guardarDatos());
        JButton btnEliminar = new JButton("ELIMINAR USUARIO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEliminar.setForeground(TEXT_WHITE);
        btnEliminar.setBackground(new Color(120, 30, 30));
        btnEliminar.setBorderPainted(false);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setOpaque(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setPreferredSize(new Dimension(160, 40));
        btnEliminar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnEliminar.setBackground(new Color(160, 40, 40)); btnEliminar.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btnEliminar.setBackground(new Color(120, 30, 30)); btnEliminar.repaint(); }
        });
        btnEliminar.addActionListener(e -> eliminarUsuario());
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));
        JPanel footerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footerLeft.setOpaque(false);
        footerLeft.add(btnEliminar);
        footer.add(footerLeft, BorderLayout.WEST);
        JPanel footerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footerRight.setOpaque(false);
        footerRight.add(btnGuardar);
        footer.add(footerRight, BorderLayout.EAST);
        body.add(footer, BorderLayout.SOUTH);
        root.add(body, BorderLayout.CENTER);
    }
    private void eliminarUsuario() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar tu usuario? Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        gestor.eliminarUsuario(usuario);
        JOptionPane.showMessageDialog(this,
                "Usuario eliminado correctamente.", "Usuario eliminado",
                JOptionPane.INFORMATION_MESSAGE);
        GestorVentanas.cambiarVentana(this, new com.veloGrid.main.Main());
    }
    private void guardarDatos() {
        String nombre = txtNombreCompleto.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El campo de nombre completo es requerido.", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            float horas = Float.parseFloat(txtHorasMovimiento.getText().trim().isEmpty() ? "0" : txtHorasMovimiento.getText().trim());
            float km = Float.parseFloat(txtKmRecorridos.getText().trim().isEmpty() ? "0" : txtKmRecorridos.getText().trim());
            float desnivel = Float.parseFloat(txtDesnivelPosAcum.getText().trim().isEmpty() ? "0" : txtDesnivelPosAcum.getText().trim());
            usuario.setHorasMovimiento(horas);
            usuario.setKmRecorridos(km);
            usuario.setDesnivelPosAcum(desnivel);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa un número válido en los campos de horas, kilómetros y desnivel.", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        usuario.setNombreCompleto(nombre);
        usuario.setTipoBicicleta((TipoBicicleta) cbTipoBicicleta.getSelectedItem());
        usuario.setNivelExperiencia((NivelExperiencia) cbExperiencia.getSelectedItem());
        usuario.setPreferenciaRuta((PreferenciaRuta) cbPreferencia.getSelectedItem());
        usuario.setUsrRegistrado(true);
        gestor.guardarUsuarios();
        JOptionPane.showMessageDialog(this, "Perfil actualizado con éxito.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        GestorVentanas.cambiarVentana(this, new VentanaPrincipal(usuario, gestor));
    }
    /**Diseño de la Ventana*/
    private JButton makeCloseButton() {
        JButton btn = new JButton();
        ImageIcon icon = new ImageIcon("media/icon_close.png");
        Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(scaled));
        btn.setToolTipText("Cerrar");
        btn.setBackground(BG_CARD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(35, 35));
        return btn;
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
        f.setEditable(true);
        return f;
    }
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        l.setBorder(new EmptyBorder(0, 2, 4, 0));
        return l;
    }
}