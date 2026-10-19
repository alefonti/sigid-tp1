package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Entrenador;
import ar.org.estrelladelsur.sigid.modelo.ProfesionalMedico;
import java.util.List;

/** Contrato de persistencia del personal del club: cuerpo técnico y área médica. */
public interface PersonalDAO {
    List<Entrenador>        listarEntrenadores();
    List<ProfesionalMedico> listarProfesionales();
}
