package e.comerce.libs.db.table;

import java.sql.SQLTimeoutException;

/** Excepció llançada quan no es pot obtenir un bloqueig de taula dins del temps permès. */
public class TableLockTimeoutException extends SQLTimeoutException {
    public TableLockTimeoutException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
