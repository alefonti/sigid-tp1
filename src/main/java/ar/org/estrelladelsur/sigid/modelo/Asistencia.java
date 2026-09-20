package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDateTime;

/** Clase asociativa entre Jugador y Evento: presencia registrada. */
public class Asistencia {

    private int              idAsistencia;
    private int              idEvento;
    private int              idJugador;
    private EstadoAsistencia estado;
    private String           motivo;
    private LocalDateTime    fechaRegistro = LocalDateTime.now();

    public Asistencia() { }

    public Asistencia(int idEvento, int idJugador, EstadoAsistencia estado) {
        this.idEvento  = idEvento;
        this.idJugador = idJugador;
        this.estado    = estado;
    }

    public int              getIdAsistencia()  { return idAsistencia; }
    public int              getIdEvento()      { return idEvento; }
    public int              getIdJugador()     { return idJugador; }
    public EstadoAsistencia getEstado()        { return estado; }
    public String           getMotivo()        { return motivo; }
    public LocalDateTime    getFechaRegistro() { return fechaRegistro; }

    public void setIdAsistencia(int v)          { this.idAsistencia = v; }
    public void setIdEvento(int v)              { this.idEvento = v; }
    public void setIdJugador(int v)             { this.idJugador = v; }
    public void setEstado(EstadoAsistencia v)   { this.estado = v; }
    public void setMotivo(String v)             { this.motivo = v; }
    public void setFechaRegistro(LocalDateTime v){ this.fechaRegistro = v; }
}
