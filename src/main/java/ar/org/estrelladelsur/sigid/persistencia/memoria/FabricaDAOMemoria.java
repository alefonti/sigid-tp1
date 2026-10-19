package ar.org.estrelladelsur.sigid.persistencia.memoria;

import ar.org.estrelladelsur.sigid.estructuras.ListaEnlazada;
import ar.org.estrelladelsur.sigid.modelo.*;
import ar.org.estrelladelsur.sigid.persistencia.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Fábrica concreta que guarda los datos en memoria, dentro de listas
 * enlazadas propias (ListaEnlazada). Se activa automáticamente cuando
 * no hay un servidor MySQL disponible, o a pedido con el argumento
 * --memoria, para que el prototipo pueda probarse en cualquier equipo.
 *
 * Los datos de arranque replican los de sql/02_datos.sql, por lo que
 * los dos modos muestran exactamente la misma información inicial.
 * Lo que se cargue durante la sesión se pierde al salir.
 */
public final class FabricaDAOMemoria extends FabricaDAO {

    // ---------------------------------------------------------------- almacenes
    final ListaEnlazada<Jugador>           jugadores      = new ListaEnlazada<>();
    final ListaEnlazada<Division>          divisiones     = new ListaEnlazada<>();
    final ListaEnlazada<Evento>            eventos        = new ListaEnlazada<>();
    final ListaEnlazada<Asistencia>        asistencias    = new ListaEnlazada<>();
    final ListaEnlazada<Convocatoria>      convocatorias  = new ListaEnlazada<>();
    final ListaEnlazada<FichaMedica>       fichas         = new ListaEnlazada<>();
    final ListaEnlazada<Lesion>            lesiones       = new ListaEnlazada<>();
    final ListaEnlazada<Tutor>             tutores        = new ListaEnlazada<>();
    final ListaEnlazada<int[]>             vinculos       = new ListaEnlazada<>(); // {jugador, tutor, primario}
    final ListaEnlazada<Notificacion>      notificaciones = new ListaEnlazada<>();
    final ListaEnlazada<Entrenador>        entrenadores   = new ListaEnlazada<>();
    final ListaEnlazada<ProfesionalMedico> profesionales  = new ListaEnlazada<>();

    private int secJugador, secEvento, secAsistencia, secConvocatoria,
                secFicha, secLesion, secNotificacion;

    private final JugadorDAO      jugadorDAO      = new JugadorMem();
    private final DivisionDAO     divisionDAO     = new DivisionMem();
    private final EventoDAO       eventoDAO       = new EventoMem();
    private final AsistenciaDAO   asistenciaDAO   = new AsistenciaMem();
    private final ConvocatoriaDAO convocatoriaDAO = new ConvocatoriaMem();
    private final FichaMedicaDAO  fichaDAO        = new FichaMem();
    private final LesionDAO       lesionDAO       = new LesionMem();
    private final TutorDAO        tutorDAO        = new TutorMem();
    private final NotificacionDAO notificacionDAO = new NotificacionMem();
    private final PersonalDAO     personalDAO     = new PersonalMem();

    public FabricaDAOMemoria() {
        DatosDemo.cargar(this);
    }

    @Override public JugadorDAO      jugadores()      { return jugadorDAO; }
    @Override public DivisionDAO     divisiones()     { return divisionDAO; }
    @Override public EventoDAO       eventos()        { return eventoDAO; }
    @Override public AsistenciaDAO   asistencias()    { return asistenciaDAO; }
    @Override public ConvocatoriaDAO convocatorias()  { return convocatoriaDAO; }
    @Override public FichaMedicaDAO  fichasMedicas()  { return fichaDAO; }
    @Override public LesionDAO       lesiones()       { return lesionDAO; }
    @Override public TutorDAO        tutores()        { return tutorDAO; }
    @Override public NotificacionDAO notificaciones() { return notificacionDAO; }
    @Override public PersonalDAO     personal()       { return personalDAO; }

    @Override public String descripcion() { return "MEMORIA (modo demostración, sin MySQL)"; }

    /** Convierte una ListaEnlazada filtrada a la List que exigen los contratos DAO. */
    private static <T> List<T> aLista(ListaEnlazada<T> origen, Predicate<T> condicion) {
        List<T> resultado = new ArrayList<>();
        for (T dato : origen) {
            if (condicion.test(dato)) {
                resultado.add(dato);
            }
        }
        return resultado;
    }

    // ================================================================ implementaciones

    private class JugadorMem implements JugadorDAO {
        @Override public int insertar(Jugador j) {
            j.setIdJugador(++secJugador);
            jugadores.agregar(j);
            return j.getIdJugador();
        }
        @Override public Jugador buscarPorId(int id) {
            return jugadores.buscar(j -> j.getIdJugador() == id);
        }
        @Override public Jugador buscarPorDocumento(String doc) {
            return jugadores.buscar(j -> doc.equals(j.getDocumento()));
        }
        @Override public List<Jugador> listarPorDivision(int idDivision) {
            return aLista(jugadores, j -> j.getIdDivision() == idDivision && j.estaActivo());
        }
        @Override public boolean actualizar(Jugador j) { return buscarPorId(j.getIdJugador()) != null; }
        @Override public boolean eliminar(int id) { return jugadores.eliminarSi(j -> j.getIdJugador() == id); }
        @Override public int contarActivosPorDivision(int idDivision) {
            return listarPorDivision(idDivision).size();
        }
        @Override public List<Jugador> listarTodos() { return aLista(jugadores, j -> true); }
    }

    private class DivisionMem implements DivisionDAO {
        @Override public List<Division> listarTodas() { return aLista(divisiones, d -> true); }
        @Override public Division buscarPorId(int id) { return divisiones.buscar(d -> d.getIdDivision() == id); }
    }

    private class EventoMem implements EventoDAO {
        @Override public int insertar(Evento e) {
            Evento choque = eventos.buscar(x -> x.getIdSede() == e.getIdSede()
                    && x.getFecha().equals(e.getFecha()) && x.getHoraInicio().equals(e.getHoraInicio()));
            if (choque != null) {   // replica la restricción UNIQUE uq_evento_sede
                throw new PersistenciaException("La sede ya tiene otro evento en ese día y horario", null);
            }
            e.setIdEvento(++secEvento);
            eventos.agregar(e);
            return e.getIdEvento();
        }
        @Override public Evento buscarPorId(int id) { return eventos.buscar(e -> e.getIdEvento() == id); }
        @Override public List<Evento> listarPorDivision(int idDivision) {
            return aLista(eventos, e -> e.getIdDivision() == idDivision);
        }
    }

    private class AsistenciaMem implements AsistenciaDAO {
        @Override public int insertar(Asistencia a) {
            a.setIdAsistencia(++secAsistencia);
            asistencias.agregar(a);
            return a.getIdAsistencia();
        }
        @Override public boolean existe(int idEvento, int idJugador) {
            return asistencias.buscar(a -> a.getIdEvento() == idEvento && a.getIdJugador() == idJugador) != null;
        }
        @Override public List<Asistencia> listarPorJugador(int idJugador) {
            return aLista(asistencias, a -> a.getIdJugador() == idJugador);
        }
        @Override public boolean actualizarMotivo(int idAsistencia, String motivo) {
            Asistencia a = asistencias.buscar(x -> x.getIdAsistencia() == idAsistencia);
            if (a == null) return false;
            a.setEstado(EstadoAsistencia.JUSTIFICADO);
            a.setMotivo(motivo);
            return true;
        }
        @Override public boolean eliminar(int idAsistencia) {
            return asistencias.eliminarSi(a -> a.getIdAsistencia() == idAsistencia);
        }
    }

    private class ConvocatoriaMem implements ConvocatoriaDAO {
        @Override public int insertar(Convocatoria c) {
            if (convocatorias.buscar(x -> x.getIdEvento() == c.getIdEvento()
                                       && x.getIdJugador() == c.getIdJugador()) != null) {
                throw new PersistenciaException("El jugador ya estaba convocado a ese partido", null);
            }
            c.setIdConvocatoria(++secConvocatoria);
            convocatorias.agregar(c);
            return c.getIdConvocatoria();
        }
        @Override public List<Convocatoria> listarPorEvento(int idEvento) {
            return aLista(convocatorias, c -> c.getIdEvento() == idEvento);
        }
        @Override public boolean eliminar(int idEvento, int idJugador) {
            return convocatorias.eliminarSi(c -> c.getIdEvento() == idEvento && c.getIdJugador() == idJugador);
        }
        @Override public int confirmarTodas(int idEvento) {
            int n = 0;
            for (Convocatoria c : convocatorias) {
                if (c.getIdEvento() == idEvento) { c.setConfirmada(true); n++; }
            }
            return n;
        }
    }

    private class FichaMem implements FichaMedicaDAO {
        @Override public int insertar(FichaMedica f) {
            f.setIdFicha(++secFicha);
            fichas.agregar(f);
            return f.getIdFicha();
        }
        @Override public FichaMedica buscarUltimaPorJugador(int idJugador) {
            FichaMedica ultima = null;
            for (FichaMedica f : fichas) {
                if (f.getIdJugador() == idJugador
                        && (ultima == null || f.getFechaVencimiento().isAfter(ultima.getFechaVencimiento()))) {
                    ultima = f;
                }
            }
            return ultima;
        }
    }

    private class LesionMem implements LesionDAO {
        @Override public int insertar(Lesion l) {
            l.setIdLesion(++secLesion);
            lesiones.agregar(l);
            return l.getIdLesion();
        }
        @Override public List<Lesion> listarActivasPorJugador(int idJugador) {
            return aLista(lesiones, l -> l.getIdJugador() == idJugador && l.estaActiva());
        }
        @Override public boolean registrarAlta(int idLesion, LocalDate fechaAlta) {
            Lesion l = lesiones.buscar(x -> x.getIdLesion() == idLesion);
            if (l == null) return false;
            l.setFechaAltaReal(fechaAlta);
            return true;
        }
    }

    private class TutorMem implements TutorDAO {
        /** Tutores de contacto primario de los jugadores convocados al evento. */
        @Override public List<Tutor> listarPorEvento(int idEvento) {
            List<Tutor> resultado = new ArrayList<>();
            for (Convocatoria c : convocatorias) {
                if (c.getIdEvento() != idEvento) continue;
                for (int[] v : vinculos) {
                    if (v[0] == c.getIdJugador() && v[2] == 1) {
                        Tutor t = buscarPorId(v[1]);
                        if (t != null && !resultado.contains(t)) {
                            resultado.add(t);
                        }
                    }
                }
            }
            return resultado;
        }
        @Override public Tutor buscarPorId(int id) { return tutores.buscar(t -> t.getIdTutor() == id); }
        @Override public List<Tutor> listarTodos() { return aLista(tutores, t -> true); }
    }

    private class NotificacionMem implements NotificacionDAO {
        @Override public int insertar(Notificacion n) {
            n.setIdNotificacion(++secNotificacion);
            notificaciones.agregar(n);
            return n.getIdNotificacion();
        }
        @Override public List<Notificacion> listarPendientes() {
            return aLista(notificaciones, n -> "PENDIENTE".equals(n.getEstadoEnvio()));
        }
        @Override public boolean actualizarEstado(int id, String estado) {
            Notificacion n = notificaciones.buscar(x -> x.getIdNotificacion() == id);
            if (n == null) return false;
            n.setEstadoEnvio(estado);
            return true;
        }
    }

    private class PersonalMem implements PersonalDAO {
        @Override public List<Entrenador> listarEntrenadores() { return aLista(entrenadores, e -> true); }
        @Override public List<ProfesionalMedico> listarProfesionales() { return aLista(profesionales, p -> true); }
    }
}
