package ar.org.estrelladelsur.sigid.modelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado de ejecutar el CU05: qué jugadores quedaron convocados
 * y cuáles fueron bloqueados por el CU10, con el motivo del bloqueo.
 */
public class ResultadoConvocatoria {

    private final List<Integer>        convocados = new ArrayList<>();
    private final Map<Integer, String> rechazados = new LinkedHashMap<>();
    private int                        notificacionesEnviadas;

    public void agregarConvocado(int idJugador)               { convocados.add(idJugador); }
    public void agregarRechazo(int idJugador, String motivo)  { rechazados.put(idJugador, motivo); }

    public List<Integer>        getConvocados()             { return convocados; }
    public Map<Integer, String> getRechazados()             { return rechazados; }
    public int                  getNotificacionesEnviadas() { return notificacionesEnviadas; }
    public void setNotificacionesEnviadas(int v)            { this.notificacionesEnviadas = v; }

    public int cantidadConvocados() { return convocados.size(); }
    public int cantidadRechazados() { return rechazados.size(); }

    @Override public String toString() {
        return "Convocados: " + convocados.size() + " | Bloqueados: " + rechazados.size()
             + " | Notificaciones: " + notificacionesEnviadas;
    }
}
