package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.algoritmos.Ordenamiento;
import ar.org.estrelladelsur.sigid.estructuras.ListaEnlazada;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.Persona;
import ar.org.estrelladelsur.sigid.persistencia.DivisionDAO;
import ar.org.estrelladelsur.sigid.persistencia.JugadorDAO;
import ar.org.estrelladelsur.sigid.persistencia.PersonalDAO;
import ar.org.estrelladelsur.sigid.persistencia.TutorDAO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Datos institucionales: divisiones y padrón general de personas.
 *
 * El método personasDelClub() es el ejemplo central de POLIMORFISMO del
 * prototipo: arma una única lista de Persona con jugadores, tutores,
 * entrenadores y profesionales; al recorrerla, cada objeto responde a
 * getRol() y getDetalle() con su propia implementación.
 */
public class ClubService {

    private final DivisionDAO divisionDAO;
    private final JugadorDAO  jugadorDAO;
    private final TutorDAO    tutorDAO;
    private final PersonalDAO personalDAO;

    public ClubService(DivisionDAO divisionDAO, JugadorDAO jugadorDAO,
                       TutorDAO tutorDAO, PersonalDAO personalDAO) {
        this.divisionDAO = divisionDAO;
        this.jugadorDAO  = jugadorDAO;
        this.tutorDAO    = tutorDAO;
        this.personalDAO = personalDAO;
    }

    public List<Division> divisiones() {
        return divisionDAO.listarTodas();
    }

    public Division division(int idDivision) {
        Division d = divisionDAO.buscarPorId(idDivision);
        if (d == null) {
            throw new ReglaNegocioException("RN-07", "No existe la división " + idDivision + ".");
        }
        return d;
    }

    /** Padrón de todas las personas del club, en orden alfabético (merge sort). */
    public Persona[] personasDelClub() {
        ListaEnlazada<Persona> todas = new ListaEnlazada<>();
        personalDAO.listarEntrenadores().forEach(todas::agregar);
        personalDAO.listarProfesionales().forEach(todas::agregar);
        tutorDAO.listarTodos().forEach(todas::agregar);
        jugadorDAO.listarTodos().forEach(todas::agregar);

        Persona[] arreglo = todas.aArreglo(new Persona[0]);
        Ordenamiento.mergeSort(arreglo, Persona::compareTo);   // compareTo heredado de Persona
        return arreglo;
    }

    /** Cantidad de personas por rol, en el orden en que aparecen. */
    public Map<String, Integer> resumenPorRol(Persona[] personas) {
        Map<String, Integer> resumen = new LinkedHashMap<>();
        for (Persona p : personas) {
            resumen.merge(p.getRol(), 1, Integer::sum);
        }
        return resumen;
    }
}
