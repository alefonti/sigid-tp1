package ar.org.estrelladelsur.sigid;

import ar.org.estrelladelsur.sigid.persistencia.ConexionBD;
import ar.org.estrelladelsur.sigid.persistencia.FabricaDAO;
import ar.org.estrelladelsur.sigid.persistencia.FabricaDAOMySQL;
import ar.org.estrelladelsur.sigid.persistencia.memoria.FabricaDAOMemoria;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;
import ar.org.estrelladelsur.sigid.ui.menu.MenuPrincipal;

import java.util.Scanner;

/**
 * Punto de entrada del prototipo operacional de SIGID (TP3).
 *
 *   java -jar sigid-3.0.0.jar            usa MySQL; si no responde, pasa a memoria
 *   java -jar sigid-3.0.0.jar --memoria  fuerza el modo demostración en memoria
 */
public class Main {

    public static void main(String[] args) {
        FabricaDAO fabrica = elegirFuenteDeDatos(args);
        Contexto   ctx     = new Contexto(fabrica);

        int recuperadas = ctx.notificaciones().sincronizarPendientes();
        if (recuperadas > 0) {
            System.out.println("Se recuperaron " + recuperadas + " avisos pendientes a la cola de notificaciones.");
        }

        // try-with-resources: el Scanner se cierra siempre, aun si el programa termina por un error
        try (Scanner sc = new Scanner(System.in)) {
            new MenuPrincipal(ctx, new Entrada(sc)).mostrar();
        } catch (Entrada.FinDeEntradaException e) {
            System.out.println();
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error inesperado: " + e);
        } finally {
            System.out.println("Fin de la sesión de SIGID.");
        }
    }

    private static FabricaDAO elegirFuenteDeDatos(String[] args) {
        for (String arg : args) {
            if ("--memoria".equals(arg)) {
                return new FabricaDAOMemoria();
            }
        }
        String error = ConexionBD.getInstancia().probarConexion();
        if (error == null) {
            return new FabricaDAOMySQL();
        }
        System.out.println("[Aviso] No se pudo conectar a MySQL (" + error + ").");
        System.out.println("[Aviso] Se inicia en modo demostración con datos en memoria.");
        return new FabricaDAOMemoria();
    }
}
