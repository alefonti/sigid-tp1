package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDate;

/** Ficha médica anual de un jugador (apto físico). */
public class FichaMedica {

    private int       idFicha;
    private int       idJugador;
    private int       idProfesional;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private boolean   aptoFisico;
    private String    observaciones;

    public FichaMedica() { }

    public FichaMedica(int idJugador, int idProfesional, LocalDate fechaEmision,
                       LocalDate fechaVencimiento, boolean aptoFisico) {
        this.idJugador        = idJugador;
        this.idProfesional    = idProfesional;
        this.fechaEmision     = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.aptoFisico       = aptoFisico;
    }

    /** Regla RN-03: la ficha habilita si el apto es favorable y no está vencida. */
    public boolean estaVigenteA(LocalDate fecha) {
        return aptoFisico && !fechaVencimiento.isBefore(fecha);
    }

    public int       getIdFicha()          { return idFicha; }
    public int       getIdJugador()        { return idJugador; }
    public int       getIdProfesional()    { return idProfesional; }
    public LocalDate getFechaEmision()     { return fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public boolean   isAptoFisico()        { return aptoFisico; }
    public String    getObservaciones()    { return observaciones; }

    public void setIdFicha(int v)               { this.idFicha = v; }
    public void setIdJugador(int v)             { this.idJugador = v; }
    public void setIdProfesional(int v)         { this.idProfesional = v; }
    public void setFechaEmision(LocalDate v)    { this.fechaEmision = v; }
    public void setFechaVencimiento(LocalDate v){ this.fechaVencimiento = v; }
    public void setAptoFisico(boolean v)        { this.aptoFisico = v; }
    public void setObservaciones(String v)      { this.observaciones = v; }
}
