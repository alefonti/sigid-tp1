package ar.org.estrelladelsur.sigid.servicio.notificacion;

import ar.org.estrelladelsur.sigid.modelo.CanalNotificacion;
import ar.org.estrelladelsur.sigid.modelo.Tutor;

/** Envío por correo electrónico: admite texto largo y agrega asunto y firma. */
public class NotificadorEmail extends Notificador {

    @Override public CanalNotificacion getCanal() { return CanalNotificacion.EMAIL; }

    @Override protected String destinoDe(Tutor tutor) { return tutor.getEmail(); }

    @Override protected String formatear(String mensaje) {
        return "[Asunto: Aviso de Estrella del Sur] " + mensaje + " -- Coordinación de Inferiores";
    }
}
