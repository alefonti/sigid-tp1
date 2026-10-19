package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.EstadoJugador;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.persistencia.JugadorDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Doble de prueba (stub) en memoria del JugadorDAO.
 * Permite ejecutar las pruebas unitarias de la capa de servicio sin
 * depender de una base de datos MySQL levantada.
 */
class DobleJugadorDAO implements JugadorDAO {

    private final List<Jugador> almacen = new ArrayList<>();
    private int secuencia = 0;

    @Override public int insertar(Jugador j) {
        j.setIdJugador(++secuencia);
        almacen.add(j);
        return j.getIdJugador();
    }

    @Override public Jugador buscarPorId(int id) {
        return almacen.stream().filter(j -> j.getIdJugador() == id).findFirst().orElse(null);
    }

    @Override public Jugador buscarPorDocumento(String doc) {
        return almacen.stream().filter(j -> j.getDocumento().equals(doc)).findFirst().orElse(null);
    }

    @Override public List<Jugador> listarPorDivision(int idDivision) {
        return almacen.stream().filter(j -> j.getIdDivision() == idDivision).toList();
    }

    @Override public boolean actualizar(Jugador j) { return buscarPorId(j.getIdJugador()) != null; }

    @Override public boolean eliminar(int id) { return almacen.removeIf(j -> j.getIdJugador() == id); }

    @Override public int contarActivosPorDivision(int idDivision) {
        return (int) almacen.stream()
                .filter(j -> j.getIdDivision() == idDivision && j.getEstado() == EstadoJugador.ACTIVO)
                .count();
    }

    @Override public List<Jugador> listarTodos() { return new ArrayList<>(almacen); }
}
