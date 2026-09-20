package ar.org.estrelladelsur.sigid.persistencia;

/** Excepción no verificada que encapsula los errores de acceso a datos. */
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
