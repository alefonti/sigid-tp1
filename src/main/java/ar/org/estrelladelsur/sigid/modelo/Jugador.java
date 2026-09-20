package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDate;
import java.time.Period;

/** Jugador de las divisiones inferiores del club. */
public class Jugador {

    private int            idJugador;
    private String         documento;
    private String         apellido;
    private String         nombre;
    private LocalDate      fechaNacimiento;
    private LocalDate      fechaAlta;
    private EstadoJugador  estado = EstadoJugador.ACTIVO;
    private int            idDivision;

    public Jugador() { }

    public Jugador(String documento, String apellido, String nombre,
                   LocalDate fechaNacimiento, int idDivision) {
        this.documento       = documento;
        this.apellido        = apellido;
        this.nombre          = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.idDivision      = idDivision;
        this.fechaAlta       = LocalDate.now();
    }

    /** Edad cumplida del jugador a la fecha indicada. */
    public int edadA(LocalDate fecha) {
        return Period.between(fechaNacimiento, fecha).getYears();
    }

    public String nombreCompleto() { return apellido + ", " + nombre; }

    public int           getIdJugador()       { return idJugador; }
    public String        getDocumento()       { return documento; }
    public String        getApellido()        { return apellido; }
    public String        getNombre()          { return nombre; }
    public LocalDate     getFechaNacimiento() { return fechaNacimiento; }
    public LocalDate     getFechaAlta()       { return fechaAlta; }
    public EstadoJugador getEstado()          { return estado; }
    public int           getIdDivision()      { return idDivision; }

    public void setIdJugador(int v)              { this.idJugador = v; }
    public void setDocumento(String v)           { this.documento = v; }
    public void setApellido(String v)            { this.apellido = v; }
    public void setNombre(String v)              { this.nombre = v; }
    public void setFechaNacimiento(LocalDate v)  { this.fechaNacimiento = v; }
    public void setFechaAlta(LocalDate v)        { this.fechaAlta = v; }
    public void setEstado(EstadoJugador v)       { this.estado = v; }
    public void setIdDivision(int v)             { this.idDivision = v; }

    @Override public String toString() {
        return "[" + idJugador + "] " + nombreCompleto() + " - DNI " + documento;
    }
}
