package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.modelo.Convocatoria;
import ar.org.estrelladelsur.sigid.modelo.Evento;
import ar.org.estrelladelsur.sigid.modelo.Partido;
import ar.org.estrelladelsur.sigid.modelo.ResultadoConvocatoria;
import ar.org.estrelladelsur.sigid.persistencia.ConvocatoriaDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 *   RN-11: sólo los partidos admiten convocatoria.
 */
public class ConvocatoriaService {

    public static final int MAXIMO_CONVOCADOS = 18;
    public static final int MINIMO_CONVOCADOS = 11;

    private static final DateTimeFormatter DD_MM = DateTimeFormatter.ofPattern("dd/MM");

    private final ConvocatoriaDAO     convocatoriaDAO;
    private final FichaMedicaService  fichaMedicaService;
    private final NotificacionService notificacionService;

    public ConvocatoriaService(ConvocatoriaDAO convocatoriaDAO,
                               FichaMedicaService fichaMedicaService,
                               NotificacionService notificacionService) {
        this.convocatoriaDAO     = convocatoriaDAO;
        this.fichaMedicaService  = fichaMedicaService;
        this.notificacionService = notificacionService;
    }

    /**
     * SOBRECARGA (TP3): recibe el evento completo. Valida de forma
     * polimórfica que admita convocatoria y arma un mensaje con los datos
     * del partido para las familias.
     */
    public ResultadoConvocatoria convocar(Evento evento, List<Integer> idsJugadores, LocalDate fecha) {
        if (!evento.admiteConvocatoria()) {
            throw new ReglaNegocioException("RN-11",
                "El evento " + evento.getIdEvento() + " es un " + evento.getTipo()
              + " y sólo los partidos admiten convocatoria.");
        }
        String mensaje = NotificacionService.MENSAJE_CONVOCATORIA;
        if (evento instanceof Partido p) {
            mensaje = "Su hijo/a fue convocado/a al partido " + p.descripcion() + " del "
                    + p.getFecha().format(DD_MM) + " a las " + p.getHoraInicio()
                    + ". Presentarse 45 minutos antes con la ficha médica al día.";
        }
        return convocar(evento.getIdEvento(), idsJugadores, fecha, mensaje);
    }

    /** SOBRECARGA (TP2): versión por identificador, con mensaje genérico. */
    public ResultadoConvocatoria convocar(int idEvento, List<Integer> idsJugadores, LocalDate fecha) {
        return convocar(idEvento, idsJugadores, fecha, NotificacionService.MENSAJE_CONVOCATORIA);
    }

    /**
     * Los jugadores sin aptitud médica quedan bloqueados con su motivo y el
     * resto se registra; al finalizar se generan los avisos a los tutores.
     *
     * @throws ReglaNegocioException si se supera el máximo de convocados (RN-04)
     */
    private ResultadoConvocatoria convocar(int idEvento, List<Integer> idsJugadores,
                                           LocalDate fecha, String mensaje) {
        if (idsJugadores == null || idsJugadores.isEmpty()) {
            throw new ReglaNegocioException("RN-04", "La convocatoria no tiene jugadores seleccionados.");
        }
        if (idsJugadores.size() > MAXIMO_CONVOCADOS) {
            throw new ReglaNegocioException("RN-04",
                "Se seleccionaron " + idsJugadores.size() + " jugadores y el máximo permitido es "
              + MAXIMO_CONVOCADOS + ".");
        }

        ResultadoConvocatoria resultado = new ResultadoConvocatoria();
        List<Integer> yaConvocados = convocatoriaDAO.listarPorEvento(idEvento).stream()
                .map(Convocatoria::getIdJugador).toList();

        for (Integer idJugador : idsJugadores) {
            if (yaConvocados.contains(idJugador)) {
                resultado.agregarRechazo(idJugador, "ya figuraba en la convocatoria");
                continue;
            }
            String motivo = fichaMedicaService.motivoDeBloqueo(idJugador, fecha);
            if (motivo != null) {
                resultado.agregarRechazo(idJugador, motivo);
                continue;                                   // salta al siguiente jugador
            }
            convocatoriaDAO.insertar(new Convocatoria(idEvento, idJugador));
            resultado.agregarConvocado(idJugador);
        }

        resultado.setNotificacionesEnviadas(notificacionService.notificarTutores(idEvento, mensaje));
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
        convocatoriaDAO.confirmarTodas(idEvento);
    }

    public List<Convocatoria> convocadosDe(int idEvento) {
        return convocatoriaDAO.listarPorEvento(idEvento);
    }

    public boolean quitarConvocado(int idEvento, int idJugador) {
        return convocatoriaDAO.eliminar(idEvento, idJugador);
    }
}
