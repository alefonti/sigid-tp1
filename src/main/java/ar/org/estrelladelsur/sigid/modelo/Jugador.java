package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDate;
import java.time.Period;

/**
 * HERENCIA: un jugador ES UNA persona del club. Hereda documento,
 * apellido, nombre y contacto, y agrega los datos deportivos propios.
 */
public class Jugador extends Persona {

    private LocalDate     fechaNacimiento;
    private LocalDate     fechaAlta;
    private EstadoJugador estado = EstadoJugador.ACTIVO;
    private int           idDivision;

    public Jugador() {
        super();
    }

    /** Constructor usado en el alta (CU01): la fecha de alta es la del día. */
    public Jugador(String documento, String apellido, String nombre,
                   LocalDate fechaNacimiento, int idDivision) {
        super(documento, apellido, nombre);   // inicializa la parte heredada
        this.fechaNacimiento = fechaNacimiento;
        this.idDivision      = idDivision;
        this.fechaAlta       = LocalDate.now();
    }

    // ------------------------------------------------------------ polimorfismo

    @Override public String getRol() { return "Jugador"; }

    @Override public String getDetalle() {
        return "Nac. " + fechaNacimiento + " | " + estado
             + " | edad " + (fechaNacimiento == null ? "-" : edadA(LocalDate.now()));
    }

    // ------------------------------------------------------------ comportamiento propio

    /** Edad cumplida del jugador a la fecha indicada. */
    public int edadA(LocalDate fecha) {
        return Period.between(fechaNacimiento, fecha).getYears();
    }

    public boolean estaActivo() { return estado == EstadoJugador.ACTIVO; }

    // ------------------------------------------------------------ accesores

    /** Alias semántico de getId(), conservado por compatibilidad con el TP2. */
    public int           getIdJugador()       { return getId(); }
    public LocalDate     getFechaNacimiento() { return fechaNacimiento; }
    public LocalDate     getFechaAlta()       { return fechaAlta; }
    public EstadoJugador getEstado()          { return estado; }
    public int           getIdDivision()      { return idDivision; }

    public void setIdJugador(int v) { setId(v); }

    public void setFechaNacimiento(LocalDate v) {
        if (v != null && v.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        this.fechaNacimiento = v;
    }

    public void setFechaAlta(LocalDate v)  { this.fechaAlta = v; }

    public void setEstado(EstadoJugador v) {
        if (v == null) {
            throw new IllegalArgumentException("El estado del jugador es obligatorio.");
        }
        this.estado = v;
    }

    public void setIdDivision(int v) { this.idDivision = v; }

    @Override public String toString() {
        return "[" + getId() + "] " + nombreCompleto() + " - DNI " + getDocumento();
    }
}
