package ar.org.estrelladelsur.sigid.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único de acceso a la base de datos MySQL.
 * Implementa el patrón Singleton para que toda la capa de persistencia
 * comparta la misma configuración de conexión (RNF de mantenibilidad).
 */
public final class ConexionBD {

    private static final String URL =
        "jdbc:mysql://192.168.10.10:3306/sigid_db"
      + "?useSSL=true&requireSSL=true&serverTimezone=America/Argentina/Buenos_Aires";
    private static final String USUARIO = "sigid_app";
    private static final String CLAVE   = "*****";   // se lee de sigid.properties en producción

    private static ConexionBD instancia;

    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontró el driver JDBC de MySQL", e);
        }
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /** Devuelve una conexión nueva; el llamador debe cerrarla (try-with-resources). */
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
