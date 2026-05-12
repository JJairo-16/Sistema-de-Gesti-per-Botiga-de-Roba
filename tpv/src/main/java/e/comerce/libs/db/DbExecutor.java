package e.comerce.libs.db;

import java.sql.SQLException;
import java.util.List;

import e.comerce.libs.db.functional.RowMapper;

/**
 * Contracte comú per executar operacions SQL.
 *
 * <p>
 * Permet que els repositoris treballin tant amb la base de dades habitual com
 * amb un context transaccional sense conèixer com s'obté la connexió.
 * </p>
 */
public interface DbExecutor {
    <T> T one(String sql, Params params, RowMapper<T> mapper) throws SQLException;

    default <T> T one(String sql, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.none(), mapper);
    }

    default <T> T one(String sql, Object param, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.of(param), mapper);
    }

    default <T> T one(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.of(params), mapper);
    }

    <T> List<T> list(String sql, Params params, RowMapper<T> mapper) throws SQLException;

    default <T> List<T> list(String sql, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.none(), mapper);
    }

    default <T> List<T> list(String sql, Object param, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.of(param), mapper);
    }

    default <T> List<T> list(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.of(params), mapper);
    }

    int update(String sql, Params params) throws SQLException;

    default int update(String sql, Object... params) throws SQLException {
        return update(sql, Params.of(params));
    }

    long insert(String sql, Params params) throws SQLException;

    default long insert(String sql, Object... params) throws SQLException {
        return insert(sql, Params.of(params));
    }

    int delete(String sql, Params params) throws SQLException;

    default int delete(String sql, Object... params) throws SQLException {
        return delete(sql, Params.of(params));
    }

    boolean exists(String sql, Params params) throws SQLException;

    default boolean exists(String sql, Object... params) throws SQLException {
        return exists(sql, Params.of(params));
    }

    long count(String sql, Params params) throws SQLException;

    default long count(String sql, Object... params) throws SQLException {
        return count(sql, Params.of(params));
    }
}
