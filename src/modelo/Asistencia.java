package modelo;

import java.time.LocalDate;

public class Asistencia {
    private int idAsistencia;
    private int idJugador;
    private LocalDate fecha;
    private boolean presente;

    public Asistencia() {
    }

    public Asistencia(int idJugador, LocalDate fecha, boolean presente) {
        this.idJugador = idJugador;
        this.fecha = fecha;
        this.presente = presente;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }
}
