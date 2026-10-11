package cl.duoc.biblioteca.vista;

import cl.duoc.biblioteca.dao.UsuarioDAO;
import cl.duoc.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginFrame() {
        setTitle("Sistema de Biblioteca - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Superior (Título) ---
        JPanel panelNorte = new JPanel();
        panelNorte.setBackground(new Color(41, 128, 185)); // Azul estilo corporativo
        JLabel lblTitulo = new JLabel("Ingreso al Sistema");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelNorte.add(lblTitulo);
        add(panelNorte, BorderLayout.NORTH);

        // --- Panel Central (Formulario) ---
        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 10, 20));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        panelCentro.add(new JLabel("Correo Electrónico:"));
        txtCorreo = new JTextField();
        panelCentro.add(txtCorreo);

        panelCentro.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panelCentro.add(txtPassword);

        add(panelCentro, BorderLayout.CENTER);

        // --- Panel Inferior (Botón) ---
        JPanel panelSur = new JPanel();
        panelSur.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        panelSur.add(btnIngresar);

        add(panelSur, BorderLayout.SOUTH);

        // --- ACCIÓN DEL BOTÓN ---
        btnIngresar.addActionListener(e -> {
            String correo = txtCorreo.getText();
            String pass = new String(txtPassword.getPassword());

            // Validación de campos vacíos
            if (correo.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese sus credenciales.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar contra la Base de Datos usando el DAO
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.autenticarUsuario(correo, pass);

            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bienvenido(a) " + usuario.getNombre());
                this.dispose(); // Cierra la ventana de login
                // Abre el menú principal pasándole el usuario logueado para manejar los roles
                new MenuPrincipalFrame(usuario).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}