package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Convocatoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del contrato ConvocatoriaDAO sobre MySQL 8.0. */
public class ConvocatoriaDAOMySQL implements ConvocatoriaDAO {

    @Override
    public int insertar(Convocatoria c) {
        String sql = "INSERT INTO convocatoria (id_evento, id_jugador, confirmada) VALUES (?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getIdEvento());
            ps.setInt(2, c.getIdJugador());
            ps.setBoolean(3, c.isConfirmada());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdConvocatoria(rs.getInt(1));
                    return c.getIdConvocatoria();
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar la convocatoria", e);
        }
    }

    @Override
    public List<Convocatoria> listarPorEvento(int idEvento) {
        String sql = "SELECT * FROM convocatoria WHERE id_evento = ?";
        List<Convocatoria> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Convocatoria c = new Convocatoria(rs.getInt("id_evento"), rs.getInt("id_jugador"));
                    c.setIdConvocatoria(rs.getInt("id_convocatoria"));
                    c.setConfirmada(rs.getBoolean("confirmada"));
                    lista.add(c);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar el evento " + idEvento, e);
        }
    }

    @Override
    public boolean eliminar(int idEvento, int idJugador) {
        String sql = "DELETE FROM convocatoria WHERE id_evento = ? AND id_jugador = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            ps.setInt(2, idJugador);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar la convocatoria", e);
        }
    }
}
