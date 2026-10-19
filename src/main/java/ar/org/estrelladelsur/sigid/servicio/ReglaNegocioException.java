package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.comun.SigidException;

/**
 * Se lanza cuando una operación viola una regla de negocio del club
 * (RN-00 a RN-13). La capa de presentación la captura para mostrar
 * un mensaje comprensible al usuario con el código de la regla.
 */
public class ReglaNegocioException extends SigidException {

    private final String codigoRegla;

    public ReglaNegocioException(String codigoRegla, String mensaje) {
        super(mensaje);
        this.codigoRegla = codigoRegla;
    }

    public String getCodigoRegla() { return codigoRegla; }

    /** Sobrescritura: antepone el código de la regla al mensaje original. */
    @Override public String getMessage() {
        return "[" + codigoRegla + "] " + super.getMessage();
    }
}
