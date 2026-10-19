package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Notificacion;

import ar.org.estrelladelsur.sigid.modelo.CanalNotificacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del contrato NotificacionDAO sobre MySQL 8.0. */
public class NotificacionDAOMySQL implements NotificacionDAO {

    @Override
    public int insertar(Notificacion n) {
        String sql = "INSERT INTO notificacion (id_tutor, id_evento, canal, mensaje, estado_envio) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, n.getIdTutor());
            ps.setInt(2, n.getIdEvento());
            ps.setString(3, n.getCanal().name());
            ps.setString(4, n.getMensaje());
            ps.setString(5, n.getEstadoEnvio());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    n.setIdNotificacion(rs.getInt(1));
                    return n.getIdNotificacion();
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar la notificación", e);
        }
    }

    @Override
    public List<Notificacion> listarPendientes() {
        String sql = "SELECT * FROM notificacion WHERE estado_envio = 'PENDIENTE' "
                   + "ORDER BY fecha_envio, id_notificacion";
        List<Notificacion> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Notificacion n = new Notificacion(rs.getInt("id_tutor"), rs.getInt("id_evento"),
                        CanalNotificacion.valueOf(rs.getString("canal")), rs.getString("mensaje"));
                n.setIdNotificacion(rs.getInt("id_notificacion"));
                n.setFechaEnvio(rs.getTimestamp("fecha_envio").toLocalDateTime());
                n.setEstadoEnvio(rs.getString("estado_envio"));
                lista.add(n);
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar las notificaciones pendientes", e);
        }
    }

    @Override
    public boolean actualizarEstado(int idNotificacion, String estado) {
        String sql = "UPDATE notificacion SET estado_envio = ? WHERE id_notificacion = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idNotificacion);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar la notificación", e);
        }
    }
}
