package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.EstadoJugador;
import ar.org.estrelladelsur.sigid.modelo.Jugador;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación JDBC del contrato JugadorDAO sobre MySQL 8.0.
 *
 * Hereda de DAOMySQLBase la apertura y cierre de la conexión, la carga
 * de parámetros y la traducción de SQLException; acá sólo queda el SQL
 * propio de la tabla jugador y el mapeo fila → objeto. Todas las
 * sentencias siguen usando PreparedStatement (RNF-02, seguridad).
 */
public class JugadorDAOMySQL extends DAOMySQLBase implements JugadorDAO {

    @Override
    public int insertar(Jugador j) {
        int id = insertar("INSERT INTO jugador (documento, apellido, nombre, fecha_nacimiento, "
                        + "fecha_alta, estado, id_division) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "Error al insertar el jugador " + j.getDocumento(),
                j.getDocumento(), j.getApellido(), j.getNombre(), j.getFechaNacimiento(),
                j.getFechaAlta(), j.getEstado(), j.getIdDivision());
        j.setIdJugador(id);
        return id;
    }

    @Override
    public Jugador buscarPorId(int idJugador) {
        return consultarUno("SELECT * FROM jugador WHERE id_jugador = ?", this::mapear,
                "Error al buscar el jugador " + idJugador, idJugador);
    }

    @Override
    public Jugador buscarPorDocumento(String documento) {
        return consultarUno("SELECT * FROM jugador WHERE documento = ?", this::mapear,
                "Error al buscar el documento " + documento, documento);
    }

    @Override
    public List<Jugador> listarPorDivision(int idDivision) {
        return consultarLista("SELECT * FROM jugador WHERE id_division = ? AND estado = 'ACTIVO' "
                            + "ORDER BY apellido, nombre", this::mapear,
                "Error al listar la división " + idDivision, idDivision);
    }

    @Override
    public boolean actualizar(Jugador j) {
        return actualizar("UPDATE jugador SET apellido = ?, nombre = ?, estado = ?, id_division = ? "
                        + "WHERE id_jugador = ?", "Error al actualizar el jugador",
                j.getApellido(), j.getNombre(), j.getEstado(), j.getIdDivision(), j.getIdJugador()) == 1;
    }

    @Override
    public boolean eliminar(int idJugador) {
        return actualizar("DELETE FROM jugador WHERE id_jugador = ?",
                "Error al eliminar el jugador " + idJugador, idJugador) == 1;
    }

    @Override
    public int contarActivosPorDivision(int idDivision) {
        return consultarEntero("SELECT COUNT(*) FROM jugador WHERE id_division = ? AND estado = 'ACTIVO'",
                "Error al contar la división " + idDivision, idDivision);
    }

    @Override
    public List<Jugador> listarTodos() {
        return consultarLista("SELECT * FROM jugador ORDER BY apellido, nombre", this::mapear,
                "Error al listar el padrón de jugadores");
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
