package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.Persona;
import ar.org.estrelladelsur.sigid.servicio.historial.Operacion;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.util.List;
import java.util.Map;

/** Menú de selección principal del prototipo. */
public class MenuPrincipal extends MenuConsola {

    public MenuPrincipal(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() {
        return "SIGID v3 - Club Social y Deportivo \"Estrella del Sur\"\n  Datos: "
             + ctx.fabrica().descripcion() + " | Fecha de trabajo: " + ctx.hoy();
    }

    @Override protected String[] opciones() {
        return new String[] {
            "Jugadores",
            "Agenda deportiva",
            "Asistencia",
            "Convocatorias",
            "Área médica",
            "Notificaciones a tutores (" + ctx.notificaciones().cantidadPendientes() + " en cola)",
            "Padrón general de personas            (polimorfismo)",
            "Historial de la sesión (" + ctx.historial().tamanio() + ")",
            "Deshacer última operación              (pila LIFO)"
        };
    }

    @Override protected String textoSalida() { return "Salir"; }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        // POLIMORFISMO: la variable es del tipo abstracto MenuConsola; mostrar()
        // ejecuta el ciclo común y despacha al ejecutarOpcion() de cada subclase.
        MenuConsola submenu = switch (opcion) {
            case 1 -> new MenuJugadores(ctx, in);
            case 2 -> new MenuAgenda(ctx, in);
            case 3 -> new MenuAsistencia(ctx, in);
            case 4 -> new MenuConvocatorias(ctx, in);
            case 5 -> new MenuAreaMedica(ctx, in);
            case 6 -> new MenuNotificaciones(ctx, in);
            default -> null;
        };
        if (submenu != null) {
            submenu.mostrar();
            return;
        }
        switch (opcion) {
            case 7 -> padronGeneral();
            case 8 -> verHistorial();
            case 9 -> deshacer();
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    private void padronGeneral() {
        Persona[] personas = ctx.club().personasDelClub();
        System.out.printf("  %-13s %-26s %-13s %s%n", "ROL", "APELLIDO Y NOMBRE", "DOCUMENTO", "DETALLE");
        for (Persona p : personas) {
            System.out.println("  " + p.ficha());       // cada subclase aporta su rol y su detalle
        }
        Map<String, Integer> resumen = ctx.club().resumenPorRol(personas);
        System.out.println("  Total: " + personas.length + " personas " + resumen);
    }

    private void verHistorial() {
        List<Operacion> ops = ctx.historial().listar();
        if (ops.isEmpty()) {
            System.out.println("  Todavía no se realizaron operaciones reversibles en esta sesión.");
            return;
        }
        System.out.println("  Operaciones (de la más reciente a la más antigua):");
        for (Operacion op : ops) {
            System.out.println("   - " + op);
        }
    }

    private void deshacer() throws DatoInvalidoException {
        if (ctx.historial().estaVacio()) {
            System.out.println("  No hay operaciones para deshacer.");
            return;
        }
        Operacion ultima = ctx.historial().listar().get(0);
        if (in.confirmar("¿Deshacer \"" + ultima.descripcion() + "\"?")) {
            Operacion revertida = ctx.historial().deshacerUltima();
            System.out.println("  [OK] Se revirtió: " + revertida.descripcion());
        }
    }
}
