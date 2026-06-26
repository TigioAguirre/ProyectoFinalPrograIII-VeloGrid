package com.veloGrid.ventanas;

import com.veloGrid.estructuras.FormateadorRutas;
import com.veloGrid.estructuras.GestorNodos;
import com.veloGrid.estructuras.GestorRutas;
import com.veloGrid.estructuras.GestorUsuarios;
import com.veloGrid.estructuras.Nodo;
import com.veloGrid.clasesBase.AristaRuta;
import com.veloGrid.clasesBase.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VentanaRutaActual extends JFrame {
    private final Usuario usuarioActivo;
    private final GestorUsuarios gestor;
    private final GestorNodos gestorNodos;
    private final GestorRutas gestorRutas;

    private JTable tablaRuta;
    private DefaultTableModel modeloTabla;
    private JButton btnAvanzar;
    private JButton btnEditar;
    private JButton btnFinalizar;

    private PanelMapaNavegacion panelMapa; // El nuevo visor GPS gráfico

    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color SUCCESS        = new Color(46, 204, 113); // Verde (Actual)
    private static final Color DANGER         = new Color(220, 50, 50);  // Rojo (Destino)
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    private static final Color DIVIDER        = new Color(35, 35, 50);
    private static final Color SCROLL_THUMB   = new Color(76, 76, 89);
    private static final Color SCROLL_HOVER   = new Color(100, 100, 115);

    /**Constructores de la Ventana*/
    public VentanaRutaActual(Usuario usuarioActivo, GestorUsuarios gestor, GestorNodos gestorNodos, GestorRutas gestorRutas) {
        this.usuarioActivo = usuarioActivo;
        this.gestor = gestor;
        this.gestorNodos = gestorNodos;
        this.gestorRutas = gestorRutas;

        setTitle("VelóGRID — Navegación Activa");
        setSize(new Dimension(1000, 600)); // Ligeramente más ancha para acomodar mapa y panel
        setMinimumSize(new Dimension(800, 500));
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        setContentPane(root);

        // --- Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_CARD);
        topBar.setBorder(new EmptyBorder(0, 20, 0, 8));
        topBar.setPreferredSize(new Dimension(0, 48));
        JLabel logo = new JLabel("MI RUTA ACTIVA - MODO NAVEGACIÓN");
        logo.setFont(new Font("Arial Black", Font.BOLD, 14));
        logo.setForeground(ORANGE_PRIMARY);
        topBar.add(logo, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        topRight.setOpaque(false);
        JButton btnVolver = makeIconButton("media/icon_back.png", 16, "Volver");
        btnVolver.addActionListener(e -> GestorVentanas.cambiarVentana(this, new VentanaPrincipal(usuarioActivo, gestor, gestorNodos, gestorRutas)));
        topRight.add(btnVolver);
        GestorVentanas.agregarBotonesVentana(this, topRight);
        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);
        GestorVentanas.habilitarArrastre(this, topBar);

        // --- Body Layout (Mapa al Centro, Controles al Este) ---
        JPanel body = new JPanel(new BorderLayout(15, 0));
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Instanciar y añadir el Mapa Gráfico
        panelMapa = new PanelMapaNavegacion(gestorNodos);
        body.add(panelMapa, BorderLayout.CENTER);

        // 2. Panel lateral (Instrucciones y Botones)
        JPanel panelLateral = new JPanel(new BorderLayout(0, 15));
        panelLateral.setBackground(BG_DARK);
        panelLateral.setPreferredSize(new Dimension(320, 0)); // Ancho fijo para el panel lateral

        JLabel lblTitulo = new JLabel("Navegación Paso a Paso");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitulo.setForeground(TEXT_WHITE);
        panelLateral.add(lblTitulo, BorderLayout.NORTH);

        String[] columnas = {"Instrucción", "Lugar / Tramo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRuta = new JTable(modeloTabla);
        tablaRuta.setBackground(BG_CARD);
        tablaRuta.setForeground(TEXT_WHITE);
        tablaRuta.setRowHeight(38);
        tablaRuta.getTableHeader().setReorderingAllowed(false);
        tablaRuta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRuta.setShowGrid(false);
        tablaRuta.setIntercellSpacing(new Dimension(0,0));

        RutaCellRenderer renderer = new RutaCellRenderer();
        tablaRuta.getColumnModel().getColumn(0).setCellRenderer(renderer);
        tablaRuta.getColumnModel().getColumn(1).setCellRenderer(renderer);
        tablaRuta.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaRuta.getColumnModel().getColumn(0).setMaxWidth(130);

        JScrollPane scrollText = new JScrollPane(tablaRuta);
        aplicarEstiloScroll(scrollText);
        panelLateral.add(scrollText, BorderLayout.CENTER);

        // Botones apilados verticalmente para mejor ajuste en el panel lateral
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 0, 10));
        panelBotones.setOpaque(false);

        btnAvanzar = makeActionButton("Siguiente Parada", ORANGE_PRIMARY, TEXT_WHITE);
        btnFinalizar = makeActionButton("Finalizar Ruta", DIVIDER, TEXT_MUTED);
        btnEditar = makeActionButton("Editar Ruta", BG_CARD, TEXT_WHITE);

        btnFinalizar.setEnabled(false); // Nace bloqueado

        panelBotones.add(btnAvanzar);
        panelBotones.add(btnFinalizar);
        panelBotones.add(btnEditar);
        panelLateral.add(panelBotones, BorderLayout.SOUTH);

        body.add(panelLateral, BorderLayout.EAST);
        root.add(body, BorderLayout.CENTER);

        wireEvents();
        cargarRutaEnTabla(); // Al cargar la tabla, también se actualizará el mapa gráficamente
    }

    /**Metodos de la Ventana*/
    private void wireEvents() {
        btnAvanzar.addActionListener(e -> {
            String rutaActual = usuarioActivo.getRutaActual();
            String[] partes = rutaActual.split(FormateadorRutas.SEP_MIXTO);
            if (partes.length >= 3) {
                try {
                    int idOrigen = Integer.parseInt(partes[0]);
                    int idDestino = Integer.parseInt(partes[2]);
                    Nodo origen = gestorNodos.buscarNodo(idOrigen);
                    if (origen != null) {
                        for (AristaRuta arista : origen.getRutasSalientes()) {
                            if (arista.getNodoFinal().getIdNodo() == idDestino) {
                                float distMetros = (float) arista.getDistancia();
                                float pendiente = (float) arista.getPendienteMedia();
                                usuarioActivo.setKmRecorridos(usuarioActivo.getKmRecorridos() + (distMetros / 1000.0f));
                                if (pendiente > 0) {
                                    usuarioActivo.setDesnivelPosAcum(usuarioActivo.getDesnivelPosAcum() + (distMetros * (pendiente / 100.0f)));
                                }
                                usuarioActivo.setHorasMovimiento(usuarioActivo.getHorasMovimiento() + ((distMetros / 1000.0f) / 15.0f));
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Error sumando estadísticas: " + ex.getMessage());
                }

                // Consumir el nodo y la arista recorridos
                String[] nuevasPartes = Arrays.copyOfRange(partes, 2, partes.length);
                usuarioActivo.setRutaActual(String.join(FormateadorRutas.SEP_MIXTO, nuevasPartes));

                String[] nodosAct = usuarioActivo.getRutaActualNodos().split(FormateadorRutas.SEP_NODOS);
                if (nodosAct.length > 1) usuarioActivo.setRutaActualNodos(String.join(FormateadorRutas.SEP_NODOS, Arrays.copyOfRange(nodosAct, 1, nodosAct.length)));

                String[] aristasAct = usuarioActivo.getRutaActualAristas().split(FormateadorRutas.SEP_ARISTAS);
                if (aristasAct.length > 1) usuarioActivo.setRutaActualAristas(String.join(FormateadorRutas.SEP_ARISTAS, Arrays.copyOfRange(aristasAct, 1, aristasAct.length)));

                gestor.guardarUsuarios();
                cargarRutaEnTabla(); // Esto re-dibujará el mapa consumiendo el tramo recorrido
            }
        });

        btnEditar.addActionListener(e -> GestorVentanas.cambiarVentana(this, new VentanaRutas(usuarioActivo, gestor, gestorNodos, gestorRutas)));

        btnFinalizar.addActionListener(e -> {
            String histAux = usuarioActivo.getHistorialAuxiliar();
            String histGlobal = usuarioActivo.getHistorial();
            String viajeTerminado = FormateadorRutas.generarTimestamp() + FormateadorRutas.SEP_FECHA + (histAux != null ? histAux : "");

            usuarioActivo.setHistorial((histGlobal == null || histGlobal.isEmpty()) ? viajeTerminado : (histGlobal + FormateadorRutas.SEP_HISTORIAL + viajeTerminado));
            usuarioActivo.setRutaActual("");
            usuarioActivo.setRutaActualNodos("");
            usuarioActivo.setRutaActualAristas("");
            usuarioActivo.setHistorialAuxiliar("");
            gestor.guardarUsuarios();

            JOptionPane.showMessageDialog(this, "¡Felicidades! Has llegado a tu destino. El viaje se ha guardado en tu historial.", "Ruta Finalizada", JOptionPane.INFORMATION_MESSAGE);
            GestorVentanas.cambiarVentana(this, new VentanaPrincipal(usuarioActivo, gestor, gestorNodos, gestorRutas));
        });
    }

    private void cargarRutaEnTabla() {
        modeloTabla.setRowCount(0);
        String rutaBruta = usuarioActivo.getRutaActual();
        List<Integer> nodosActivosEnMapa = new ArrayList<>();

        if (rutaBruta == null || rutaBruta.trim().isEmpty()) {
            modeloTabla.addRow(new Object[]{"INFO", "No hay ruta activa."});
            btnAvanzar.setEnabled(false);
            btnEditar.setEnabled(false);
            panelMapa.setRutaActiva(nodosActivosEnMapa); // Limpia el mapa
            return;
        }

        String[] partes = rutaBruta.split(FormateadorRutas.SEP_MIXTO);
        for (int i = 0; i < partes.length; i++) {
            if (i % 2 == 0) {
                try {
                    int idNodo = Integer.parseInt(partes[i]);
                    nodosActivosEnMapa.add(idNodo); // Añadir ID para que el mapa lo grafique
                    Nodo nodo = gestorNodos.buscarNodo(idNodo);
                    String nombreNodo = (nodo != null) ? nodo.getNombreNodo() : ("ID: " + idNodo);

                    if (i == 0) modeloTabla.addRow(new Object[]{"📍 AHORA", nombreNodo});
                    else if (i == partes.length - 1) modeloTabla.addRow(new Object[]{"🏁 LLEGADA", nombreNodo});
                    else modeloTabla.addRow(new Object[]{"📍 PARADA", nombreNodo});
                } catch (NumberFormatException e) {
                    modeloTabla.addRow(new Object[]{"ERROR", "Nodo Inválido"});
                }
            } else {
                modeloTabla.addRow(new Object[]{"   ↓ VÍA", partes[i]});
            }
        }

        // Enviar la lista de IDs de la ruta restante al componente del Mapa para que se actualice
        panelMapa.setRutaActiva(nodosActivosEnMapa);

        if (partes.length <= 1) {
            btnAvanzar.setEnabled(false);
            btnAvanzar.setBackground(DIVIDER);
            btnAvanzar.setForeground(TEXT_MUTED);
            btnFinalizar.setEnabled(true);
            btnFinalizar.setBackground(DANGER);
            btnFinalizar.setForeground(TEXT_WHITE);
            btnEditar.setEnabled(false);
        }
    }

    // =============================================================================
    // COMPONENTE: PANEL DE MAPA CON NAVEGACIÓN GRÁFICA EN TIEMPO REAL
    // =============================================================================
    // =============================================================================
    // COMPONENTE: PANEL DE MAPA CON NAVEGACIÓN GRÁFICA EN TIEMPO REAL
    // =============================================================================
    private class PanelMapaNavegacion extends JPanel {
        private double escala = 0.5;
        private double traslacionX = 0;
        private double traslacionY = 0;
        private Point puntoArrastre = null;
        private boolean inicioCentrado = false;

        private final Image imagenFondo;
        private final GestorNodos gestorNodos;
        private List<Integer> rutaNodosIds = new ArrayList<>(); // Almacena la ruta viva actual

        PanelMapaNavegacion(GestorNodos gestorNodos) {
            this.gestorNodos = gestorNodos;
            Image img = null;
            try { img = ImageIO.read(new java.io.File("baseDeDatos/realBajo.jpg")); }
            catch (java.io.IOException ex) { System.err.println("No se pudo cargar el mapa."); }
            this.imagenFondo = img;

            setBackground(new Color(15, 15, 20));
            setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
            setCursor(new Cursor(Cursor.MOVE_CURSOR));

            // Navegación (Rueda zoom, Click arrastrar)
            addMouseWheelListener((MouseWheelListener) e -> {
                int mx = e.getX(), my = e.getY();
                double wx = (mx - traslacionX) / escala;
                double wy = (my - traslacionY) / escala;
                double factor = 1.1;
                escala = e.getWheelRotation() < 0 ? escala * factor : escala / factor;
                escala = Math.max(Math.min(escala, 4.0), 0.1);
                traslacionX = mx - wx * escala;
                traslacionY = my - wy * escala;
                repaint();
            });

            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { if(e.getButton() == MouseEvent.BUTTON1) puntoArrastre = e.getPoint(); }
                @Override public void mouseReleased(MouseEvent e) { if(e.getButton() == MouseEvent.BUTTON1) puntoArrastre = null; }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override public void mouseDragged(MouseEvent e) {
                    if (puntoArrastre != null) {
                        traslacionX += e.getX() - puntoArrastre.x;
                        traslacionY += e.getY() - puntoArrastre.y;
                        puntoArrastre = e.getPoint();
                        repaint();
                    }
                }
            });

            // AQUÍ ESTÁ LA MAGIA: Escuchamos cuando el panel por fin tiene tamaño
            addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    if (!inicioCentrado && getWidth() > 0 && getHeight() > 0) {
                        inicioCentrado = true;
                        centrarEnNodoActual(); // Forzamos el centrado inicial
                        repaint();
                    }
                }
            });
        }

        // Extraemos la lógica matemática de la cámara a un método independiente
        private void centrarEnNodoActual() {
            if (rutaNodosIds != null && !rutaNodosIds.isEmpty() && getWidth() > 0 && getHeight() > 0) {
                Nodo nodoActual = gestorNodos.buscarNodo(rutaNodosIds.get(0));
                if (nodoActual != null) {
                    escala = 1.5; // Zoom automático de navegación
                    traslacionX = (getWidth() / 2.0) - (nodoActual.getCoordenada().getPosX() * escala);
                    traslacionY = (getHeight() / 2.0) - (nodoActual.getCoordenada().getPosY() * escala);
                }
            }
        }

        // Actualiza la línea de trazado e intenta centrar la cámara en la posición del ciclista
        public void setRutaActiva(List<Integer> nodosIds) {
            this.rutaNodosIds = nodosIds;
            centrarEnNodoActual(); // Centra la cámara si la ventana ya es visible
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            java.awt.geom.AffineTransform tx = g2.getTransform();

            g2.translate(traslacionX, traslacionY);
            g2.scale(escala, escala);

            // 1. Mapa Base
            if (imagenFondo != null) {
                g2.drawImage(imagenFondo, 0, 0, imagenFondo.getWidth(this), imagenFondo.getHeight(this), this);
            }

            // 2. Calles / Aristas generales (Atenuadas para no distraer)
            g2.setColor(new Color(150, 150, 150, 60));
            g2.setStroke(new BasicStroke((float) (1.5 / escala)));
            for (Nodo nodo : gestorNodos.getMapaNodos().values()) {
                for (AristaRuta ruta : nodo.getRutasSalientes()) {
                    int x1 = (int) ruta.getNodoInicio().getCoordenada().getPosX();
                    int y1 = (int) ruta.getNodoInicio().getCoordenada().getPosY();
                    int x2 = (int) ruta.getNodoFinal().getCoordenada().getPosX();
                    int y2 = (int) ruta.getNodoFinal().getCoordenada().getPosY();
                    g2.drawLine(x1, y1, x2, y2);
                }
            }

            // 3. LA RUTA ACTIVA (Gruesa y Brillante, representa lo que falta por recorrer)
            if (rutaNodosIds != null && rutaNodosIds.size() > 1) {
                g2.setStroke(new BasicStroke((float) (6.0 / escala), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(ORANGE_PRIMARY);
                for (int i = 0; i < rutaNodosIds.size() - 1; i++) {
                    Nodo n1 = gestorNodos.buscarNodo(rutaNodosIds.get(i));
                    Nodo n2 = gestorNodos.buscarNodo(rutaNodosIds.get(i+1));
                    if (n1 != null && n2 != null) {
                        g2.drawLine((int)n1.getCoordenada().getPosX(), (int)n1.getCoordenada().getPosY(),
                                (int)n2.getCoordenada().getPosX(), (int)n2.getCoordenada().getPosY());
                    }
                }
            }

            g2.setTransform(tx); // Regresar a escala de pantalla para los Nodos

            // 4. Dibujar Nodos de la Ruta Activa con colores de estado
            if (rutaNodosIds != null && !rutaNodosIds.isEmpty()) {
                for (int i = 0; i < rutaNodosIds.size(); i++) {
                    Nodo n = gestorNodos.buscarNodo(rutaNodosIds.get(i));
                    if (n == null) continue;

                    int screenX = (int) (n.getCoordenada().getPosX() * escala + traslacionX);
                    int screenY = (int) (n.getCoordenada().getPosY() * escala + traslacionY);

                    // Lógica de colores semafóricos
                    Color fillCol = ORANGE_PRIMARY; // Nodos intermedios por defecto
                    int radio = 8;
                    String etiqueta = n.getNombreNodo();

                    if (i == 0) {
                        fillCol = SUCCESS; // Punto actual (Verde)
                        radio = 11;
                        etiqueta = "📍 AHORA: " + etiqueta;
                    }
                    else if (i == rutaNodosIds.size() - 1) {
                        fillCol = DANGER; // Destino (Rojo)
                        radio = 11;
                        etiqueta = "🏁 DESTINO";
                    }

                    // Círculo del nodo
                    g2.setColor(fillCol);
                    g2.fillOval(screenX - radio, screenY - radio, radio*2, radio*2);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawOval(screenX - radio, screenY - radio, radio*2, radio*2);

                    // Dibujar nombre de la calle/nodo
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(etiqueta);
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.fillRoundRect(screenX - tw / 2 - 4, screenY - radio - fm.getHeight() - 4, tw + 8, fm.getHeight() + 2, 6, 6);
                    g2.setColor(Color.WHITE);
                    g2.drawString(etiqueta, screenX - tw / 2, screenY - radio - 6);
                }
            }
        }
    }

    // =============================================================================
    // Utilidades y Estilos UI
    // =============================================================================
    private class RutaCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(new Font("Arial", Font.BOLD, 13));
            setBorder(new EmptyBorder(5, 15, 5, 15));
            int totalRows = table.getRowCount();
            if (totalRows == 1 || row == 0) { c.setBackground(SUCCESS); c.setForeground(Color.WHITE); }
            else if (row == totalRows - 1)  { c.setBackground(DANGER); c.setForeground(Color.WHITE); }
            else                            { c.setBackground(BG_CARD); c.setForeground(TEXT_WHITE); }

            ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BG_DARK), new EmptyBorder(5, 15, 5, 15)));
            return c;
        }
    }

    private JButton makeActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial Black", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(bg.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { if (btn.isEnabled()) btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton makeIconButton(String iconPath, int size, String tooltip) {
        JButton btn = new JButton();
        try {
            Image scaled = new ImageIcon(iconPath).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
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

    private void aplicarEstiloScroll(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = SCROLL_THUMB; this.trackColor = BG_DARK; }
            @Override protected JButton createDecreaseButton(int o) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected JButton createIncreaseButton(int o) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) { g.setColor(BG_DARK); g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height); }
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