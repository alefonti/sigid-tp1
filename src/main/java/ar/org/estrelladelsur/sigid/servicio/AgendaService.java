package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.algoritmos.Ordenamiento;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.persistencia.EventoDAO;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Realiza el CU04 - Programar evento (nuevo en el prototipo del TP3).
 *
 * Reglas de negocio:
 *   RN-12: no se programan eventos en fechas pasadas ni fuera del horario
 *          de uso del predio (08:00 a 22:00).
 */
public class AgendaService {

    public static final LocalTime APERTURA_PREDIO = LocalTime.of(8, 0);
    public static final LocalTime CIERRE_PREDIO   = LocalTime.of(22, 0);

    private final EventoDAO eventoDAO;

    public AgendaService(EventoDAO eventoDAO) {
        this.eventoDAO = eventoDAO;
    }

    public int programar(Evento evento, LocalDate hoy) {
        if (evento.getFecha().isBefore(hoy)) {
            throw new ReglaNegocioException("RN-12",
                "No se puede programar un evento en una fecha pasada (" + evento.getFecha() + ").");
        }
        if (evento.getHoraInicio().isBefore(APERTURA_PREDIO) || evento.getHoraFin().isAfter(CIERRE_PREDIO)) {
            throw new ReglaNegocioException("RN-12",
                "El predio se usa entre las " + APERTURA_PREDIO + " y las " + CIERRE_PREDIO + ".");
        }
        return eventoDAO.insertar(evento);
    }

    public Evento obtener(int idEvento) {
        Evento e = eventoDAO.buscarPorId(idEvento);
        if (e == null) {
            throw new ReglaNegocioException("RN-07", "No existe un evento con el identificador " + idEvento + ".");
        }
        return e;
    }

    /**
     * Agenda cronológica de una división. Se ordena por INSERCIÓN porque la
     * agenda de una división es corta y llega casi ordenada desde la base,
     * caso en el que inserción se acerca a O(n).
     */
    public Evento[] agendaDe(int idDivision) {
        Evento[] agenda = eventoDAO.listarPorDivision(idDivision).toArray(new Evento[0]);
        Ordenamiento.insercion(agenda, Evento::compareTo);
        return agenda;
    }

    /** Próximo evento que admite convocatoria (es decir, un partido) desde hoy. */
    public Evento proximoPartido(int idDivision, LocalDate hoy) {
        for (Evento e : agendaDe(idDivision)) {
            if (e.esFuturo(hoy) && e.admiteConvocatoria()) {
                return e;
            }
        }
        return null;
    }
}
