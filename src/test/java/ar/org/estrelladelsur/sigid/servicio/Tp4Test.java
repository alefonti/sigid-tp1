package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.archivos.ArchivoException;
import ar.org.estrelladelsur.sigid.archivos.RegistroErrores;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.FilaPresentismo;
import ar.org.estrelladelsur.sigid.persistencia.PersistenciaException;
import ar.org.estrelladelsur.sigid.persistencia.memoria.FabricaDAOMemoria;
import ar.org.estrelladelsur.sigid.servicio.ArchivoService.ResultadoImportacion;
import ar.org.estrelladelsur.sigid.servicio.ReporteService.ReportePresentismo;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de las funcionalidades del TP4. Usan la fábrica en memoria,
 * que arranca con los mismos datos que sql/02_datos.sql.
 */
class Tp4Test {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 8);

    private Contexto ctx;
    private Division sub13;

    @TempDir Path carpeta;

    @BeforeEach
    void preparar() {
        ctx   = new Contexto(new FabricaDAOMemoria());
        sub13 = ctx.club().division(1);
        RegistroErrores.usarArchivo(carpeta.resolve("logs/errores.log"));
    }

    @Test
    @DisplayName("CP-41: FilaPresentismo cuenta por estado en un arreglo y calcula el porcentaje")
    void filaPresentismo() {
        FilaPresentismo f = new FilaPresentismo(1, "Acuña", "Tomás");
        f.sumar(EstadoAsistencia.PRESENTE, 2);
        f.sumar(EstadoAsistencia.TARDE, 1);
        f.sumar(EstadoAsistencia.AUSENTE, 1);

        assertEquals(4, f.total());
        assertEquals(75.0, f.porcentaje());
        int[] copia = f.getConteo();
        copia[0] = 99;                                   // copia defensiva
        assertEquals(2, f.cantidad(EstadoAsistencia.PRESENTE));
        assertThrows(IllegalArgumentException.class, () -> f.sumar(EstadoAsistencia.AUSENTE, -1));
    }

    @Test
    @DisplayName("CP-42: el reporte queda ordenado de mayor a menor y los totales cuadran")
    void reportePresentismo() {
        ReportePresentismo r = ctx.reportes().presentismo(sub13);
        FilaPresentismo[] filas = r.filas();

        assertEquals(ctx.jugadores().buscarPorDivision(1).size(), filas.length);
        for (int i = 1; i < filas.length; i++) {
            assertTrue(filas[i - 1].porcentaje() >= filas[i].porcentaje());
        }
        int sumaFilas = 0;
        for (FilaPresentismo f : filas) sumaFilas += f.total();
        int sumaTotales = 0;
        for (int t : r.totalesPorEstado()) sumaTotales += t;
        assertEquals(sumaFilas, sumaTotales);

        for (FilaPresentismo f : r.enRiesgo()) {
            assertTrue(f.porcentaje() < AsistenciaService.UMBRAL_ALERTA);
        }
    }

    @Test
    @DisplayName("CP-43: cerrar un entrenamiento registra ausentes y lo finaliza (RN-11, RN-12)")
    void cerrarEntrenamiento() {
        // el entrenamiento del 22/10 (id 8) quedó con la asistencia cargada a medias
        Evento entrenamiento = ctx.agenda().obtener(8);
        assertTrue(ctx.reportes().pendientesDeCierre(sub13, HOY).contains(entrenamiento));

        int ausentes = ctx.reportes().cerrarEvento(entrenamiento.getIdEvento(), HOY);

        assertEquals(2, ausentes);                       // faltaban Cabrera (3) y Ortiz (16)
        assertEquals(EstadoAsistencia.AUSENTE, ctx.fabrica().asistencias().listarPorJugador(16).stream()
                .filter(a -> a.getIdEvento() == 8).findFirst().orElseThrow().getEstado());
        assertEquals("FINALIZADO", ctx.agenda().obtener(entrenamiento.getIdEvento()).getEstado());
        assertFalse(ctx.reportes().pendientesDeCierre(sub13, HOY).contains(entrenamiento));

        ReglaNegocioException otraVez = assertThrows(ReglaNegocioException.class,
                () -> ctx.reportes().cerrarEvento(entrenamiento.getIdEvento(), HOY));
        assertEquals("RN-12", otraVez.getCodigoRegla());
    }

    @Test
    @DisplayName("CP-44: no se puede cerrar un evento que todavía no se jugó (RN-11)")
    void cerrarEventoFuturo() {
        Evento cualquiera = ctx.reportes().pendientesDeCierre(sub13, HOY).get(0);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> ctx.reportes().cerrarEvento(cualquiera.getIdEvento(), cualquiera.getFecha()));
        assertEquals("RN-11", e.getCodigoRegla());
    }

    @Test
    @DisplayName("CP-45: exportar el padrón y volver a leerlo devuelve los mismos jugadores")
    void exportarPadron() throws IOException {
        Path destino = carpeta.resolve("reportes/padron.csv");
        int escritos = ctx.archivos().exportarPadron(destino);

        List<String> lineas = Files.readAllLines(destino, StandardCharsets.UTF_8);
        assertEquals(escritos + 1, lineas.size());       // + encabezado
        assertTrue(lineas.get(0).endsWith("documento;apellido;nombre;fecha_nacimiento;id_division;estado"));
        assertTrue(lineas.stream().anyMatch(l -> l.startsWith("55123456;Acuña;Tomás;14/03/2013;1;")));
    }

    @Test
    @DisplayName("CP-46: la importación registra las líneas válidas y explica cada rechazo")
    void importarJugadores() throws IOException {
        Path origen = carpeta.resolve("nuevos.csv");
        Files.writeString(origen, String.join("\n",
                "documento;apellido;nombre;fecha_nacimiento;id_division;estado",
                "56111222;Ibarra;Facundo;14/02/2013;1;",           // válida
                "56222333;Ledesma;Ciro;20/08/2012;1;",             // RN-01 categoría
                "55123456;Acuña;Tomás;14/03/2013;1;",              // RN-06 duplicado
                "5611.222;Molina;Gael;10/10/2013;1;",              // documento con punto
                "56333444;Navarro;Lautaro;31/02/2013;1;",          // fecha inexistente
                "56444555;Sosa;Valentín;2013-05-05"),             // faltan columnas
                StandardCharsets.UTF_8);

        ResultadoImportacion r = ctx.archivos().importarJugadores(origen);

        assertEquals(6, r.leidas());
        assertEquals(1, r.registrados());
        assertEquals(5, r.errores().size());
        assertNotNull(ctx.jugadores().buscarPorDocumento("56111222"));
        assertTrue(r.errores().stream().anyMatch(m -> m.contains("RN-01")));
        assertTrue(r.errores().stream().anyMatch(m -> m.contains("RN-06")));
        assertTrue(r.errores().stream().anyMatch(m -> m.contains("Línea 7")));
    }

    @Test
    @DisplayName("CP-47: un archivo inexistente lanza ArchivoException con la causa original")
    void archivoInexistente() {
        ArchivoException e = assertThrows(ArchivoException.class,
                () -> ctx.archivos().importarJugadores(carpeta.resolve("no-existe.csv")));
        assertInstanceOf(java.nio.file.NoSuchFileException.class, e.getCause());
    }

    @Test
    @DisplayName("CP-48: la bitácora agrega cada error al final del archivo")
    void bitacoraDeErrores() throws IOException {
        RegistroErrores.registrar("prueba 1", new PersistenciaException("falla simulada", null));
        RegistroErrores.registrar("prueba 2", new IllegalStateException("otra falla"));

        String contenido = Files.readString(RegistroErrores.archivo(), StandardCharsets.UTF_8);
        assertTrue(contenido.indexOf("prueba 1") < contenido.indexOf("prueba 2"));
        assertTrue(contenido.contains("falla simulada"));
    }
}
