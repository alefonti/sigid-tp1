package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Convocatoria;
import java.util.List;

/** Contrato de persistencia de convocatorias. */
public interface ConvocatoriaDAO {
    int                insertar(Convocatoria convocatoria);
    List<Convocatoria> listarPorEvento(int idEvento);
    boolean            eliminar(int idEvento, int idJugador);
}
