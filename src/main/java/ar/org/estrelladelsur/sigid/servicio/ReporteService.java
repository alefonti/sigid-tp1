package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.algoritmos.Ordenamiento;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.FilaPresentismo;
import ar.org.estrelladelsur.sigid.persistencia.EventoDAO;
import ar.org.estrelladelsur.sigid.persistencia.PlanillaDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * TP4 - Realiza el CU12 - Emitir reporte de presentismo y el
 * CU13 - Cerrar evento.
 *
 * Reglas de negocio:
 *   RN-11: sólo se cierra un evento cuya fecha ya pasó.
 *   RN-12: un evento suspendido o ya finalizado no se vuelve a cerrar.
 */
public class ReporteService {

    /**
     * Resultado del reporte. Combina las dos estructuras a propósito:
     *  - filas y totales son ARREGLOS: su tamaño se conoce al armarlos
     *    (jugadores leídos / cantidad fija de estados) y el arreglo de
     *    filas es lo que reciben los algoritmos de ordenamiento del TP3;
     *  - enRiesgo es un ARRAYLIST: no se sabe de antemano cuántos jugadores
     *    van a quedar por debajo del umbral, así que la lista crece sola.
     */
    public record ReportePresentismo(Division division, FilaPresentismo[] filas,
                                     int[] totalesPorEstado, List<FilaPresentismo> enRiesgo,
                                     double promedio) { }

    private final PlanillaDAO planillaDAO;
    private final EventoDAO   eventoDAO;

    public ReporteService(PlanillaDAO planillaDAO, EventoDAO eventoDAO) {
        this.planillaDAO = planillaDAO;
        this.eventoDAO   = eventoDAO;
    }

    // ================================================================ CU12

    public ReportePresentismo presentismo(Division division) {
        // 1) la consulta devuelve un ArrayList (cantidad de filas desconocida)
        List<FilaPresentismo> leidas = planillaDAO.presentismoPorDivision(division.getIdDivision());

        // 2) se pasa a arreglo para ordenarlo con merge sort (estable):
        //    mayor porcentaje primero y, a igual porcentaje, orden alfabético
        FilaPresentismo[] filas = leidas.toArray(new FilaPresentismo[0]);
        Ordenamiento.mergeSort(filas,
                Comparator.comparingDouble(FilaPresentismo::porcentaje).reversed()
                          .thenComparing(FilaPresentismo::getNombreCompleto));

        // 3) acumuladores: arreglo de tamaño fijo, uno por estado
        int[] totales = new int[EstadoAsistencia.values().length];
        ArrayList<FilaPresentismo> enRiesgo = new ArrayList<>();
        double suma = 0;
        int conRegistros = 0;

        for (FilaPresentismo fila : filas) {
            int[] conteo = fila.getConteo();
            for (int i = 0; i < conteo.length; i++) {
                totales[i] += conteo[i];
            }
            if (fila.total() > 0) {
                suma += fila.porcentaje();
                conRegistros++;
                if (fila.porcentaje() < AsistenciaService.UMBRAL_ALERTA) {
                    enRiesgo.add(fila);
                }
            }
        }
        double promedio = conRegistros == 0 ? 0 : Math.round(suma * 10 / conRegistros) / 10d;
        return new ReportePresentismo(division, filas, totales, enRiesgo, promedio);
    }

    // ================================================================ CU13

    public List<Evento> pendientesDeCierre(Division division, LocalDate hoy) {
        return planillaDAO.pendientesDeCierre(division.getIdDivision(), hoy);
    }

    /** @return cantidad de ausencias que se registraron automáticamente */
    public int cerrarEvento(int idEvento, LocalDate hoy) {
        Evento evento = eventoDAO.buscarPorId(idEvento);
        if (evento == null) {
            throw new ReglaNegocioException("RN-07", "No existe el evento " + idEvento + ".");
        }
        if (!evento.getFecha().isBefore(hoy)) {
            throw new ReglaNegocioException("RN-11",
                "El evento es del " + evento.getFecha() + "; sólo se cierran eventos ya realizados.");
        }
        if ("SUSPENDIDO".equals(evento.getEstado()) || "FINALIZADO".equals(evento.getEstado())) {
            throw new ReglaNegocioException("RN-12",
                "El evento está " + evento.getEstado().toLowerCase() + " y no puede cerrarse.");
        }
        return planillaDAO.cerrarEvento(idEvento);
    }
}
