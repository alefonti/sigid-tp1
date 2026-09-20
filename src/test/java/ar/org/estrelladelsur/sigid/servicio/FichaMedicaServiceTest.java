package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.FichaMedica;
import ar.org.estrelladelsur.sigid.modelo.Gravedad;
import ar.org.estrelladelsur.sigid.modelo.Lesion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias del CU10 - Validar aptitud médica (regla RN-03). */
class FichaMedicaServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 9, 20);

    private DoblesMedicos.DobleFichaMedicaDAO fichaDAO;
    private DoblesMedicos.DobleLesionDAO      lesionDAO;
    private FichaMedicaService                servicio;

    @BeforeEach
    void prepararEscenario() {
        fichaDAO  = new DoblesMedicos.DobleFichaMedicaDAO();
        lesionDAO = new DoblesMedicos.DobleLesionDAO();
        servicio  = new FichaMedicaService(fichaDAO, lesionDAO);
    }

    @Test
    @DisplayName("CP-06: un jugador con ficha vigente y sin lesiones queda habilitado")
    void habilitaJugadorConFichaVigente() {
        fichaDAO.insertar(new FichaMedica(1, 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), true));

        assertTrue(servicio.estaHabilitado(1, HOY));
        assertNull(servicio.motivoDeBloqueo(1, HOY));
    }

    @Test
    @DisplayName("CP-07: la ficha vencida bloquea al jugador e informa la fecha")
    void bloqueaFichaVencida() {
        fichaDAO.insertar(new FichaMedica(2, 1,
                LocalDate.of(2025, 4, 10), LocalDate.of(2026, 4, 10), true));

        String motivo = servicio.motivoDeBloqueo(2, HOY);

        assertNotNull(motivo);
        assertTrue(motivo.contains("venció"));
        assertFalse(servicio.estaHabilitado(2, HOY));
    }

    @Test
    @DisplayName("CP-08: el apto físico desfavorable bloquea al jugador")
    void bloqueaAptoDesfavorable() {
        fichaDAO.insertar(new FichaMedica(3, 1,
                LocalDate.of(2026, 2, 2), LocalDate.of(2027, 2, 2), false));

        assertEquals("su apto físico es desfavorable", servicio.motivoDeBloqueo(3, HOY));
    }

    @Test
    @DisplayName("CP-09: una lesión activa bloquea aunque la ficha esté vigente")
    void bloqueaLesionActiva() {
        fichaDAO.insertar(new FichaMedica(4, 1,
                LocalDate.of(2026, 3, 12), LocalDate.of(2027, 3, 12), true));
        lesionDAO.insertar(new Lesion(4, "Distensión", Gravedad.MODERADA, LocalDate.of(2026, 9, 5)));

        assertEquals("registra una lesión activa", servicio.motivoDeBloqueo(4, HOY));
    }

    @Test
    @DisplayName("CP-10: tras el alta médica el jugador vuelve a estar habilitado")
    void habilitaTrasAltaMedica() {
        fichaDAO.insertar(new FichaMedica(5, 1,
                LocalDate.of(2026, 3, 12), LocalDate.of(2027, 3, 12), true));
        int idLesion = lesionDAO.insertar(
                new Lesion(5, "Esguince", Gravedad.LEVE, LocalDate.of(2026, 8, 2)));

        assertFalse(servicio.estaHabilitado(5, HOY));

        lesionDAO.registrarAlta(idLesion, LocalDate.of(2026, 9, 1));

        assertTrue(servicio.estaHabilitado(5, HOY));
    }

    @Test
    @DisplayName("CP-11: el jugador sin ficha cargada no puede ser convocado")
    void bloqueaJugadorSinFicha() {
        assertEquals("no tiene ficha médica cargada", servicio.motivoDeBloqueo(99, HOY));
    }

    @Test
    @DisplayName("CP-12: RN-08 rechaza una ficha cuyo vencimiento no es posterior a la emisión")
    void rechazaVigenciaInvalida() {
        FichaMedica invalida = new FichaMedica(6, 1,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), true);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(invalida));

        assertEquals("RN-08", ex.getCodigoRegla());
    }
}
