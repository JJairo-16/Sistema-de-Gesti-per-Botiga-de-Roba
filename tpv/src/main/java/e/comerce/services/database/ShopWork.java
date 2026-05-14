package e.comerce.services.database;

import java.sql.SQLException;

/** Treball de botiga que s'executa dins d'una transacció. */
@FunctionalInterface
public interface ShopWork<T> {
    T execute(ShopTransaction transaction) throws SQLException;
}
