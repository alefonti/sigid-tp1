package app;

import dao.AsistenciaDAO;
import modelo.Asistencia;
import modelo.Jugador;
import servicio.JugadorService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase de demostracion del prototipo operacional (Kendall & Kendall, 2011).
 * Ejercita dos de los modulos centrales del sistema: registro de jugadores
 * y registro/consulta de asistencia a entrenamientos.
 *
 * Requiere una base de datos MySQL "sigid_db" creada a partir de
 * sql/schema.sql y el conector mysql-connector-j en el classpath.
 */
public class Main {

    public static void main(String[] args) {
        JugadorService jugadorService = new JugadorService();
        AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

        try {
            // 1) Registrar un nuevo jugador en la división Sub-13 (id_division = 1)
            Jugador nuevo = new Jugador(
                    "Bautista", "Fernández", LocalDate.of(2013, 8, 20),
                    "56789012", 1, 1);
            jugadorService.registrarJugador(nuevo);
            System.out.println("Jugador registrado correctamente.");

            // 2) Listar jugadores de la división Sub-13
            List<Jugador> jugadores = jugadorService.obtenerJugadoresDeDivision(1);
            System.out.println("Jugadores en Sub-13:");
            jugadores.forEach(System.out::println);

            // 3) Registrar asistencia de un entrenamiento
            if (!jugadores.isEmpty()) {
                int idJugador = jugadores.get(0).getIdJugador();
                asistenciaDAO.registrar(new Asistencia(idJugador, LocalDate.now(), true));
                double porcentaje = asistenciaDAO.calcularPorcentajeAsistencia(idJugador);
                System.out.printf("Porcentaje de asistencia del jugador %d: %.1f%%%n",
                        idJugador, porcentaje);
            }

        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Regla de negocio incumplida: " + e.getMessage());
        }
    }
}
