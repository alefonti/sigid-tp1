package ar.org.estrelladelsur.sigid.persistencia;

import ar.org.estrelladelsur.sigid.modelo.Division;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del contrato DivisionDAO sobre MySQL 8.0. */
public class DivisionDAOMySQL implements DivisionDAO {

    @Override
    public List<Division> listarTodas() {
        String sql = "SELECT * FROM division ORDER BY anio_nac_desde DESC";
        List<Division> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar las divisiones", e);
        }
    }

    @Override
    public Division buscarPorId(int idDivision) {
        String sql = "SELECT * FROM division WHERE id_division = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idDivision);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar la división " + idDivision, e);
        }
    }

    private Division mapear(ResultSet rs) throws SQLException {
        Division d = new Division(rs.getInt("id_division"), rs.getString("nombre"),
                rs.getString("categoria"), rs.getInt("anio_nac_desde"),
                rs.getInt("anio_nac_hasta"), rs.getInt("cupo_maximo"));
        d.setIdEntrenador(rs.getInt("id_entrenador"));
        return d;
    }
}
