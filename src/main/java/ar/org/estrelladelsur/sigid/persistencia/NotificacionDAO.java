package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Notificacion;

/** Contrato de persistencia de notificaciones. */
public interface NotificacionDAO {
    int insertar(Notificacion notificacion);
}
