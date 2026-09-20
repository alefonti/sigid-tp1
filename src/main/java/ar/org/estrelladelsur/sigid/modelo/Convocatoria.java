package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDateTime;

/** Clase asociativa entre Jugador y Evento de tipo PARTIDO. */
public class Convocatoria {

    private int           idConvocatoria;
    private int           idEvento;
    private int           idJugador;
    private LocalDateTime fechaConvocatoria = LocalDateTime.now();
    private boolean       confirmada;

    public Convocatoria() { }

    public Convocatoria(int idEvento, int idJugador) {
        this.idEvento  = idEvento;
        this.idJugador = idJugador;
    }

    public int           getIdConvocatoria()    { return idConvocatoria; }
    public int           getIdEvento()          { return idEvento; }
    public int           getIdJugador()         { return idJugador; }
    public LocalDateTime getFechaConvocatoria() { return fechaConvocatoria; }
    public boolean       isConfirmada()         { return confirmada; }

    public void setIdConvocatoria(int v)             { this.idConvocatoria = v; }
    public void setIdEvento(int v)                   { this.idEvento = v; }
    public void setIdJugador(int v)                  { this.idJugador = v; }
    public void setFechaConvocatoria(LocalDateTime v){ this.fechaConvocatoria = v; }
    public void setConfirmada(boolean v)             { this.confirmada = v; }
}
