package ar.org.estrelladelsur.sigid.algoritmos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de los algoritmos de ordenación y búsqueda (TP3). */
class AlgoritmosTest {

    private Integer[] aleatorios(int n, long semilla) {
        Random r = new Random(semilla);
        Integer[] datos = new Integer[n];
        for (int i = 0; i < n; i++) {
            datos[i] = r.nextInt(1000);
        }
        return datos;
    }

    @Test
    @DisplayName("CP-29: merge sort, quick sort e inserción producen el mismo orden que Arrays.sort")
    void ordenamientos() {
        Integer[] esperado = aleatorios(500, 7);
        Integer[] merge    = esperado.clone();
        Integer[] quick    = esperado.clone();
        Integer[] insercion = esperado.clone();
        Arrays.sort(esperado);

        Ordenamiento.mergeSort(merge, Comparator.naturalOrder());
        Ordenamiento.quickSort(quick, Comparator.naturalOrder());
        Ordenamiento.insercion(insercion, Comparator.naturalOrder());

        assertArrayEquals(esperado, merge);
        assertArrayEquals(esperado, quick);
        assertArrayEquals(esperado, insercion);
    }

    @Test
    @DisplayName("CP-30: merge sort es estable (los empates conservan el orden original)")
    void mergeSortEstable() {
        String[] datos = {"b1", "a1", "b2", "a2", "b3"};
        Ordenamiento.mergeSort(datos, Comparator.comparing(s -> s.charAt(0)));   // ordena sólo por la letra
        assertArrayEquals(new String[] {"a1", "a2", "b1", "b2", "b3"}, datos);
    }

    @Test
    @DisplayName("CP-31: la búsqueda binaria encuentra cada elemento y devuelve -1 si no está")
    void busquedaBinaria() {
        Integer[] datos = {3, 8, 15, 23, 42, 57, 91};
        for (int i = 0; i < datos.length; i++) {
            assertEquals(i, Busqueda.binaria(datos, datos[i], Function.identity(), Comparator.naturalOrder()));
        }
        assertEquals(-1, Busqueda.binaria(datos, 50, Function.identity(), Comparator.naturalOrder()));
        assertEquals(-1, Busqueda.binaria(new Integer[0], 1, Function.identity(), Comparator.naturalOrder()));
        assertEquals(3, Busqueda.secuencial(datos, x -> x > 20));
    }
}
