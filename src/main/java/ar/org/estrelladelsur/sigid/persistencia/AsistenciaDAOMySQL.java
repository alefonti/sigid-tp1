package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Asistencia;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del contrato AsistenciaDAO sobre MySQL 8.0. */
public class AsistenciaDAOMySQL implements AsistenciaDAO {

    @Override
    public int insertar(Asistencia a) {
        String sql = "INSERT INTO asistencia (id_evento, id_jugador, estado, motivo) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getIdEvento());
            ps.setInt(2, a.getIdJugador());
            ps.setString(3, a.getEstado().name());
            ps.setString(4, a.getMotivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setIdAsistencia(rs.getInt(1));
                    return a.getIdAsistencia();
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar la asistencia", e);
        }
    }

    @Override
    public boolean existe(int idEvento, int idJugador) {
        String sql = "SELECT 1 FROM asistencia WHERE id_evento = ? AND id_jugador = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            ps.setInt(2, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar la asistencia", e);
        }
    }

    @Override
    public List<Asistencia> listarPorJugador(int idJugador) {
        String sql = "SELECT * FROM asistencia WHERE id_jugador = ?";
        List<Asistencia> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asistencia a = new Asistencia(rs.getInt("id_evento"), rs.getInt("id_jugador"),
                            EstadoAsistencia.valueOf(rs.getString("estado")));
                    a.setIdAsistencia(rs.getInt("id_asistencia"));
                    a.setMotivo(rs.getString("motivo"));
                    lista.add(a);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar la asistencia del jugador", e);
        }
    }

    @Override
    public boolean actualizarMotivo(int idAsistencia, String motivo) {
        String sql = "UPDATE asistencia SET estado = 'JUSTIFICADO', motivo = ? WHERE id_asistencia = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idAsistencia);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al justificar la inasistencia", e);
        }
    }

    @Override
    public boolean eliminar(int idAsistencia) {
        String sql = "DELETE FROM asistencia WHERE id_asistencia = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAsistencia);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar la asistencia " + idAsistencia, e);
        }
    }
}
