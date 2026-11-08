package ar.org.estrelladelsur.sigid.archivos;

import ar.org.estrelladelsur.sigid.comun.DatoInvalidoException;
import ar.org.estrelladelsur.sigid.modelo.EstadoJugador;
import ar.org.estrelladelsur.sigid.modelo.Jugador;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Formato CSV del padrón de jugadores. Se usa en los dos sentidos:
 * exportar el padrón para la secretaría y cargar en bloque las
 * inscripciones que llegan en una planilla al inicio de la temporada.
 *
 *   documento;apellido;nombre;fecha_nacimiento;id_division;estado
 *   55667788;Ibarra;Facundo;14/02/2013;1;ACTIVO
 */
public class JugadorCSV extends ArchivoCSV<Jugador> {

    /** STRICT: rechaza fechas inexistentes como 31/02 en lugar de "corregirlas". */
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final String[] COLUMNAS =
            {"documento", "apellido", "nombre", "fecha_nacimiento", "id_division", "estado"};

    @Override
    protected String[] encabezado() {
        return COLUMNAS.clone();
    }

    @Override
    protected String[] aCampos(Jugador j) {
        return new String[] {
            j.getDocumento(), j.getApellido(), j.getNombre(),
            j.getFechaNacimiento().format(FECHA),
            String.valueOf(j.getIdDivision()),
            j.getEstado().name()
        };
    }

    @Override
    protected Jugador desdeCampos(String[] c) throws DatoInvalidoException {
        for (int i = 0; i < 5; i++) {                  // las 5 primeras columnas son obligatorias
            if (c[i].isEmpty()) {
                throw new DatoInvalidoException(COLUMNAS[i], "falta el dato \"" + COLUMNAS[i] + "\".");
            }
        }
        LocalDate nacimiento;
        try {
            nacimiento = LocalDate.parse(c[3], FECHA);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException("fecha_nacimiento", "'" + c[3] + "' no es una fecha válida (DD/MM/AAAA).", e);
        }
        int idDivision;
        try {
            idDivision = Integer.parseInt(c[4]);
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("id_division", "'" + c[4] + "' no es un número de división.", e);
        }

        // los setters del modelo validan el formato del documento y la fecha
        // (lanzan IllegalArgumentException, que ArchivoCSV anota como error de la línea)
        Jugador j = new Jugador(c[0], c[1], c[2], null, idDivision);
        j.setFechaNacimiento(nacimiento);
        if (!c[5].isEmpty()) {
            try {
                j.setEstado(EstadoJugador.valueOf(c[5].toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new DatoInvalidoException("estado", "el estado '" + c[5] + "' no existe.", e);
            }
        }
        return j;
    }
}
