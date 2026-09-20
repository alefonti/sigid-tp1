package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Tutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del contrato TutorDAO sobre MySQL 8.0. */
public class TutorDAOMySQL implements TutorDAO {

    /** Tutores de contacto primario de los jugadores convocados a un evento. */
    @Override
    public List<Tutor> listarPorEvento(int idEvento) {
        String sql = "SELECT DISTINCT t.* "
                   + "FROM tutor t "
                   + "JOIN jugador_tutor jt ON jt.id_tutor = t.id_tutor AND jt.contacto_primario = TRUE "
                   + "JOIN convocatoria c  ON c.id_jugador = jt.id_jugador "
                   + "WHERE c.id_evento = ?";
        List<Tutor> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tutor t = new Tutor(rs.getInt("id_tutor"), rs.getString("apellido"),
                            rs.getString("nombre"), rs.getString("telefono"), rs.getString("email"));
                    t.setDocumento(rs.getString("documento"));
                    lista.add(t);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los tutores del evento", e);
        }
    }
}
