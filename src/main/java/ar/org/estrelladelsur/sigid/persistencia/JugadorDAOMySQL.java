package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.EstadoJugador;
import ar.org.estrelladelsur.sigid.modelo.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del contrato JugadorDAO sobre MySQL 8.0.
 * Todas las sentencias usan PreparedStatement para evitar inyección de SQL
 * (requerimiento no funcional RNF-02, de seguridad).
 */
public class JugadorDAOMySQL implements JugadorDAO {

    @Override
    public int insertar(Jugador j) {
        String sql = "INSERT INTO jugador (documento, apellido, nombre, fecha_nacimiento, "
                   + "fecha_alta, estado, id_division) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, j.getDocumento());
            ps.setString(2, j.getApellido());
            ps.setString(3, j.getNombre());
            ps.setDate(4, Date.valueOf(j.getFechaNacimiento()));
            ps.setDate(5, Date.valueOf(j.getFechaAlta()));
            ps.setString(6, j.getEstado().name());
            ps.setInt(7, j.getIdDivision());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    j.setIdJugador(rs.getInt(1));
                    return j.getIdJugador();
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar el jugador", e);
        }
    }

    @Override
    public Jugador buscarPorId(int idJugador) {
        String sql = "SELECT * FROM jugador WHERE id_jugador = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el jugador " + idJugador, e);
        }
    }

    @Override
    public Jugador buscarPorDocumento(String documento) {
        String sql = "SELECT * FROM jugador WHERE documento = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el documento " + documento, e);
        }
    }

    @Override
    public List<Jugador> listarPorDivision(int idDivision) {
        String sql = "SELECT * FROM jugador WHERE id_division = ? AND estado = 'ACTIVO' "
                   + "ORDER BY apellido, nombre";
        List<Jugador> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar la división " + idDivision, e);
        }
    }

    @Override
    public boolean actualizar(Jugador j) {
        String sql = "UPDATE jugador SET apellido = ?, nombre = ?, estado = ?, id_division = ? "
                   + "WHERE id_jugador = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, j.getApellido());
            ps.setString(2, j.getNombre());
            ps.setString(3, j.getEstado().name());
            ps.setInt(4, j.getIdDivision());
            ps.setInt(5, j.getIdJugador());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el jugador", e);
        }
    }

    @Override
    public boolean eliminar(int idJugador) {
        String sql = "DELETE FROM jugador WHERE id_jugador = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el jugador " + idJugador, e);
        }
    }

    @Override
    public int contarActivosPorDivision(int idDivision) {
        String sql = "SELECT COUNT(*) FROM jugador WHERE id_division = ? AND estado = 'ACTIVO'";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al contar la división " + idDivision, e);
        }
    }

    private Jugador mapear(ResultSet rs) throws SQLException {
        Jugador j = new Jugador();
        j.setIdJugador(rs.getInt("id_jugador"));
        j.setDocumento(rs.getString("documento"));
        j.setApellido(rs.getString("apellido"));
        j.setNombre(rs.getString("nombre"));
        j.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        j.setFechaAlta(rs.getDate("fecha_alta").toLocalDate());
        j.setEstado(EstadoJugador.valueOf(rs.getString("estado")));
        j.setIdDivision(rs.getInt("id_division"));
        return j;
    }
}
