package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDate;

/** Lesión registrada por el profesional médico. */
public class Lesion {

    private int       idLesion;
    private int       idJugador;
    private int       idProfesional;
    private String    tipo;
    private Gravedad  gravedad;
    private LocalDate fechaInicio;
    private LocalDate fechaAltaReal;   // null = lesión activa

    public Lesion() { }

    public Lesion(int idJugador, String tipo, Gravedad gravedad, LocalDate fechaInicio) {
        this.idJugador   = idJugador;
        this.tipo        = tipo;
        this.gravedad    = gravedad;
        this.fechaInicio = fechaInicio;
    }

    /** Una lesión está activa mientras no tenga fecha de alta médica. */
    public boolean estaActiva() { return fechaAltaReal == null; }

    public int       getIdLesion()      { return idLesion; }
    public int       getIdJugador()     { return idJugador; }
    public int       getIdProfesional() { return idProfesional; }
    public String    getTipo()          { return tipo; }
    public Gravedad  getGravedad()      { return gravedad; }
    public LocalDate getFechaInicio()   { return fechaInicio; }
    public LocalDate getFechaAltaReal() { return fechaAltaReal; }

    public void setIdLesion(int v)            { this.idLesion = v; }
    public void setIdJugador(int v)           { this.idJugador = v; }
    public void setIdProfesional(int v)       { this.idProfesional = v; }
    public void setTipo(String v)             { this.tipo = v; }
    public void setGravedad(Gravedad v)       { this.gravedad = v; }
    public void setFechaInicio(LocalDate v)   { this.fechaInicio = v; }
    public void setFechaAltaReal(LocalDate v) { this.fechaAltaReal = v; }
}
