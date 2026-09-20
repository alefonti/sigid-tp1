package ar.org.estrelladelsur.sigid;

import ar.org.estrelladelsur.sigid.ui.ConsolaUI;

/**
 * Punto de entrada del prototipo operacional de SIGID.
 *
 * Requiere una base MySQL 8.0 creada con los scripts de la carpeta sql/
 * y el conector mysql-connector-j en el classpath.
 */
public class Main {
    public static void main(String[] args) {
        new ConsolaUI().ejecutar();
    }
}
