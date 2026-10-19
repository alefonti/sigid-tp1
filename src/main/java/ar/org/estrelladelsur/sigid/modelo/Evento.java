package ar.org.estrelladelsur.sigid.modelo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * ABSTRACCIÓN: actividad de la agenda deportiva de una división.
 *
 * Un evento siempre es, en concreto, un Entrenamiento o un Partido. Cada
 * subclase define si admite convocatoria y cómo se describe; lo común
 * (fecha, horario, sede, estado) vive acá una sola vez.
 */
public abstract class Evento implements Identificable, Comparable<Evento> {

    private int       id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String    estado = "PROGRAMADO";
    private int       idDivision;
    private int       idSede;

    protected Evento() { }

    protected Evento(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                     int idDivision, int idSede) {
        this.fecha      = fecha;
        this.horaInicio = horaInicio;
        this.idDivision = idDivision;
        this.idSede     = idSede;
        setHoraFin(horaFin);
    }

    /**
     * Método de fábrica: crea la subclase que corresponde al tipo leído de
     * la base de datos. Quien lo usa sólo conoce la abstracción Evento.
     */
    public static Evento crear(TipoEvento tipo) {
        return switch (tipo) {
            case PARTIDO       -> new Partido();
            case ENTRENAMIENTO -> new Entrenamiento();
        };
    }

    // ------------------------------------------------------------ métodos abstractos

    public abstract TipoEvento getTipo();

    /** Sólo los partidos admiten convocatoria (CU05). */
    public abstract boolean admiteConvocatoria();

    /** Descripción específica de cada tipo de evento. */
    public abstract String descripcion();

    // ------------------------------------------------------------ comportamiento común

    public long duracionMinutos() {
        return Duration.between(horaInicio, horaFin).toMinutes();
    }

    public boolean esPartido() { return getTipo() == TipoEvento.PARTIDO; }

    public boolean esFuturo(LocalDate hoy) { return !fecha.isBefore(hoy); }

    /** Orden cronológico (fecha y hora de inicio). */
    @Override
    public int compareTo(Evento otro) {
        int porFecha = fecha.compareTo(otro.fecha);
        return porFecha != 0 ? porFecha : horaInicio.compareTo(otro.horaInicio);
    }

    // ------------------------------------------------------------ accesores

    @Override public int getId()   { return id; }
    public int       getIdEvento() { return id; }
    public LocalDate getFecha()      { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin()    { return horaFin; }
    public String    getEstado()     { return estado; }
    public int       getIdDivision() { return idDivision; }
    public int       getIdSede()     { return idSede; }

    public void setIdEvento(int v)         { this.id = v; }
    public void setFecha(LocalDate v)      { this.fecha = v; }
    public void setHoraInicio(LocalTime v) { this.horaInicio = v; }
    public void setEstado(String v)        { this.estado = v; }
    public void setIdDivision(int v)       { this.idDivision = v; }
    public void setIdSede(int v)           { this.idSede = v; }

    /** Encapsulamiento: el horario de fin debe ser posterior al de inicio. */
    public final void setHoraFin(LocalTime v) {
        if (v != null && horaInicio != null && !v.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        }
        this.horaFin = v;
    }

    @Override public String toString() {
        return String.format("[%d] %-13s %s %s-%s  %-34s %s",
                id, getTipo(), fecha, horaInicio, horaFin, descripcion(), estado);
    }
}
