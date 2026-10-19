package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.estructuras.EstructuraVaciaException;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.persistencia.PersistenciaException;
import ar.org.estrelladelsur.sigid.servicio.ReglaNegocioException;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.util.List;

/**
 * ABSTRACCIÓN de un menú de selección por consola (patrón Template Method).
 *
 * El ciclo "mostrar opciones → leer elección → ejecutar → volver a
 * mostrar" y el tratamiento centralizado de excepciones se escriben una
 * sola vez acá. Cada submenú concreto sólo define su título, sus opciones
 * y qué hacer con cada una.
 */
public abstract class MenuConsola {

    protected static final String LINEA = "-".repeat(64);

    protected final Contexto ctx;
    protected final Entrada  in;

    protected MenuConsola(Contexto ctx, Entrada in) {
        this.ctx = ctx;
        this.in  = in;
    }

    protected abstract String   titulo();
    protected abstract String[] opciones();
    protected abstract void     ejecutarOpcion(int opcion) throws DatoInvalidoException;

    /** Texto de la opción 0; el menú principal lo redefine como "Salir". */
    protected String textoSalida() { return "Volver al menú anterior"; }

    /** MÉTODO PLANTILLA: final para que ningún submenú altere el ciclo. */
    public final void mostrar() {
        int opcion;
        do {
            imprimirEncabezado();
            try {
                opcion = in.leerEntero("Opción", 0, opciones().length);
            } catch (DatoInvalidoException e) {
                System.out.println("  [!] " + e.getMessage());
                opcion = -1;                              // fuerza otra vuelta del ciclo
                continue;
            }
            if (opcion != 0) {
                procesar(opcion);
            }
        } while (opcion != 0);
    }

    /** Ejecuta la opción y traduce cada tipo de excepción a un mensaje claro. */
    private void procesar(int opcion) {
        System.out.println(LINEA);
        try {
            ejecutarOpcion(opcion);
        } catch (DatoInvalidoException e) {
            System.out.println("  [!] Dato inválido en \"" + e.getCampo() + "\": " + e.getMessage());
        } catch (ReglaNegocioException e) {
            System.out.println("  [X] Operación rechazada " + e.getMessage());
        } catch (EstructuraVaciaException e) {
            System.out.println("  [i] " + e.getMessage());
        } catch (PersistenciaException e) {
            String causa = e.getCause() != null ? " (" + e.getCause().getMessage() + ")" : "";
            System.out.println("  [X] Error de base de datos: " + e.getMessage() + causa);
        } catch (IllegalArgumentException e) {
            // la lanzan los setters del modelo cuando un dato no respeta su formato
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private void imprimirEncabezado() {
        System.out.println();
        System.out.println("=".repeat(64));
        System.out.println("  " + titulo());
        System.out.println("=".repeat(64));
        String[] ops = opciones();
        for (int i = 0; i < ops.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, ops[i]);
        }
        System.out.println("  0. " + textoSalida());
        System.out.println("=".repeat(64));
    }

    // ================================================================ utilidades compartidas

    /** Muestra las divisiones y pide elegir una. Lo usan varios submenús. */
    protected Division elegirDivision() throws DatoInvalidoException {
        List<Division> divisiones = ctx.club().divisiones();
        System.out.println("  Divisiones:");
        for (Division d : divisiones) {
            System.out.printf("    %d) %s%n", d.getIdDivision(), d);
        }
        int id = in.conReintentos(() -> in.leerEntero("Id de división"));
        return ctx.club().division(id);
    }
}
