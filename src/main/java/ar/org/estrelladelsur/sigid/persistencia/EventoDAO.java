package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Evento;
import java.util.List;

/** Contrato de persistencia de la agenda deportiva (nuevo en el TP3). */
public interface EventoDAO {
    int          insertar(Evento evento);
    Evento       buscarPorId(int idEvento);
    List<Evento> listarPorDivision(int idDivision);
}
