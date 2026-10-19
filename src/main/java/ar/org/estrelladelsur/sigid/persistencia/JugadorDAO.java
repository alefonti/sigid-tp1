package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Jugador;
import java.util.List;

/**
 * Contrato de persistencia de jugadores.
 * La capa de servicio depende de esta interfaz y no de su implementación
 * concreta (inversión de dependencias), lo que permite sustituirla por
 * un doble de prueba en el modelo de pruebas.
 */
public interface JugadorDAO {
    int           insertar(Jugador jugador);
    Jugador       buscarPorId(int idJugador);
    Jugador       buscarPorDocumento(String documento);
    List<Jugador> listarPorDivision(int idDivision);
    boolean       actualizar(Jugador jugador);
    boolean       eliminar(int idJugador);
    int           contarActivosPorDivision(int idDivision);
    /** Padrón completo (activos e inactivos), usado por búsquedas y ordenamientos. */
    List<Jugador> listarTodos();
}
