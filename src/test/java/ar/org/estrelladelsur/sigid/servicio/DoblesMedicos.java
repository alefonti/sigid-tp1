package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.FichaMedica;
import ar.org.estrelladelsur.sigid.modelo.Lesion;
import ar.org.estrelladelsur.sigid.persistencia.FichaMedicaDAO;
import ar.org.estrelladelsur.sigid.persistencia.LesionDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Dobles de prueba en memoria del área médica. */
class DoblesMedicos {

    static class DobleFichaMedicaDAO implements FichaMedicaDAO {
        private final List<FichaMedica> almacen = new ArrayList<>();
        private int secuencia = 0;

        @Override public int insertar(FichaMedica f) {
            f.setIdFicha(++secuencia);
            almacen.add(f);
            return f.getIdFicha();
        }

        @Override public FichaMedica buscarUltimaPorJugador(int idJugador) {
            return almacen.stream()
                    .filter(f -> f.getIdJugador() == idJugador)
                    .reduce((a, b) -> b.getFechaVencimiento().isAfter(a.getFechaVencimiento()) ? b : a)
                    .orElse(null);
        }
    }

    static class DobleLesionDAO implements LesionDAO {
        private final List<Lesion> almacen = new ArrayList<>();
        private int secuencia = 0;

        @Override public int insertar(Lesion l) {
            l.setIdLesion(++secuencia);
            almacen.add(l);
            return l.getIdLesion();
        }

        @Override public List<Lesion> listarActivasPorJugador(int idJugador) {
            return almacen.stream()
                    .filter(l -> l.getIdJugador() == idJugador && l.estaActiva())
                    .toList();
        }

        @Override public boolean registrarAlta(int idLesion, LocalDate fechaAlta) {
            for (Lesion l : almacen) {
                if (l.getIdLesion() == idLesion) {
                    l.setFechaAltaReal(fechaAlta);
                    return true;
                }
            }
            return false;
        }
    }
}
