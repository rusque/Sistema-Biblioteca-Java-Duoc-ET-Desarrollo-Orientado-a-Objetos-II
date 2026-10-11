package cl.duoc.biblioteca.vista;

import cl.duoc.biblioteca.dao.PrestamoDAO;
import cl.duoc.biblioteca.modelo.Prestamo;

import javax.swing.*;
import java.awt.*;

public class PrestamosFrame extends JFrame {

    private JTextField txtIdEstudiante, txtIdLibro, txtIdPrestamo;
    private JButton btnPrestar, btnDevolver;
    private JProgressBar barraProgreso;

    public PrestamosFrame() {
        setTitle("Préstamos y Devoluciones");
        setSize(480, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL CENTRAL (Formulario) ---
        JPanel panelCentro = new JPanel(new GridLayout(4, 2, 10, 15));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelCentro.add(new JLabel("ID Estudiante (Solo Préstamo):"));
        txtIdEstudiante = new JTextField();
        panelCentro.add(txtIdEstudiante);

        panelCentro.add(new JLabel("ID Libro (Solo Préstamo):"));
        txtIdLibro = new JTextField();
        panelCentro.add(txtIdLibro);

        panelCentro.add(new JLabel("ID Préstamo (Solo Devolución):"));
        txtIdPrestamo = new JTextField();
        panelCentro.add(txtIdPrestamo);

        btnPrestar = new JButton("Realizar Préstamo");
        btnDevolver = new JButton("Registrar Devolución");
        panelCentro.add(btnPrestar);
        panelCentro.add(btnDevolver);

        add(panelCentro, BorderLayout.CENTER);

        // --- PANEL INFERIOR (Barra de progreso) ---
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Esperando...");
        panelSur.add(barraProgreso, BorderLayout.CENTER);

        add(panelSur, BorderLayout.SOUTH);

        // --- EVENTOS CON HILOS (THREADS) ---
        btnPrestar.addActionListener(e -> ejecutarPrestamoConHilo());
        btnDevolver.addActionListener(e -> ejecutarDevolucionConHilo());
    }

    private void ejecutarPrestamoConHilo() {
        String idEstudiante = txtIdEstudiante.getText();
        String idLibro = txtIdLibro.getText();

        if (idEstudiante.isEmpty() || idLibro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Para prestar, ingrese el ID del Estudiante y el ID del Libro.");
            return;
        }

        deshabilitarInterfaz("Procesando préstamo...");

        Thread hiloPrestamo = new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 20) {
                    Thread.sleep(300);
                    final int progreso = i;
                    SwingUtilities.invokeLater(() -> barraProgreso.setValue(progreso));
                }

                int idEst = Integer.parseInt(idEstudiante);
                int idLib = Integer.parseInt(idLibro);

                Prestamo prestamo = new Prestamo();
                prestamo.setIdEstudiante(idEst);
                prestamo.setIdLibro(idLib);
                prestamo.setFechaPrestamo(java.sql.Date.valueOf(java.time.LocalDate.now()));
                prestamo.setFechaDevolucion(java.sql.Date.valueOf(java.time.LocalDate.now().plusDays(7)));
                prestamo.setDevuelto(false);

                PrestamoDAO prestamoDAO = new PrestamoDAO();
                boolean exito = prestamoDAO.registrarPrestamo(prestamo);

                SwingUtilities.invokeLater(() -> {
                    if (exito) {
                        barraProgreso.setString("¡Préstamo exitoso!");
                        JOptionPane.showMessageDialog(this, "Préstamo registrado. El stock ha disminuido en 1.");
                    } else {
                        barraProgreso.setString("Error al guardar");
                        JOptionPane.showMessageDialog(this, "No se pudo registrar. Verifique stock y los IDs.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    restaurarInterfaz();
                });

            } catch (InterruptedException | NumberFormatException ex) {
                SwingUtilities.invokeLater(() -> {
                    barraProgreso.setString("Error en el proceso");
                    JOptionPane.showMessageDialog(this, "Asegúrese de ingresar solo números en los IDs.");
                    restaurarInterfaz();
                });
            }
        });
        hiloPrestamo.start();
    }

    private void ejecutarDevolucionConHilo() {
        String idPrestamoStr = txtIdPrestamo.getText();

        // VALIDACIÓN: Ahora solo exigimos el ID del Préstamo
        if (idPrestamoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Para devolver, ingrese SOLO el ID del Préstamo.");
            return;
        }

        deshabilitarInterfaz("Procesando devolución...");

        Thread hiloDevolucion = new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 20) {
                    Thread.sleep(300);
                    final int progreso = i;
                    SwingUtilities.invokeLater(() -> barraProgreso.setValue(progreso));
                }

                int idPrestamo = Integer.parseInt(idPrestamoStr);

                PrestamoDAO prestamoDAO = new PrestamoDAO();
                // Llamamos al DAO enviando únicamente el código unitario del préstamo
                boolean exito = prestamoDAO.registrarDevolucion(idPrestamo);

                SwingUtilities.invokeLater(() -> {
                    if (exito) {
                        barraProgreso.setString("¡Devolución exitosa!");
                        JOptionPane.showMessageDialog(this, "Devolución registrada automáticamente. El stock del libro ha aumentado en 1.");
                    } else {
                        barraProgreso.setString("Error al devolver");
                        JOptionPane.showMessageDialog(this, "No se pudo registrar. Verifique que el ID de Préstamo exista y que no haya sido devuelto antes.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    restaurarInterfaz();
                });

            } catch (InterruptedException | NumberFormatException ex) {
                SwingUtilities.invokeLater(() -> {
                    barraProgreso.setString("Error en el proceso");
                    JOptionPane.showMessageDialog(this, "Asegúrese de ingresar solo números en el ID de préstamo.");
                    restaurarInterfaz();
                });
            }
        });
        hiloDevolucion.start();
    }

    private void deshabilitarInterfaz(String mensaje) {
        btnPrestar.setEnabled(false);
        btnDevolver.setEnabled(false);
        txtIdEstudiante.setEnabled(false);
        txtIdLibro.setEnabled(false);
        txtIdPrestamo.setEnabled(false);
        barraProgreso.setString(mensaje);
    }

    private void restaurarInterfaz() {
        btnPrestar.setEnabled(true);
        btnDevolver.setEnabled(true);
        txtIdEstudiante.setEnabled(true);
        txtIdLibro.setEnabled(true);
        txtIdPrestamo.setEnabled(true);
        txtIdEstudiante.setText("");
        txtIdLibro.setText("");
        txtIdPrestamo.setText("");
        barraProgreso.setValue(0);
        barraProgreso.setString("Esperando...");
    }
}