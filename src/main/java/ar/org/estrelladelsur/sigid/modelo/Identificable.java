package ar.org.estrelladelsur.sigid.modelo;

/**
 * Contrato mínimo de toda entidad persistente: exponer su identificador.
 * Lo implementan Persona y Evento, y lo aprovechan las estructuras de
 * datos genéricas para buscar elementos por id.
 */
public interface Identificable {
    int getId();
}
