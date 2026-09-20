package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.FichaMedica;

/** Contrato de persistencia de fichas médicas. */
public interface FichaMedicaDAO {
    int         insertar(FichaMedica ficha);
    /** Última ficha emitida para el jugador, o null si no tiene ninguna. */
    FichaMedica buscarUltimaPorJugador(int idJugador);
}
