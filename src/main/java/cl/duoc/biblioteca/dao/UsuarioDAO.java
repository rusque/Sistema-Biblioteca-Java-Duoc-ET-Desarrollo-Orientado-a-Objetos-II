package cl.duoc.biblioteca.dao;

import cl.duoc.biblioteca.conexion.DatabaseConnection;
import cl.duoc.biblioteca.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Agregamos la implementación de ICrud
public class UsuarioDAO implements ICrud<Usuario> {

    /**
     * Método para autenticar un usuario en el sistema.
     * Retorna un objeto Usuario si las credenciales son correctas, o null si fallan.
     */
    public Usuario autenticarUsuario(String correo, String contrasena) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contraseña = ?";
        Connection con = DatabaseConnection.getInstancia().getConexion();

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, contrasena);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setRut(rs.getString("rut"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setContrasena(rs.getString("contraseña"));
                usuario.setRol(rs.getString("rol"));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario en BD: " + e.getMessage());
        }

        return usuario;
    }

    // --- MÉTODOS OBLIGATORIOS DE LA INTERFAZ ICrud ---
    @Override
    public boolean crear(Usuario objeto) {
        return false;
    }

    @Override
    public List<Usuario> leerTodos() {
        return new ArrayList<>();
    }

    @Override
    public boolean actualizar(Usuario objeto) {
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return false;
    }
}