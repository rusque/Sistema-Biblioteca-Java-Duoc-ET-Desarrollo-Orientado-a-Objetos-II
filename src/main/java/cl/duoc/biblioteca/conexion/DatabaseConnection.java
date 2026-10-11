package cl.duoc.biblioteca.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Instancia estática única para el patrón Singleton
    private static DatabaseConnection instancia;
    private Connection conexion;

    // Credenciales adaptadas a tu entorno local
    // NOTA: Cambié "speedfast_db" por "biblioteca". Verifica el nombre exacto en tu script SQL.
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "N17tr00.77";

    // Constructor privado para evitar instanciación externa
    private DatabaseConnection() {
        try {
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión a la base de datos establecida exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    // Método estático que devuelve la única instancia de la clase
    public static DatabaseConnection getInstancia() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        } else {
            try {
                // Si la conexión se cerró por algún motivo, la volvemos a abrir
                if (instancia.getConexion().isClosed()) {
                    instancia = new DatabaseConnection();
                }
            } catch (SQLException e) {
                System.err.println("Error al verificar estado de la conexión: " + e.getMessage());
            }
        }
        return instancia;
    }

    // Método para obtener el objeto Connection para ejecutar consultas
    public Connection getConexion() {
        return conexion;
    }
}