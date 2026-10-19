package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.FichaMedica;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.time.LocalDate;

/** Submenú del CU02 - Gestionar ficha médica y CU10 - Validar aptitud médica. */
public class MenuAreaMedica extends MenuConsola {

    public MenuAreaMedica(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() { return "ÁREA MÉDICA"; }

    @Override protected String[] opciones() {
        return new String[] {
            "Consultar aptitud de un jugador        (CU10)",
            "Informe de aptitud de una división     (CU10)",
            "Registrar ficha médica                 (CU02)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        switch (opcion) {
            case 1 -> consultar();
            case 2 -> informe();
            case 3 -> registrarFicha();
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    private void consultar() throws DatoInvalidoException {
        Jugador j = ctx.jugadores().obtener(in.leerEntero("Id del jugador"));
        String motivo = ctx.fichasMedicas().motivoDeBloqueo(j.getIdJugador(), ctx.hoy());
        System.out.println(motivo == null
                ? "  " + j.nombreCompleto() + " está habilitado para ser convocado."
                : "  " + j.nombreCompleto() + " NO está habilitado porque " + motivo + ".");
    }

    private void informe() throws DatoInvalidoException {
        Division d = elegirDivision();
        int aptos = 0, noAptos = 0;
        for (Jugador j : ctx.jugadores().buscarPorDivision(d.getIdDivision())) {
            String motivo = ctx.fichasMedicas().motivoDeBloqueo(j.getIdJugador(), ctx.hoy());
            if (motivo == null) {
                aptos++;
            } else {
                noAptos++;
                System.out.printf("  %-26s %s%n", j.nombreCompleto(), motivo);
            }
        }
        System.out.println("  " + d.getNombre() + ": " + aptos + " aptos, " + noAptos + " no aptos.");
    }

    private void registrarFicha() throws DatoInvalidoException {
        Jugador j = ctx.jugadores().obtener(in.leerEntero("Id del jugador"));
        LocalDate emision = in.conReintentos(() -> in.leerFecha("Fecha de emisión"));
        LocalDate vence   = in.conReintentos(() -> in.leerFecha("Fecha de vencimiento"));
        boolean apto      = in.confirmar("¿Apto físico favorable?");
        int profesional   = in.leerEntero("Profesional (1 Dra. Domínguez, 2 Lic. Ruiz)", 1, 2);

        FichaMedica ficha = new FichaMedica(j.getIdJugador(), profesional, emision, vence, apto);
        ficha.setObservaciones(in.leerTexto("Observaciones (opcional)"));
        int id = ctx.fichasMedicas().registrar(ficha);
        System.out.println("  [OK] Ficha " + id + " registrada para " + j.nombreCompleto() + ".");
    }
}
