package ar.org.estrelladelsur.sigid.algoritmos;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Algoritmos de búsqueda.
 *
 *  - binaria: O(log n). Exige que el arreglo esté ordenado por la misma
 *    clave que se busca. Se usa para localizar un jugador por DNI.
 *  - secuencial: O(n). No exige orden y admite cualquier condición; se
 *    usa para buscar por coincidencia parcial de apellido.
 */
public final class Busqueda {

    private Busqueda() { }

    /**
     * Búsqueda binaria iterativa.
     *
     * @param datos     arreglo ORDENADO de forma ascendente según la clave
     * @param clave     valor buscado
     * @param extractor función que obtiene la clave de cada elemento
     * @param orden     criterio de comparación de las claves
     * @return la posición del elemento, o -1 si no está
     */
    public static <T, K> int binaria(T[] datos, K clave,
                                     Function<? super T, ? extends K> extractor,
                                     Comparator<? super K> orden) {
        int bajo = 0;
        int alto = datos.length - 1;
        while (bajo <= alto) {
            int medio = (bajo + alto) >>> 1;
            int cmp = orden.compare(extractor.apply(datos[medio]), clave);
            if (cmp == 0) {
                return medio;
            } else if (cmp < 0) {
                bajo = medio + 1;            // la clave está en la mitad derecha
            } else {
                alto = medio - 1;            // la clave está en la mitad izquierda
            }
        }
        return -1;
    }

    /** Búsqueda secuencial: primera posición que cumple la condición, o -1. */
    public static <T> int secuencial(T[] datos, Predicate<? super T> condicion) {
        for (int i = 0; i < datos.length; i++) {
            if (condicion.test(datos[i])) {
                return i;
            }
        }
        return -1;
    }
}
