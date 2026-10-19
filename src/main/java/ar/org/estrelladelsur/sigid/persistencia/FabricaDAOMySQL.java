package ar.org.estrelladelsur.sigid.persistencia;

/** Fábrica concreta que entrega las implementaciones JDBC sobre MySQL 8.0. */
public class FabricaDAOMySQL extends FabricaDAO {

    // Los DAO JDBC no guardan estado, así que se crean una sola vez y se reutilizan.
    private final JugadorDAO      jugadorDAO      = new JugadorDAOMySQL();
    private final DivisionDAO     divisionDAO     = new DivisionDAOMySQL();
    private final EventoDAO       eventoDAO       = new EventoDAOMySQL();
    private final AsistenciaDAO   asistenciaDAO   = new AsistenciaDAOMySQL();
    private final ConvocatoriaDAO convocatoriaDAO = new ConvocatoriaDAOMySQL();
    private final FichaMedicaDAO  fichaDAO        = new FichaMedicaDAOMySQL();
    private final LesionDAO       lesionDAO       = new LesionDAOMySQL();
    private final TutorDAO        tutorDAO        = new TutorDAOMySQL();
    private final NotificacionDAO notificacionDAO = new NotificacionDAOMySQL();
    private final PersonalDAO     personalDAO     = new PersonalDAOMySQL();

    @Override public JugadorDAO      jugadores()      { return jugadorDAO; }
    @Override public DivisionDAO     divisiones()     { return divisionDAO; }
    @Override public EventoDAO       eventos()        { return eventoDAO; }
    @Override public AsistenciaDAO   asistencias()    { return asistenciaDAO; }
    @Override public ConvocatoriaDAO convocatorias()  { return convocatoriaDAO; }
    @Override public FichaMedicaDAO  fichasMedicas()  { return fichaDAO; }
    @Override public LesionDAO       lesiones()       { return lesionDAO; }
    @Override public TutorDAO        tutores()        { return tutorDAO; }
    @Override public NotificacionDAO notificaciones() { return notificacionDAO; }
    @Override public PersonalDAO     personal()       { return personalDAO; }

    @Override public String descripcion() {
        return "MySQL (" + ConexionBD.getInstancia().getUrl().replaceAll("\\?.*", "") + ")";
    }
}
