package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias del CU01 - Gestionar jugador. */
class JugadorServiceTest {

    private DobleJugadorDAO dao;
    private JugadorService  servicio;
    private Division        sub13;

    @BeforeEach
    void prepararEscenario() {
        dao      = new DobleJugadorDAO();
        servicio = new JugadorService(dao);
        sub13    = new Division(1, "Sub-13", "SUB13", 2013, 2013, 3);
    }

    private Jugador nuevo(String doc, int anio) {
        return new Jugador(doc, "Pérez", "Juan", LocalDate.of(anio, 5, 10), 1);
    }

    @Test
    @DisplayName("CP-01: un jugador con la edad correcta se registra y recibe identificador")
    void registraJugadorValido() {
        int id = servicio.registrar(nuevo("55111222", 2013), sub13);

        assertEquals(1, id);
        assertEquals(1, dao.contarActivosPorDivision(1));
    }

    @Test
    @DisplayName("CP-02: RN-01 rechaza al jugador cuyo año de nacimiento no corresponde")
    void rechazaEdadFueraDeCategoria() {
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(nuevo("55111333", 2009), sub13));

        assertEquals("RN-01", ex.getCodigoRegla());
        assertEquals(0, dao.contarActivosPorDivision(1));
    }

    @Test
    @DisplayName("CP-03: RN-06 impide registrar dos jugadores con el mismo documento")
    void rechazaDocumentoDuplicado() {
        servicio.registrar(nuevo("55111444", 2013), sub13);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(nuevo("55111444", 2013), sub13));

        assertEquals("RN-06", ex.getCodigoRegla());
        assertEquals(1, dao.contarActivosPorDivision(1));
    }

    @Test
    @DisplayName("CP-04: RN-02 bloquea el alta cuando la división llegó a su cupo")
    void rechazaCupoCompleto() {
        servicio.registrar(nuevo("55111555", 2013), sub13);
        servicio.registrar(nuevo("55111666", 2013), sub13);
        servicio.registrar(nuevo("55111777", 2013), sub13);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(nuevo("55111888", 2013), sub13));

        assertEquals("RN-02", ex.getCodigoRegla());
        assertEquals(3, dao.contarActivosPorDivision(1));
    }

    @Test
    @DisplayName("CP-05: los datos obligatorios incompletos detienen el alta")
    void rechazaDatosIncompletos() {
        Jugador sinApellido = new Jugador("55111999", "", "Juan", LocalDate.of(2013, 1, 1), 1);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(sinApellido, sub13));

        assertEquals("RN-00", ex.getCodigoRegla());
    }
}
