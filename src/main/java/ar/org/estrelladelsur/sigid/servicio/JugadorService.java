package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.persistencia.JugadorDAO;

import java.util.List;

/**
 * Realiza el CU01 - Gestionar jugador.
 *
 * Reglas de negocio que aplica:
 *   RN-01: el año de nacimiento del jugador debe estar admitido por la división.
 *   RN-02: no se puede superar el cupo máximo de la división.
 *   RN-06: el documento del jugador es único en el club.
 */
public class JugadorService {

    private final JugadorDAO jugadorDAO;

    public JugadorService(JugadorDAO jugadorDAO) {
        this.jugadorDAO = jugadorDAO;
    }

    /**
     * Registra un jugador validando previamente las reglas de negocio.
     *
     * @return el identificador asignado por la base de datos
     * @throws ReglaNegocioException si alguna regla no se cumple
     */
    public int registrar(Jugador jugador, Division division) {
        validarDatosObligatorios(jugador);
        validarDocumentoUnico(jugador.getDocumento());
        validarCategoria(jugador, division);
        validarCupo(division);
        return jugadorDAO.insertar(jugador);
    }

    private void validarDatosObligatorios(Jugador j) {
        if (j.getDocumento() == null || j.getDocumento().isBlank()
                || j.getApellido() == null || j.getApellido().isBlank()
                || j.getNombre() == null || j.getNombre().isBlank()
                || j.getFechaNacimiento() == null) {
            throw new ReglaNegocioException("RN-00",
                "Documento, apellido, nombre y fecha de nacimiento son obligatorios.");
        }
    }

    private void validarDocumentoUnico(String documento) {
        if (jugadorDAO.buscarPorDocumento(documento) != null) {
            throw new ReglaNegocioException("RN-06",
                "Ya existe un jugador registrado con el documento " + documento + ".");
        }
    }

    /** RN-01: la división admite un rango cerrado de años de nacimiento. */
    private void validarCategoria(Jugador j, Division d) {
        int anio = j.getFechaNacimiento().getYear();
        if (!d.admiteAnioNacimiento(anio)) {
            throw new ReglaNegocioException("RN-01",
                "El jugador nació en " + anio + " y la división " + d.getNombre()
              + " admite nacidos entre " + d.getAnioNacDesde() + " y " + d.getAnioNacHasta() + ".");
        }
    }

    /** RN-02: la división no puede exceder su cupo máximo de jugadores activos. */
    private void validarCupo(Division d) {
        int activos = jugadorDAO.contarActivosPorDivision(d.getIdDivision());
        if (activos >= d.getCupoMaximo()) {
            throw new ReglaNegocioException("RN-02",
                "La división " + d.getNombre() + " alcanzó su cupo máximo de "
              + d.getCupoMaximo() + " jugadores.");
        }
    }

    public List<Jugador> buscarPorDivision(int idDivision) {
        return jugadorDAO.listarPorDivision(idDivision);
    }

    public Jugador buscarPorId(int idJugador) {
        return jugadorDAO.buscarPorId(idJugador);
    }

    public boolean darDeBaja(int idJugador) {
        Jugador j = jugadorDAO.buscarPorId(idJugador);
        if (j == null) {
            throw new ReglaNegocioException("RN-07",
                "No existe un jugador con el identificador " + idJugador + ".");
        }
        j.setEstado(ar.org.estrelladelsur.sigid.modelo.EstadoJugador.BAJA);
        return jugadorDAO.actualizar(j);
    }
}
