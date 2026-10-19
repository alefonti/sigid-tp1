package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.comun.SigidException;
import ar.org.estrelladelsur.sigid.estructuras.Cola;
import ar.org.estrelladelsur.sigid.modelo.Notificacion;
import ar.org.estrelladelsur.sigid.modelo.Tutor;
import ar.org.estrelladelsur.sigid.persistencia.NotificacionDAO;
import ar.org.estrelladelsur.sigid.persistencia.TutorDAO;
import ar.org.estrelladelsur.sigid.servicio.notificacion.Notificador;

import java.util.ArrayList;
import java.util.List;

/**
 * Realiza el CU06 - Notificar a tutores, incluido por el CU05.
 *
 * TP3: las notificaciones ya no se "envían" en el mismo momento en que se
 * generan. Se registran como PENDIENTE y se agregan a una COLA (FIFO); el
 * coordinador decide cuándo procesarla y los avisos salen en el mismo
 * orden en que se generaron. Si un envío falla, queda marcado con ERROR
 * y el resto de la cola sigue procesándose.
 */
public class NotificacionService {

    public static final String MENSAJE_CONVOCATORIA =
            "Su hijo/a fue convocado/a. Consulte el detalle en SIGID.";

    private final TutorDAO          tutorDAO;
    private final NotificacionDAO   notificacionDAO;
    private final Cola<Notificacion> pendientes = new Cola<>();

    public NotificacionService(TutorDAO tutorDAO, NotificacionDAO notificacionDAO) {
        this.tutorDAO        = tutorDAO;
        this.notificacionDAO = notificacionDAO;
    }

    /** Recupera de la base los avisos que quedaron pendientes en sesiones anteriores. */
    public int sincronizarPendientes() {
        int agregadas = 0;
        for (Notificacion n : notificacionDAO.listarPendientes()) {
            pendientes.encolar(n);
            agregadas++;
        }
        return agregadas;
    }

    /** SOBRECARGA: versión con el mensaje genérico (la usa el CU05 del TP2). */
    public int notificarTutores(int idEvento) {
        return notificarTutores(idEvento, MENSAJE_CONVOCATORIA);
    }

    /**
     * SOBRECARGA: genera un aviso por cada tutor primario vinculado al evento,
     * eligiendo el canal según los datos de contacto, y lo encola.
     *
     * @return cantidad de notificaciones generadas
     */
    public int notificarTutores(int idEvento, String mensaje) {
        List<Tutor> tutores = tutorDAO.listarPorEvento(idEvento);
        int generadas = 0;
        for (Tutor t : tutores) {
            Notificador canal = Notificador.elegirPara(t);
            Notificacion n = new Notificacion(t.getIdTutor(), idEvento, canal.getCanal(), mensaje);
            notificacionDAO.insertar(n);
            pendientes.encolar(n);
            generadas++;
        }
        return generadas;
    }

    /**
     * Procesa la cola completa respetando el orden de llegada.
     *
     * @return una línea de comprobante por cada aviso procesado
     */
    public List<String> procesarCola() {
        List<String> comprobantes = new ArrayList<>();
        while (!pendientes.estaVacia()) {
            Notificacion n = pendientes.desencolar();
            try {
                Tutor tutor = tutorDAO.buscarPorId(n.getIdTutor());
                if (tutor == null) {
                    throw new ReglaNegocioException("RN-13", "El tutor " + n.getIdTutor() + " no existe.");
                }
                String comprobante = Notificador.para(n.getCanal()).enviar(tutor, n);
                notificacionDAO.actualizarEstado(n.getIdNotificacion(), "ENVIADA");
                n.setEstadoEnvio("ENVIADA");
                comprobantes.add("OK    " + comprobante);
            } catch (SigidException e) {
                // un envío fallido no detiene el procesamiento del resto de la cola
                notificacionDAO.actualizarEstado(n.getIdNotificacion(), "ERROR");
                n.setEstadoEnvio("ERROR");
                comprobantes.add("ERROR aviso " + n.getIdNotificacion() + ": " + e.getMessage());
            }
        }
        return comprobantes;
    }

    /** Vista de la cola sin modificarla (se recorre con su iterador). */
    public List<Notificacion> verPendientes() {
        List<Notificacion> copia = new ArrayList<>();
        for (Notificacion n : pendientes) {
            copia.add(n);
        }
        return copia;
    }

    public int cantidadPendientes() { return pendientes.tamanio(); }
}
