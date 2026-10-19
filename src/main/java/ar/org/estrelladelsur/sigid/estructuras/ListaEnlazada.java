package ar.org.estrelladelsur.sigid.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Lista simplemente enlazada genérica, implementada desde cero.
 *
 * Se usa para cargar en memoria el padrón de jugadores y el listado de
 * personas del club. Mantiene referencias a la cabeza y a la cola para
 * que agregar al final sea O(1). Implementa Iterable, por lo que puede
 * recorrerse con un for-each como cualquier colección de Java.
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> cabeza;
    private Nodo<T> ultimo;
    private int     tamanio;

    /** Agrega al final de la lista. O(1). */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamanio++;
    }

    /** Agrega al principio de la lista. O(1). */
    public void agregarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        if (ultimo == null) {
            ultimo = nuevo;
        }
        tamanio++;
    }

    /** Devuelve el elemento de la posición indicada. O(n). */
    public T obtener(int indice) {
        validarIndice(indice);
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    /** Elimina la primera ocurrencia que cumpla la condición. O(n). */
    public boolean eliminarSi(Predicate<T> condicion) {
        Nodo<T> anterior = null;
        Nodo<T> actual   = cabeza;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == ultimo) {
                    ultimo = anterior;
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual   = actual.getSiguiente();
        }
        return false;
    }

    /** Búsqueda secuencial: primer elemento que cumple la condición, o null. */
    public T buscar(Predicate<T> condicion) {
        for (T dato : this) {
            if (condicion.test(dato)) {
                return dato;
            }
        }
        return null;
    }

    /** Nueva lista con los elementos que cumplen la condición. */
    public ListaEnlazada<T> filtrar(Predicate<T> condicion) {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        for (T dato : this) {
            if (condicion.test(dato)) {
                resultado.agregar(dato);
            }
        }
        return resultado;
    }

    public int     tamanio()  { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }

    /** Copia los elementos a un arreglo, que es lo que reciben los algoritmos de ordenación. */
    @SuppressWarnings("unchecked")
    public T[] aArreglo(T[] destino) {
        T[] arreglo = destino.length >= tamanio
                    ? destino
                    : (T[]) java.lang.reflect.Array.newInstance(
                          destino.getClass().getComponentType(), tamanio);
        int i = 0;
        for (T dato : this) {
            arreglo[i++] = dato;
        }
        return arreglo;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                "Índice " + indice + " fuera de rango (tamaño " + tamanio + ").");
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;

            @Override public boolean hasNext() { return actual != null; }

            @Override public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
