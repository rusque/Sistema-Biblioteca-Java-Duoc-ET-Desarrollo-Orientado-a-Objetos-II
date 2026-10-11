package cl.duoc.biblioteca.vista;

import cl.duoc.biblioteca.dao.PrestamoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportesFrame extends JFrame {

    private JTable tablaReportes;
    private DefaultTableModel modeloTabla;
    private PrestamoDAO prestamoDAO;

    public ReportesFrame() {
        prestamoDAO = new PrestamoDAO();

        setTitle("Reportes del Sistema");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Título superior
        JLabel lblTitulo = new JLabel("Historial de Préstamos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // Tabla central
        modeloTabla = new DefaultTableModel(new String[]{"ID Préstamo", "Estudiante", "Libro", "Fecha Préstamo", "Fecha Devolución", "Estado"}, 0);
        tablaReportes = new JTable(modeloTabla);
        add(new JScrollPane(tablaReportes), BorderLayout.CENTER);

        // Botón inferior para cargar datos
        JPanel panelSur = new JPanel();
        JButton btnCargar = new JButton("Cargar Datos");
        btnCargar.addActionListener(e -> cargarReportes());
        panelSur.add(btnCargar);
        add(panelSur, BorderLayout.SOUTH);

        // Cargar automáticamente al abrir la ventana
        cargarReportes();
    }

    private void cargarReportes() {
        modeloTabla.setRowCount(0); // Limpiar la tabla
        List<Object[]> reportes = prestamoDAO.listarPrestamos();

        for (Object[] fila : reportes) {
            modeloTabla.addRow(fila);
        }
    }
}