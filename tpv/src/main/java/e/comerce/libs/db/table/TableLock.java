package e.comerce.libs.db.table;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Descriu un bloqueig de taula que es pot aplicar abans d'executar una operació crítica.
 */
public final class TableLock {
    private final String tableName;
    private final TableLockMode mode;

    private static final Pattern NORMALIZED_NAME_PATTERN = Pattern.compile("\\w+");

    private TableLock(String tableName, TableLockMode mode) {
        this.tableName = validateTableName(tableName);
        this.mode = Objects.requireNonNull(mode, "El mode de bloqueig no pot ser nul");
    }

    public static TableLock read(String tableName) {
        return new TableLock(tableName, TableLockMode.READ);
    }

    public static TableLock write(String tableName) {
        return new TableLock(tableName, TableLockMode.WRITE);
    }

    public String toSql() {
        return tableName + " " + mode.sql();
    }

    private static String validateTableName(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("El nom de la taula no pot estar buit");
        }

        String normalized = tableName.trim();

        if (!NORMALIZED_NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("El nom de la taula només pot contenir lletres, números i guions baixos");
        }

        return normalized;
    }
}
