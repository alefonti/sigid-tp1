package ar.org.estrelladelsur.sigid.estructuras;

/**
 * Nodo genérico de una estructura enlazada: guarda un dato y la
 * referencia al nodo siguiente. Es la pieza que comparten la lista
 * enlazada, la pila y la cola.
 */
class Nodo<T> {

    private final T dato;
    private Nodo<T> siguiente;

    Nodo(T dato) {
        this.dato = dato;
    }

    T       getDato()      { return dato; }
    Nodo<T> getSiguiente() { return siguiente; }
    void    setSiguiente(Nodo<T> siguiente) { this.siguiente = siguiente; }
}
