package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Asistencia;
import java.util.List;

/** Contrato de persistencia de asistencias. */
public interface AsistenciaDAO {
    int              insertar(Asistencia asistencia);
    boolean          existe(int idEvento, int idJugador);
    List<Asistencia> listarPorJugador(int idJugador);
    boolean          actualizarMotivo(int idAsistencia, String motivo);
}
