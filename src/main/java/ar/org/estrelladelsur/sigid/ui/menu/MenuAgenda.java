package ar.org.estrelladelsur.sigid.ui.menu;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.Entrenamiento;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.Partido;
import ar.org.estrelladelsur.sigid.ui.Contexto;
import ar.org.estrelladelsur.sigid.ui.Entrada;

import java.time.LocalDate;
import java.time.LocalTime;

/** Submenú del CU04 - Programar evento. */
public class MenuAgenda extends MenuConsola {

    public MenuAgenda(Contexto ctx, Entrada in) {
        super(ctx, in);
    }

    @Override protected String titulo() { return "AGENDA DEPORTIVA"; }

    @Override protected String[] opciones() {
        return new String[] {
            "Ver agenda de una división             (ordenada por inserción)",
            "Programar entrenamiento                (CU04)",
            "Programar partido                      (CU04)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) throws DatoInvalidoException {
        switch (opcion) {
            case 1 -> verAgenda();
            case 2 -> programar(false);
            case 3 -> programar(true);
            default -> throw new IllegalStateException("Opción no contemplada: " + opcion);
        }
    }

    private void verAgenda() throws DatoInvalidoException {
        Division d = elegirDivision();
        Evento[] agenda = ctx.agenda().agendaDe(d.getIdDivision());
        System.out.println("\n  Agenda " + d.getNombre() + " (" + agenda.length + " eventos)");
        LocalDate hoy = ctx.hoy();
        for (Evento e : agenda) {
            // toString() llama a descripcion(): cada subclase muestra lo suyo
            System.out.println("  " + (e.esFuturo(hoy) ? "> " : "  ") + e);
        }
        Evento proximo = ctx.agenda().proximoPartido(d.getIdDivision(), hoy);
        System.out.println(proximo == null
                ? "  No hay partidos programados."
                : "  Próximo partido: " + proximo.descripcion() + " el " + proximo.getFecha());
    }

    private void programar(boolean esPartido) throws DatoInvalidoException {
        Division d = elegirDivision();
        LocalDate fecha  = in.conReintentos(() -> in.leerFecha("Fecha"));
        LocalTime inicio = in.conReintentos(() -> in.leerHora("Hora de inicio"));
        LocalTime fin    = in.conReintentos(() -> in.leerHora("Hora de fin"));
        int sede         = in.leerEntero("Sede (1 Predio, 2 Polideportivo, 3 Cancha auxiliar)", 1, 3);

        // La variable es del tipo abstracto; el objeto concreto se decide en ejecución
        Evento evento;
        if (esPartido) {
            String rival     = in.leerTextoObligatorio("Rival");
            String condicion = in.leerTextoObligatorio("Condición (LOCAL/VISITANTE)");
            evento = new Partido(fecha, inicio, fin, d.getIdDivision(), sede, rival, condicion);
        } else {
            evento = new Entrenamiento(fecha, inicio, fin, d.getIdDivision(), sede);
        }
        int id = ctx.agenda().programar(evento, ctx.hoy());
        System.out.println("  [OK] Evento " + id + " programado: " + evento.descripcion()
                         + " el " + fecha + " de " + inicio + " a " + fin + ".");
    }
}
