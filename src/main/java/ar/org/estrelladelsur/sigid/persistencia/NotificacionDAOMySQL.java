package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Notificacion;

import java.sql.*;

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
}
