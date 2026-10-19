package ar.org.estrelladelsur.sigid.servicio.historial;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ABSTRACCIÓN de una acción del usuario que puede revertirse
 * (patrón Command). Cada subclase sabe cómo deshacerse a sí misma; la
 * pila de historial sólo invoca deshacer() sin conocer el tipo concreto.
 */
public abstract class Operacion {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final LocalTime momento = LocalTime.now();

    /** Revierte el efecto de la operación sobre la base de datos. */
    public abstract void deshacer();

    /** Texto que se muestra en el historial. */
    public abstract String descripcion();

    public LocalTime getMomento() { return momento; }

    @Override public String toString() {
        return momento.format(HORA) + "  " + descripcion();
    }
}
