package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.FilaPresentismo;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato de persistencia de la planilla de asistencia (nuevo en el TP4).
 * Agrupa las operaciones que trabajan sobre varias tablas a la vez:
 * el reporte de presentismo y el cierre de un evento.
 */
public interface PlanillaDAO {

    /** Presentismo de los jugadores activos de una división, con un total por estado. */
    List<FilaPresentismo> presentismoPorDivision(int idDivision);

    /** Eventos de la división ya jugados (fecha anterior a hoy) que siguen sin finalizar. */
    List<Evento> pendientesDeCierre(int idDivision, LocalDate hoy);

    /**
     * Cierra un evento en una única transacción: registra como AUSENTE a
     * cada jugador activo de la división que no tenga asistencia cargada y
     * cambia el estado del evento a FINALIZADO.
     *
     * @return cantidad de ausencias registradas automáticamente
     */
    int cerrarEvento(int idEvento);
}
