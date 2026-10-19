package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.estructuras.EstructuraVaciaException;
import ar.org.estrelladelsur.sigid.modelo.*;
import ar.org.estrelladelsur.sigid.servicio.DoblesConvocatoria.DobleConvocatoriaDAO;
import ar.org.estrelladelsur.sigid.servicio.DoblesConvocatoria.DobleNotificacionDAO;
import ar.org.estrelladelsur.sigid.servicio.DoblesConvocatoria.DobleTutorDAO;
import ar.org.estrelladelsur.sigid.servicio.historial.HistorialOperaciones;
import ar.org.estrelladelsur.sigid.servicio.historial.OperacionBajaJugador;
import ar.org.estrelladelsur.sigid.servicio.notificacion.NotificadorSMS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de las funcionalidades de servicio incorporadas en el TP3. */
class Tp3ServiciosTest {

    private JugadorService servicioConPadron(DobleJugadorDAO dao) {
        JugadorService s = new JugadorService(dao);
        Division sub13 = new Division(1, "Sub-13", "SUB13", 2013, 2013, 25);
        String[][] datos = {{"55345678", "Cabrera"}, {"55123456", "Acuña"},
                            {"55456789", "Domínguez"}, {"55234567", "Benítez"}};
        for (String[] d : datos) {
            s.registrar(new Jugador(d[0], d[1], "X", LocalDate.of(2013, 1, 1), 1), sub13);
        }
        return s;
    }

    @Test
    @DisplayName("CP-35: buscar por DNI (binaria) y por apellido sin tildes (secuencial)")
    void busquedasDeJugadores() {
        JugadorService s = servicioConPadron(new DobleJugadorDAO());

        assertEquals("Domínguez", s.buscarPorDocumento("55456789").getApellido());
        assertNull(s.buscarPorDocumento("99999999"));
        assertEquals(1, s.buscarPorApellido("dominguez").tamanio());
        assertEquals(0, s.buscarPorApellido("zzz").tamanio());
    }

    @Test
    @DisplayName("CP-36: el plantel se ordena según el criterio elegido")
    void plantelOrdenado() {
        JugadorService s = servicioConPadron(new DobleJugadorDAO());
        Jugador[] porApellido = s.plantelOrdenado(1, CriterioOrden.APELLIDO);
        Jugador[] porDni      = s.plantelOrdenado(1, CriterioOrden.DOCUMENTO);

        assertEquals("Acuña", porApellido[0].getApellido());
        assertEquals("Domínguez", porApellido[3].getApellido());
        assertEquals("55123456", porDni[0].getDocumento());
        assertEquals("55456789", porDni[3].getDocumento());
    }

    @Test
    @DisplayName("CP-37: la cola de notificaciones se procesa en orden FIFO y por el canal de cada tutor")
    void colaDeNotificaciones() {
        Tutor conMail = new Tutor(1, "Acuña", "Marcela", "11-3344-5566", "macuna@correo.com");
        Tutor sinMail = new Tutor(2, "Medina", "Carlos", "11-3322-1100", null);
        DobleNotificacionDAO notifDAO = new DobleNotificacionDAO();
        NotificacionService s = new NotificacionService(new DobleTutorDAO(List.of(conMail, sinMail)), notifDAO);

        assertEquals(2, s.notificarTutores(9, "Mensaje de prueba"));
        assertEquals(2, s.cantidadPendientes());

        List<String> comprobantes = s.procesarCola();
        assertTrue(comprobantes.get(0).contains("EMAIL"));     // primero en entrar...
        assertTrue(comprobantes.get(1).contains("SMS"));       // ...primero en salir
        assertEquals(0, s.cantidadPendientes());
        assertTrue(notifDAO.enviadas.stream().allMatch(n -> n.getEstadoEnvio().equals("ENVIADA")));
    }

    @Test
    @DisplayName("CP-38: el SMS se recorta a 160 caracteres")
    void smsRecortado() {
        Tutor t = new Tutor(2, "Medina", "Carlos", "11-3322-1100", null);
        Notificacion larga = new Notificacion(2, 9, CanalNotificacion.SMS, "x".repeat(300));
        String comprobante = new NotificadorSMS().enviar(t, larga);
        String texto = comprobante.substring(comprobante.indexOf("SIGID:"));
        assertEquals(NotificadorSMS.LIMITE_CARACTERES, texto.length());
    }

    @Test
    @DisplayName("CP-39: deshacer la baja de un jugador restituye su estado; la pila vacía lo informa")
    void deshacerBaja() {
        DobleJugadorDAO dao = new DobleJugadorDAO();
        JugadorService s = servicioConPadron(dao);
        HistorialOperaciones historial = new HistorialOperaciones();

        Jugador j = s.obtener(1);
        s.darDeBaja(1);
        historial.registrar(new OperacionBajaJugador(s, j, EstadoJugador.ACTIVO));
        assertEquals(EstadoJugador.BAJA, s.obtener(1).getEstado());

        historial.deshacerUltima();
        assertEquals(EstadoJugador.ACTIVO, s.obtener(1).getEstado());
        assertThrows(EstructuraVaciaException.class, historial::deshacerUltima);
    }

    @Test
    @DisplayName("CP-40: RN-11 impide convocar a un entrenamiento")
    void convocatoriaSoloPartidos() {
        ConvocatoriaService s = new ConvocatoriaService(new DobleConvocatoriaDAO(),
                new FichaMedicaService(new DoblesMedicos.DobleFichaMedicaDAO(), new DoblesMedicos.DobleLesionDAO()),
                new NotificacionService(new DobleTutorDAO(List.of()), new DobleNotificacionDAO()));
        Evento entrenamiento = new Entrenamiento(LocalDate.of(2026, 10, 20),
                LocalTime.of(17, 0), LocalTime.of(18, 30), 1, 1);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> s.convocar(entrenamiento, List.of(1, 2), LocalDate.of(2026, 10, 20)));
        assertEquals("RN-11", ex.getCodigoRegla());
    }
}
