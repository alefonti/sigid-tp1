package ar.org.estrelladelsur.sigid.servicio;

/**
 * Se lanza cuando una operación viola una regla de negocio del club
 * (RN-01 a RN-05). La capa de presentación la captura para mostrar
 * un mensaje comprensible al usuario.
 */
public class ReglaNegocioException extends RuntimeException {

    private final String codigoRegla;

    public ReglaNegocioException(String codigoRegla, String mensaje) {
        super(mensaje);
        this.codigoRegla = codigoRegla;
    }

    public String getCodigoRegla() { return codigoRegla; }

    @Override public String getMessage() {
        return "[" + codigoRegla + "] " + super.getMessage();
    }
}
