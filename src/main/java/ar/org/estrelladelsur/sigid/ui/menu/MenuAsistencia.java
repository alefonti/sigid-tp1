package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.servicio.AsistenciaService;
import ar.org.estrelladelsur.sigid.servicio.AsistenciaService.Presentismo;
import ar.org.estrelladelsur.sigid.servicio.ReglaNegocioException;
import ar.org.estrelladelsur.sigid.servicio.historial.OperacionAsistencia;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.util.ArrayList;
import java.util.List;

/** Submenú del CU03 - Registrar asistencia y CU11 - Justificar inasistencia. */
public class MenuAsistencia extends MenuConsola {

    public MenuAsistencia(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() { return "ASISTENCIA"; }

    @Override protected String[] opciones() {
        return new String[] {
            "Tomar asistencia de un evento          (CU03 + CU11)",
            "Ranking de presentismo                 (quick sort)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        switch (opcion) {
            case 1 -> tomarAsistencia();
            case 2 -> ranking();
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    private void tomarAsistencia() throws DatoInvalidoException {
        Evento evento = ctx.agenda().obtener(in.leerEntero("Id del evento"));
        List<Jugador> plantel = ctx.jugadores().buscarPorDivision(evento.getIdDivision());
        System.out.println("  " + evento);
        System.out.println("  Estados: P = presente, A = ausente, T = tarde, J = justificado");

        List<Integer> registrados = new ArrayList<>();
        int i = 0;
        while (i < plantel.size()) {                                  // estructura while
            Jugador j = plantel.get(i);
            String opcion = in.leerTexto("  " + j.nombreCompleto()).toUpperCase();
            EstadoAsistencia estado = switch (opcion) {
                case "P", "" -> EstadoAsistencia.PRESENTE;
                case "A"     -> EstadoAsistencia.AUSENTE;
                case "T"     -> EstadoAsistencia.TARDE;
                case "J"     -> EstadoAsistencia.JUSTIFICADO;
                default      -> null;
            };
            if (estado == null) {
                System.out.println("    [!] Opción inválida, se vuelve a preguntar.");
                continue;                                   // no avanza el índice
            }
            try {
                int idAsistencia = ctx.asistencias().registrar(evento.getIdEvento(), j.getIdJugador(), estado);
                registrados.add(idAsistencia);
                // CU11 «extend»: sólo cuando el jugador falta o tiene justificación
                if (estado == EstadoAsistencia.AUSENTE || estado == EstadoAsistencia.JUSTIFICADO) {
                    String motivo = in.leerTexto("    Motivo (Enter para omitir)");
                    if (!motivo.isBlank()) {
                        ctx.asistencias().justificarInasistencia(idAsistencia, motivo);
                    }
                }
            } catch (ReglaNegocioException e) {
                System.out.println("    " + e.getMessage());   // sigue con el próximo jugador
            }
            i++;
        }
        if (!registrados.isEmpty()) {
            ctx.historial().registrar(new OperacionAsistencia(ctx.asistencias(), evento.getIdEvento(), registrados));
        }
        System.out.println("  [OK] Se registraron " + registrados.size() + " asistencias.");
    }

    private void ranking() throws DatoInvalidoException {
        Division d = elegirDivision();
        Presentismo[] filas = ctx.asistencias().ranking(ctx.jugadores().buscarPorDivision(d.getIdDivision()));
        System.out.printf("%n  Ranking de presentismo %s%n", d.getNombre());
        System.out.printf("  %-3s %-26s %-8s %s%n", "#", "JUGADOR", "EVENTOS", "PRESENTISMO");
        int alertas = 0;
        for (int pos = 0; pos < filas.length; pos++) {                 // estructura for
            Presentismo p = filas[pos];
            boolean alerta = p.eventos() > 0 && p.porcentaje() < AsistenciaService.UMBRAL_ALERTA;
            if (alerta) alertas++;
            System.out.printf("  %-3d %-26s %-8d %5.1f %% %s%n", pos + 1, p.jugador().nombreCompleto(),
                    p.eventos(), p.porcentaje(),
                    p.eventos() == 0 ? "(sin registros)" : alerta ? "<- ALERTA" : "");
        }
        System.out.println("  Jugadores debajo del " + AsistenciaService.UMBRAL_ALERTA + " %: " + alertas);
    }
}
