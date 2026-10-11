package cl.duoc.biblioteca.dao;

import cl.duoc.biblioteca.conexion.DatabaseConnection;
import cl.duoc.biblioteca.modelo.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // MÉTODO SINCRONIZADO: Registrar préstamo y descontar stock
    public synchronized boolean registrarPrestamo(Prestamo prestamo) {
        Connection con = DatabaseConnection.getInstancia().getConexion();

        String sqlCheckStock = "SELECT stock FROM libros WHERE id = ?";
        String sqlUpdateStock = "UPDATE libros SET stock = stock - 1 WHERE id = ?";
        String sqlInsertPrestamo = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?)";

        try {
            con.setAutoCommit(false);

            // 1. Verificar stock
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheckStock)) {
                psCheck.setInt(1, prestamo.getIdLibro());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        int stock = rs.getInt("stock");
                        if (stock <= 0) {
                            System.out.println("Error: No hay stock disponible para este libro.");
                            con.rollback();
                            return false;
                        }
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            // 2. Descontar stock
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateStock)) {
                psUpdate.setInt(1, prestamo.getIdLibro());
                psUpdate.executeUpdate();
            }

            // 3. Registrar el préstamo
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsertPrestamo)) {
                psInsert.setInt(1, prestamo.getIdEstudiante());
                psInsert.setInt(2, prestamo.getIdLibro());
                psInsert.setDate(3, prestamo.getFechaPrestamo());
                psInsert.setDate(4, prestamo.getFechaDevolucion());
                psInsert.setBoolean(5, false);
                psInsert.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            System.err.println("Error en la transacción de préstamo: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar autoCommit: " + e.getMessage());
            }
        }
    }

    // MÉTODO SINCRONIZADO: Registrar devolución SOLO con el ID del Préstamo
    public synchronized boolean registrarDevolucion(int idPrestamo) {
        Connection con = DatabaseConnection.getInstancia().getConexion();

        String sqlGetLibro = "SELECT id_libro, devuelto FROM prestamos WHERE id = ?";
        String sqlUpdatePrestamo = "UPDATE prestamos SET devuelto = 1 WHERE id = ?";
        String sqlUpdateStock = "UPDATE libros SET stock = stock + 1 WHERE id = ?";

        try {
            con.setAutoCommit(false);

            int idLibro = -1;

            // 1. Buscar qué libro corresponde y si ya fue devuelto
            try (PreparedStatement psGet = con.prepareStatement(sqlGetLibro)) {
                psGet.setInt(1, idPrestamo);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        boolean devuelto = rs.getBoolean("devuelto");
                        if (devuelto) {
                            con.rollback();
                            return false;
                        }
                        idLibro = rs.getInt("id_libro");
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            // 2. Marcar devuelto
            try (PreparedStatement psPrestamo = con.prepareStatement(sqlUpdatePrestamo)) {
                psPrestamo.setInt(1, idPrestamo);
                psPrestamo.executeUpdate();
            }

            // 3. Devolver stock
            try (PreparedStatement psStock = con.prepareStatement(sqlUpdateStock)) {
                psStock.setInt(1, idLibro);
                psStock.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // MÉTODO: Listar los reportes
    public List<Object[]> listarPrestamos() {
        List<Object[]> lista = new ArrayList<>();
        // Usamos l.título como está en tu base de datos
        String sql = "SELECT p.id, e.nombre AS estudiante, l.título AS libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto " +
                "FROM prestamos p " +
                "JOIN estudiantes e ON p.id_estudiante = e.id " +
                "JOIN libros l ON p.id_libro = l.id";

        try {
            Connection con = DatabaseConnection.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[6];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("estudiante");
                fila[2] = rs.getString("libro");
                fila[3] = rs.getDate("fecha_prestamo");
                fila[4] = rs.getDate("fecha_devolucion");
                fila[5] = rs.getBoolean("devuelto") ? "Devuelto" : "Pendiente";
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al generar reporte: " + e.getMessage());
        }
        return lista;
    }
}