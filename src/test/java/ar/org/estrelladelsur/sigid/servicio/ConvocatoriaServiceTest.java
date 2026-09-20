package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.FichaMedica;
import ar.org.estrelladelsur.sigid.modelo.Gravedad;
import ar.org.estrelladelsur.sigid.modelo.Lesion;
import ar.org.estrelladelsur.sigid.modelo.ResultadoConvocatoria;
import ar.org.estrelladelsur.sigid.modelo.Tutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración de la capa de servicio para el CU05 - Generar
 * convocatoria, que incluye al CU10 (validar aptitud) y al CU06 (notificar).
 */
class ConvocatoriaServiceTest {

    private static final LocalDate FECHA_PARTIDO = LocalDate.of(2026, 9, 27);
    private static final int       ID_EVENTO     = 3;

    private DoblesMedicos.DobleFichaMedicaDAO           fichaDAO;
    private DoblesMedicos.DobleLesionDAO                lesionDAO;
    private DoblesConvocatoria.DobleConvocatoriaDAO     convocatoriaDAO;
    private DoblesConvocatoria.DobleNotificacionDAO     notificacionDAO;
    private ConvocatoriaService                         servicio;

    @BeforeEach
    void prepararEscenario() {
        fichaDAO        = new DoblesMedicos.DobleFichaMedicaDAO();
        lesionDAO       = new DoblesMedicos.DobleLesionDAO();
        convocatoriaDAO = new DoblesConvocatoria.DobleConvocatoriaDAO();
        notificacionDAO = new DoblesConvocatoria.DobleNotificacionDAO();

        List<Tutor> tutores = List.of(
                new Tutor(1, "Acuña",   "Marcela", "11-3344-5566", "macuna@correo.com"),
                new Tutor(2, "Benítez", "Jorge",   "11-2233-4455", null));

        servicio = new ConvocatoriaService(
                convocatoriaDAO,
                new FichaMedicaService(fichaDAO, lesionDAO),
                new NotificacionService(
                        new DoblesConvocatoria.DobleTutorDAO(tutores), notificacionDAO));
    }

    /** Carga una ficha vigente para el jugador indicado. */
    private void conFichaVigente(int idJugador) {
        fichaDAO.insertar(new FichaMedica(idJugador, 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), true));
    }

    @Test
    @DisplayName("CP-13: todos los jugadores habilitados quedan convocados y se notifica a los tutores")
    void convocaJugadoresHabilitados() {
        conFichaVigente(1);
        conFichaVigente(2);
        conFichaVigente(4);

        ResultadoConvocatoria r = servicio.convocar(ID_EVENTO, List.of(1, 2, 4), FECHA_PARTIDO);

        assertEquals(3, r.cantidadConvocados());
        assertEquals(0, r.cantidadRechazados());
        assertEquals(3, convocatoriaDAO.listarPorEvento(ID_EVENTO).size());
        assertEquals(2, r.getNotificacionesEnviadas());
        assertEquals(2, notificacionDAO.enviadas.size());
    }

    @Test
    @DisplayName("CP-14: RN-03 bloquea al jugador con ficha vencida y convoca al resto")
    void bloqueaFichaVencidaYConvocaAlResto() {
        conFichaVigente(1);
        fichaDAO.insertar(new FichaMedica(3, 1,
                LocalDate.of(2025, 4, 10), LocalDate.of(2026, 4, 10), true));  // vencida

        ResultadoConvocatoria r = servicio.convocar(ID_EVENTO, List.of(1, 3), FECHA_PARTIDO);

        assertEquals(1, r.cantidadConvocados());
        assertEquals(1, r.cantidadRechazados());
        assertTrue(r.getConvocados().contains(1));
        assertTrue(r.getRechazados().get(3).contains("venció"));
        assertEquals(1, convocatoriaDAO.listarPorEvento(ID_EVENTO).size());
    }

    @Test
    @DisplayName("CP-15: RN-03 bloquea al jugador con lesión activa")
    void bloqueaLesionActiva() {
        conFichaVigente(7);
        lesionDAO.insertar(new Lesion(7, "Distensión", Gravedad.MODERADA, LocalDate.of(2026, 9, 5)));

        ResultadoConvocatoria r = servicio.convocar(ID_EVENTO, List.of(7), FECHA_PARTIDO);

        assertEquals(0, r.cantidadConvocados());
        assertEquals("registra una lesión activa", r.getRechazados().get(7));
    }

    @Test
    @DisplayName("CP-16: RN-04 rechaza una convocatoria de más de 18 jugadores")
    void rechazaExcesoDeConvocados() {
        List<Integer> diecinueve = new ArrayList<>();
        for (int i = 1; i <= 19; i++) {
            conFichaVigente(i);
            diecinueve.add(i);
        }

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.convocar(ID_EVENTO, diecinueve, FECHA_PARTIDO));

        assertEquals("RN-04", ex.getCodigoRegla());
        assertTrue(convocatoriaDAO.listarPorEvento(ID_EVENTO).isEmpty(),
                   "No debe persistirse ninguna convocatoria si la validación falla");
    }

    @Test
    @DisplayName("CP-17: RN-05 impide confirmar una lista con menos de 11 jugadores")
    void rechazaListaIncompleta() {
        conFichaVigente(1);
        conFichaVigente(2);
        servicio.convocar(ID_EVENTO, List.of(1, 2), FECHA_PARTIDO);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.confirmarLista(ID_EVENTO));

        assertEquals("RN-05", ex.getCodigoRegla());
    }

    @Test
    @DisplayName("CP-18: una lista de 11 jugadores habilitados se confirma sin errores")
    void confirmaListaCompleta() {
        List<Integer> once = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            conFichaVigente(i);
            once.add(i);
        }
        servicio.convocar(ID_EVENTO, once, FECHA_PARTIDO);

        assertDoesNotThrow(() -> servicio.confirmarLista(ID_EVENTO));
    }

    @Test
    @DisplayName("CP-19: una convocatoria vacía es rechazada")
    void rechazaConvocatoriaVacia() {
        assertThrows(ReglaNegocioException.class,
                () -> servicio.convocar(ID_EVENTO, List.of(), FECHA_PARTIDO));
    }
}
