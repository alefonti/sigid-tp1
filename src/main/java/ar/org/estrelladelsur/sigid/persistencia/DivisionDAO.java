package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Division;
import java.util.List;

/** Contrato de persistencia de divisiones (nuevo en el TP3). */
public interface DivisionDAO {
    List<Division> listarTodas();
    Division       buscarPorId(int idDivision);
}
