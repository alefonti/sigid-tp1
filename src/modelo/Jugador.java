package modelo;

import java.time.LocalDate;

public class Jugador {
    private int idJugador;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String dni;
    private int idDivision;
    private int idTutor;

    public Jugador() {
    }

    public Jugador(String nombre, String apellido, LocalDate fechaNacimiento,
                   String dni, int idDivision, int idTutor) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.dni = dni;
        this.idDivision = idDivision;
        this.idTutor = idTutor;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getIdDivision() {
        return idDivision;
    }

    public void setIdDivision(int idDivision) {
        this.idDivision = idDivision;
    }

    public int getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(int idTutor) {
        this.idTutor = idTutor;
    }

    @Override
    public String toString() {
        return idJugador + " - " + apellido + ", " + nombre + " (DNI " + dni + ")";
    }
}
