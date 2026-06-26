package com.veloGrid.ventanas;

import com.veloGrid.ventanas.modificacionesSwing.ButtonOutLine;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class PanelCover extends JPanel {
    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));
    private ActionListener event;
    private MigLayout layout;
    private JLabel title;
    private JLabel description;
    private JLabel description1;
    private ButtonOutLine button;
    private boolean isLogin;
    // Paleta VeloGrid Cover
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color ORANGE_DARK = new Color(180, 40, 0);
    /**Constructor del Cover del Login*/
    public PanelCover() {
        setPreferredSize(new java.awt.Dimension(327, 300));
        setOpaque(false);
        layout = new MigLayout("wrap, fill", "[center]", "push[]25[]10[]25[]push");
        setLayout(layout);
        init();
    }
    /**Metodos del Cover*/
    private void init() {
        title = new JLabel("Bienvenido a VelóGrid");
        title.setFont(new Font("Arial Black", Font.BOLD, 20)); // Adaptado a la tipografía VeloGrid
        title.setForeground(new Color(245, 245, 245));
        add(title);
        description = new JLabel("Para Ingresar");
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(new Color(245, 245, 245));
        add(description);
        description1 = new JLabel("Accede con tus credenciales");
        description1.setFont(new Font("Arial", Font.PLAIN, 14));
        description1.setForeground(new Color(245, 245, 245));
        add(description1);
        button = new ButtonOutLine();
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(new Color(255, 255, 255));
        button.setText("INICIAR SESIÓN");
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.actionPerformed(ae);
            }
        });
        add(button, "w 60%, h 40");
    }
    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Nuevo gradiente VeloGrid (Naranja a Naranja Oscuro)
        GradientPaint gra = new GradientPaint(0, 0, ORANGE_PRIMARY, 0, getHeight(), ORANGE_DARK);
        g2.setPaint(gra);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }
    public void addEvent(ActionListener event) {
        this.event = event;
    }
    public void registerLeft(double v) {
        v = Double.valueOf(df.format(v));
        login(false);
        layout.setComponentConstraints(title, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description1, "pad 0 -" + v + "% 0 0");
    }
    public void registerRight(double v) {
        v = Double.valueOf(df.format(v));
        login(false);
        layout.setComponentConstraints(title, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description, "pad 0 -" + v + "% 0 0");
        layout.setComponentConstraints(description1, "pad 0 -" + v + "% 0 0");
    }
    public void loginLeft(double v) {
        v = Double.valueOf(df.format(v));
        login(true);
        layout.setComponentConstraints(title, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description1, "pad 0 " + v + "% 0 " + v + "%");
    }
    public void loginRight(double v) {
        v = Double.valueOf(df.format(v));
        login(true);
        layout.setComponentConstraints(title, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description, "pad 0 " + v + "% 0 " + v + "%");
        layout.setComponentConstraints(description1, "pad 0 " + v + "% 0 " + v + "%");
    }
    public void login(boolean login) {
        if (this.isLogin != login) {
            if (login) {
                title.setText("Bienvenido a VelóGrid");
                description.setText("Regístrate con tu información");
                description1.setText("para poder acceder a VelóGrid");
                button.setText("REGISTRARME");
            } else {
                title.setText("Bienvenido a VelóGrid");
                description.setText("¿Ya tienes cuenta?");
                description1.setText("Ingresa a VelóGrid");
                button.setText("INICIAR SESIÓN");
            }
            this.isLogin = login;
        }
    }
}