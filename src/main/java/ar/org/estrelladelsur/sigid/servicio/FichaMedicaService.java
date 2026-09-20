package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.FichaMedica;
import ar.org.estrelladelsur.sigid.persistencia.FichaMedicaDAO;
import ar.org.estrelladelsur.sigid.persistencia.LesionDAO;

import java.time.LocalDate;

/**
 * Realiza el CU02 - Gestionar ficha médica y el CU10 - Validar aptitud médica,
 * que el CU05 incluye antes de confirmar una convocatoria.
 *
 * Regla de negocio RN-03: un jugador está habilitado únicamente si su última
 * ficha médica tiene apto físico favorable, no está vencida y el jugador no
 * registra lesiones activas.
 */
public class FichaMedicaService {

    private final FichaMedicaDAO fichaDAO;
    private final LesionDAO      lesionDAO;

    public FichaMedicaService(FichaMedicaDAO fichaDAO, LesionDAO lesionDAO) {
        this.fichaDAO  = fichaDAO;
        this.lesionDAO = lesionDAO;
    }

    /**
     * Evalúa la aptitud médica del jugador a una fecha dada.
     *
     * @return null si el jugador está habilitado, o el motivo del bloqueo.
     */
    public String motivoDeBloqueo(int idJugador, LocalDate fecha) {
        FichaMedica ficha = fichaDAO.buscarUltimaPorJugador(idJugador);

        if (ficha == null) {
            return "no tiene ficha médica cargada";
        }
        if (!ficha.isAptoFisico()) {
            return "su apto físico es desfavorable";
        }
        if (ficha.getFechaVencimiento().isBefore(fecha)) {
            return "su ficha médica venció el " + ficha.getFechaVencimiento();
        }
        if (!lesionDAO.listarActivasPorJugador(idJugador).isEmpty()) {
            return "registra una lesión activa";
        }
        return null;
    }

    /** Atajo booleano de {@link #motivoDeBloqueo(int, LocalDate)}. */
    public boolean estaHabilitado(int idJugador, LocalDate fecha) {
        return motivoDeBloqueo(idJugador, fecha) == null;
    }

    /**
     * Registra una ficha médica nueva.
     *
     * @throws ReglaNegocioException si el período de vigencia es inválido (RN-08)
     */
    public int registrar(FichaMedica ficha) {
        if (ficha.getFechaVencimiento() == null || ficha.getFechaEmision() == null
                || !ficha.getFechaVencimiento().isAfter(ficha.getFechaEmision())) {
            throw new ReglaNegocioException("RN-08",
                "La fecha de vencimiento debe ser posterior a la de emisión.");
        }
        return fichaDAO.insertar(ficha);
    }
}
