package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Jugador;

import java.util.Comparator;

/**
 * Criterios con los que el usuario puede ordenar un plantel.
 * Cada constante encapsula su propio Comparator, que se le pasa tal cual
 * al algoritmo de ordenación genérico.
 */
public enum CriterioOrden {

    APELLIDO   ("Apellido y nombre",           Comparator.<Jugador>naturalOrder()),
    EDAD       ("Edad (de menor a mayor)",     Comparator.comparing(Jugador::getFechaNacimiento).reversed()),
    DOCUMENTO  ("Número de documento",         Comparator.comparing(Jugador::getDocumento)),
    ANTIGUEDAD ("Antigüedad en el club",       Comparator.comparing(Jugador::getFechaAlta));

    private final String              etiqueta;
    private final Comparator<Jugador> comparador;

    CriterioOrden(String etiqueta, Comparator<Jugador> comparador) {
        this.etiqueta   = etiqueta;
        this.comparador = comparador;
    }

    public String              getEtiqueta()   { return etiqueta; }
    public Comparator<Jugador> getComparador() { return comparador; }
}
