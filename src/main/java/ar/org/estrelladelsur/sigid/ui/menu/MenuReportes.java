package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.FilaPresentismo;
import ar.org.estrelladelsur.sigid.servicio.ArchivoService.ResultadoImportacion;
import ar.org.estrelladelsur.sigid.servicio.AsistenciaService;
import ar.org.estrelladelsur.sigid.servicio.ReporteService.ReportePresentismo;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.nio.file.Path;
import java.util.List;

/** TP4 - Submenú de reportes, cierre de eventos y archivos (CU12 a CU15). */
public class MenuReportes extends MenuConsola {

    static final Path CARPETA_REPORTES = Path.of("reportes");
    static final Path IMPORTACION_POR_DEFECTO = Path.of("importar", "jugadores_nuevos.csv");

    public MenuReportes(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() { return "REPORTES, CIERRE DE EVENTOS Y ARCHIVOS"; }

    @Override protected String[] opciones() {
        return new String[] {
            "Reporte de presentismo por división      (CU12)",
            "Cerrar eventos ya realizados             (CU13)",
            "Exportar presentismo a CSV               (CU14)",
            "Exportar padrón de jugadores a CSV       (CU14)",
            "Importar inscripciones desde CSV         (CU15)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        switch (opcion) {
            case 1 -> mostrarPresentismo(ctx.reportes().presentismo(elegirDivision()));
            case 2 -> cerrarEventos();
            case 3 -> exportarPresentismo();
            case 4 -> exportarPadron();
            case 5 -> importarJugadores();
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    // ================================================================ CU12

    private void mostrarPresentismo(ReportePresentismo r) {
        EstadoAsistencia[] estados = EstadoAsistencia.values();
        System.out.println("  Presentismo - " + r.division().getNombre()
                         + " (ordenado de mayor a menor, merge sort)");
        System.out.printf("  %-4s %-26s", "#", "JUGADOR");
        for (EstadoAsistencia e : estados) {
            System.out.printf(" %5s", e.name().substring(0, 4));
        }
        System.out.printf(" %7s%n", "%");

        FilaPresentismo[] filas = r.filas();
        for (int i = 0; i < filas.length; i++) {
            FilaPresentismo f = filas[i];
            System.out.printf("  %-4d %-26s", i + 1, f.getNombreCompleto());
            for (EstadoAsistencia e : estados) {
                System.out.printf(" %5d", f.cantidad(e));
            }
            String marca = f.total() == 0 ? "  (sin registros)"
                         : f.porcentaje() < AsistenciaService.UMBRAL_ALERTA ? "  <- en riesgo" : "";
            System.out.printf(" %6.1f%%%s%n", f.porcentaje(), marca);
        }
        System.out.printf("  %-4s %-26s", "", "TOTAL DIVISIÓN");
        for (int total : r.totalesPorEstado()) {
            System.out.printf(" %5d", total);
        }
        System.out.printf(" %6.1f%% promedio%n", r.promedio());

        List<FilaPresentismo> riesgo = r.enRiesgo();
        if (riesgo.isEmpty()) {
            System.out.println("  Ningún jugador está por debajo del " + AsistenciaService.UMBRAL_ALERTA + "%.");
        } else {
            System.out.println("  " + riesgo.size() + " jugador(es) por debajo del "
                             + AsistenciaService.UMBRAL_ALERTA + "%: " + riesgo);
        }
    }

    // ================================================================ CU13

    private void cerrarEventos() throws DatoInvalidoException {
        Division division = elegirDivision();
        List<Evento> pendientes = ctx.reportes().pendientesDeCierre(division, ctx.hoy());
        if (pendientes.isEmpty()) {
            System.out.println("  " + division.getNombre() + " no tiene eventos realizados sin cerrar.");
            return;
        }
        System.out.println("  Eventos ya realizados que siguen abiertos:");
        for (Evento e : pendientes) {
            System.out.println("    " + e);
        }
        int id = in.leerEntero("Id del evento a cerrar");
        if (in.confirmar("Los jugadores sin asistencia cargada quedarán AUSENTES. ¿Confirma?")) {
            int ausentes = ctx.reportes().cerrarEvento(id, ctx.hoy());
            System.out.println("  [OK] Evento " + id + " FINALIZADO. Ausencias registradas: " + ausentes + ".");
        }
    }

    // ================================================================ CU14

    private void exportarPresentismo() throws DatoInvalidoException {
        ReportePresentismo r = ctx.reportes().presentismo(elegirDivision());
        String nombre = "presentismo_" + r.division().getNombre().toLowerCase() + "_" + ctx.hoy() + ".csv";
        Path destino = CARPETA_REPORTES.resolve(nombre);
        int filas = ctx.archivos().exportarPresentismo(r, destino);
        System.out.println("  [OK] Se exportaron " + filas + " jugadores a " + destino);
    }

    private void exportarPadron() {
        Path destino = CARPETA_REPORTES.resolve("padron_jugadores_" + ctx.hoy() + ".csv");
        int filas = ctx.archivos().exportarPadron(destino);
        System.out.println("  [OK] Se exportaron " + filas + " jugadores a " + destino);
    }

    // ================================================================ CU15

    private void importarJugadores() {
        String ruta = in.leerTexto("Archivo a importar (Enter = " + IMPORTACION_POR_DEFECTO + ")");
        Path origen = ruta.isBlank() ? IMPORTACION_POR_DEFECTO : Path.of(ruta);
        ResultadoImportacion res = ctx.archivos().importarJugadores(origen);
        System.out.println("  Líneas procesadas: " + res.leidas()
                         + " | registrados: " + res.registrados()
                         + " | rechazados: " + res.errores().size());
        for (String error : res.errores()) {
            System.out.println("   - " + error);
        }
    }
}
