package servicio;

import dao.JugadorDAO;
import modelo.Jugador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Capa de negocio (Servicio) para la entidad Jugador. Aplica las reglas
 * propias del club antes de delegar la persistencia al DAO, siguiendo la
 * arquitectura en 3 capas planteada en la propuesta de solución.
 */
public class JugadorService {

    private final JugadorDAO jugadorDAO = new JugadorDAO();

    public void registrarJugador(Jugador jugador) throws SQLException {
        validarEdad(jugador);
        jugadorDAO.registrar(jugador);
    }

    private void validarEdad(Jugador jugador) {
        int edad = Period.between(jugador.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 6 || edad > 18) {
            throw new IllegalArgumentException(
                    "El jugador debe tener entre 6 y 18 años para pertenecer a las divisiones inferiores.");
        }
    }

    public List<Jugador> obtenerJugadoresDeDivision(int idDivision) throws SQLException {
        return jugadorDAO.listarPorDivision(idDivision);
    }

    public boolean promoverDivision(int idJugador, int nuevaDivision) throws SQLException {
        return jugadorDAO.actualizarDivision(idJugador, nuevaDivision);
    }
}
