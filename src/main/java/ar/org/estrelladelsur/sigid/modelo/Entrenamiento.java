package ar.org.estrelladelsur.sigid.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/** HERENCIA: práctica semanal de una división. No admite convocatoria. */
public class Entrenamiento extends Evento {

    public Entrenamiento() {
        super();
    }

    public Entrenamiento(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                         int idDivision, int idSede) {
        super(fecha, horaInicio, horaFin, idDivision, idSede);
    }

    @Override public TipoEvento getTipo()          { return TipoEvento.ENTRENAMIENTO; }
    @Override public boolean    admiteConvocatoria() { return false; }

    @Override public String descripcion() {
        return "Entrenamiento de " + duracionMinutos() + " min";
    }
}
