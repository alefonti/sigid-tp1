package ar.org.estrelladelsur.sigid.estructuras;

import java.util.Iterator;

/**
 * Pila genérica (LIFO: el último en entrar es el primero en salir)
 * implementada con nodos enlazados.
 *
 * En SIGID guarda el historial de operaciones de la sesión para poder
 * DESHACER la última acción del usuario: siempre se revierte la más
 * reciente, que es exactamente el comportamiento de una pila.
 */
public class Pila<T> implements Iterable<T> {

    private Nodo<T> tope;
    private int     tamanio;
    private final int capacidadMaxima;

    public Pila() {
        this(Integer.MAX_VALUE);
    }

    /** @param capacidadMaxima cantidad máxima de elementos; al superarla se descarta el más antiguo */
    public Pila(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva.");
        }
        this.capacidadMaxima = capacidadMaxima;
    }

    /** Apila un elemento. O(1), salvo cuando hay que descartar el fondo. */
    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
        if (tamanio > capacidadMaxima) {
            descartarFondo();
        }
    }

    /** Quita y devuelve el elemento del tope. O(1). */
    public T desapilar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("pila de operaciones");
        }
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--;
        return dato;
    }

    /** Devuelve el elemento del tope sin quitarlo. O(1). */
    public T verTope() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("pila de operaciones");
        }
        return tope.getDato();
    }

    public boolean estaVacia() { return tope == null; }
    public int     tamanio()   { return tamanio; }

    private void descartarFondo() {
        Nodo<T> actual = tope;
        while (actual.getSiguiente().getSiguiente() != null) {
            actual = actual.getSiguiente();
        }
        actual.setSiguiente(null);
        tamanio--;
    }

    /** Recorre desde el tope hacia el fondo (de la más reciente a la más antigua). */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = tope;
            @Override public boolean hasNext() { return actual != null; }
            @Override public T next() {
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
