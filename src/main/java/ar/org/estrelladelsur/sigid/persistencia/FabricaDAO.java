package ar.org.estrelladelsur.sigid.persistencia;

/**
 * ABSTRACCIÓN de la fuente de datos (patrón Abstract Factory).
 *
 * La capa de servicio y la interfaz de usuario piden sus DAO a una
 * FabricaDAO sin saber si del otro lado hay un MySQL o una base en
 * memoria. Cambiar de motor implica cambiar una sola línea en Main.
 */
public abstract class FabricaDAO {

    public abstract JugadorDAO      jugadores();
    public abstract DivisionDAO     divisiones();
    public abstract EventoDAO       eventos();
    public abstract AsistenciaDAO   asistencias();
    public abstract ConvocatoriaDAO convocatorias();
    public abstract FichaMedicaDAO  fichasMedicas();
    public abstract LesionDAO       lesiones();
    public abstract TutorDAO        tutores();
    public abstract NotificacionDAO notificaciones();
    public abstract PersonalDAO     personal();

    /** Texto que la consola muestra para indicar contra qué fuente se trabaja. */
    public abstract String descripcion();
}
