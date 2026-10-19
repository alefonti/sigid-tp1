package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.Notificacion;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.util.List;

/** Submenú del CU06 - Notificar a tutores (cola FIFO). */
public class MenuNotificaciones extends MenuConsola {

    public MenuNotificaciones(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() {
        return "NOTIFICACIONES (" + ctx.notificaciones().cantidadPendientes() + " en cola)";
    }

    @Override protected String[] opciones() {
        return new String[] {
            "Ver cola de avisos pendientes",
            "Procesar la cola (envío en orden FIFO) (CU06)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        switch (opcion) {
            case 1 -> verCola();
            case 2 -> procesar();
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    private void verCola() {
        List<Notificacion> cola = ctx.notificaciones().verPendientes();
        if (cola.isEmpty()) {
            System.out.println("  No hay avisos pendientes.");
            return;
        }
        int pos = 1;
        for (Notificacion n : cola) {
            System.out.printf("  %2d. [%s] tutor %d | evento %d | %s%n", pos++, n.getCanal(),
                    n.getIdTutor(), n.getIdEvento(), recortar(n.getMensaje(), 60));
        }
    }

    private void procesar() throws DatoInvalidoException {
        if (ctx.notificaciones().cantidadPendientes() == 0) {
            System.out.println("  No hay avisos pendientes.");
            return;
        }
        if (!in.confirmar("¿Enviar " + ctx.notificaciones().cantidadPendientes() + " avisos?")) {
            return;
        }
        for (String comprobante : ctx.notificaciones().procesarCola()) {
            System.out.println("  " + recortar(comprobante, 118));
        }
    }

    private static String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max - 3) + "...";
    }
}
