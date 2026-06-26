package com.veloGrid.ventanas;

import com.veloGrid.estructuras.*;
import com.veloGrid.main.Main;
import com.veloGrid.clasesBase.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaPrincipal extends JFrame {
    private Usuario usuarioActivo;
    private GestorUsuarios gestor;
    private GestorNodos gestorNodos;
    private GestorRutas gestorRutas;

    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color BG_CARD_HOVER  = new Color(30, 30, 42);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);

    /**Constructores de la Ventana*/
    public VentanaPrincipal(Usuario usuarioActivo) {
        this(usuarioActivo, new GestorUsuarios());
    }

    public VentanaPrincipal(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;
        initUI();
    }

    public VentanaPrincipal(Usuario usuarioActivo, GestorUsuarios gestor) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = new GestorNodos();
        this.gestorRutas = new GestorRutas();
        this.gestorNodos.cargarNodosCSV("baseDeDatos/nodosRegistrados.csv");
        this.gestorNodos.cargarPuntosInteresCSV("baseDeDatos/puntosInteres.csv");
        this.gestorRutas.cargarLimitesCSV("baseDeDatos/limitesRutas.csv");
        this.gestorRutas.cargarRutasDesdeCSV("baseDeDatos/rutasRegistradas.csv", this.gestorNodos);
        initUI();
    }

    /**Metodos de la Ventana*/
    private void initUI() {
        setTitle("VelóGRID");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setMinimumSize(new Dimension(700, 500));
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        setContentPane(root);

        // --- BARRA SUPERIOR ---
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

        JButton btnVolver = makeIconButton("media/icon_logout.png", 18, "Cerrar Sesión");
        btnVolver.addActionListener(e -> {
            GestorVentanas.cambiarVentana(this, new Main());
        });
        topRight.add(btnVolver);

        JButton btnPerfil = makeTextButton("Mi Perfil", false);
        btnPerfil.addActionListener(e -> {
            GestorVentanas.cambiarVentana(this, new VentanaConfiguracion(usuarioActivo, gestor));
        });
        topRight.add(btnPerfil);

        if (usuarioActivo.EsAdmin()) {
            JButton btnAdmin = makeTextButton("Admin", false);
            btnAdmin.addActionListener(e -> {
                GestorVentanas.cambiarVentana(this, new VentanaAdmin(usuarioActivo, gestor, gestorNodos, gestorRutas));
            });
            topRight.add(btnAdmin);
        }

        // Inyección de los 3 botones automáticos (Minimizar, Maximizar, Cerrar)
        GestorVentanas.agregarBotonesVentana(this, topRight);

        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, topBar);

        // --- CUERPO PRINCIPAL ---
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(40, 60, 40, 60));

        String nombre = usuarioActivo.getNombreCompleto() != null
                ? usuarioActivo.getNombreCompleto()
                : usuarioActivo.getNombreUsuario();

        JPanel greetPanel = new JPanel();
        greetPanel.setOpaque(false);
        greetPanel.setLayout(new BoxLayout(greetPanel, BoxLayout.Y_AXIS));

        JLabel lblGreet = new JLabel("Bienvenido,");
        lblGreet.setFont(new Font("Arial", Font.PLAIN, 14));
        lblGreet.setForeground(TEXT_MUTED);

        JLabel lblName = new JLabel(nombre);
        lblName.setFont(new Font("Arial Black", Font.BOLD, 26));
        lblName.setForeground(TEXT_WHITE);

        greetPanel.add(lblGreet);
        greetPanel.add(Box.createVerticalStrut(4));
        greetPanel.add(lblName);
        body.add(greetPanel, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(3, 2, 20, 20));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(25, 0, 0, 0));

        // --- BOTONES DE MÓDULOS (Actualizados con GestorVentanas) ---

        cards.add(makeModuleCard(
                "PLANIFICAR RUTA",
                "Construye tu recorrido seguro por ciclovías",
                "→",
                e -> {
                    String rutaActual = usuarioActivo.getRutaActual();
                    if (rutaActual != null && !rutaActual.trim().isEmpty()) {
                        GestorVentanas.cambiarVentana(this, new VentanaRutaActual(usuarioActivo, gestor, gestorNodos, gestorRutas));
                    } else {
                        GestorVentanas.cambiarVentana(this, new VentanaRutas(usuarioActivo, gestor, gestorNodos, gestorRutas));
                    }
                }
        ));

        cards.add(makeModuleCard(
                "ALERTAS",
                "Reporta incidentes y consulta avisos activos",
                "→",
                e -> {
                    GestorVentanas.cambiarVentana(this, new VentanaAlertas(usuarioActivo, gestor, gestorNodos, gestorRutas));
                }
        ));

        cards.add(makeModuleCard(
                "ESTADÍSTICAS",
                "Revisa tu progreso y los datos de tu bicicleta",
                "→",
                e -> {
                    GestorVentanas.cambiarVentana(this, new VentanaEstadisticas(usuarioActivo, gestor));
                }
        ));

        cards.add(makeModuleCard(
                "AGREGAR RUTA",
                "Registra nuevos tramos y expande el mapa",
                "→",
                e -> {
                    GestorVentanas.cambiarVentana(this, new VentanaAgregarRutaNueva(usuarioActivo, gestor, gestorNodos, gestorRutas));
                }
        ));

        cards.add(makeModuleCard(
                "AGREGAR UBICACIÓN",
                "Registra nuevas intersecciones o puntos de interés",
                "→",
                e -> {
                    GestorVentanas.cambiarVentana(this, new VentanaAgregarUbicacion(usuarioActivo, gestor, gestorNodos, gestorRutas));
                }
        ));

        cards.add(makeModuleCard(
                "ELIMINAR LUGAR",
                "Solicita la eliminación de nodos o puntos obsoletos",
                "→",
                e -> {
                    GestorVentanas.cambiarVentana(this, new VentanaEliminacionLugar(usuarioActivo, gestor, gestorNodos, gestorRutas));
                }
        ));

        JPanel emptyCard = new JPanel();
        emptyCard.setOpaque(false);
        cards.add(emptyCard);

        body.add(cards, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }

    /**Diseño Ventana*/
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

        JLabel lblDesc = new JLabel("<html><body style='width:130px'>" + desc + "</body></html>");
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

    private JButton makeIconButton(String iconPath, int size, String tooltip) {
        JButton btn = new JButton();
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            btn.setText("O");
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

    private JButton makeTextButton(String text, boolean danger) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setForeground(danger ? TEXT_MUTED : TEXT_WHITE);
        btn.setBackground(BG_CARD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(text.equals("X") ? 35 : 90, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(ORANGE_PRIMARY); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(danger ? TEXT_MUTED : TEXT_WHITE); }
        });
        return btn;
    }
}