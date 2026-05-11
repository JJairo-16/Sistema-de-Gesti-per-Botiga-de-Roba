package e.comerce.libs.db;

import java.sql.SQLException;

/** Treball SQL que s'executa dins d'un context de base de dades. */
@FunctionalInterface
public interface SqlWork<T> {
    T execute(Database.Context db) throws SQLException;
}
