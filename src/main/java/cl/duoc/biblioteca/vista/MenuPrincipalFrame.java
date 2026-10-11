package cl.duoc.biblioteca.vista;

import cl.duoc.biblioteca.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    private Usuario usuarioLogueado;

    public MenuPrincipalFrame(Usuario usuario) {
        this.usuarioLogueado = usuario;

        setTitle("Sistema de Gestión de Biblioteca - Menú Principal");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior de bienvenida
        JPanel panelNorte = new JPanel();
        panelNorte.setBackground(new Color(41, 128, 185));
        JLabel lblBienvenida = new JLabel("Bienvenido(a), " + usuario.getNombre() + " | Rol: " + usuario.getRol());
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        panelNorte.add(lblBienvenida);
        add(panelNorte, BorderLayout.NORTH);

        // Panel central con los botones del menú (Grid)
        JPanel panelCentral = new JPanel(new GridLayout(2, 2, 20, 20));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JButton btnLibros = new JButton("Gestión de Libros");
        JButton btnEstudiantes = new JButton("Gestión de Estudiantes");
        JButton btnPrestamos = new JButton("Préstamos y Devoluciones");
        JButton btnReportes = new JButton("Reportes");

        // Agregar botones al panel
        panelCentral.add(btnLibros);
        panelCentral.add(btnEstudiantes);
        panelCentral.add(btnPrestamos);
        panelCentral.add(btnReportes);
        add(panelCentral, BorderLayout.CENTER);

        // Lógica de roles: Si es estudiante, deshabilitamos la gestión total
        if ("estudiante".equalsIgnoreCase(usuario.getRol())) {
            btnLibros.setEnabled(false);
            btnEstudiantes.setEnabled(false);
            btnReportes.setEnabled(false);
        }

        // --- ACCIONES DE LOS BOTONES ---

        // Abre la ventana de Gestión de Libros
        btnLibros.addActionListener(e -> {
            GestionLibrosFrame gestionLibros = new GestionLibrosFrame();
            gestionLibros.setVisible(true);
        });

        // Abre la ventana de Gestión de Estudiantes
        btnEstudiantes.addActionListener(e -> {
            GestionEstudiantesFrame gestionEstudiantes = new GestionEstudiantesFrame();
            gestionEstudiantes.setVisible(true);
        });

        // Abre la ventana de Préstamos (Hilo en segundo plano)
        btnPrestamos.addActionListener(e -> {
            PrestamosFrame prestamosFrame = new PrestamosFrame();
            prestamosFrame.setVisible(true);
        });

        // Abre la ventana de Reportes
        btnReportes.addActionListener(e -> {
            ReportesFrame reportesFrame = new ReportesFrame();
            reportesFrame.setVisible(true);
        });

        // Panel inferior con botón de cerrar sesión
        JPanel panelSur = new JPanel();
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> {
            this.dispose(); // Cierra el menú principal
            new LoginFrame().setVisible(true); // Abre el login nuevamente
        });
        panelSur.add(btnCerrarSesion);
        add(panelSur, BorderLayout.SOUTH);
    }
}