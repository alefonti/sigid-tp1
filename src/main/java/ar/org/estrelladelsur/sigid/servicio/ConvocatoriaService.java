package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Convocatoria;
import ar.org.estrelladelsur.sigid.modelo.ResultadoConvocatoria;
import ar.org.estrelladelsur.sigid.persistencia.ConvocatoriaDAO;

import java.time.LocalDate;
import java.util.List;

/**
 * Realiza el CU05 - Generar convocatoria.
 *
 * Incluye («include») al CU10 - Validar aptitud médica, delegado en
 * {@link FichaMedicaService}, y al CU06 - Notificar a tutores, delegado en
 * {@link NotificacionService}.
 *
 * Reglas de negocio que aplica:
 *   RN-03: no se convoca a un jugador sin aptitud médica vigente.
 *   RN-04: una convocatoria admite como máximo 18 jugadores.
 *   RN-05: la lista definitiva no puede tener menos de 11 jugadores.
 */
public class ConvocatoriaService {

    public static final int MAXIMO_CONVOCADOS = 18;
    public static final int MINIMO_CONVOCADOS = 11;

    private final ConvocatoriaDAO    convocatoriaDAO;
    private final FichaMedicaService fichaMedicaService;
    private final NotificacionService notificacionService;

    public ConvocatoriaService(ConvocatoriaDAO convocatoriaDAO,
                               FichaMedicaService fichaMedicaService,
                               NotificacionService notificacionService) {
        this.convocatoriaDAO     = convocatoriaDAO;
        this.fichaMedicaService  = fichaMedicaService;
        this.notificacionService = notificacionService;
    }

    /**
     * Convoca a los jugadores indicados para un partido.
     * Los jugadores sin aptitud médica quedan bloqueados con su motivo y el
     * resto se registra; al finalizar se notifica a los tutores.
     *
     * @throws ReglaNegocioException si se supera el máximo de convocados (RN-04)
     */
    public ResultadoConvocatoria convocar(int idEvento, List<Integer> idsJugadores, LocalDate fecha) {

        if (idsJugadores == null || idsJugadores.isEmpty()) {
            throw new ReglaNegocioException("RN-04", "La convocatoria no tiene jugadores seleccionados.");
        }
        if (idsJugadores.size() > MAXIMO_CONVOCADOS) {
            throw new ReglaNegocioException("RN-04",
                "Se seleccionaron " + idsJugadores.size() + " jugadores y el máximo permitido es "
              + MAXIMO_CONVOCADOS + ".");
        }

        ResultadoConvocatoria resultado = new ResultadoConvocatoria();

        for (Integer idJugador : idsJugadores) {
            String motivo = fichaMedicaService.motivoDeBloqueo(idJugador, fecha);
            if (motivo != null) {
                resultado.agregarRechazo(idJugador, motivo);
                continue;
            }
            convocatoriaDAO.insertar(new Convocatoria(idEvento, idJugador));
            resultado.agregarConvocado(idJugador);
        }

        resultado.setNotificacionesEnviadas(notificacionService.notificarTutores(idEvento));
        return resultado;
    }

    /**
     * Confirma la lista definitiva antes del partido.
     *
     * @throws ReglaNegocioException si no se alcanza el mínimo de jugadores (RN-05)
     */
    public void confirmarLista(int idEvento) {
        int convocados = convocatoriaDAO.listarPorEvento(idEvento).size();
        if (convocados < MINIMO_CONVOCADOS) {
            throw new ReglaNegocioException("RN-05",
                "La convocatoria tiene " + convocados + " jugadores y el mínimo para disputar "
              + "el partido es " + MINIMO_CONVOCADOS + ".");
        }
    }

    public boolean quitarConvocado(int idEvento, int idJugador) {
        return convocatoriaDAO.eliminar(idEvento, idJugador);
    }
}
