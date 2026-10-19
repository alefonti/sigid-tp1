package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Notificacion;
import java.util.List;

/** Contrato de persistencia de notificaciones. */
public interface NotificacionDAO {
    int                insertar(Notificacion notificacion);
    /** Notificaciones en estado PENDIENTE, en orden de generación. */
    List<Notificacion> listarPendientes();
    boolean            actualizarEstado(int idNotificacion, String estado);
}
