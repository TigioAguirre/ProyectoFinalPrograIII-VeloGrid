package com.veloGrid.ventanas;

import com.veloGrid.estructuras.*;
import com.veloGrid.clasesBase.Usuario;
import com.veloGrid.ventanas.modificacionesSwing.Button;
import com.veloGrid.ventanas.modificacionesSwing.MyPasswordField;
import com.veloGrid.ventanas.modificacionesSwing.MyTextField;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.CardLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

public class PanelLoginAndRegister extends JLayeredPane {
    private JPanel login;
    private JPanel register;
    private GestorUsuarios gestor;
    private MyTextField txtUserLogin;
    private MyPasswordField txtPassLogin;
    private MyTextField     txtUserRegister;
    private MyTextField     txtEmailRegister;
    private MyPasswordField txtPassRegister;
    // Paleta VeloGrid
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color BG_CARD        = new Color(22, 22, 30);
    private static final Color ORANGE_PRIMARY = new Color(252, 76, 2);
    private static final Color TEXT_WHITE     = new Color(240, 240, 240);
    private static final Color TEXT_MUTED     = new Color(140, 140, 155);
    /**Constructores de la Ventana*/
    public PanelLoginAndRegister() {
        gestor = new GestorUsuarios();
        setLayout(new CardLayout());
        login = new JPanel();
        login.setBackground(BG_DARK);
        login.setPreferredSize(new java.awt.Dimension(327, 300));
        add(login, "card_login");
        register = new JPanel();
        register.setBackground(BG_DARK);
        register.setPreferredSize(new java.awt.Dimension(327, 300));
        add(register, "card_register");
        initRegister();
        initLogin();
        login.setVisible(false);
        register.setVisible(true);
    }
    /**Metodos de la Ventana*/
    private void initRegister() {
        register.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        JLabel label = new JLabel("Crear Cuenta");
        label.setFont(new Font("Arial Black", Font.BOLD, 26));
        label.setForeground(TEXT_WHITE);
        register.add(label);
        txtUserRegister = new MyTextField();
        txtUserRegister.setHint("Usuario");
        txtUserRegister.setForeground(TEXT_WHITE);
        txtUserRegister.setBackground(BG_CARD);
        register.add(txtUserRegister, "w 60%");
        txtPassRegister = new MyPasswordField();
        txtPassRegister.setHint("Contraseña");
        txtPassRegister.setForeground(TEXT_WHITE);
        txtPassRegister.setBackground(BG_CARD);
        register.add(txtPassRegister, "w 60%");
        Button cmd = new Button();
        cmd.setBackground(ORANGE_PRIMARY);
        cmd.setForeground(TEXT_WHITE);
        cmd.setFont(new Font("Arial", Font.BOLD, 12));
        cmd.setText("REGÍSTRATE");
        cmd.addActionListener(e -> registrarse());
        register.add(cmd, "w 40%, h 40");
    }
    private void initLogin() {
        login.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        JLabel label = new JLabel("Iniciar Sesión");
        label.setFont(new Font("Arial Black", Font.BOLD, 26));
        label.setForeground(TEXT_WHITE);
        login.add(label);
        txtUserLogin = new MyTextField();
        txtUserLogin.setHint("Usuario");
        txtUserLogin.setForeground(TEXT_WHITE);
        txtUserLogin.setBackground(BG_CARD);
        login.add(txtUserLogin, "w 60%");
        txtPassLogin = new MyPasswordField();
        txtPassLogin.setHint("Contraseña");
        txtPassLogin.setForeground(TEXT_WHITE);
        txtPassLogin.setBackground(BG_CARD);
        login.add(txtPassLogin, "w 60%");
        JButton cmdForget = new JButton("¿Olvidé mi Contraseña?");
        cmdForget.setForeground(TEXT_MUTED);
        cmdForget.setFont(new Font("Arial", Font.PLAIN, 12));
        cmdForget.setContentAreaFilled(false);
        cmdForget.setBorderPainted(false);
        cmdForget.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdForget.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Contacta al administrador para recuperar tu cuenta.",
                        "Recuperar Contraseña", JOptionPane.INFORMATION_MESSAGE));
        login.add(cmdForget);
        Button cmd = new Button();
        cmd.setBackground(ORANGE_PRIMARY);
        cmd.setForeground(TEXT_WHITE);
        cmd.setFont(new Font("Arial", Font.BOLD, 12));
        cmd.setText("INICIAR SESIÓN");
        cmd.addActionListener(e -> iniciarSesion());
        login.add(cmd, "w 40%, h 40");
    }
    private void iniciarSesion() {
        String user = txtUserLogin.getText().trim();
        String pass = new String(txtPassLogin.getPassword());
        if (user.isEmpty() || pass.isEmpty() || user.equals("Usuario") || pass.equals("Contraseña")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, llena todos los campos.",
                    "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario usuarioLogueado = gestor.autenticarUsuario(user, pass);
        if (usuarioLogueado != null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            JFrame ventanaActual = (window instanceof JFrame) ? (JFrame) window : null;
            JFrame ventanaNueva = !usuarioLogueado.getUsrRegistrado()
                    ? new VentanaConfiguracion(usuarioLogueado, gestor)
                    : new VentanaPrincipal(usuarioLogueado, gestor);
            if (ventanaActual != null) {
                GestorVentanas.cambiarVentana(ventanaActual, ventanaNueva);
            } else {
                ventanaNueva.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos.",
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void registrarse() {
        String user = txtUserRegister.getText().trim();
        String pass = new String(txtPassRegister.getPassword());
        if (user.isEmpty() || pass.isEmpty() || user.equals("Usuario") || pass.equals("Contraseña")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, llena todos los campos.",
                    "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean exito = gestor.registrarNuevoUsuario(user, pass);
        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "¡Registro exitoso! Ahora puedes iniciar sesión.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtUserRegister.setText("");
            txtEmailRegister.setText("");
            txtPassRegister.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "El nombre de usuario ya existe. Intenta con otro.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void showRegister(boolean show) {
        if (show) {
            register.setVisible(true);
            login.setVisible(false);
        } else {
            register.setVisible(false);
            login.setVisible(true);
        }
    }
}