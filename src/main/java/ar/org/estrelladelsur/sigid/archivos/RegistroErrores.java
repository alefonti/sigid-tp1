package ar.org.estrelladelsur.sigid.archivos;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Bitácora de errores técnicos en logs/sigid-errores.log.
 *
 * Al usuario se le muestra un mensaje claro; el detalle técnico (la
 * causa y la traza) queda en el archivo para quien dé soporte. Se abre
 * en modo APPEND para no perder lo registrado en sesiones anteriores.
 *
 * Registrar un error nunca debe provocar otro: si el archivo no se
 * puede escribir, se avisa por la salida de error y el programa sigue.
 */
public final class RegistroErrores {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Path archivo = Path.of("logs", "sigid-errores.log");

    private RegistroErrores() { }

    /** Permite redirigir la bitácora (lo usan las pruebas con una carpeta temporal). */
    public static void usarArchivo(Path destino) {
        archivo = destino;
    }

    public static Path archivo() {
        return archivo;
    }

    public static void registrar(String contexto, Throwable error) {
        StringWriter traza = new StringWriter();
        error.printStackTrace(new PrintWriter(traza));
        String entrada = "[" + LocalDateTime.now().format(FORMATO) + "] " + contexto + System.lineSeparator()
                       + traza + System.lineSeparator();
        try {
            Path carpeta = archivo.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.writeString(archivo, entrada, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir la bitácora de errores: " + e.getMessage());
        }
    }
}
