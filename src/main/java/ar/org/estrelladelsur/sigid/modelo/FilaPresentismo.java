package ar.org.estrelladelsur.sigid.modelo;

/**
 * Fila del reporte de presentismo de un jugador.
 *
 * Los contadores se guardan en un ARREGLO de enteros indexado por el
 * ordinal de EstadoAsistencia: como la cantidad de estados es fija
 * (PRESENTE, AUSENTE, TARDE, JUSTIFICADO), un arreglo es la estructura
 * más simple y eficiente; acceder a conteo[estado.ordinal()] es directo.
 */
public class FilaPresentismo {

    private final int    idJugador;
    private final String apellido;
    private final String nombre;
    private final int[]  conteo = new int[EstadoAsistencia.values().length];

    public FilaPresentismo(int idJugador, String apellido, String nombre) {
        this.idJugador = idJugador;
        this.apellido  = apellido;
        this.nombre    = nombre;
    }

    /** Suma registros de un estado (la consulta SQL devuelve un total por estado). */
    public void sumar(EstadoAsistencia estado, int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        conteo[estado.ordinal()] += cantidad;
    }

    public int cantidad(EstadoAsistencia estado) {
        return conteo[estado.ordinal()];
    }

    /** Total de eventos con asistencia registrada. */
    public int total() {
        int suma = 0;
        for (int valor : conteo) {          // recorrido del arreglo con for-each
            suma += valor;
        }
        return suma;
    }

    /** PRESENTE y TARDE cuentan como asistencia efectiva (mismo criterio que el TP3). */
    public int efectivos() {
        return cantidad(EstadoAsistencia.PRESENTE) + cantidad(EstadoAsistencia.TARDE);
    }

    /** Porcentaje entre 0 y 100 con un decimal; 0 si no hay registros. */
    public double porcentaje() {
        int total = total();
        return total == 0 ? 0d : Math.round(efectivos() * 1000d / total) / 10d;
    }

    /** Copia defensiva: quien la reciba no puede modificar los contadores internos. */
    public int[] getConteo() { return conteo.clone(); }

    public int    getIdJugador()      { return idJugador; }
    public String getApellido()       { return apellido; }
    public String getNombre()         { return nombre; }
    public String getNombreCompleto() { return apellido + ", " + nombre; }

    @Override public String toString() {
        return getNombreCompleto() + " " + porcentaje() + "%";
    }
}
