package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/** Entrenamiento o partido de una división. */
public class Evento {

    private int        idEvento;
    private TipoEvento tipo;
    private LocalDate  fecha;
    private LocalTime  horaInicio;
    private LocalTime  horaFin;
    private String     rival;
    private String     condicion;
    private String     estado = "PROGRAMADO";
    private int        idDivision;
    private int        idSede;

    public Evento() { }

    public Evento(TipoEvento tipo, LocalDate fecha, LocalTime horaInicio,
                  LocalTime horaFin, int idDivision, int idSede) {
        this.tipo       = tipo;
        this.fecha      = fecha;
        this.horaInicio = horaInicio;
        this.horaFin    = horaFin;
        this.idDivision = idDivision;
        this.idSede     = idSede;
    }

    public boolean esPartido() { return tipo == TipoEvento.PARTIDO; }

    public int        getIdEvento()   { return idEvento; }
    public TipoEvento getTipo()       { return tipo; }
    public LocalDate  getFecha()      { return fecha; }
    public LocalTime  getHoraInicio() { return horaInicio; }
    public LocalTime  getHoraFin()    { return horaFin; }
    public String     getRival()      { return rival; }
    public String     getCondicion()  { return condicion; }
    public String     getEstado()     { return estado; }
    public int        getIdDivision() { return idDivision; }
    public int        getIdSede()     { return idSede; }

    public void setIdEvento(int v)        { this.idEvento = v; }
    public void setTipo(TipoEvento v)     { this.tipo = v; }
    public void setFecha(LocalDate v)     { this.fecha = v; }
    public void setHoraInicio(LocalTime v){ this.horaInicio = v; }
    public void setHoraFin(LocalTime v)   { this.horaFin = v; }
    public void setRival(String v)        { this.rival = v; }
    public void setCondicion(String v)    { this.condicion = v; }
    public void setEstado(String v)       { this.estado = v; }
    public void setIdDivision(int v)      { this.idDivision = v; }
    public void setIdSede(int v)          { this.idSede = v; }

    @Override public String toString() {
        return tipo + " " + fecha + " " + horaInicio + (rival != null ? " vs " + rival : "");
    }
}
