package com.veloGrid.main;

import com.veloGrid.ventanas.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class Main extends JFrame {

    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));
    private MigLayout layout;
    private PanelCover cover;
    private PanelLoginAndRegister loginAndRegister;
    private boolean isLogin = true;
    private final double addSize = 30;
    private final double coverSize = 40;
    private final double loginSize = 60;
    private JLayeredPane bg;

    // Paleta para VeloGrid
    private static final Color BG_DARK      = new Color(15, 15, 20);
    private static final Color TEXT_MUTED   = new Color(140, 140, 155);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);

    public Main() {
        initComponents();
        init();
    }

    private void initComponents() {
        bg = new JLayeredPane();
        bg.setBackground(BG_DARK);
        bg.setOpaque(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        setPreferredSize(ConfiguracionVentanas.TAMAÑO_ESTANDAR);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bg, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void init() {
        layout = new MigLayout("fill, insets 0");
        cover = new PanelCover();
        loginAndRegister = new PanelLoginAndRegister();
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                double fractionCover;
                double fractionLogin;
                double size = coverSize;
                if (fraction <= 0.5f) {
                    size += fraction * addSize;
                } else {
                    size += addSize - fraction * addSize;
                }
                if (isLogin) {
                    fractionCover = 1f - fraction;
                    fractionLogin = fraction;
                    if (fraction >= 0.5f) {
                        cover.registerRight(fractionCover * 100);
                    } else {
                        cover.loginRight(fractionLogin * 100);
                    }
                } else {
                    fractionCover = fraction;
                    fractionLogin = 1f - fraction;
                    if (fraction <= 0.5f) {
                        cover.registerLeft(fraction * 100);
                    } else {
                        cover.loginLeft((1f - fraction) * 100);
                    }
                }
                if (fraction >= 0.5f) {
                    loginAndRegister.showRegister(isLogin);
                }
                fractionCover = Double.valueOf(df.format(fractionCover));
                fractionLogin = Double.valueOf(df.format(fractionLogin));
                layout.setComponentConstraints(cover, "width " + size + "%, pos " + fractionCover + "al 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width " + loginSize + "%, pos " + fractionLogin + "al 0 n 100%");
                bg.revalidate();
            }
            @Override
            public void end() {
                isLogin = !isLogin;
            }
        };

        Animator animator = new Animator(800, target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0);

        // --- Botón Cerrar ---
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("sansserif", Font.BOLD, 16)); // Fuente un poco más grande
        closeButton.setMargin(new Insets(0, 0, 0, 0)); // Centrado perfecto
        closeButton.setForeground(TEXT_MUTED);
        closeButton.setBackground(BG_DARK);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(true);
        closeButton.setOpaque(true);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(ORANGE_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(TEXT_MUTED);
            }
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        // --- Botón Maximizar / Restaurar ---
        JButton maxButton = new JButton("◻");
        maxButton.setFont(new Font("sansserif", Font.BOLD, 18)); // Fuente un poco más grande
        maxButton.setMargin(new Insets(0, 0, 0, 0)); // Centrado perfecto
        maxButton.setForeground(TEXT_MUTED);
        maxButton.setBackground(BG_DARK);
        maxButton.setBorderPainted(false);
        maxButton.setFocusPainted(false);
        maxButton.setContentAreaFilled(true);
        maxButton.setOpaque(true);
        maxButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        maxButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                maxButton.setForeground(ORANGE_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                maxButton.setForeground(TEXT_MUTED);
            }
        });
        maxButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getExtendedState() == JFrame.NORMAL) {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                } else {
                    setExtendedState(JFrame.NORMAL);
                }
            }
        });

        // --- Botón Minimizar ---
        JButton minButton = new JButton("—");
        minButton.setFont(new Font("sansserif", Font.BOLD, 14));
        minButton.setMargin(new Insets(0, 0, 0, 0)); // Centrado perfecto
        minButton.setForeground(TEXT_MUTED);
        minButton.setBackground(BG_DARK);
        minButton.setBorderPainted(false);
        minButton.setFocusPainted(false);
        minButton.setContentAreaFilled(true);
        minButton.setOpaque(true);
        minButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                minButton.setForeground(ORANGE_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                minButton.setForeground(TEXT_MUTED);
            }
        });
        minButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setExtendedState(JFrame.ICONIFIED);
            }
        });

        bg.setLayout(layout);
        bg.add(cover, "width " + coverSize + "%, pos " + (isLogin ? "1al" : "0al") + " 0 n 100%");
        bg.add(loginAndRegister, "width " + loginSize + "%, pos " + (isLogin ? "0al" : "1al") + " 0 n 100%");

        // --- DIMENSIONES MÁS RECTANGULARES (w 45) ---
        // Se separan de 45 en 45 para que las cajas (hitboxes) sean idénticas.
        bg.add(minButton, "pos 100%-135 0, w 45, h 35");
        bg.add(maxButton, "pos 100%-90 0, w 45, h 35");
        bg.add(closeButton, "pos 100%-45 0, w 45, h 35");

        bg.setLayer(closeButton, JLayeredPane.MODAL_LAYER);
        bg.setLayer(maxButton, JLayeredPane.MODAL_LAYER);
        bg.setLayer(minButton, JLayeredPane.MODAL_LAYER);

        loginAndRegister.showRegister(!isLogin);
        cover.login(isLogin);

        // --- ¡EL EVENTO QUE FALTABA PARA QUE FUNCIONE EL ANIMATOR! ---
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) {
                    animator.start();
                }
            }
        });
    }

    public static void main(String[] args) {
        // Activar pipeline OpenGL de Java2D ANTES de que Swing inicialice cualquier ventana.
        // Esto delega el compositing y el drawImage a la GPU, reduciendo la carga de CPU
        // especialmente al hacer zoom/pan sobre imágenes grandes como el mapa.
        System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}