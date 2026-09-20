package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Gravedad;
import ar.org.estrelladelsur.sigid.modelo.Lesion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del contrato LesionDAO sobre MySQL 8.0. */
public class LesionDAOMySQL implements LesionDAO {

    @Override
    public int insertar(Lesion l) {
        String sql = "INSERT INTO lesion (id_jugador, id_profesional, tipo, gravedad, "
                   + "fecha_inicio, fecha_alta_real) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getIdJugador());
            ps.setInt(2, l.getIdProfesional());
            ps.setString(3, l.getTipo());
            ps.setString(4, l.getGravedad().name());
            ps.setDate(5, Date.valueOf(l.getFechaInicio()));
            ps.setDate(6, l.getFechaAltaReal() == null ? null : Date.valueOf(l.getFechaAltaReal()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    l.setIdLesion(rs.getInt(1));
                    return l.getIdLesion();
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar la lesión", e);
        }
    }

    @Override
    public List<Lesion> listarActivasPorJugador(int idJugador) {
        String sql = "SELECT * FROM lesion WHERE id_jugador = ? AND fecha_alta_real IS NULL";
        List<Lesion> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lesion l = new Lesion(rs.getInt("id_jugador"), rs.getString("tipo"),
                            Gravedad.valueOf(rs.getString("gravedad")),
                            rs.getDate("fecha_inicio").toLocalDate());
                    l.setIdLesion(rs.getInt("id_lesion"));
                    lista.add(l);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar las lesiones activas", e);
        }
    }

    @Override
    public boolean registrarAlta(int idLesion, LocalDate fechaAlta) {
        String sql = "UPDATE lesion SET fecha_alta_real = ? WHERE id_lesion = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaAlta));
            ps.setInt(2, idLesion);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el alta médica", e);
        }
    }
}
