package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/** HERENCIA: encuentro oficial contra un rival. Admite convocatoria. */
public class Partido extends Evento {

    private String rival;
    private String condicion;   // LOCAL | VISITANTE

    public Partido() {
        super();
    }

    public Partido(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                   int idDivision, int idSede, String rival, String condicion) {
        super(fecha, horaInicio, horaFin, idDivision, idSede);
        setRival(rival);
        setCondicion(condicion);
    }

    @Override public TipoEvento getTipo()            { return TipoEvento.PARTIDO; }
    @Override public boolean    admiteConvocatoria() { return true; }

    @Override public String descripcion() {
        return "vs " + rival + " (" + condicion + ")";
    }

    public String getRival()     { return rival; }
    public String getCondicion() { return condicion; }

    public final void setRival(String v) {
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("Un partido debe tener rival.");
        }
        this.rival = v.trim();
    }

    public final void setCondicion(String v) {
        if (v == null || !(v.equalsIgnoreCase("LOCAL") || v.equalsIgnoreCase("VISITANTE"))) {
            throw new IllegalArgumentException("La condición debe ser LOCAL o VISITANTE.");
        }
        this.condicion = v.toUpperCase();
    }
}
