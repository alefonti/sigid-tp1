package ar.org.estrelladelsur.sigid.archivos;

import ar.org.estrelladelsur.sigid.comun.SigidException;

/**
 * Excepción no verificada que encapsula los errores de entrada/salida
 * (IOException) al leer o escribir archivos. Cumple el mismo papel que
 * PersistenciaException con las SQLException: la capa de servicio y la
 * interfaz no dependen de java.io.
 */
public class ArchivoException extends SigidException {

    public ArchivoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
