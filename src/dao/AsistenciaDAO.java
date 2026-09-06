package dao;

import modelo.Asistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    public void registrar(Asistencia asistencia) throws SQLException {
        String sql = "INSERT INTO asistencia (id_jugador, fecha, presente) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, asistencia.getIdJugador());
            stmt.setDate(2, Date.valueOf(asistencia.getFecha()));
            stmt.setBoolean(3, asistencia.isPresente());
            stmt.executeUpdate();
        }
    }

    /** Calcula el porcentaje de asistencia de un jugador (para reportes). */
    public double calcularPorcentajeAsistencia(int idJugador) throws SQLException {
        String sql = "SELECT presente FROM asistencia WHERE id_jugador = ?";
        int total = 0;
        int presentes = 0;
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idJugador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    total++;
                    if (rs.getBoolean("presente")) {
                        presentes++;
                    }
                }
            }
        }
        return total == 0 ? 0.0 : (presentes * 100.0) / total;
    }

    public List<Asistencia> listarPorJugador(int idJugador) throws SQLException {
        List<Asistencia> resultado = new ArrayList<>();
        String sql = "SELECT * FROM asistencia WHERE id_jugador = ? ORDER BY fecha";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idJugador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Asistencia a = new Asistencia();
                    a.setIdAsistencia(rs.getInt("id_asistencia"));
                    a.setIdJugador(rs.getInt("id_jugador"));
                    a.setFecha(rs.getDate("fecha").toLocalDate());
                    a.setPresente(rs.getBoolean("presente"));
                    resultado.add(a);
                }
            }
        }
        return resultado;
    }
}
