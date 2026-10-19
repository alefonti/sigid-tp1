package ar.org.estrelladelsur.sigid.servicio.historial;

import ar.org.estrelladelsur.sigid.estructuras.Pila;

import java.util.ArrayList;
import java.util.List;

/**
 * Historial de la sesión basado en una PILA (LIFO).
 * Guarda hasta 20 operaciones; deshacer siempre revierte la más reciente.
 */
public class HistorialOperaciones {

    public static final int CAPACIDAD = 20;

    private final Pila<Operacion> pila = new Pila<>(CAPACIDAD);

    public void registrar(Operacion operacion) {
        pila.apilar(operacion);
    }

    /**
     * Desapila y revierte la última operación.
     *
     * @throws ar.org.estrelladelsur.sigid.estructuras.EstructuraVaciaException si no hay nada para deshacer
     */
    public Operacion deshacerUltima() {
        Operacion ultima = pila.desapilar();
        ultima.deshacer();          // polimorfismo: cada subclase se revierte a su manera
        return ultima;
    }

    /** Operaciones de la más reciente a la más antigua. */
    public List<Operacion> listar() {
        List<Operacion> lista = new ArrayList<>();
        for (Operacion op : pila) {
            lista.add(op);
        }
        return lista;
    }

    public boolean estaVacio() { return pila.estaVacia(); }
    public int     tamanio()   { return pila.tamanio(); }
}
