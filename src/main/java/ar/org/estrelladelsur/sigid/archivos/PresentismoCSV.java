package ar.org.estrelladelsur.sigid.archivos;

import ar.org.estrelladelsur.sigid.modelo.EstadoAsistencia;
import ar.org.estrelladelsur.sigid.modelo.FilaPresentismo;

/**
 * Formato CSV del reporte de presentismo de una división. Es sólo de
 * salida: el entrenador lo abre en Excel o lo comparte con la comisión.
 */
public class PresentismoCSV extends ArchivoCSV<FilaPresentismo> {

    private static final EstadoAsistencia[] ESTADOS = EstadoAsistencia.values();

    /** Las columnas de estados se generan recorriendo el arreglo del enum. */
    @Override
    protected String[] encabezado() {
        String[] columnas = new String[ESTADOS.length + 3];
        columnas[0] = "id_jugador";
        columnas[1] = "jugador";
        for (int i = 0; i < ESTADOS.length; i++) {
            columnas[i + 2] = ESTADOS[i].name().toLowerCase();
        }
        columnas[columnas.length - 1] = "porcentaje";
        return columnas;
    }

    @Override
    protected String[] aCampos(FilaPresentismo f) {
        String[] campos = new String[ESTADOS.length + 3];
        campos[0] = String.valueOf(f.getIdJugador());
        campos[1] = f.getNombreCompleto();
        int[] conteo = f.getConteo();
        for (int i = 0; i < conteo.length; i++) {
            campos[i + 2] = String.valueOf(conteo[i]);
        }
        // coma decimal, como la espera la planilla de cálculo en es-AR
        campos[campos.length - 1] = String.valueOf(f.porcentaje()).replace('.', ',');
        return campos;
    }

    @Override
    protected FilaPresentismo desdeCampos(String[] campos) {
        throw new UnsupportedOperationException("El reporte de presentismo es sólo de salida.");
    }
}
