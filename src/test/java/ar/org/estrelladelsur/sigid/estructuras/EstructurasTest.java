package ar.org.estrelladelsur.sigid.estructuras;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de las estructuras de datos propias (TP3). */
class EstructurasTest {

    @Test
    @DisplayName("CP-26: la lista enlazada agrega, obtiene, busca y elimina conservando el orden")
    void listaEnlazada() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.agregar("B");
        lista.agregar("C");
        lista.agregarAlInicio("A");

        assertEquals(3, lista.tamanio());
        assertEquals("A", lista.obtener(0));
        assertEquals("C", lista.obtener(2));
        assertEquals("B", lista.buscar(s -> s.equals("B")));

        assertTrue(lista.eliminarSi(s -> s.equals("C")));   // elimina el último
        lista.agregar("D");                                // la referencia al final sigue siendo válida
        assertEquals("D", lista.obtener(2));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(5));
    }

    @Test
    @DisplayName("CP-27: la pila devuelve los elementos en orden LIFO y respeta su capacidad")
    void pila() {
        Pila<Integer> pila = new Pila<>(3);
        for (int i = 1; i <= 4; i++) {
            pila.apilar(i);            // el 1 se descarta al apilar el 4
        }
        assertEquals(3, pila.tamanio());
        assertEquals(4, pila.desapilar());
        assertEquals(3, pila.desapilar());
        assertEquals(2, pila.desapilar());
        assertTrue(pila.estaVacia());
        assertThrows(EstructuraVaciaException.class, pila::desapilar);
    }

    @Test
    @DisplayName("CP-28: la cola devuelve los elementos en orden FIFO")
    void cola() {
        Cola<String> cola = new Cola<>();
        cola.encolar("primero");
        cola.encolar("segundo");
        cola.encolar("tercero");

        assertEquals("primero", cola.verFrente());
        assertEquals("primero", cola.desencolar());
        assertEquals("segundo", cola.desencolar());
        cola.encolar("cuarto");
        assertEquals("tercero", cola.desencolar());
        assertEquals("cuarto", cola.desencolar());
        assertThrows(EstructuraVaciaException.class, cola::desencolar);
    }
}
