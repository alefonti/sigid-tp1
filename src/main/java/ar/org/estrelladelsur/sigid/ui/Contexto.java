package ar.org.estrelladelsur.sigid.ui;

import ar.org.estrelladelsur.sigid.persistencia.FabricaDAO;
import ar.org.estrelladelsur.sigid.servicio.*;
import ar.org.estrelladelsur.sigid.servicio.historial.HistorialOperaciones;

import java.time.LocalDate;

/**
 * Agrupa los servicios de la sesión. Se construye una sola vez en Main
 * a partir de la fábrica de DAO elegida (MySQL o memoria) y se comparte
 * entre todos los menús: así cada menú recibe sus dependencias por
 * constructor en lugar de crearlas por su cuenta.
 */
public class Contexto {

    private final FabricaDAO           fabrica;
    private final JugadorService       jugadores;
    private final AsistenciaService    asistencias;
    private final FichaMedicaService   fichasMedicas;
    private final NotificacionService  notificaciones;
    private final ConvocatoriaService  convocatorias;
    private final AgendaService        agenda;
    private final ClubService          club;
    private final HistorialOperaciones historial = new HistorialOperaciones();

    public Contexto(FabricaDAO fabrica) {
        this.fabrica        = fabrica;
        this.jugadores      = new JugadorService(fabrica.jugadores());
        this.asistencias    = new AsistenciaService(fabrica.asistencias());
        this.fichasMedicas  = new FichaMedicaService(fabrica.fichasMedicas(), fabrica.lesiones());
        this.notificaciones = new NotificacionService(fabrica.tutores(), fabrica.notificaciones());
        this.convocatorias  = new ConvocatoriaService(fabrica.convocatorias(), fichasMedicas, notificaciones);
        this.agenda         = new AgendaService(fabrica.eventos());
        this.club           = new ClubService(fabrica.divisiones(), fabrica.jugadores(),
                                              fabrica.tutores(), fabrica.personal());
    }

    /** Fecha de trabajo. Puede fijarse con -Dsigid.hoy=AAAA-MM-DD para demostraciones. */
    public LocalDate hoy() {
        String fijada = System.getProperty("sigid.hoy");
        return fijada != null ? LocalDate.parse(fijada) : LocalDate.now();
    }

    public FabricaDAO           fabrica()        { return fabrica; }
    public JugadorService       jugadores()      { return jugadores; }
    public AsistenciaService    asistencias()    { return asistencias; }
    public FichaMedicaService   fichasMedicas()  { return fichasMedicas; }
    public NotificacionService  notificaciones() { return notificaciones; }
    public ConvocatoriaService  convocatorias()  { return convocatorias; }
    public AgendaService        agenda()         { return agenda; }
    public ClubService          club()           { return club; }
    public HistorialOperaciones historial()      { return historial; }
}
