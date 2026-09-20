package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.FichaMedica;

import java.sql.*;

/** Implementación JDBC del contrato FichaMedicaDAO sobre MySQL 8.0. */
public class FichaMedicaDAOMySQL implements FichaMedicaDAO {

    @Override
    public int insertar(FichaMedica f) {
        String sql = "INSERT INTO ficha_medica (id_jugador, id_profesional, fecha_emision, "
                   + "fecha_vencimiento, apto_fisico, observaciones) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getIdJugador());
            ps.setInt(2, f.getIdProfesional());
            ps.setDate(3, Date.valueOf(f.getFechaEmision()));
            ps.setDate(4, Date.valueOf(f.getFechaVencimiento()));
            ps.setBoolean(5, f.isAptoFisico());
            ps.setString(6, f.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    f.setIdFicha(rs.getInt(1));
                    return f.getIdFicha();
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar la ficha médica", e);
        }
    }

    @Override
    public FichaMedica buscarUltimaPorJugador(int idJugador) {
        String sql = "SELECT * FROM ficha_medica WHERE id_jugador = ? "
                   + "ORDER BY fecha_vencimiento DESC LIMIT 1";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                FichaMedica f = new FichaMedica();
                f.setIdFicha(rs.getInt("id_ficha"));
                f.setIdJugador(rs.getInt("id_jugador"));
                f.setIdProfesional(rs.getInt("id_profesional"));
                f.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
                f.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
                f.setAptoFisico(rs.getBoolean("apto_fisico"));
                f.setObservaciones(rs.getString("observaciones"));
                return f;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar la ficha del jugador " + idJugador, e);
        }
    }
}
