package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.comun.SigidException;

/**
 * Excepción no verificada que encapsula los errores de acceso a datos
 * (SQLException). Así la capa de servicio no depende de java.sql.
 */
public class PersistenciaException extends SigidException {
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
