package dao;

import modelo.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) responsable de la persistencia de la entidad
 * Jugador contra la base de datos MySQL. Forma parte de la capa de Datos
 * dentro de la arquitectura en 3 capas del prototipo.
 */
public class JugadorDAO {

    public void registrar(Jugador jugador) throws SQLException {
        String sql = "INSERT INTO jugador (nombre, apellido, fecha_nacimiento, dni, id_division, id_tutor) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, jugador.getNombre());
            stmt.setString(2, jugador.getApellido());
            stmt.setDate(3, Date.valueOf(jugador.getFechaNacimiento()));
            stmt.setString(4, jugador.getDni());
            stmt.setInt(5, jugador.getIdDivision());
            stmt.setInt(6, jugador.getIdTutor());
            stmt.executeUpdate();
        }
    }

    public List<Jugador> listarPorDivision(int idDivision) throws SQLException {
        List<Jugador> resultado = new ArrayList<>();
        String sql = "SELECT * FROM jugador WHERE id_division = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idDivision);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Jugador j = new Jugador();
                    j.setIdJugador(rs.getInt("id_jugador"));
                    j.setNombre(rs.getString("nombre"));
                    j.setApellido(rs.getString("apellido"));
                    j.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                    j.setDni(rs.getString("dni"));
                    j.setIdDivision(rs.getInt("id_division"));
                    j.setIdTutor(rs.getInt("id_tutor"));
                    resultado.add(j);
                }
            }
        }
        return resultado;
    }

    public boolean actualizarDivision(int idJugador, int nuevaDivision) throws SQLException {
        String sql = "UPDATE jugador SET id_division = ? WHERE id_jugador = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, nuevaDivision);
            stmt.setInt(2, idJugador);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idJugador) throws SQLException {
        String sql = "DELETE FROM jugador WHERE id_jugador = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idJugador);
            return stmt.executeUpdate() > 0;
        }
    }
}
