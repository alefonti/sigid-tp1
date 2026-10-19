package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.Partido;
import ar.org.estrelladelsur.sigid.modelo.TipoEvento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del contrato EventoDAO.
 *
 * La tabla evento guarda entrenamientos y partidos juntos (columna tipo).
 * Al leer, el método de fábrica Evento.crear() devuelve la subclase que
 * corresponde y, si es un Partido, se completan rival y condición: la
 * herencia del modelo se traduce a una sola tabla ("single table").
 */
public class EventoDAOMySQL implements EventoDAO {

    @Override
    public int insertar(Evento e) {
        String sql = "INSERT INTO evento (tipo, fecha, hora_inicio, hora_fin, rival, condicion, "
                   + "estado, id_division, id_sede) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getTipo().name());
            ps.setDate(2, Date.valueOf(e.getFecha()));
            ps.setTime(3, Time.valueOf(e.getHoraInicio()));
            ps.setTime(4, Time.valueOf(e.getHoraFin()));
            if (e instanceof Partido p) {              // pattern matching de instanceof
                ps.setString(5, p.getRival());
                ps.setString(6, p.getCondicion());
            } else {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
            }
            ps.setString(7, e.getEstado());
            ps.setInt(8, e.getIdDivision());
            ps.setInt(9, e.getIdSede());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setIdEvento(rs.getInt(1));
                }
            }
            return e.getIdEvento();
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new PersistenciaException("La sede ya tiene otro evento en ese día y horario", ex);
        } catch (SQLException ex) {
            throw new PersistenciaException("Error al registrar el evento", ex);
        }
    }

    @Override
    public Evento buscarPorId(int idEvento) {
        String sql = "SELECT * FROM evento WHERE id_evento = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el evento " + idEvento, e);
        }
    }

    @Override
    public List<Evento> listarPorDivision(int idDivision) {
        String sql = "SELECT * FROM evento WHERE id_division = ?";
        List<Evento> lista = new ArrayList<>();
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
            throw new PersistenciaException("Error al listar la agenda de la división", e);
        }
    }

    private Evento mapear(ResultSet rs) throws SQLException {
        Evento e = Evento.crear(TipoEvento.valueOf(rs.getString("tipo")));
        e.setIdEvento(rs.getInt("id_evento"));
        e.setFecha(rs.getDate("fecha").toLocalDate());
        e.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
        e.setHoraFin(rs.getTime("hora_fin").toLocalTime());
        e.setEstado(rs.getString("estado"));
        e.setIdDivision(rs.getInt("id_division"));
        e.setIdSede(rs.getInt("id_sede"));
        if (e instanceof Partido p) {
            p.setRival(rs.getString("rival"));
            p.setCondicion(rs.getString("condicion"));
        }
        return e;
    }
}
