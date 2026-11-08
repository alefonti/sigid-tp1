package ar.org.estrelladelsur.sigid.servicio;

import ar.org.estrelladelsur.sigid.archivos.ArchivoCSV;
import ar.org.estrelladelsur.sigid.archivos.JugadorCSV;
import ar.org.estrelladelsur.sigid.archivos.PresentismoCSV;
import ar.org.estrelladelsur.sigid.archivos.RegistroErrores;
import ar.org.estrelladelsur.sigid.modelo.Division;
import ar.org.estrelladelsur.sigid.modelo.Jugador;
import ar.org.estrelladelsur.sigid.persistencia.JugadorDAO;
import ar.org.estrelladelsur.sigid.persistencia.PersistenciaException;
import ar.org.estrelladelsur.sigid.servicio.ReporteService.ReportePresentismo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TP4 - Realiza el CU14 - Exportar información y el CU15 - Importar
 * inscripciones. Conecta los archivos CSV con las reglas del negocio:
 * un jugador importado pasa por las mismas validaciones que uno cargado
 * a mano (RN-00, RN-01, RN-02 y RN-06 de JugadorService).
 */
public class ArchivoService {

    /** Resumen de una importación, para mostrar en pantalla. */
    public record ResultadoImportacion(int leidas, int registrados, List<String> errores) { }

    private final JugadorDAO     jugadorDAO;
    private final JugadorService jugadores;
    private final ClubService    club;
    private final JugadorCSV     formatoJugador     = new JugadorCSV();
    private final PresentismoCSV formatoPresentismo = new PresentismoCSV();

    public ArchivoService(JugadorDAO jugadorDAO, JugadorService jugadores, ClubService club) {
        this.jugadorDAO = jugadorDAO;
        this.jugadores  = jugadores;
        this.club       = club;
    }

    public int exportarPadron(Path destino) {
        return formatoJugador.escribir(destino, jugadorDAO.listarTodos());
    }

    public int exportarPresentismo(ReportePresentismo reporte, Path destino) {
        // el arreglo ordenado se ve como List sin copiarlo (Arrays.asList)
        return formatoPresentismo.escribir(destino, Arrays.asList(reporte.filas()));
    }

    /**
     * Lee el archivo y registra cada jugador por separado: si uno no cumple
     * una regla, se anota el motivo y se sigue con el resto.
     */
    public ResultadoImportacion importarJugadores(Path origen) {
        ArchivoCSV.Lectura<Jugador> lectura = formatoJugador.leer(origen);
        List<String> errores = new ArrayList<>(lectura.errores());
        int registrados = 0;

        for (Jugador j : lectura.registros()) {
            String quien = j.getApellido() + " (DNI " + j.getDocumento() + "): ";
            try {
                Division division = club.division(j.getIdDivision());
                jugadores.registrar(j, division);
                registrados++;
            } catch (ReglaNegocioException e) {
                errores.add(quien + e.getMessage());
            } catch (PersistenciaException e) {
                errores.add(quien + "error de base de datos, ver logs.");
                RegistroErrores.registrar("Importación de " + origen, e);
            }
        }
        int leidas = lectura.registros().size() + lectura.errores().size();
        return new ResultadoImportacion(leidas, registrados, errores);
    }
}
