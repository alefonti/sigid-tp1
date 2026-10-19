package ar.org.estrelladelsur.sigid.comun;

/**
 * Raíz de la jerarquía de excepciones no verificadas de SIGID.
 *
 * Herencia aplicada a las excepciones: la capa de presentación puede
 * capturar cualquier error propio del sistema con un único catch de
 * SigidException y, si necesita distinguirlos, capturar primero la
 * subclase específica (ReglaNegocioException, PersistenciaException,
 * EstructuraVaciaException).
 */
public class SigidException extends RuntimeException {

    public SigidException(String mensaje) {
        super(mensaje);
    }

    public SigidException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
