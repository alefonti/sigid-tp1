package ar.org.estrelladelsur.sigid.servicio.notificacion;

import ar.org.estrelladelsur.sigid.modelo.CanalNotificacion;
import ar.org.estrelladelsur.sigid.modelo.Notificacion;
import ar.org.estrelladelsur.sigid.modelo.Tutor;
import ar.org.estrelladelsur.sigid.servicio.ReglaNegocioException;

/**
 * ABSTRACCIÓN + POLIMORFISMO: canal de envío de un aviso a un tutor.
 *
 * El algoritmo de envío es siempre el mismo (obtener el destino, darle
 * formato al texto y despacharlo), pero cada canal resuelve esos pasos a
 * su manera. NotificacionService trabaja con la referencia abstracta y no
 * sabe si del otro lado hay un correo o un SMS.
 */
public abstract class Notificador {

    /** Canal que representa la implementación concreta. */
    public abstract CanalNotificacion getCanal();

    /** Dirección o número al que se envía; null si el tutor no lo tiene cargado. */
    protected abstract String destinoDe(Tutor tutor);

    /** Adapta el texto a las restricciones del canal. */
    protected abstract String formatear(String mensaje);

    /**
     * MÉTODO PLANTILLA: fija el orden de los pasos y delega los detalles
     * en las subclases. Es final para que ningún canal altere el flujo.
     *
     * @return comprobante del envío simulado
     * @throws ReglaNegocioException (RN-13) si el tutor no tiene el dato de contacto del canal
     */
    public final String enviar(Tutor tutor, Notificacion notificacion) {
        String destino = destinoDe(tutor);
        if (destino == null || destino.isBlank()) {
            throw new ReglaNegocioException("RN-13",
                "El tutor " + tutor.nombreCompleto() + " no tiene cargado un dato de contacto para "
              + getCanal() + ".");
        }
        String texto = formatear(notificacion.getMensaje());
        // En producción acá se invocaría al proveedor (SMTP / pasarela SMS).
        return String.format("%-5s -> %-24s %s", getCanal(), destino, texto);
    }

    // ---------------------------------------------------------------- fábricas

    /** Devuelve el notificador correspondiente a un canal ya decidido. */
    public static Notificador para(CanalNotificacion canal) {
        return switch (canal) {
            case EMAIL -> new NotificadorEmail();
            case SMS   -> new NotificadorSMS();
        };
    }

    /** Regla de selección del CU06: correo si el tutor lo tiene, SMS en caso contrario. */
    public static Notificador elegirPara(Tutor tutor) {
        return tutor.tieneEmail() ? new NotificadorEmail() : new NotificadorSMS();
    }
}
