package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Asistencia;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Implementación JDBC del contrato AsistenciaDAO sobre MySQL 8.0. */
public class AsistenciaDAOMySQL extends DAOMySQLBase implements AsistenciaDAO {

    @Override
    public int insertar(Asistencia a) {
        int id = insertar("INSERT INTO asistencia (id_evento, id_jugador, estado, motivo) VALUES (?, ?, ?, ?)",
                "Error al registrar la asistencia",
                a.getIdEvento(), a.getIdJugador(), a.getEstado(), a.getMotivo());
        a.setIdAsistencia(id);
        return id;
    }

    @Override
    public boolean existe(int idEvento, int idJugador) {
        return consultarEntero("SELECT COUNT(*) FROM asistencia WHERE id_evento = ? AND id_jugador = ?",
                "Error al verificar la asistencia", idEvento, idJugador) > 0;
    }

    @Override
    public List<Asistencia> listarPorJugador(int idJugador) {
        return consultarLista("SELECT * FROM asistencia WHERE id_jugador = ?", this::mapear,
                "Error al listar la asistencia del jugador", idJugador);
    }

    @Override
    public boolean actualizarMotivo(int idAsistencia, String motivo) {
        return actualizar("UPDATE asistencia SET estado = 'JUSTIFICADO', motivo = ? WHERE id_asistencia = ?",
                "Error al justificar la inasistencia", motivo, idAsistencia) == 1;
    }

    @Override
    public boolean eliminar(int idAsistencia) {
        return actualizar("DELETE FROM asistencia WHERE id_asistencia = ?",
                "Error al eliminar la asistencia " + idAsistencia, idAsistencia) == 1;
    }

    private Asistencia mapear(ResultSet rs) throws SQLException {
        Asistencia a = new Asistencia(rs.getInt("id_evento"), rs.getInt("id_jugador"),
                EstadoAsistencia.valueOf(rs.getString("estado")));
        a.setIdAsistencia(rs.getInt("id_asistencia"));
        a.setMotivo(rs.getString("motivo"));
        return a;
    }
}
