package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Asistencia;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.persistencia.AsistenciaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias del CU03 - Registrar asistencia y del CU11 que lo extiende. */
class AsistenciaServiceTest {

    /** Doble de prueba en memoria del AsistenciaDAO. */
    static class DobleAsistenciaDAO implements AsistenciaDAO {
        final List<Asistencia> almacen = new ArrayList<>();
        private int secuencia = 0;

        @Override public int insertar(Asistencia a) {
            a.setIdAsistencia(++secuencia);
            almacen.add(a);
            return a.getIdAsistencia();
        }

        @Override public boolean existe(int idEvento, int idJugador) {
            return almacen.stream()
                    .anyMatch(a -> a.getIdEvento() == idEvento && a.getIdJugador() == idJugador);
        }

        @Override public List<Asistencia> listarPorJugador(int idJugador) {
            return almacen.stream().filter(a -> a.getIdJugador() == idJugador).toList();
        }

        @Override public boolean actualizarMotivo(int idAsistencia, String motivo) {
            for (Asistencia a : almacen) {
                if (a.getIdAsistencia() == idAsistencia) {
                    a.setMotivo(motivo);
                    return true;
                }
            }
            return false;
        }
    }

    private DobleAsistenciaDAO dao;
    private AsistenciaService  servicio;

    @BeforeEach
    void prepararEscenario() {
        dao      = new DobleAsistenciaDAO();
        servicio = new AsistenciaService(dao);
    }

    @Test
    @DisplayName("CP-20: la asistencia se registra y devuelve identificador")
    void registraAsistencia() {
        int id = servicio.registrar(1, 10, EstadoAsistencia.PRESENTE);

        assertEquals(1, id);
        assertTrue(dao.existe(1, 10));
    }

    @Test
    @DisplayName("CP-21: RN-09 impide registrar dos veces al mismo jugador en el mismo evento")
    void rechazaAsistenciaDuplicada() {
        servicio.registrar(1, 10, EstadoAsistencia.PRESENTE);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(1, 10, EstadoAsistencia.AUSENTE));

        assertEquals("RN-09", ex.getCodigoRegla());
        assertEquals(1, dao.almacen.size());
    }

    @Test
    @DisplayName("CP-22: el porcentaje de asistencia computa PRESENTE y TARDE")
    void calculaPorcentajeDeAsistencia() {
        servicio.registrar(1, 10, EstadoAsistencia.PRESENTE);
        servicio.registrar(2, 10, EstadoAsistencia.TARDE);
        servicio.registrar(3, 10, EstadoAsistencia.AUSENTE);
        servicio.registrar(4, 10, EstadoAsistencia.JUSTIFICADO);

        assertEquals(50.0, servicio.porcentajeAsistencia(10), 0.01);
    }

    @Test
    @DisplayName("CP-23: un jugador sin registros devuelve 0 % y no falla")
    void porcentajeSinRegistros() {
        assertEquals(0.0, servicio.porcentajeAsistencia(99), 0.01);
    }

    @Test
    @DisplayName("CP-24: CU11 agrega el motivo a una inasistencia registrada")
    void justificaInasistencia() {
        int id = servicio.registrar(1, 10, EstadoAsistencia.AUSENTE);

        assertTrue(servicio.justificarInasistencia(id, "Certificado escolar"));
        assertEquals("Certificado escolar", dao.almacen.get(0).getMotivo());
    }

    @Test
    @DisplayName("CP-25: RN-10 rechaza una justificación vacía")
    void rechazaJustificacionVacia() {
        int id = servicio.registrar(1, 10, EstadoAsistencia.AUSENTE);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> servicio.justificarInasistencia(id, "   "));

        assertEquals("RN-10", ex.getCodigoRegla());
    }
}
