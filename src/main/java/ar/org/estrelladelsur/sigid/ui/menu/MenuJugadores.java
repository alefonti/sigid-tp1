package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.estructuras.ListaEnlazada;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.EstadoJugador;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.servicio.CriterioOrden;
import ar.org.estrelladelsur.sigid.servicio.historial.OperacionBajaJugador;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.time.LocalDate;

/** Submenú del CU01 - Gestionar jugador y CU07 - Consultar plantel. */
public class MenuJugadores extends MenuConsola {

    public MenuJugadores(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() { return "JUGADORES"; }

    @Override protected String[] opciones() {
        return new String[] {
            "Registrar jugador                      (CU01)",
            "Ver plantel ordenado                   (CU07 - merge sort)",
            "Buscar jugador por DNI                 (búsqueda binaria)",
            "Buscar jugadores por apellido          (búsqueda secuencial)",
            "Dar de baja un jugador                 (CU01 - se puede deshacer)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        switch (opcion) {
            case 1 -> registrar();
            case 2 -> verPlantel();
            case 3 -> buscarPorDni();
            case 4 -> buscarPorApellido();
            case 5 -> darDeBaja();
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    private void registrar() throws DatoInvalidoException {
        Division division = elegirDivision();
        String documento  = in.conReintentos(() -> in.leerTextoObligatorio("Documento (sin puntos)"));
        String apellido   = in.leerTextoObligatorio("Apellido");
        String nombre     = in.leerTextoObligatorio("Nombre");
        LocalDate nac     = in.conReintentos(() -> in.leerFecha("Fecha de nacimiento"));

        // Creación del objeto con su constructor: los setters validan el formato
        Jugador nuevo = new Jugador(documento, apellido, nombre, nac, division.getIdDivision());
        int id = ctx.jugadores().registrar(nuevo, division);
        System.out.println("  [OK] " + nuevo.nombreCompleto() + " quedó registrado en "
                         + division.getNombre() + " con el id " + id + ".");
    }

    private void verPlantel() throws DatoInvalidoException {
        Division division = elegirDivision();
        CriterioOrden[] criterios = CriterioOrden.values();
        System.out.println("  Ordenar por:");
        for (int i = 0; i < criterios.length; i++) {
            System.out.printf("    %d) %s%n", i + 1, criterios[i].getEtiqueta());
        }
        int elegido = in.leerEntero("Criterio", 1, criterios.length);
        CriterioOrden criterio = criterios[elegido - 1];

        Jugador[] plantel = ctx.jugadores().plantelOrdenado(division.getIdDivision(), criterio);
        if (plantel.length == 0) {
            System.out.println("  La división no tiene jugadores activos.");
            return;
        }
        System.out.printf("%n  %s | %d jugadores | orden: %s%n", division.getNombre(),
                          plantel.length, criterio.getEtiqueta());
        System.out.printf("  %-4s %-26s %-10s %-11s %-5s %s%n",
                          "ID", "JUGADOR", "DNI", "NACIMIENTO", "EDAD", "ASIST.");
        LocalDate hoy = ctx.hoy();
        for (Jugador j : plantel) {
            System.out.printf("  %-4d %-26s %-10s %-11s %-5d %5.1f %%%n",
                    j.getIdJugador(), j.nombreCompleto(), j.getDocumento(),
                    j.getFechaNacimiento(), j.edadA(hoy),
                    ctx.asistencias().porcentajeAsistencia(j.getIdJugador()));
        }
    }

    private void buscarPorDni() throws DatoInvalidoException {
        String dni = in.leerTextoObligatorio("DNI a buscar");
        Jugador j = ctx.jugadores().buscarPorDocumento(dni);
        if (j == null) {
            System.out.println("  No hay ningún jugador con el DNI " + dni + ".");
            return;
        }
        Division d = ctx.club().division(j.getIdDivision());
        System.out.println("  Encontrado: " + j.ficha());
        System.out.println("  División " + d.getNombre() + " | alta " + j.getFechaAlta()
                         + " | presentismo " + ctx.asistencias().porcentajeAsistencia(j.getIdJugador()) + " %");
    }

    private void buscarPorApellido() throws DatoInvalidoException {
        String fragmento = in.leerTextoObligatorio("Apellido o parte del apellido");
        ListaEnlazada<Jugador> encontrados = ctx.jugadores().buscarPorApellido(fragmento);
        if (encontrados.estaVacia()) {
            System.out.println("  Sin coincidencias para \"" + fragmento + "\".");
            return;
        }
        System.out.println("  " + encontrados.tamanio() + " coincidencia(s):");
        for (Jugador j : encontrados) {
            System.out.println("   " + j.ficha());
        }
    }

    private void darDeBaja() throws DatoInvalidoException {
        int id = in.leerEntero("Id del jugador");
        Jugador j = ctx.jugadores().obtener(id);
        EstadoJugador estadoPrevio = j.getEstado();
        if (!in.confirmar("¿Confirma la baja de " + j.nombreCompleto() + "?")) {
            System.out.println("  Baja cancelada.");
            return;
        }
        ctx.jugadores().darDeBaja(id);
        ctx.historial().registrar(new OperacionBajaJugador(ctx.jugadores(), j, estadoPrevio));
        System.out.println("  [OK] " + j.nombreCompleto() + " fue dado de baja. "
                         + "Puede revertirlo con \"Deshacer última operación\".");
    }
}
