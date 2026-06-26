package com.veloGrid.ventanas;

import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.estructuras.Nodo;
import com.veloGrid.estructuras.PuntoInteres;
import com.veloGrid.clasesBase.Coordenada;
import com.veloGrid.clasesBase.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;

/**
 * VentanaAgregarNodo — Flujo integrado de mapa pryGrafo
 *
 * Pasos actualizados:
 * 1. PASO_MAPA        → Panel de mapa interactivo (clic derecho coloca el pin).
 * 2. PASO_DATOS       → El usuario escribe nombre y marca si es ciclovía.
 * 3. PASO_CONFIRMACION → Resumen del punto antes de guardar; el usuario confirma o regresa.
 */
public class VentanaAgregarNodo extends JFrame {

    // ── Dependencias ────────────────────────────────────────────────────────────
    private final Usuario        usuarioActivo;
    private final GestorUsuarios gestor;
    private final GestorNodos    gestorNodos;
    private final GestorRutas    gestorRutas;

    // ── Paleta VeloGrid ──────────────────────────────────────────────────────────
    private static final Color BG_DARK        = new Color(15,  15,  20);
    private static final Color BG_CARD        = new Color(22,  22,  30);
    private static final Color BG_CARD_HOVER  = new Color(30,  30,  42);
    private static final Color ORANGE_PRIMARY = new Color(252, 76,  2);
    private static final Color GREEN_OK       = new Color(40,  200, 80);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35,  35,  50);

    // ── Estado del flujo ──────────────────────────────────────────────────
    private enum Paso { MAPA, DATOS, CONFIRMACION }
    private Paso pasoActual = Paso.MAPA;

    // ── Datos capturados ──────────────────────────────────────────────────────────
    private String  nombrePendiente;
    private boolean esCicloviaPendiente;
    private float   coordXPendiente;
    private float   coordYPendiente;

    // ── Widgets reutilizados ──────────────────────────────────────────────────────
    private JTextField txtNombre;
    private JCheckBox  chkCiclovia;
    private PanelMapa  panelMapa;

    // ── Contenedor de pasos ───────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     panelPasos;

    // ── Top-bar dinámica ──────────────────────────────────────────────────────────
    private JLabel  lblTitulo;
    private JButton btnAccionTopBar;

    // =============================================================================
    // Constructor
    // =============================================================================
    public VentanaAgregarNodo(Usuario usuarioActivo, GestorUsuarios gestor,
                              GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor        = gestor;
        this.gestorNodos   = gestorNodos;
        this.gestorRutas   = gestorRutas;
        initUI();
    }

    // =============================================================================
    // Construcción de la UI
    // =============================================================================
    private void initUI() {
        setTitle("VelóGRID — Agregar Nodo");
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        setContentPane(root);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildPanelPasos(), BorderLayout.CENTER);
    }

    // ── Top-bar ──────────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_CARD);
        topBar.setBorder(new EmptyBorder(0, 20, 0, 8));
        topBar.setPreferredSize(new Dimension(0, 48));

        lblTitulo = new JLabel("NUEVO NODO — Paso 1: Ubicación en el mapa");
        lblTitulo.setFont(new Font("Arial Black", Font.BOLD, 13));
        lblTitulo.setForeground(ORANGE_PRIMARY);
        topBar.add(lblTitulo, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        topRight.setOpaque(false);

        JButton btnVolver = makeTextButton("< Volver");
        btnVolver.addActionListener(e -> retroceder());
        topRight.add(btnVolver);

        btnAccionTopBar = makeAccionButton("Continuar >");
        btnAccionTopBar.addActionListener(e -> avanzar());
        topRight.add(btnAccionTopBar);

        GestorVentanas.agregarBotonesVentana(this, topRight);
        topBar.add(topRight, BorderLayout.EAST);
        GestorVentanas.habilitarArrastre(this, topBar);
        return topBar;
    }

    // ── CardLayout con los 3 pasos ordenados ──────────────────────────────────────
    private JPanel buildPanelPasos() {
        cardLayout = new CardLayout();
        panelPasos = new JPanel(cardLayout);
        panelPasos.setBackground(BG_DARK);

        panelPasos.add(buildPasoMapa(),                        Paso.MAPA.name());
        panelPasos.add(buildPasoDatos(),                       Paso.DATOS.name());
        panelPasos.add(new JPanel(new BorderLayout()),         Paso.CONFIRMACION.name()); // Placeholder dinámico

        return panelPasos;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // PASO 1 — Mapa interactivo
    // ─────────────────────────────────────────────────────────────────────────────
    private JPanel buildPasoMapa() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);

        JPanel instrBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        instrBar.setBackground(new Color(20, 20, 28));
        instrBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));

        JLabel icono = new JLabel("📍");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        instrBar.add(icono);

        JLabel lblInstr = new JLabel("Haz clic derecho en el mapa para colocar el pin. Rueda para zoom · Clic izquierdo para mover.");
        lblInstr.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInstr.setForeground(TEXT_MUTED);
        instrBar.add(lblInstr);

        wrapper.add(instrBar, BorderLayout.NORTH);

        panelMapa = new PanelMapa(gestorNodos);
        wrapper.add(panelMapa, BorderLayout.CENTER);

        return wrapper;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // PASO 2 — Datos (nombre + ciclovía)
    // ─────────────────────────────────────────────────────────────────────────────
    private JPanel buildPasoDatos() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        card.setMaximumSize(new Dimension(460, 999));

        JLabel lblTitle = new JLabel("Información del nodo");
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 15));
        lblTitle.setForeground(TEXT_WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTitle);

        card.add(Box.createVerticalStrut(6));

        JLabel lblSub = new JLabel("Ingresa el nombre y si el punto cuenta con ciclovía.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblSub);

        card.add(Box.createVerticalStrut(28));

        JLabel lblNombre = new JLabel("Nombre / Cruce:");
        lblNombre.setForeground(TEXT_MUTED);
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblNombre);
        card.add(Box.createVerticalStrut(6));

        txtNombre = makeTextField();
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtNombre);

        card.add(Box.createVerticalStrut(22));

        chkCiclovia = new JCheckBox("¿Cuenta con ciclovía?");
        chkCiclovia.setOpaque(false);
        chkCiclovia.setForeground(TEXT_WHITE);
        chkCiclovia.setFont(new Font("Arial", Font.PLAIN, 12));
        chkCiclovia.setFocusPainted(false);
        chkCiclovia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkCiclovia.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(chkCiclovia);

        card.add(Box.createVerticalStrut(30));

        JLabel lblHint = new JLabel("→ En el siguiente paso confirmarás el registro.");
        lblHint.setFont(new Font("Arial", Font.ITALIC, 11));
        lblHint.setForeground(TEXT_MUTED);
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblHint);

        outer.add(card);
        return outer;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // PASO 3 — Confirmación
    // ─────────────────────────────────────────────────────────────────────────────
    private JPanel buildPasoConfirmacion() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        card.setMaximumSize(new Dimension(500, 999));

        JLabel lblTitle = new JLabel("¿Confirmar este punto?");
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTitle);

        card.add(Box.createVerticalStrut(24));

        card.add(makeResumenFila("Nombre:",       nombrePendiente));
        card.add(Box.createVerticalStrut(12));
        card.add(makeResumenFila("Ciclovía:",     esCicloviaPendiente ? "Sí" : "No"));
        card.add(Box.createVerticalStrut(12));
        card.add(makeResumenFila("Coord X:",      String.format("%.2f", coordXPendiente)));
        card.add(Box.createVerticalStrut(12));
        card.add(makeResumenFila("Coord Y:",      String.format("%.2f", coordYPendiente)));
        card.add(Box.createVerticalStrut(12));
        card.add(makeResumenFila("Agregado por:", usuarioActivo.getNombreUsuario()));

        card.add(Box.createVerticalStrut(30));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnGuardar = new JButton("✓  Guardar nodo");
        btnGuardar.setBackground(GREEN_OK);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial Black", Font.BOLD, 12));
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(170, 36));
        btnGuardar.addActionListener(e -> guardarNodo());
        btnPanel.add(btnGuardar);

        JButton btnCancelar = new JButton("← Volver a los datos");
        btnCancelar.setBackground(BG_CARD_HOVER);
        btnCancelar.setForeground(TEXT_MUTED);
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(160, 36));
        btnCancelar.addActionListener(e -> irAPaso(Paso.DATOS));
        btnPanel.add(btnCancelar);

        card.add(btnPanel);

        outer.add(card);
        return outer;
    }

    // =============================================================================
    // Lógica de navegación
    // =============================================================================
    private void avanzar() {
        switch (pasoActual) {
            case MAPA:
                if (!panelMapa.tienePinColocado()) {
                    mostrarError("Haz clic derecho en el mapa para seleccionar la ubicación del nodo.");
                    return;
                }
                coordXPendiente = panelMapa.getPinX();
                coordYPendiente = panelMapa.getPinY();
                irAPaso(Paso.DATOS);
                break;

            case DATOS:
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    mostrarError("Por favor ingresa un nombre para el nodo.");
                    return;
                }
                nombrePendiente     = nombre;
                esCicloviaPendiente = chkCiclovia.isSelected();
                irAPaso(Paso.CONFIRMACION);
                break;

            case CONFIRMACION:
                guardarNodo();
                break;
        }
    }

    private void retroceder() {
        switch (pasoActual) {
            case MAPA:
                GestorVentanas.cambiarVentana(this,
                        new VentanaAgregarUbicacion(usuarioActivo, gestor, gestorNodos, gestorRutas));
                break;
            case DATOS:
                irAPaso(Paso.MAPA);
                break;
            case CONFIRMACION:
                irAPaso(Paso.DATOS);
                break;
        }
    }

    private void irAPaso(Paso nuevoPaso) {
        pasoActual = nuevoPaso;

        switch (nuevoPaso) {
            case MAPA:
                lblTitulo.setText("NUEVO NODO — Paso 1: Ubicación en el mapa");
                btnAccionTopBar.setText("Continuar >");
                btnAccionTopBar.setVisible(true);
                cardLayout.show(panelPasos, Paso.MAPA.name());
                panelMapa.requestFocusInWindow();
                break;

            case DATOS:
                lblTitulo.setText("NUEVO NODO — Paso 2: Datos");
                btnAccionTopBar.setText("Continuar >");
                btnAccionTopBar.setVisible(true);
                cardLayout.show(panelPasos, Paso.DATOS.name());
                break;

            case CONFIRMACION:
                lblTitulo.setText("NUEVO NODO — Paso 3: Confirmar");
                btnAccionTopBar.setVisible(false);
                panelPasos.remove(panelPasos.getComponent(2));
                panelPasos.add(buildPasoConfirmacion(), Paso.CONFIRMACION.name());
                cardLayout.show(panelPasos, Paso.CONFIRMACION.name());
                break;
        }
    }

    // =============================================================================
    // Guardado final
    // =============================================================================
    private void guardarNodo() {
        try {
            int nuevoId = gestorNodos.getMapaNodos().keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0) + 1;

            Coordenada coord = new Coordenada(coordXPendiente, coordYPendiente);
            Nodo nuevoNodo   = new Nodo(nuevoId, nombrePendiente, coord,
                    esCicloviaPendiente,
                    usuarioActivo.getNombreUsuario());

            gestorNodos.getMapaNodos().put(nuevoId, nuevoNodo);
            gestorNodos.guardarNodosCSV("baseDeDatos/nodosRegistrados.csv");

            JOptionPane.showMessageDialog(this,
                    "✓  Nodo \"" + nombrePendiente + "\" registrado con ID " + nuevoId + ".",
                    "Nodo guardado",
                    JOptionPane.INFORMATION_MESSAGE);

            GestorVentanas.cambiarVentana(this,
                    new VentanaPrincipal(usuarioActivo, gestor, gestorNodos, gestorRutas));

        } catch (Exception ex) {
            mostrarError("No se pudo guardar el nodo: " + ex.getMessage());
        }
    }

    // =============================================================================
    // Panel de mapa integrado
    // =============================================================================
    private static class PanelMapa extends JPanel {

        private double  escala         = 0.5;
        private double  traslacionX    = 0;
        private double  traslacionY    = 0;
        private Point   puntoArrastre  = null;
        private boolean inicioCentrado = false;

        private final Image       imagenFondo;
        private final GestorNodos gestorNodos;

        private boolean pinColocado = false;
        private float   pinX, pinY;

        private static final int RADIO_NODO = 7;
        private static final int RADIO_POI  = 9;
        private static final int RADIO_PIN  = 9;

        PanelMapa(GestorNodos gestorNodos) {
            this.gestorNodos = gestorNodos;

            Image img = null;
            try {
                img = ImageIO.read(new java.io.File("baseDeDatos/realBajo.jpg"));
            } catch (java.io.IOException ex) {
                System.err.println("No se pudo cargar el mapa: " + ex.getMessage());
            }
            this.imagenFondo = img;

            setBackground(new Color(15, 15, 20));
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            configurarNavegacion();
            configurarInteraccion();

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (!inicioCentrado && getWidth() > 0 && getHeight() > 0) {
                        aplicarCentradoInicial();
                        inicioCentrado = true;
                        repaint();
                    }
                }
            });
        }

        private void aplicarCentradoInicial() {
            double targetX = 7620.58;
            double targetY = 10824.20;
            escala = 1.0;

            if (imagenFondo != null) {
                int imgWidth  = imagenFondo.getWidth(this);
                int imgHeight = imagenFondo.getHeight(this);
                if (imgWidth > 0 && imgHeight > 0) {
                    double escalaMinimaX = (double) getWidth()  / imgWidth;
                    double escalaMinimaY = (double) getHeight() / imgHeight;
                    double escalaMinima  = Math.max(escalaMinimaX, escalaMinimaY);
                    if (escala < escalaMinima) escala = escalaMinima;
                }
            }

            traslacionX = (getWidth()  / 2.0) - (targetX * escala);
            traslacionY = (getHeight() / 2.0) - (targetY * escala);
            limitarTraslacion();
        }

        private void configurarNavegacion() {
            addMouseWheelListener((MouseWheelListener) e -> {
                int mx = e.getX(), my = e.getY();
                double wx = (mx - traslacionX) / escala;
                double wy = (my - traslacionY) / escala;

                double factor   = 1.1;
                double nuevaEsc = e.getWheelRotation() < 0 ? escala * factor : escala / factor;
                nuevaEsc        = Math.min(nuevaEsc, 4.0);
                nuevaEsc        = Math.max(nuevaEsc, escalaMinima());

                escala      = nuevaEsc;
                traslacionX = mx - wx * escala;
                traslacionY = my - wy * escala;
                limitarTraslacion();
                repaint();
            });

            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) puntoArrastre = e.getPoint();
                }
                @Override public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) puntoArrastre = null;
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override public void mouseDragged(MouseEvent e) {
                    if (puntoArrastre != null) {
                        traslacionX += e.getX() - puntoArrastre.x;
                        traslacionY += e.getY() - puntoArrastre.y;
                        puntoArrastre = e.getPoint();
                        limitarTraslacion();
                        repaint();
                    }
                }
            });
        }

        private void configurarInteraccion() {
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        pinX = (float) ((e.getX() - traslacionX) / escala);
                        pinY = (float) ((e.getY() - traslacionY) / escala);
                        pinColocado = true;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);

            java.awt.geom.AffineTransform tx = g2.getTransform();

            g2.translate(traslacionX, traslacionY);
            g2.scale(escala, escala);

            // Fondo
            if (imagenFondo != null) {
                int w = imagenFondo.getWidth(this);
                int h = imagenFondo.getHeight(this);
                if (w > 0 && h > 0) g2.drawImage(imagenFondo, 0, 0, w, h, this);
            }

            // Aristas
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke((float) (3.0 / escala)));
            for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
                for (com.veloGrid.clasesBase.AristaRuta ruta : nodo.getRutasSalientes()) {
                    int x1 = (int) ruta.getNodoInicio().getCoordenada().getPosX();
                    int y1 = (int) ruta.getNodoInicio().getCoordenada().getPosY();
                    int x2 = (int) ruta.getNodoFinal().getCoordenada().getPosX();
                    int y2 = (int) ruta.getNodoFinal().getCoordenada().getPosY();
                    g2.drawLine(x1, y1, x2, y2);
                }
            }

            // Restaurar a coordenadas de pantalla para dibujar nodos en tamaño fijo
            g2.setTransform(tx);
            g2.setFont(new Font("Arial", Font.BOLD, 11));

            for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
                int screenX  = (int) (nodo.getCoordenada().getPosX() * escala + traslacionX);
                int screenY  = (int) (nodo.getCoordenada().getPosY() * escala + traslacionY);
                boolean esPOI = nodo instanceof PuntoInteres;

                if (esPOI) {
                    // POI → relleno naranja + borde negro
                    int r = RADIO_POI;
                    g2.setColor(new Color(252, 76, 2));
                    g2.fillOval(screenX - r, screenY - r, r * 2, r * 2);
                    g2.setColor(new Color(15, 15, 20));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(screenX - r, screenY - r, r * 2, r * 2);
                } else {
                    // Nodo → relleno negro + borde naranja
                    int r = RADIO_NODO;
                    g2.setColor(new Color(15, 15, 20));
                    g2.fillOval(screenX - r, screenY - r, r * 2, r * 2);
                    g2.setColor(new Color(252, 76, 2));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(screenX - r, screenY - r, r * 2, r * 2);
                }

                // Etiqueta
                int r           = esPOI ? RADIO_POI : RADIO_NODO;
                FontMetrics fm  = g2.getFontMetrics();
                String nombre   = nodo.getNombreNodo();
                int tw          = fm.stringWidth(nombre);
                int th          = fm.getHeight();

                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRoundRect(screenX - tw / 2 - 4, screenY - r - th - 2, tw + 8, th + 2, 6, 6);
                g2.setColor(esPOI ? new Color(252, 76, 2) : new Color(240, 240, 240));
                g2.drawString(nombre, screenX - tw / 2, screenY - r - 4);
            }

            // Pin visual
            if (pinColocado) {
                int screenPinX = (int) (pinX * escala + traslacionX);
                int screenPinY = (int) (pinY * escala + traslacionY);

                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillOval(screenPinX - RADIO_PIN + 2, screenPinY - RADIO_PIN + 2, RADIO_PIN * 2, RADIO_PIN * 2);
                g2.setColor(new Color(252, 76, 2));
                g2.fillOval(screenPinX - RADIO_PIN, screenPinY - RADIO_PIN, RADIO_PIN * 2, RADIO_PIN * 2);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(screenPinX - RADIO_PIN, screenPinY - RADIO_PIN, RADIO_PIN * 2, RADIO_PIN * 2);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(screenPinX - 4, screenPinY, screenPinX + 4, screenPinY);
                g2.drawLine(screenPinX, screenPinY - 4, screenPinX, screenPinY + 4);

                String info = String.format("Pin: (%.1f, %.1f)   Clic derecho para mover", pinX, pinY);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(info);
                int th = fm.getHeight();
                int px = getWidth() - tw - 16;
                int py = getHeight() - 12;

                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRoundRect(px - 8, py - th, tw + 16, th + 6, 8, 8);
                g2.setColor(new Color(252, 76, 2));
                g2.drawString(info, px, py);
            } else {
                String hint = "Clic derecho → colocar pin  |  Rueda → zoom  |  Clic izquierdo → mover";
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(hint);
                int py = getHeight() - 12;

                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRoundRect((getWidth() - tw) / 2 - 8, py - fm.getHeight(), tw + 16, fm.getHeight() + 6, 8, 8);
                g2.setColor(new Color(140, 140, 155));
                g2.drawString(hint, (getWidth() - tw) / 2, py);
            }
        }

        private double escalaMinima() {
            if (imagenFondo == null) return 0.1;
            int iw = imagenFondo.getWidth(this);
            int ih = imagenFondo.getHeight(this);
            if (iw <= 0 || ih <= 0) return 0.1;
            return Math.max((double) getWidth() / iw, (double) getHeight() / ih);
        }

        private void limitarTraslacion() {
            if (imagenFondo == null) return;
            int iw = imagenFondo.getWidth(this);
            int ih = imagenFondo.getHeight(this);
            if (iw <= 0 || ih <= 0) return;

            double sw = iw * escala;
            double sh = ih * escala;

            traslacionX = sw >= getWidth()  ? Math.min(0, Math.max(traslacionX, getWidth()  - sw)) : Math.max(0, Math.min(traslacionX, getWidth()  - sw));
            traslacionY = sh >= getHeight() ? Math.min(0, Math.max(traslacionY, getHeight() - sh)) : Math.max(0, Math.min(traslacionY, getHeight() - sh));
        }

        boolean tienePinColocado() { return pinColocado; }
        float   getPinX()          { return pinX; }
        float   getPinY()          { return pinY; }
    }

    // =============================================================================
    // Helpers de diseño
    // =============================================================================
    private JPanel makeResumenFila(String etiqueta, String valor) {
        JPanel fila = new JPanel(new BorderLayout(16, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setPreferredSize(new Dimension(110, 28));

        JLabel val = new JLabel(valor);
        val.setFont(new Font("Arial", Font.BOLD, 13));
        val.setForeground(TEXT_WHITE);

        fila.add(lbl, BorderLayout.WEST);
        fila.add(val, BorderLayout.CENTER);
        return fila;
    }

    private JTextField makeTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(BG_CARD);
        txt.setForeground(TEXT_WHITE);
        txt.setCaretColor(ORANGE_PRIMARY);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1),
                new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    private JButton makeTextButton(String texto) {
        JButton btn = new JButton(texto);
        btn.setForeground(TEXT_MUTED);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeAccionButton(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(ORANGE_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial Black", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 30));
        return btn;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }
}