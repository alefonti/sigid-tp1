package ar.org.estrelladelsur.sigid.persistencia.memoria;

import ar.org.estrelladelsur.sigid.modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Datos de arranque del modo memoria. Son los mismos registros que
 * inserta sql/02_datos.sql, en el mismo orden, de modo que los
 * identificadores coinciden en los dos modos de ejecución.
 */
final class DatosDemo {

    private DatosDemo() { }

    static void cargar(FabricaDAOMemoria f) {
        cargarPersonal(f);
        cargarDivisiones(f);
        cargarJugadores(f);
        cargarTutores(f);
        cargarAreaMedica(f);
        cargarAgenda(f);
        cargarAsistencias(f);
        cargarConvocatoriasYNotificaciones(f);
    }

    private static void cargarPersonal(FabricaDAOMemoria f) {
        f.entrenadores.agregar(entrenador(1, "28456123", "López",   "Ricardo", "AFA-10233"));
        f.entrenadores.agregar(entrenador(2, "30112456", "Suárez",  "Matías",  "AFA-11894"));
        f.entrenadores.agregar(entrenador(3, "25998741", "Peralta", "Gustavo", "AFA-09117"));

        ProfesionalMedico p1 = new ProfesionalMedico("Domínguez", "Carolina", "MN-84512", "Medicina deportiva");
        p1.setId(1);
        ProfesionalMedico p2 = new ProfesionalMedico("Ruiz", "Esteban", "MN-77310", "Kinesiología");
        p2.setId(2);
        f.profesionales.agregar(p1);
        f.profesionales.agregar(p2);
    }

    private static Entrenador entrenador(int id, String doc, String ape, String nom, String lic) {
        Entrenador e = new Entrenador(doc, ape, nom, lic);
        e.setId(id);
        return e;
    }

    private static void cargarDivisiones(FabricaDAOMemoria f) {
        Division d1 = new Division(1, "Sub-13", "SUB13", 2013, 2013, 25); d1.setIdEntrenador(1);
        Division d2 = new Division(2, "Sub-15", "SUB15", 2011, 2011, 25); d2.setIdEntrenador(2);
        Division d3 = new Division(3, "Sub-17", "SUB17", 2009, 2009, 28); d3.setIdEntrenador(3);
        f.divisiones.agregar(d1);
        f.divisiones.agregar(d2);
        f.divisiones.agregar(d3);
    }

    private static void cargarJugadores(FabricaDAOMemoria f) {
        Object[][] filas = {
            {"55123456", "Acuña",     "Tomás",     "2013-03-14", "2025-02-10", "ACTIVO",   1},
            {"55234567", "Benítez",   "Lucas",     "2013-07-02", "2025-02-10", "ACTIVO",   1},
            {"55345678", "Cabrera",   "Joaquín",   "2013-11-21", "2025-03-01", "ACTIVO",   1},
            {"55456789", "Domínguez", "Bautista",  "2013-01-09", "2025-03-01", "ACTIVO",   1},
            {"54123456", "Escobar",   "Thiago",    "2011-05-30", "2024-02-15", "ACTIVO",   2},
            {"54234567", "Figueroa",  "Iván",      "2011-09-12", "2024-02-15", "ACTIVO",   2},
            {"54345678", "Gómez",     "Santino",   "2011-12-04", "2024-08-20", "ACTIVO",   2},
            {"53123456", "Herrera",   "Nicolás",   "2009-04-18", "2023-03-06", "ACTIVO",   3},
            {"53234567", "Ibáñez",    "Franco",    "2009-08-25", "2023-03-06", "ACTIVO",   3},
            {"53345678", "Juárez",    "Valentín",  "2009-02-11", "2024-03-04", "INACTIVO", 3},
            // incorporados en el TP3 para completar el plantel Sub-13
            {"55567890", "Ferreyra",  "Mateo",     "2013-04-22", "2026-03-02", "ACTIVO",   1},
            {"55678901", "Giménez",   "Benjamín",  "2013-06-30", "2026-03-02", "ACTIVO",   1},
            {"55789012", "Ledesma",   "Felipe",    "2013-08-15", "2026-03-02", "ACTIVO",   1},
            {"55890123", "Medina",    "Lautaro",   "2013-10-03", "2026-03-09", "ACTIVO",   1},
            {"55901234", "Núñez",     "Ignacio",   "2013-12-19", "2026-03-09", "ACTIVO",   1},
            {"56012345", "Ortiz",     "Valentino", "2013-02-27", "2026-03-09", "ACTIVO",   1},
            {"56123456", "Paz",       "Agustín",   "2013-05-08", "2026-04-06", "ACTIVO",   1},
            {"56234567", "Quiroga",   "Dante",     "2013-09-11", "2026-04-06", "ACTIVO",   1},
            {"56345678", "Romero",    "Emiliano",  "2013-07-25", "2026-04-06", "ACTIVO",   1},
        };
        for (Object[] r : filas) {
            Jugador j = new Jugador((String) r[0], (String) r[1], (String) r[2],
                    LocalDate.parse((String) r[3]), (Integer) r[6]);
            j.setFechaAlta(LocalDate.parse((String) r[4]));
            j.setEstado(EstadoJugador.valueOf((String) r[5]));
            f.jugadores().insertar(j);
        }
    }

    private static void cargarTutores(FabricaDAOMemoria f) {
        Object[][] filas = {
            {1,  "27888111", "Acuña",    "Marcela",  "11-3344-5566", "macuna@correo.com"},
            {2,  "29112333", "Benítez",  "Jorge",    "11-2233-4455", "jbenitez@correo.com"},
            {3,  "31445666", "Cabrera",  "Silvina",  "11-7788-9900", "scabrera@correo.com"},
            {4,  "26778999", "Escobar",  "Daniel",   "11-4411-2200", "descobar@correo.com"},
            {5,  "28334555", "Herrera",  "Patricia", "11-9911-3344", "pherrera@correo.com"},
            {6,  "30556777", "Acuña",    "Sergio",   "11-6677-1122", "sacuna@correo.com"},
            {7,  "29667888", "Juárez",   "Andrea",   "11-2255-6677", "ajuarez@correo.com"},
            {8,  "32778899", "Ferreyra", "Mónica",   "11-5566-7788", "mferreyra@correo.com"},
            {9,  "27445566", "Medina",   "Carlos",   "11-3322-1100", null},
            {10, "33889900", "Romero",   "Gabriela", "11-8877-6655", "gromero@correo.com"},
        };
        for (Object[] r : filas) {
            Tutor t = new Tutor((Integer) r[0], (String) r[2], (String) r[3], (String) r[4], (String) r[5]);
            t.setDocumento((String) r[1]);
            f.tutores.agregar(t);
        }
        // {id_jugador, id_tutor, contacto_primario(1/0)} + parentesco
        int[][] vinculos = {{1,1,1},{1,6,0},{2,2,1},{3,3,1},{5,4,1},{8,5,1},{10,7,1},
                            {11,8,1},{14,9,1},{19,10,1}};
        String[] parentescos = {"Madre","Padre","Padre","Madre","Padre","Madre","Madre",
                                "Madre","Padre","Madre"};
        for (int i = 0; i < vinculos.length; i++) {
            f.vinculos.agregar(vinculos[i]);
            Tutor t = f.tutores().buscarPorId(vinculos[i][1]);
            if (t.getParentesco() == null) {
                t.setParentesco(parentescos[i]);
            }
        }
    }

    private static void cargarAreaMedica(FabricaDAOMemoria f) {
        Object[][] fichas = {
            {1, "2026-03-01", "2027-03-01", true}, {2, "2026-02-20", "2027-02-20", true},
            {3, "2025-04-10", "2026-04-10", true}, {4, "2026-03-05", "2027-03-05", true},
            {5, "2026-01-15", "2027-01-15", true}, {6, "2026-02-02", "2027-02-02", false},
            {7, "2026-03-12", "2027-03-12", true}, {8, "2026-02-28", "2027-02-28", true},
            {9, "2026-03-18", "2027-03-18", true},
            {11, "2026-03-02", "2027-03-02", true}, {12, "2026-03-02", "2027-03-02", true},
            {13, "2026-03-03", "2027-03-03", true}, {14, "2026-03-09", "2027-03-09", true},
            {15, "2026-03-09", "2027-03-09", true}, {16, "2026-03-10", "2027-03-10", true},
            {17, "2026-04-06", "2027-04-06", true}, {18, "2026-04-06", "2027-04-06", true},
            {19, "2026-04-07", "2027-04-07", true},
        };
        for (Object[] r : fichas) {
            FichaMedica fm = new FichaMedica((Integer) r[0], 1, LocalDate.parse((String) r[1]),
                    LocalDate.parse((String) r[2]), (Boolean) r[3]);
            f.fichasMedicas().insertar(fm);
        }
        lesion(f, 2,  "Esguince de tobillo grado I",  Gravedad.LEVE,     "2026-08-02", "2026-08-25");
        lesion(f, 7,  "Distensión de isquiotibiales", Gravedad.MODERADA, "2026-09-05", null);
        lesion(f, 9,  "Contractura lumbar",           Gravedad.LEVE,     "2026-09-12", null);
        lesion(f, 10, "Fractura de escafoides",       Gravedad.GRAVE,    "2026-06-18", "2026-09-01");
    }

    private static void lesion(FabricaDAOMemoria f, int jug, String tipo, Gravedad g,
                               String inicio, String alta) {
        Lesion l = new Lesion(jug, tipo, g, LocalDate.parse(inicio));
        l.setIdProfesional(2);
        if (alta != null) {
            l.setFechaAltaReal(LocalDate.parse(alta));
        }
        f.lesiones().insertar(l);
    }

    private static void cargarAgenda(FabricaDAOMemoria f) {
        entrenamiento(f, "2026-09-15", "17:00", "18:30", 1, 1, "FINALIZADO");
        entrenamiento(f, "2026-09-17", "17:00", "18:30", 1, 1, "FINALIZADO");
        partido(f, "2026-09-27", "10:00", "11:30", 1, 1, "Club Atlético Norte", "LOCAL", "FINALIZADO");
        entrenamiento(f, "2026-09-16", "19:00", "20:30", 2, 2, "FINALIZADO");
        partido(f, "2026-09-28", "12:00", "13:30", 2, 2, "Deportivo Oeste", "VISITANTE", "FINALIZADO");
        entrenamiento(f, "2026-09-18", "19:00", "20:30", 3, 3, "FINALIZADO");
        // agenda de octubre/noviembre incorporada en el TP3
        entrenamiento(f, "2026-10-20", "17:00", "18:30", 1, 1, "PROGRAMADO");
        entrenamiento(f, "2026-10-22", "17:00", "18:30", 1, 1, "PROGRAMADO");
        partido(f, "2026-10-25", "10:00", "11:30", 1, 1, "Social Lugano", "LOCAL", "PROGRAMADO");
        partido(f, "2026-11-01", "12:00", "13:30", 2, 2, "Villa Real", "VISITANTE", "PROGRAMADO");
    }

    private static void entrenamiento(FabricaDAOMemoria f, String fecha, String ini, String fin,
                                      int div, int sede, String estado) {
        Evento e = new Entrenamiento(LocalDate.parse(fecha), LocalTime.parse(ini),
                LocalTime.parse(fin), div, sede);
        e.setEstado(estado);
        f.eventos().insertar(e);
    }

    private static void partido(FabricaDAOMemoria f, String fecha, String ini, String fin,
                                int div, int sede, String rival, String cond, String estado) {
        Evento e = new Partido(LocalDate.parse(fecha), LocalTime.parse(ini),
                LocalTime.parse(fin), div, sede, rival, cond);
        e.setEstado(estado);
        f.eventos().insertar(e);
    }

    private static void cargarAsistencias(FabricaDAOMemoria f) {
        Object[][] filas = {
            {1, 1, "PRESENTE", null}, {1, 2, "PRESENTE", null}, {1, 3, "AUSENTE", null},
            {1, 4, "TARDE", "Llegó 20 minutos después"},
            {2, 1, "PRESENTE", null}, {2, 2, "JUSTIFICADO", "Certificado escolar"},
            {2, 3, "AUSENTE", null}, {2, 4, "PRESENTE", null},
            {4, 5, "PRESENTE", null}, {4, 6, "PRESENTE", null},
            {4, 7, "JUSTIFICADO", "Lesión en tratamiento"},
            {6, 8, "PRESENTE", null}, {6, 9, "AUSENTE", null},
            {6, 10, "JUSTIFICADO", "Jugador con baja temporaria"},
        };
        for (Object[] r : filas) {
            Asistencia a = new Asistencia((Integer) r[0], (Integer) r[1],
                    EstadoAsistencia.valueOf((String) r[2]));
            a.setMotivo((String) r[3]);
            f.asistencias().insertar(a);
        }
    }

    private static void cargarConvocatoriasYNotificaciones(FabricaDAOMemoria f) {
        Convocatoria c1 = new Convocatoria(3, 1); c1.setConfirmada(true);
        Convocatoria c2 = new Convocatoria(3, 2); c2.setConfirmada(true);
        Convocatoria c3 = new Convocatoria(3, 4);
        f.convocatorias().insertar(c1);
        f.convocatorias().insertar(c2);
        f.convocatorias().insertar(c3);

        notificacion(f, 1, 3, CanalNotificacion.EMAIL,
                "Tomás fue convocado al partido del 27/09 a las 10:00 en el predio del club.", "ENVIADA");
        notificacion(f, 6, 3, CanalNotificacion.SMS,
                "Tomás fue convocado al partido del 27/09 a las 10:00.", "ENVIADA");
        notificacion(f, 2, 3, CanalNotificacion.EMAIL,
                "Lucas fue convocado al partido del 27/09 a las 10:00 en el predio del club.", "ENVIADA");
        notificacion(f, 3, 9, CanalNotificacion.EMAIL,
                "Joaquín tiene la ficha médica vencida: debe renovarla antes del partido del 25/10.",
                "PENDIENTE");
    }

    private static void notificacion(FabricaDAOMemoria f, int tutor, int evento,
                                     CanalNotificacion canal, String msj, String estado) {
        Notificacion n = new Notificacion(tutor, evento, canal, msj);
        n.setEstadoEnvio(estado);
        f.notificaciones().insertar(n);
    }
}
