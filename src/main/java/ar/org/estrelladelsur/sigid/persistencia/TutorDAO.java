package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Tutor;
import java.util.List;

/** Contrato de persistencia de tutores. */
public interface TutorDAO {
    List<Tutor> listarPorEvento(int idEvento);
}
