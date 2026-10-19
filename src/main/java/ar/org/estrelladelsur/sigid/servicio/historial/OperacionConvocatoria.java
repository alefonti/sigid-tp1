package ar.org.estrelladelsur.sigid.servicio.historial;

import ar.org.estrelladelsur.sigid.servicio.ConvocatoriaService;

import java.util.List;

/** Convocatoria a un partido: deshacerla quita a los jugadores convocados. */
public class OperacionConvocatoria extends Operacion {

    private final ConvocatoriaService servicio;
    private final int                 idEvento;
    private final List<Integer>       convocados;

    public OperacionConvocatoria(ConvocatoriaService servicio, int idEvento, List<Integer> convocados) {
        this.servicio   = servicio;
        this.idEvento   = idEvento;
        this.convocados = List.copyOf(convocados);
    }

    @Override public void deshacer() {
        for (int idJugador : convocados) {
            servicio.quitarConvocado(idEvento, idJugador);
        }
    }

    @Override public String descripcion() {
        return "Convocatoria al partido " + idEvento + " (" + convocados.size() + " jugadores)";
    }
}
