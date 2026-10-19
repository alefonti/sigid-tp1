package ar.org.estrelladelsur.sigid.servicio.notificacion;

import ar.org.estrelladelsur.sigid.modelo.CanalNotificacion;
import ar.org.estrelladelsur.sigid.modelo.Tutor;

/** Envío por SMS: el texto se recorta a 160 caracteres, límite del estándar. */
public class NotificadorSMS extends Notificador {

    public static final int LIMITE_CARACTERES = 160;
    private static final String PREFIJO = "SIGID: ";

    @Override public CanalNotificacion getCanal() { return CanalNotificacion.SMS; }

    @Override protected String destinoDe(Tutor tutor) { return tutor.getTelefono(); }

    @Override protected String formatear(String mensaje) {
        String texto = PREFIJO + mensaje;
        return texto.length() <= LIMITE_CARACTERES
                ? texto
                : texto.substring(0, LIMITE_CARACTERES - 3) + "...";
    }
}
