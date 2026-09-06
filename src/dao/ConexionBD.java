package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de centralizar la conexion JDBC a la base de datos MySQL
 * "sigid_db". Se utiliza el patron Singleton simple para reutilizar la
 * conexion dentro del prototipo.
 */
public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sigid_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root"; // reemplazar por variable de entorno en producción

    private static Connection conexion;

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        }
        return conexion;
    }
}
