package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.CanalNotificacion;
import ar.org.estrelladelsur.sigid.modelo.Notificacion;
import ar.org.estrelladelsur.sigid.modelo.Tutor;
import ar.org.estrelladelsur.sigid.persistencia.NotificacionDAO;
import ar.org.estrelladelsur.sigid.persistencia.TutorDAO;

import java.util.List;

/**
 * Realiza el CU06 - Notificar a tutores, incluido por el CU04 y el CU05.
 * El canal se elige según los datos de contacto disponibles: correo
 * electrónico si el tutor lo tiene cargado y SMS en caso contrario.
 */
public class NotificacionService {

    private final TutorDAO        tutorDAO;
    private final NotificacionDAO notificacionDAO;

    public NotificacionService(TutorDAO tutorDAO, NotificacionDAO notificacionDAO) {
        this.tutorDAO        = tutorDAO;
        this.notificacionDAO = notificacionDAO;
    }

    /**
     * Genera una notificación por cada tutor vinculado al evento.
     *
     * @return cantidad de notificaciones registradas
     */
    public int notificarTutores(int idEvento) {
        List<Tutor> tutores = tutorDAO.listarPorEvento(idEvento);
        int enviadas = 0;
        for (Tutor t : tutores) {
            CanalNotificacion canal = (t.getEmail() != null && !t.getEmail().isBlank())
                                    ? CanalNotificacion.EMAIL
                                    : CanalNotificacion.SMS;
            String mensaje = "Su hijo/a fue convocado/a. Consulte el detalle en SIGID.";
            notificacionDAO.insertar(new Notificacion(t.getIdTutor(), idEvento, canal, mensaje));
            enviadas++;
        }
        return enviadas;
    }
}
