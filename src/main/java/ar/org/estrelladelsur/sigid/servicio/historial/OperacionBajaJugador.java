package ar.org.estrelladelsur.sigid.servicio.historial;

import ar.org.estrelladelsur.sigid.modelo.EstadoJugador;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.servicio.JugadorService;

/** Baja de un jugador: deshacerla le devuelve el estado que tenía antes. */
public class OperacionBajaJugador extends Operacion {

    private final JugadorService servicio;
    private final int            idJugador;
    private final String         nombre;
    private final EstadoJugador  estadoAnterior;

    public OperacionBajaJugador(JugadorService servicio, Jugador jugador, EstadoJugador estadoAnterior) {
        this.servicio       = servicio;
        this.idJugador      = jugador.getIdJugador();
        this.nombre         = jugador.nombreCompleto();
        this.estadoAnterior = estadoAnterior;
    }

    @Override public void deshacer() {
        servicio.cambiarEstado(idJugador, estadoAnterior);
    }

    @Override public String descripcion() {
        return "Baja del jugador " + nombre + " (estado previo " + estadoAnterior + ")";
    }
}
