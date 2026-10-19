package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Entrenador;
import ar.org.estrelladelsur.sigid.modelo.ProfesionalMedico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del contrato PersonalDAO sobre MySQL 8.0. */
public class PersonalDAOMySQL implements PersonalDAO {

    @Override
    public List<Entrenador> listarEntrenadores() {
        String sql = "SELECT * FROM entrenador";
        List<Entrenador> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Entrenador e = new Entrenador(rs.getString("documento"), rs.getString("apellido"),
                        rs.getString("nombre"), rs.getString("nro_licencia"));
                e.setId(rs.getInt("id_entrenador"));
                e.setTelefono(rs.getString("telefono"));
                e.setEmail(rs.getString("email"));
                lista.add(e);
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los entrenadores", e);
        }
    }

    @Override
    public List<ProfesionalMedico> listarProfesionales() {
        String sql = "SELECT * FROM profesional";
        List<ProfesionalMedico> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProfesionalMedico p = new ProfesionalMedico(rs.getString("apellido"),
                        rs.getString("nombre"), rs.getString("matricula"), rs.getString("especialidad"));
                p.setId(rs.getInt("id_profesional"));
                lista.add(p);
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los profesionales", e);
        }
    }
}
