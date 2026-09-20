package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Asistencia;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.persistencia.AsistenciaDAO;

import java.util.List;

/**
 * Realiza el CU03 - Registrar asistencia y el CU11 - Justificar inasistencia,
 * que lo extiende («extend») cuando el jugador figura como ausente.
 *
 * Regla de negocio RN-09: no puede registrarse dos veces la asistencia
 * del mismo jugador al mismo evento.
 */
public class AsistenciaService {

    private final AsistenciaDAO asistenciaDAO;

    public AsistenciaService(AsistenciaDAO asistenciaDAO) {
        this.asistenciaDAO = asistenciaDAO;
    }

    public int registrar(int idEvento, int idJugador, EstadoAsistencia estado) {
        if (estado == null) {
            throw new ReglaNegocioException("RN-09", "Debe indicarse el estado de asistencia.");
        }
        if (asistenciaDAO.existe(idEvento, idJugador)) {
            throw new ReglaNegocioException("RN-09",
                "La asistencia del jugador " + idJugador + " ya fue registrada para este evento.");
        }
        return asistenciaDAO.insertar(new Asistencia(idEvento, idJugador, estado));
    }

    /** CU11: extiende al CU03 agregando el motivo de una inasistencia. */
    public boolean justificarInasistencia(int idAsistencia, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaNegocioException("RN-10",
                "La justificación no puede quedar vacía.");
        }
        return asistenciaDAO.actualizarMotivo(idAsistencia, motivo);
    }

    /**
     * Porcentaje de presentismo del jugador sobre los eventos registrados.
     * Los estados PRESENTE y TARDE computan como asistencia efectiva.
     *
     * @return valor entre 0 y 100; 0 si el jugador no tiene registros
     */
    public double porcentajeAsistencia(int idJugador) {
        List<Asistencia> registros = asistenciaDAO.listarPorJugador(idJugador);
        if (registros.isEmpty()) {
            return 0d;
        }
        long presentes = registros.stream()
                .filter(a -> a.getEstado() == EstadoAsistencia.PRESENTE
                          || a.getEstado() == EstadoAsistencia.TARDE)
                .count();
        return Math.round(presentes * 1000d / registros.size()) / 10d;
    }
}
