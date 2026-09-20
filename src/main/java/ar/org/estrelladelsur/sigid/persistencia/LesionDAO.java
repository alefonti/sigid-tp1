package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Lesion;
import java.util.List;

/** Contrato de persistencia de lesiones. */
public interface LesionDAO {
    int          insertar(Lesion lesion);
    List<Lesion> listarActivasPorJugador(int idJugador);
    boolean      registrarAlta(int idLesion, java.time.LocalDate fechaAlta);
}
