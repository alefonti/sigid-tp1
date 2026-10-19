package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Convocatoria;
import ar.org.estrelladelsur.sigid.modelo.Notificacion;
import ar.org.estrelladelsur.sigid.modelo.Tutor;
import ar.org.estrelladelsur.sigid.persistencia.ConvocatoriaDAO;
import ar.org.estrelladelsur.sigid.persistencia.NotificacionDAO;
import ar.org.estrelladelsur.sigid.persistencia.TutorDAO;

import java.util.ArrayList;
import java.util.List;

/** Dobles de prueba en memoria para convocatorias y notificaciones. */
class DoblesConvocatoria {

    static class DobleConvocatoriaDAO implements ConvocatoriaDAO {
        final List<Convocatoria> almacen = new ArrayList<>();
        private int secuencia = 0;

        @Override public int insertar(Convocatoria c) {
            c.setIdConvocatoria(++secuencia);
            almacen.add(c);
            return c.getIdConvocatoria();
        }

        @Override public List<Convocatoria> listarPorEvento(int idEvento) {
            return almacen.stream().filter(c -> c.getIdEvento() == idEvento).toList();
        }

        @Override public boolean eliminar(int idEvento, int idJugador) {
            return almacen.removeIf(c -> c.getIdEvento() == idEvento && c.getIdJugador() == idJugador);
        }

        @Override public int confirmarTodas(int idEvento) {
            int n = 0;
            for (Convocatoria c : almacen) {
                if (c.getIdEvento() == idEvento) { c.setConfirmada(true); n++; }
            }
            return n;
        }
    }

    static class DobleTutorDAO implements TutorDAO {
        private final List<Tutor> tutores;
        DobleTutorDAO(List<Tutor> tutores) { this.tutores = tutores; }
        @Override public List<Tutor> listarPorEvento(int idEvento) { return tutores; }
        @Override public Tutor buscarPorId(int idTutor) {
            return tutores.stream().filter(t -> t.getIdTutor() == idTutor).findFirst().orElse(null);
        }
        @Override public List<Tutor> listarTodos() { return tutores; }
    }

    static class DobleNotificacionDAO implements NotificacionDAO {
        final List<Notificacion> enviadas = new ArrayList<>();
        @Override public int insertar(Notificacion n) {
            enviadas.add(n);
            n.setIdNotificacion(enviadas.size());
            return enviadas.size();
        }
        @Override public List<Notificacion> listarPendientes() {
            return enviadas.stream().filter(n -> "PENDIENTE".equals(n.getEstadoEnvio())).toList();
        }
        @Override public boolean actualizarEstado(int id, String estado) {
            for (Notificacion n : enviadas) {
                if (n.getIdNotificacion() == id) { n.setEstadoEnvio(estado); return true; }
            }
            return false;
        }
    }
}
