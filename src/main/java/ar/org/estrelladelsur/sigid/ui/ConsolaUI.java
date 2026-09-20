package ar.org.estrelladelsur.sigid.ui;

import ar.org.estrelladelsur.sigid.modelo.*;
import ar.org.estrelladelsur.sigid.persistencia.*;
import ar.org.estrelladelsur.sigid.servicio.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Capa de presentación del prototipo operacional.
 * Sólo resuelve el diálogo con el usuario: toda la validación
 * de reglas de negocio vive en la capa de servicio.
 */
public class ConsolaUI {

    private final Scanner sc = new Scanner(System.in);

    private final JugadorService      jugadorService;
    private final AsistenciaService   asistenciaService;
    private final ConvocatoriaService convocatoriaService;
    private final FichaMedicaService  fichaMedicaService;

    public ConsolaUI() {
        JugadorDAO      jugadorDAO      = new JugadorDAOMySQL();
        FichaMedicaDAO  fichaDAO        = new FichaMedicaDAOMySQL();
        LesionDAO       lesionDAO       = new LesionDAOMySQL();
        AsistenciaDAO   asistenciaDAO   = new AsistenciaDAOMySQL();
        ConvocatoriaDAO convocatoriaDAO = new ConvocatoriaDAOMySQL();
        TutorDAO        tutorDAO        = new TutorDAOMySQL();
        NotificacionDAO notificacionDAO = new NotificacionDAOMySQL();

        this.jugadorService      = new JugadorService(jugadorDAO);
        this.asistenciaService   = new AsistenciaService(asistenciaDAO);
        this.fichaMedicaService  = new FichaMedicaService(fichaDAO, lesionDAO);
        this.convocatoriaService = new ConvocatoriaService(
                convocatoriaDAO, fichaMedicaService,
                new NotificacionService(tutorDAO, notificacionDAO));
    }

    public void ejecutar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            switch (leerTexto("Opción")) {
                case "1" -> altaJugador();
                case "2" -> listarPlantel();
                case "3" -> tomarAsistencia();
                case "4" -> armarConvocatoria();
                case "5" -> consultarAptitud();
                case "0" -> salir = true;
                default  -> System.out.println("Opción inválida.");
            }
        }
        System.out.println("Fin de la sesión.");
    }

    private void mostrarMenu() {
        System.out.println("""

            ===========================================================
              SIGID - Club Social y Deportivo "Estrella del Sur"
            ===========================================================
              1. Registrar jugador               (CU01)
              2. Listar plantel de una división  (CU07)
              3. Registrar asistencia            (CU03)
              4. Generar convocatoria            (CU05)
              5. Consultar aptitud médica        (CU10)
              0. Salir
            ===========================================================""");
    }

    // ---------------------------------------------------------------- CU01
    private void altaJugador() {
        try {
            Jugador j = new Jugador(
                    leerTexto("Documento"),
                    leerTexto("Apellido"),
                    leerTexto("Nombre"),
                    LocalDate.parse(leerTexto("Fecha de nacimiento (AAAA-MM-DD)")),
                    leerEntero("Id de división"));

            // En la versión final la división se recupera con DivisionDAO.
            Division division = new Division(j.getIdDivision(), "Sub-13", "SUB13", 2013, 2013, 25);

            int id = jugadorService.registrar(j, division);
            System.out.println("Jugador registrado con el identificador " + id + ".");
        } catch (ReglaNegocioException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Dato inválido: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- CU07
    private void listarPlantel() {
        List<Jugador> plantel = jugadorService.buscarPorDivision(leerEntero("Id de división"));
        if (plantel.isEmpty()) {
            System.out.println("La división no tiene jugadores activos.");
            return;
        }
        System.out.printf("%-5s %-28s %-12s %s%n", "ID", "JUGADOR", "DOCUMENTO", "ASISTENCIA");
        for (Jugador j : plantel) {
            System.out.printf("%-5d %-28s %-12s %.1f %%%n",
                    j.getIdJugador(), j.nombreCompleto(), j.getDocumento(),
                    asistenciaService.porcentajeAsistencia(j.getIdJugador()));
        }
    }

    // ---------------------------------------------------------------- CU03
    private void tomarAsistencia() {
        int idEvento = leerEntero("Id del evento");
        for (Jugador j : jugadorService.buscarPorDivision(leerEntero("Id de división"))) {
            String opcion = leerTexto(j.nombreCompleto() + " [P]resente / [A]usente / [T]arde");
            EstadoAsistencia estado = switch (opcion.toUpperCase()) {
                case "A" -> EstadoAsistencia.AUSENTE;
                case "T" -> EstadoAsistencia.TARDE;
                default  -> EstadoAsistencia.PRESENTE;
            };
            try {
                int idAsistencia = asistenciaService.registrar(idEvento, j.getIdJugador(), estado);
                // CU11 «extend»: sólo si el jugador figura ausente
                if (estado == EstadoAsistencia.AUSENTE) {
                    String motivo = leerTexto("  Motivo (Enter para omitir)");
                    if (!motivo.isBlank()) {
                        asistenciaService.justificarInasistencia(idAsistencia, motivo);
                    }
                }
            } catch (ReglaNegocioException e) {
                System.out.println("  " + e.getMessage());
            }
        }
        System.out.println("Asistencia registrada.");
    }

    // ---------------------------------------------------------------- CU05
    private void armarConvocatoria() {
        int idEvento = leerEntero("Id del partido");
        List<Integer> seleccionados = new ArrayList<>();
        for (Jugador j : jugadorService.buscarPorDivision(leerEntero("Id de división"))) {
            if (leerTexto("¿Convocar a " + j.nombreCompleto() + "? [s/n]").equalsIgnoreCase("s")) {
                seleccionados.add(j.getIdJugador());
            }
        }
        try {
            ResultadoConvocatoria r =
                    convocatoriaService.convocar(idEvento, seleccionados, LocalDate.now());

            System.out.println("\n" + r);
            r.getRechazados().forEach((id, motivo) ->
                    System.out.println("  Bloqueado el jugador " + id + ": " + motivo));

            convocatoriaService.confirmarLista(idEvento);
            System.out.println("Convocatoria confirmada.");
        } catch (ReglaNegocioException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------------------------------------------------------------- CU10
    private void consultarAptitud() {
        int idJugador = leerEntero("Id del jugador");
        String motivo = fichaMedicaService.motivoDeBloqueo(idJugador, LocalDate.now());
        System.out.println(motivo == null
                ? "El jugador está habilitado para ser convocado."
                : "El jugador no está habilitado porque " + motivo + ".");
    }

    // ---------------------------------------------------------------- utilidades
    private String leerTexto(String etiqueta) {
        System.out.print(etiqueta + ": ");
        return sc.nextLine().trim();
    }

    private int leerEntero(String etiqueta) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(etiqueta));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero.");
            }
        }
    }
}
