package ar.org.estrelladelsur.sigid.estructuras;

import ar.org.estrelladelsur.sigid.comun.SigidException;

/** Se lanza al intentar extraer un elemento de una pila o cola vacía. */
public class EstructuraVaciaException extends SigidException {
    public EstructuraVaciaException(String estructura) {
        super("La " + estructura + " está vacía.");
    }
}
