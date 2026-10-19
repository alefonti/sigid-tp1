package ar.org.estrelladelsur.sigid.estructuras;

import java.util.Iterator;

/**
 * Cola genérica (FIFO: el primero en entrar es el primero en salir)
 * implementada con nodos enlazados y referencias al frente y al final.
 *
 * En SIGID ordena el envío de notificaciones a los tutores: los avisos
 * se procesan en el mismo orden en que se generaron, para que ninguna
 * familia reciba la convocatoria después que otra que se cargó más tarde.
 */
public class Cola<T> implements Iterable<T> {

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int     tamanio;

    /** Agrega un elemento al final de la cola. O(1). */
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamanio++;
    }

    /** Quita y devuelve el elemento del frente. O(1). */
    public T desencolar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("cola de notificaciones");
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /** Devuelve el elemento del frente sin quitarlo. O(1). */
    public T verFrente() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("cola de notificaciones");
        }
        return frente.getDato();
    }

    public boolean estaVacia() { return frente == null; }
    public int     tamanio()   { return tamanio; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = frente;
            @Override public boolean hasNext() { return actual != null; }
            @Override public T next() {
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
