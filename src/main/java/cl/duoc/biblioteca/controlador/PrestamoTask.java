package cl.duoc.biblioteca.controlador;

import cl.duoc.biblioteca.dao.PrestamoDAO;
import cl.duoc.biblioteca.modelo.Prestamo;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class PrestamoTask implements Runnable {

    private Prestamo prestamo;
    private PrestamoDAO prestamoDAO;

    // Constructor que recibe los datos necesarios para operar
    public PrestamoTask(Prestamo prestamo, PrestamoDAO prestamoDAO) {
        this.prestamo = prestamo;
        this.prestamoDAO = prestamoDAO;
    }

    @Override
    public void run() {
        // 1. Esto se ejecuta en SEGUNDO PLANO (no congela la pantalla)
        // Llama al método synchronized que creamos en PrestamoDAO
        boolean exito = prestamoDAO.registrarPrestamo(prestamo);

        // 2. Volvemos al hilo de la interfaz gráfica (Event Dispatch Thread) para mostrar el mensaje
        SwingUtilities.invokeLater(() -> {
            if (exito) {
                JOptionPane.showMessageDialog(null,
                        "Préstamo registrado exitosamente en segundo plano.\nStock actualizado.",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Error al registrar el préstamo. Verifique el stock o la conexión.",
                        "Error de Transacción",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}