package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.FilaPresentismo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Implementación JDBC del contrato PlanillaDAO (TP4). */
public class PlanillaDAOMySQL extends DAOMySQLBase implements PlanillaDAO {

    /**
     * Un único SELECT con LEFT JOIN y GROUP BY devuelve una fila por jugador
     * con cuatro columnas de totales (una por estado). El LEFT JOIN hace que
     * también aparezcan los jugadores sin ningún registro de asistencia.
     */
    private static final String SQL_PRESENTISMO = """
            SELECT j.id_jugador, j.apellido, j.nombre,
                   COALESCE(SUM(a.estado = 'PRESENTE'), 0)    AS presente,
                   COALESCE(SUM(a.estado = 'AUSENTE'), 0)     AS ausente,
                   COALESCE(SUM(a.estado = 'TARDE'), 0)       AS tarde,
                   COALESCE(SUM(a.estado = 'JUSTIFICADO'), 0) AS justificado
              FROM jugador j
              LEFT JOIN asistencia a ON a.id_jugador = j.id_jugador
             WHERE j.id_division = ? AND j.estado = 'ACTIVO'
             GROUP BY j.id_jugador, j.apellido, j.nombre
             ORDER BY j.apellido, j.nombre
            """;

    @Override
    public List<FilaPresentismo> presentismoPorDivision(int idDivision) {
        return consultarLista(SQL_PRESENTISMO, rs -> {
            FilaPresentismo fila = new FilaPresentismo(rs.getInt("id_jugador"),
                    rs.getString("apellido"), rs.getString("nombre"));
            // recorre el arreglo de constantes del enum: el nombre de cada
            // columna coincide con el del estado en minúsculas
            for (EstadoAsistencia estado : EstadoAsistencia.values()) {
                fila.sumar(estado, rs.getInt(estado.name().toLowerCase()));
            }
            return fila;
        }, "Error al calcular el presentismo de la división " + idDivision, idDivision);
    }

    @Override
    public List<Evento> pendientesDeCierre(int idDivision, LocalDate hoy) {
        return consultarLista("SELECT * FROM evento WHERE id_division = ? AND fecha < ? "
                            + "AND estado IN ('PROGRAMADO', 'CONFIRMADO') ORDER BY fecha, hora_inicio",
                EventoDAOMySQL::mapear, "Error al buscar eventos pendientes de cierre",
                idDivision, hoy);
    }

    /**
     * Entrenamiento: se espera a todo el plantel activo de la división.
     * Partido: se espera sólo a los convocados.
     */
    private static final String SQL_AUSENTES_ENTRENAMIENTO = """
            INSERT INTO asistencia (id_evento, id_jugador, estado, motivo)
            SELECT e.id_evento, j.id_jugador, 'AUSENTE', 'Registrado al cerrar el evento'
              FROM evento e
              JOIN jugador j ON j.id_division = e.id_division AND j.estado = 'ACTIVO'
             WHERE e.id_evento = ?
               AND NOT EXISTS (SELECT 1 FROM asistencia a
                                WHERE a.id_evento = e.id_evento AND a.id_jugador = j.id_jugador)
            """;

    private static final String SQL_AUSENTES_PARTIDO = """
            INSERT INTO asistencia (id_evento, id_jugador, estado, motivo)
            SELECT c.id_evento, c.id_jugador, 'AUSENTE', 'Registrado al cerrar el evento'
              FROM convocatoria c
             WHERE c.id_evento = ?
               AND NOT EXISTS (SELECT 1 FROM asistencia a
                                WHERE a.id_evento = c.id_evento AND a.id_jugador = c.id_jugador)
            """;

    @Override
    public int cerrarEvento(int idEvento) {
        return enTransaccion(cn -> {
            String tipo;
            // 1) bloquea la fila del evento hasta el commit (FOR UPDATE) para que
            //    nadie lo cierre en paralelo
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT tipo, estado FROM evento WHERE id_evento = ? FOR UPDATE")) {
                ps.setInt(1, idEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new PersistenciaException("No existe el evento " + idEvento, null);
                    }
                    if ("FINALIZADO".equals(rs.getString("estado"))) {
                        throw new PersistenciaException("El evento " + idEvento + " ya estaba cerrado", null);
                    }
                    tipo = rs.getString("tipo");
                }
            }
            // 2) registra los ausentes
            int ausentes;
            String sql = "PARTIDO".equals(tipo) ? SQL_AUSENTES_PARTIDO : SQL_AUSENTES_ENTRENAMIENTO;
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idEvento);
                ausentes = ps.executeUpdate();
            }
            // 3) finaliza el evento
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE evento SET estado = 'FINALIZADO' WHERE id_evento = ?")) {
                ps.setInt(1, idEvento);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se pudo actualizar el estado del evento " + idEvento);
                }
            }
            return ausentes;
        }, "Error al cerrar el evento " + idEvento);
    }
}
