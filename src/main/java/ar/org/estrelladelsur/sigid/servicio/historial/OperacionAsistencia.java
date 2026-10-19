package ar.org.estrelladelsur.sigid.servicio.historial;

import ar.org.estrelladelsur.sigid.servicio.AsistenciaService;

import java.util.List;

/** Toma de asistencia de un evento: deshacerla borra los registros cargados. */
public class OperacionAsistencia extends Operacion {

    private final AsistenciaService servicio;
    private final int               idEvento;
    private final List<Integer>     idsAsistencia;

    public OperacionAsistencia(AsistenciaService servicio, int idEvento, List<Integer> idsAsistencia) {
        this.servicio      = servicio;
        this.idEvento      = idEvento;
        this.idsAsistencia = List.copyOf(idsAsistencia);
    }

    @Override public void deshacer() {
        for (int id : idsAsistencia) {
            servicio.eliminar(id);
        }
    }

    @Override public String descripcion() {
        return "Asistencia del evento " + idEvento + " (" + idsAsistencia.size() + " registros)";
    }
}
