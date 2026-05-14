package e.comerce.libs.db.table;

/** Mode de bloqueig que s'aplicarà sobre una taula. */
public enum TableLockMode {
    READ("READ"),
    WRITE("WRITE");

    private final String sql;

    TableLockMode(String sql) {
        this.sql = sql;
    }

    String sql() {
        return sql;
    }
}
