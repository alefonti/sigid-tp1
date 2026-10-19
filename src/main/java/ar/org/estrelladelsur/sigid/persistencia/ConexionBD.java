package ar.org.estrelladelsur.sigid.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Punto único de acceso a la base de datos MySQL (patrón Singleton).
 *
 * TP3: la URL, el usuario y la clave ya no están escritos en el código.
 * Se leen de un archivo sigid.properties ubicado junto al programa o,
 * si no existe, del que viene empaquetado en src/main/resources.
 */
public final class ConexionBD {

    private static final String ARCHIVO = "sigid.properties";

    private static ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String clave;

    private ConexionBD() {
        Properties cfg = cargarConfiguracion();
        this.url     = cfg.getProperty("db.url",
                "jdbc:mysql://localhost:3306/sigid_db?serverTimezone=America/Argentina/Buenos_Aires");
        this.usuario = cfg.getProperty("db.usuario", "root");
        this.clave   = cfg.getProperty("db.clave", "");
        // Desde JDBC 4 el driver se registra solo si su .jar está en el classpath
        // (mysql-connector-j, declarado en el pom.xml). Si falta, getConexion()
        // lanza "No suitable driver" y Main pasa al modo en memoria.
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /** Devuelve una conexión nueva; el llamador debe cerrarla (try-with-resources). */
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    /**
     * Verifica que el servidor responda. Se usa al iniciar para decidir
     * si se trabaja contra MySQL o en modo demostración en memoria.
     *
     * @return null si la conexión fue exitosa, o el motivo de la falla
     */
    public String probarConexion() {
        DriverManager.setLoginTimeout(3);
        try (Connection cn = getConexion()) {
            return cn.isValid(2) ? null : "la conexión no respondió";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public String getUrl() { return url; }

    /**
     * Lee la configuración. Ejemplo de manejo de excepciones con
     * try-with-resources, catch de IOException y bloque finally.
     */
    private static Properties cargarConfiguracion() {
        Properties cfg = new Properties();
        Path externo = Path.of(ARCHIVO);
        InputStream entrada = null;
        try {
            entrada = Files.exists(externo)
                    ? Files.newInputStream(externo)
                    : ConexionBD.class.getClassLoader().getResourceAsStream(ARCHIVO);
            if (entrada != null) {
                cfg.load(entrada);
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + ARCHIVO + ", se usan valores por defecto: "
                             + e.getMessage());
        } finally {
            if (entrada != null) {
                try {
                    entrada.close();
                } catch (IOException ignorada) {
                    // el archivo ya fue leído; un error al cerrarlo no afecta al programa
                }
            }
        }
        return cfg;
    }
}
