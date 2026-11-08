package ar.org.estrelladelsur.sigid.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clase ABSTRACTA base de todos los DAO JDBC (patrón DAO + Template Method).
 *
 * Cada DAO concreto repetía la misma secuencia: pedir la conexión, preparar
 * la sentencia, cargar los parámetros, ejecutar, recorrer el ResultSet,
 * cerrar todo y traducir la SQLException. Esa secuencia se escribe una sola
 * vez acá; las subclases sólo aportan el SQL y cómo convertir una fila en
 * un objeto del modelo (Mapeador).
 *
 * Los parámetros se reciben como un ARREGLO de Object (varargs), y los
 * resultados se devuelven en un ArrayList, porque la cantidad de filas que
 * trae una consulta no se conoce de antemano.
 */
public abstract class DAOMySQLBase {

    /** Convierte la fila actual del ResultSet en un objeto del modelo. */
    @FunctionalInterface
    protected interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    /** Bloque de trabajo que se ejecuta dentro de una transacción. */
    @FunctionalInterface
    protected interface Trabajo<R> {
        R ejecutar(Connection cn) throws SQLException;
    }

    /** Conexión nueva tomada del Singleton ConexionBD. */
    protected Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }

    // ================================================================ consultas

    /** SELECT que devuelve varias filas. */
    protected <T> ArrayList<T> consultarLista(String sql, Mapeador<T> mapeador,
                                              String error, Object... parametros) {
        ArrayList<T> resultado = new ArrayList<>();
        try (Connection cn = conexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapeador.mapear(rs));
                }
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException(error, e);
        }
    }

    /** SELECT que devuelve a lo sumo una fila; null si no hay resultados. */
    protected <T> T consultarUno(String sql, Mapeador<T> mapeador,
                                 String error, Object... parametros) {
        ArrayList<T> filas = consultarLista(sql, mapeador, error, parametros);
        return filas.isEmpty() ? null : filas.get(0);
    }

    /** SELECT COUNT(*) u otro valor entero único. */
    protected int consultarEntero(String sql, String error, Object... parametros) {
        Integer valor = consultarUno(sql, rs -> rs.getInt(1), error, parametros);
        return valor == null ? 0 : valor;
    }

    // ================================================================ actualizaciones

    /** INSERT / UPDATE / DELETE. Devuelve la cantidad de filas afectadas. */
    protected int actualizar(String sql, String error, Object... parametros) {
        try (Connection cn = conexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new PersistenciaException(error + ": el registro viola una restricción de la base", e);
        } catch (SQLException e) {
            throw new PersistenciaException(error, e);
        }
    }

    /** INSERT que devuelve la clave autonumérica generada por MySQL. */
    protected int insertar(String sql, String error, Object... parametros) {
        try (Connection cn = conexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarParametros(ps, parametros);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new PersistenciaException(error + ": el registro viola una restricción de la base", e);
        } catch (SQLException e) {
            throw new PersistenciaException(error, e);
        }
    }

    /**
     * Ejecuta varias sentencias como una unidad: o se confirman todas
     * (commit) o no se confirma ninguna (rollback). Si el rollback también
     * falla, ese segundo error se agrega como "suprimido" para no perderlo.
     */
    protected <R> R enTransaccion(Trabajo<R> trabajo, String error) {
        try (Connection cn = conexion()) {
            cn.setAutoCommit(false);
            try {
                R resultado = trabajo.ejecutar(cn);
                cn.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                try {
                    cn.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                if (e instanceof PersistenciaException pe) {
                    throw pe;
                }
                throw new PersistenciaException(error + " (se deshicieron los cambios)", e);
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException(error, e);
        }
    }

    // ================================================================ utilidades

    /** Carga el arreglo de parámetros en el PreparedStatement (posiciones 1..n). */
    protected static void cargarParametros(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object valor = parametros[i];
            if (valor == null) {
                ps.setNull(i + 1, java.sql.Types.VARCHAR);
            } else if (valor instanceof LocalDate fecha) {
                ps.setDate(i + 1, Date.valueOf(fecha));
            } else if (valor instanceof Enum<?> constante) {
                ps.setString(i + 1, constante.name());
            } else {
                ps.setObject(i + 1, valor);
            }
        }
    }
}
