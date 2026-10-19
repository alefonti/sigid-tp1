package ar.org.estrelladelsur.sigid.algoritmos;

import java.util.Comparator;

/**
 * Algoritmos de ordenación implementados sin recurrir a Arrays.sort().
 * Son genéricos: reciben un Comparator, de modo que el mismo código
 * ordena jugadores por apellido, por edad o por presentismo.
 *
 *  - mergeSort: O(n log n) en todos los casos y ESTABLE (no altera el
 *    orden relativo de elementos iguales). Se usa para el plantel, donde
 *    dos jugadores con la misma edad deben conservar el orden alfabético.
 *  - quickSort: O(n log n) en promedio, ordena en el lugar sin memoria
 *    extra. Se usa para el ranking de presentismo.
 *  - insercion: O(n²) pero muy eficiente para listas cortas o casi
 *    ordenadas; se usa para la agenda de eventos de una división.
 */
public final class Ordenamiento {

    private Ordenamiento() { }   // clase utilitaria: no se instancia

    // ================================================================ MERGE SORT

    public static <T> void mergeSort(T[] datos, Comparator<? super T> criterio) {
        if (datos == null || datos.length < 2) {
            return;
        }
        @SuppressWarnings("unchecked")
        T[] auxiliar = (T[]) new Object[datos.length];
        mergeSort(datos, auxiliar, 0, datos.length - 1, criterio);
    }

    private static <T> void mergeSort(T[] datos, T[] aux, int izq, int der,
                                      Comparator<? super T> criterio) {
        if (izq >= der) {
            return;                                   // caso base: un solo elemento
        }
        int medio = (izq + der) >>> 1;
        mergeSort(datos, aux, izq, medio, criterio);  // ordena mitad izquierda
        mergeSort(datos, aux, medio + 1, der, criterio); // ordena mitad derecha
        intercalar(datos, aux, izq, medio, der, criterio);
    }

    private static <T> void intercalar(T[] datos, T[] aux, int izq, int medio, int der,
                                       Comparator<? super T> criterio) {
        for (int k = izq; k <= der; k++) {
            aux[k] = datos[k];
        }
        int i = izq, j = medio + 1;
        for (int k = izq; k <= der; k++) {
            if (i > medio) {
                datos[k] = aux[j++];
            } else if (j > der) {
                datos[k] = aux[i++];
            } else if (criterio.compare(aux[j], aux[i]) < 0) {
                datos[k] = aux[j++];
            } else {
                datos[k] = aux[i++];                  // "<=" mantiene la estabilidad
            }
        }
    }

    // ================================================================ QUICK SORT

    public static <T> void quickSort(T[] datos, Comparator<? super T> criterio) {
        if (datos == null || datos.length < 2) {
            return;
        }
        quickSort(datos, 0, datos.length - 1, criterio);
    }

    private static <T> void quickSort(T[] datos, int bajo, int alto, Comparator<? super T> criterio) {
        while (bajo < alto) {
            int p = particionar(datos, bajo, alto, criterio);
            // se recursa sobre la parte más chica para acotar la profundidad de la pila
            if (p - bajo < alto - p) {
                quickSort(datos, bajo, p - 1, criterio);
                bajo = p + 1;
            } else {
                quickSort(datos, p + 1, alto, criterio);
                alto = p - 1;
            }
        }
    }

    /** Partición de Lomuto con pivote en la mediana de tres. */
    private static <T> int particionar(T[] datos, int bajo, int alto, Comparator<? super T> criterio) {
        int medio = (bajo + alto) >>> 1;
        if (criterio.compare(datos[medio], datos[bajo]) < 0) intercambiar(datos, medio, bajo);
        if (criterio.compare(datos[alto],  datos[bajo]) < 0) intercambiar(datos, alto, bajo);
        if (criterio.compare(datos[medio], datos[alto]) < 0) intercambiar(datos, medio, alto);
        T pivote = datos[alto];

        int i = bajo - 1;
        for (int j = bajo; j < alto; j++) {
            if (criterio.compare(datos[j], pivote) <= 0) {
                intercambiar(datos, ++i, j);
            }
        }
        intercambiar(datos, i + 1, alto);
        return i + 1;
    }

    // ================================================================ INSERCIÓN

    public static <T> void insercion(T[] datos, Comparator<? super T> criterio) {
        if (datos == null) {
            return;
        }
        for (int i = 1; i < datos.length; i++) {
            T actual = datos[i];
            int j = i - 1;
            while (j >= 0 && criterio.compare(datos[j], actual) > 0) {
                datos[j + 1] = datos[j];             // desplaza a la derecha
                j--;
            }
            datos[j + 1] = actual;
        }
    }

    private static <T> void intercambiar(T[] datos, int a, int b) {
        T tmp = datos[a];
        datos[a] = datos[b];
        datos[b] = tmp;
    }
}
