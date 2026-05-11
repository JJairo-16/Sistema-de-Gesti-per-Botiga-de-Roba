package e.comerce.libs.db;

import java.sql.SQLException;

/** Treball SQL sense valor de retorn que s'executa dins d'un context de base de dades. */
@FunctionalInterface
public interface SqlRunnable {
    void execute(Database.Context db) throws SQLException;
}
