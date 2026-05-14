package e.comerce.libs.db.functional;

import java.sql.SQLException;

import e.comerce.libs.db.Database;

/** Treball SQL que s'executa dins d'un context de base de dades. */
@FunctionalInterface
public interface SqlWork<T> {
    T execute(Database.Context db) throws SQLException;
}
