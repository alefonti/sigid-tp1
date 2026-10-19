package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.algoritmos.Ordenamiento;
import ar.org.estrelladelsur.sigid.modelo.Asistencia;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.persistencia.AsistenciaDAO;

import java.util.Comparator;
import java.util.List;

/**
 * Realiza el CU03 - Registrar asistencia y el CU11 - Justificar inasistencia,
 * que lo extiende («extend») cuando el jugador figura como ausente.
 *
 * Reglas de negocio:
 *   RN-09: no puede registrarse dos veces la asistencia del mismo jugador al mismo evento.
 *   RN-10: la justificación de una inasistencia no puede quedar vacía.
 */
public class AsistenciaService {

    /** Fila del ranking de presentismo (record: clase inmutable de datos, Java 16+). */
    public record Presentismo(Jugador jugador, double porcentaje, int eventos) { }

    /** Por debajo de este porcentaje el entrenador recibe una alerta (RF-12). */
    public static final double UMBRAL_ALERTA = 75.0;

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

    /** Elimina un registro; lo invoca la operación "deshacer". */
    public boolean eliminar(int idAsistencia) {
        return asistenciaDAO.eliminar(idAsistencia);
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
        int presentes = 0;
        for (Asistencia a : registros) {             // estructura repetitiva clásica
            if (a.getEstado() == EstadoAsistencia.PRESENTE
                    || a.getEstado() == EstadoAsistencia.TARDE) {
                presentes++;
            }
        }
        return Math.round(presentes * 1000d / registros.size()) / 10d;
    }

    /**
     * TP3 - Ranking de presentismo ordenado con QUICK SORT de mayor a menor;
     * a igual porcentaje, primero el que tiene más eventos registrados.
     */
    public Presentismo[] ranking(List<Jugador> plantel) {
        Presentismo[] filas = new Presentismo[plantel.size()];
        for (int i = 0; i < plantel.size(); i++) {
            Jugador j = plantel.get(i);
            filas[i] = new Presentismo(j, porcentajeAsistencia(j.getIdJugador()),
                                       asistenciaDAO.listarPorJugador(j.getIdJugador()).size());
        }
        Ordenamiento.quickSort(filas,
                Comparator.comparingDouble(Presentismo::porcentaje).reversed()
                          .thenComparing(Comparator.comparingInt(Presentismo::eventos).reversed()));
        return filas;
    }
}
