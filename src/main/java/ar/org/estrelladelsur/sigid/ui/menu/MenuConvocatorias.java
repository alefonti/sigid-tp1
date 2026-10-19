package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.Convocatoria;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.modelo.ResultadoConvocatoria;
import ar.org.estrelladelsur.sigid.servicio.ReglaNegocioException;
import ar.org.estrelladelsur.sigid.servicio.historial.OperacionConvocatoria;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.util.ArrayList;
import java.util.List;

/** Submenú del CU05 - Generar convocatoria (incluye CU10 y CU06). */
public class MenuConvocatorias extends MenuConsola {

    public MenuConvocatorias(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() { return "CONVOCATORIAS"; }

    @Override protected String[] opciones() {
        return new String[] {
            "Generar convocatoria a un partido      (CU05)",
            "Ver convocados de un partido"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        switch (opcion) {
            case 1 -> generar();
            case 2 -> verConvocados();
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    private void generar() throws DatoInvalidoException {
        Evento evento = ctx.agenda().obtener(in.leerEntero("Id del partido"));
        System.out.println("  " + evento);
        if (!evento.admiteConvocatoria()) {          // pregunta polimórfica al objeto
            throw new ReglaNegocioException("RN-11",
                "El evento elegido es un " + evento.getTipo() + "; sólo se convoca a partidos.");
        }

        List<Jugador> plantel = ctx.jugadores().buscarPorDivision(evento.getIdDivision());
        List<Integer> aptos = new ArrayList<>();
        System.out.printf("  %-4s %-26s %s%n", "ID", "JUGADOR", "APTITUD (CU10)");
        for (Jugador j : plantel) {
            String motivo = ctx.fichasMedicas().motivoDeBloqueo(j.getIdJugador(), evento.getFecha());
            if (motivo == null) {
                aptos.add(j.getIdJugador());
            }
            System.out.printf("  %-4d %-26s %s%n", j.getIdJugador(), j.nombreCompleto(),
                              motivo == null ? "apto" : "NO APTO: " + motivo);
        }

        List<Integer> seleccion;
        if (in.confirmar("¿Convocar a todos los aptos (" + aptos.size() + ")?")) {
            seleccion = aptos;
        } else {
            seleccion = in.leerListaEnteros("Ids a convocar, separados por coma");
        }

        ResultadoConvocatoria r = ctx.convocatorias().convocar(evento, seleccion, evento.getFecha());
        System.out.println("\n  " + r);
        r.getRechazados().forEach((id, motivo) ->
                System.out.println("  Bloqueado el jugador " + id + ": " + motivo));
        if (r.cantidadConvocados() > 0) {
            ctx.historial().registrar(
                    new OperacionConvocatoria(ctx.convocatorias(), evento.getIdEvento(), r.getConvocados()));
        }
        System.out.println("  Avisos en cola de notificaciones: " + ctx.notificaciones().cantidadPendientes());

        ctx.convocatorias().confirmarLista(evento.getIdEvento());     // RN-05
        System.out.println("  [OK] Lista confirmada: alcanza el mínimo para disputar el partido.");
    }

    private void verConvocados() throws DatoInvalidoException {
        Evento evento = ctx.agenda().obtener(in.leerEntero("Id del partido"));
        List<Convocatoria> lista = ctx.convocatorias().convocadosDe(evento.getIdEvento());
        System.out.println("  " + evento + " | " + lista.size() + " convocados");
        for (Convocatoria c : lista) {
            Jugador j = ctx.jugadores().buscarPorId(c.getIdJugador());
            System.out.printf("   - %-26s %s%n", j.nombreCompleto(), c.isConfirmada() ? "confirmado" : "pendiente");
        }
    }
}
