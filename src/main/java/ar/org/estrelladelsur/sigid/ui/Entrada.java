package ar.org.estrelladelsur.sigid.ui;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.comun.SigidException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lectura y conversión de los datos que tipea el usuario.
 *
 * Toda conversión que puede fallar (texto a número, a fecha o a hora)
 * captura la excepción estándar de Java (NumberFormatException,
 * DateTimeParseException) y la relanza como DatoInvalidoException, que es
 * verificada: el compilador obliga a cada menú a tratarla.
 */
public class Entrada {

    /** Se lanza cuando se agota la entrada estándar (por ejemplo, al usar un guion de prueba). */
    public static class FinDeEntradaException extends SigidException {
        public FinDeEntradaException() { super("Se terminó la entrada de datos."); }
    }

    /** Lector que puede fallar; permite reutilizar la lógica de reintentos. */
    @FunctionalInterface
    public interface Lector<T> {
        T leer() throws DatoInvalidoException;
    }

    public static final int MAX_INTENTOS = 3;
    private static final DateTimeFormatter FECHA_AR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner sc;
    private final boolean eco;   // repite lo leído cuando la entrada no es una terminal

    public Entrada(Scanner sc) {
        this.sc  = sc;
        this.eco = System.console() == null;
    }

    // ================================================================ lecturas básicas

    public String leerTexto(String etiqueta) {
        System.out.print(etiqueta + ": ");
        if (!sc.hasNextLine()) {
            throw new FinDeEntradaException();
        }
        String linea = sc.nextLine().trim();
        if (eco) {
            System.out.println(linea);
        }
        return linea;
    }

    public String leerTextoObligatorio(String etiqueta) throws DatoInvalidoException {
        String valor = leerTexto(etiqueta);
        if (valor.isEmpty()) {
            throw new DatoInvalidoException(etiqueta, "el campo es obligatorio.");
        }
        return valor;
    }

    public int leerEntero(String etiqueta) throws DatoInvalidoException {
        String valor = leerTexto(etiqueta);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException(etiqueta, "'" + valor + "' no es un número entero.", e);
        }
    }

    /** Entero dentro de un rango cerrado [min, max]. */
    public int leerEntero(String etiqueta, int min, int max) throws DatoInvalidoException {
        int valor = leerEntero(etiqueta);
        if (valor < min || valor > max) {
            throw new DatoInvalidoException(etiqueta,
                "el valor debe estar entre " + min + " y " + max + " (se ingresó " + valor + ").");
        }
        return valor;
    }

    /** Acepta AAAA-MM-DD (ISO) o DD/MM/AAAA (formato habitual en Argentina). */
    public LocalDate leerFecha(String etiqueta) throws DatoInvalidoException {
        String valor = leerTexto(etiqueta + " (DD/MM/AAAA)");
        try {
            return valor.contains("/") ? LocalDate.parse(valor, FECHA_AR) : LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(etiqueta, "'" + valor + "' no es una fecha válida.", e);
        }
    }

    public LocalTime leerHora(String etiqueta) throws DatoInvalidoException {
        String valor = leerTexto(etiqueta + " (HH:MM)");
        try {
            return LocalTime.parse(valor.length() == 4 ? "0" + valor : valor);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(etiqueta, "'" + valor + "' no es una hora válida.", e);
        }
    }

    public boolean confirmar(String pregunta) throws DatoInvalidoException {
        String r = leerTexto(pregunta + " [s/n]").toLowerCase();
        return switch (r) {
            case "s", "si", "sí" -> true;
            case "n", "no"       -> false;
            default -> throw new DatoInvalidoException(pregunta, "responda 's' o 'n'.");
        };
    }

    /** Lista de enteros separados por coma, por ejemplo "1, 4, 11". */
    public List<Integer> leerListaEnteros(String etiqueta) throws DatoInvalidoException {
        String valor = leerTexto(etiqueta);
        List<Integer> ids = new ArrayList<>();
        for (String parte : valor.split(",")) {
            if (parte.isBlank()) continue;
            try {
                ids.add(Integer.parseInt(parte.trim()));
            } catch (NumberFormatException e) {
                throw new DatoInvalidoException(etiqueta, "'" + parte.trim() + "' no es un identificador.", e);
            }
        }
        return ids;
    }

    // ================================================================ reintentos

    /**
     * Ejecuta un lector hasta que devuelva un valor válido o se agoten los
     * intentos. Estructura repetitiva do-while con manejo de excepciones.
     */
    public <T> T conReintentos(Lector<T> lector) throws DatoInvalidoException {
        int intento = 0;
        DatoInvalidoException ultimoError;
        do {
            try {
                return lector.leer();
            } catch (DatoInvalidoException e) {
                ultimoError = e;
                intento++;
                if (intento < MAX_INTENTOS) {
                    System.out.println("  [!] " + e.getMessage() + " Intente nuevamente ("
                                     + (MAX_INTENTOS - intento) + " restantes).");
                }
            }
        } while (intento < MAX_INTENTOS);
        throw ultimoError;
    }
}
