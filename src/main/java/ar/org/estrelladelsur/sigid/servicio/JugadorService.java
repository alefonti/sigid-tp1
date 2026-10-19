package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.algoritmos.Busqueda;
import ar.org.estrelladelsur.sigid.algoritmos.Ordenamiento;
import ar.org.estrelladelsur.sigid.estructuras.ListaEnlazada;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.EstadoJugador;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.persistencia.JugadorDAO;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;

/**
 * Realiza el CU01 - Gestionar jugador y el CU07 - Consultar plantel.
 *
 * Reglas de negocio que aplica:
 *   RN-00: documento, apellido, nombre y fecha de nacimiento son obligatorios.
 *   RN-01: el año de nacimiento del jugador debe estar admitido por la división.
 *   RN-02: no se puede superar el cupo máximo de la división.
 *   RN-06: el documento del jugador es único en el club.
 *   RN-07: sólo se opera sobre jugadores existentes.
 */
public class JugadorService {

    private final JugadorDAO jugadorDAO;

    public JugadorService(JugadorDAO jugadorDAO) {
        this.jugadorDAO = jugadorDAO;
    }

    // ================================================================ alta (TP2)

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

    // ================================================================ consultas

    public List<Jugador> buscarPorDivision(int idDivision) {
        return jugadorDAO.listarPorDivision(idDivision);
    }

    public Jugador buscarPorId(int idJugador) {
        return jugadorDAO.buscarPorId(idJugador);
    }

    /** Igual que buscarPorId pero exige que exista (RN-07). */
    public Jugador obtener(int idJugador) {
        Jugador j = jugadorDAO.buscarPorId(idJugador);
        if (j == null) {
            throw new ReglaNegocioException("RN-07",
                "No existe un jugador con el identificador " + idJugador + ".");
        }
        return j;
    }

    /**
     * TP3 - Plantel ordenado por el criterio elegido con MERGE SORT.
     * Se usa merge sort porque es estable: si dos jugadores empatan en el
     * criterio (misma edad, por ejemplo) conservan el orden alfabético que
     * traen de la base.
     */
    public Jugador[] plantelOrdenado(int idDivision, CriterioOrden criterio) {
        Jugador[] plantel = jugadorDAO.listarPorDivision(idDivision).toArray(new Jugador[0]);
        Ordenamiento.mergeSort(plantel, Comparator.<Jugador>naturalOrder());  // base alfabética
        Ordenamiento.mergeSort(plantel, criterio.getComparador());
        return plantel;
    }

    /**
     * TP3 - Búsqueda por DNI con BÚSQUEDA BINARIA.
     * 1) se carga el padrón en un arreglo, 2) se ordena por documento con
     * quick sort y 3) se busca en O(log n).
     *
     * @return el jugador, o null si no está registrado
     */
    public Jugador buscarPorDocumento(String documento) {
        Jugador[] padron = jugadorDAO.listarTodos().toArray(new Jugador[0]);
        Ordenamiento.quickSort(padron, Comparator.comparing(Jugador::getDocumento));
        int pos = Busqueda.binaria(padron, documento.trim(), Jugador::getDocumento,
                                   Comparator.naturalOrder());
        return pos >= 0 ? padron[pos] : null;
    }

    /**
     * TP3 - Búsqueda SECUENCIAL por coincidencia parcial de apellido,
     * sin distinguir mayúsculas ni tildes ("dominguez" encuentra "Domínguez").
     * Recorre una ListaEnlazada propia y devuelve otra con los resultados.
     */
    public ListaEnlazada<Jugador> buscarPorApellido(String fragmento) {
        ListaEnlazada<Jugador> padron = new ListaEnlazada<>();
        for (Jugador j : jugadorDAO.listarTodos()) {
            padron.agregar(j);
        }
        String buscado = normalizar(fragmento);
        return padron.filtrar(j -> normalizar(j.getApellido()).contains(buscado));
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
                         .replaceAll("\\p{M}", "");
    }

    // ================================================================ cambios de estado

    public boolean darDeBaja(int idJugador) {
        Jugador j = obtener(idJugador);
        if (j.getEstado() == EstadoJugador.BAJA) {
            throw new ReglaNegocioException("RN-07", j.nombreCompleto() + " ya estaba dado de baja.");
        }
        j.setEstado(EstadoJugador.BAJA);
        return jugadorDAO.actualizar(j);
    }

    /** Usado por la operación "deshacer" para restituir el estado previo. */
    public boolean cambiarEstado(int idJugador, EstadoJugador nuevoEstado) {
        Jugador j = obtener(idJugador);
        j.setEstado(nuevoEstado);
        return jugadorDAO.actualizar(j);
    }
}
