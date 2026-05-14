package e.comerce.libs.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import e.comerce.libs.db.functional.RowMapper;
import e.comerce.libs.db.functional.SqlRunnable;
import e.comerce.libs.db.functional.SqlWork;
import e.comerce.libs.db.table.TableLock;
import e.comerce.libs.db.table.TableLockTimeoutException;

/**
 * Classe principal per treballar amb la base de dades de manera senzilla.
 *
 * <p>
 * Aquesta classe encapsula l'ús de {@link Connection},
 * {@link PreparedStatement} i {@link ResultSet}.
 * </p>
 *
 * <p>
 * Permet treballar de dues maneres:
 * </p>
 *
 * <ul>
 * <li>amb mètodes directes, utilitzant el {@link Pool} intern,</li>
 * <li>amb API fluida, utilitzant una {@link Connection} o un {@link DataSource}
 * extern.</li>
 * </ul>
 *
 * <p>
 * Quan s'utilitza {@link #using(Connection)}, la connexió no es tanca per
 * defecte.
 * Quan s'utilitza {@link #using(DataSource)}, la connexió obtinguda sí que es
 * tanca per defecte.
 * </p>
 */
public class Database implements DbExecutor {

    private final Pool pool;

    /**
     * Crea una instància de {@code Database} a partir d'un {@link Pool}.
     *
     * @param pool pool de connexions que s'utilitzarà per defecte
     */
    public Database(Pool pool) {
        if (pool == null) {
            throw new IllegalArgumentException("El pool no pot ser nul.");
        }

        this.pool = pool;
    }

    /**
     * Crea un context fluït utilitzant una connexió externa.
     *
     * <p>
     * Per defecte, la connexió no es tanca automàticament.
     * </p>
     *
     * @param conn connexió externa
     * @return context fluït de base de dades
     */
    public Context using(Connection conn) {
        return using(conn, false);
    }

    /**
     * Crea un context fluït utilitzant una connexió externa.
     *
     * @param conn            connexió externa
     * @param closeConnection indica si la connexió s'ha de tancar automàticament
     * @return context fluït de base de dades
     */
    public Context using(Connection conn, boolean closeConnection) {
        return new Context(conn, closeConnection);
    }

    /**
     * Crea un context fluït utilitzant un {@link DataSource}.
     *
     * <p>
     * Per defecte, la connexió obtinguda del {@link DataSource} sí que es tanca
     * automàticament.
     * </p>
     *
     * @param src DataSource extern
     * @return context fluït de base de dades
     */
    public Context using(DataSource src) {
        return using(src, true);
    }

    /**
     * Crea un context fluït utilitzant un {@link DataSource}.
     *
     * @param src             DataSource extern
     * @param closeConnection indica si la connexió obtinguda s'ha de tancar
     *                        automàticament
     * @return context fluït de base de dades
     */
    public Context using(DataSource src, boolean closeConnection) {
        return new Context(src, closeConnection);
    }

    /**
     * Executa una consulta SQL que espera obtenir una única fila sense paràmetres.
     *
     * @param sql    consulta SQL completa
     * @param mapper funció que converteix la fila en un objecte
     * @param <T>    tipus d'objecte retornat
     * @return objecte trobat o {@code null}
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public <T> T one(String sql, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.none(), mapper);
    }

    /**
     * Executa una consulta SQL que espera obtenir una única fila.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix la fila en un objecte
     * @param <T>    tipus d'objecte retornat
     * @return objecte trobat o {@code null}
     * @throws SQLException si hi ha un error SQL
     */
    public <T> T one(String sql, Params params, RowMapper<T> mapper) throws SQLException {
        return context().one(sql, params, mapper);
    }

    /**
     * Sobrecàrrega de {@link #one(String, Params, RowMapper)} per a consultes
     * amb un únic paràmetre.
     *
     * @param sql    consulta SQL completa
     * @param param  paràmetre que substituirà el {@code ?}
     * @param mapper funció que converteix la fila en un objecte
     * @param <T>    tipus d'objecte retornat
     * @return objecte trobat o {@code null}
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public <T> T one(String sql, Object param, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.of(param), mapper);
    }

    /**
     * Sobrecàrrega de {@link #one(String, Params, RowMapper)} per a consultes
     * amb diversos paràmetres.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix la fila en un objecte
     * @param <T>    tipus d'objecte retornat
     * @return objecte trobat o {@code null}
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public <T> T one(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.of(params), mapper);
    }

    /**
     * Executa una consulta SQL que retorna diverses files sense paràmetres.
     *
     * @param sql    consulta SQL completa
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T>    tipus d'objecte de cada element de la llista
     * @return llista d'objectes trobats
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public <T> List<T> list(String sql, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.none(), mapper);
    }

    /**
     * Executa una consulta SQL que retorna diverses files.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T>    tipus d'objecte de cada element de la llista
     * @return llista d'objectes trobats
     * @throws SQLException si hi ha un error SQL
     */
    public <T> List<T> list(String sql, Params params, RowMapper<T> mapper) throws SQLException {
        return context().list(sql, params, mapper);
    }

    /**
     * Sobrecàrrega de {@link #list(String, Params, RowMapper)} per a consultes
     * amb un únic paràmetre.
     *
     * @param sql    consulta SQL completa
     * @param param  paràmetre que substituirà el {@code ?}
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T>    tipus d'objecte de cada element de la llista
     * @return llista d'objectes trobats
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public <T> List<T> list(String sql, Object param, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.of(param), mapper);
    }

    /**
     * Sobrecàrrega de {@link #list(String, Params, RowMapper)} per a consultes
     * amb diversos paràmetres.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T>    tipus d'objecte de cada element de la llista
     * @return llista d'objectes trobats
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public <T> List<T> list(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.of(params), mapper);
    }

    /**
     * Executa una sentència SQL de modificació.
     *
     * @param sql    sentència SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return nombre de files afectades
     * @throws SQLException si hi ha un error SQL
     */
    public int update(String sql, Params params) throws SQLException {
        return context().update(sql, params);
    }

    /**
     * Sobrecàrrega de {@link #update(String, Params)} que permet passar
     * els paràmetres directament.
     *
     * @param sql    sentència SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return nombre de files afectades
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public int update(String sql, Object... params) throws SQLException {
        return update(sql, Params.of(params));
    }

    /**
     * Executa una sentència {@code INSERT} i retorna la clau generada.
     *
     * @param sql    sentència {@code INSERT} completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return clau generada o {@code -1}
     * @throws SQLException si hi ha un error SQL
     */
    public long insert(String sql, Params params) throws SQLException {
        return context().insert(sql, params);
    }

    /**
     * Sobrecàrrega de {@link #insert(String, Params)} que permet passar
     * els paràmetres directament.
     *
     * @param sql    sentència {@code INSERT} completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return clau generada o {@code -1}
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public long insert(String sql, Object... params) throws SQLException {
        return insert(sql, Params.of(params));
    }

    /**
     * Executa una sentència {@code DELETE}.
     *
     * @param sql    sentència {@code DELETE} completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return nombre de files eliminades
     * @throws SQLException si hi ha un error SQL
     */
    public int delete(String sql, Params params) throws SQLException {
        return update(sql, params);
    }

    /**
     * Sobrecàrrega de {@link #delete(String, Params)} que permet passar
     * els paràmetres directament.
     *
     * @param sql    sentència {@code DELETE} completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return nombre de files eliminades
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public int delete(String sql, Object... params) throws SQLException {
        return delete(sql, Params.of(params));
    }

    /**
     * Comprova si una consulta retorna almenys una fila.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return {@code true} si la consulta retorna alguna fila
     * @throws SQLException si hi ha un error SQL
     */
    public boolean exists(String sql, Params params) throws SQLException {
        return context().exists(sql, params);
    }

    /**
     * Sobrecàrrega de {@link #exists(String, Params)} que permet passar
     * els paràmetres directament.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return {@code true} si la consulta retorna alguna fila
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public boolean exists(String sql, Object... params) throws SQLException {
        return exists(sql, Params.of(params));
    }

    /**
     * Executa una consulta que retorna un comptador.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return valor numèric de la primera columna, o {@code 0} si no hi ha resultat
     * @throws SQLException si hi ha un error SQL
     */
    public long count(String sql, Params params) throws SQLException {
        return context().count(sql, params);
    }

    /**
     * Sobrecàrrega de {@link #count(String, Params)} que permet passar
     * els paràmetres directament.
     *
     * @param sql    consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return valor numèric de la primera columna, o {@code 0} si no hi ha resultat
     * @throws SQLException si hi ha un error SQL
     */
    @Override
    public long count(String sql, Object... params) throws SQLException {
        return count(sql, Params.of(params));
    }

    /**
     * Executa un conjunt d'operacions dins d'una transacció.
     *
     * <p>
     * Totes les operacions reben el mateix context i, per tant, utilitzen la
     * mateixa connexió. Si el treball acaba correctament es fa {@code commit};
     * si es produeix una excepció es fa {@code rollback}.
     * </p>
     *
     * @param work treball a executar dins de la transacció
     * @param <T> tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si hi ha un error SQL
     */
    public <T> T transaction(SqlWork<T> work) throws SQLException {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, work);
    }

    /**
     * Executa un conjunt d'operacions sense valor de retorn dins d'una transacció.
     *
     * @param work treball a executar dins de la transacció
     * @throws SQLException si hi ha un error SQL
     */
    public void transaction(SqlRunnable work) throws SQLException {
        Objects.requireNonNull(work, "El treball transaccional no pot ser nul");

        transaction(db -> {
            work.execute(db);
            return null;
        });
    }

    /**
     * Executa un conjunt d'operacions dins d'una transacció amb un nivell
     * d'aïllament concret.
     *
     * @param isolationLevel nivell d'aïllament de {@link Connection}
     * @param work treball a executar dins de la transacció
     * @param <T> tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si hi ha un error SQL
     */
    public <T> T transaction(int isolationLevel, SqlWork<T> work) throws SQLException {
        Objects.requireNonNull(work, "El treball transaccional no pot ser nul");

        try (Connection conn = pool.getDataSource().getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            int previousIsolation = conn.getTransactionIsolation();

            try {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(isolationLevel);

                T result = work.execute(using(conn, false));

                conn.commit();
                return result;
            } catch (Exception ex) {
                conn.rollback();

                if (ex instanceof SQLException sqlEx) {
                    throw sqlEx;
                }

                throw new SQLException("Error executant la transacció", ex);
            } finally {
                conn.setTransactionIsolation(previousIsolation);
                conn.setAutoCommit(previousAutoCommit);
            }
        }
    }

    /**
     * Executa una transacció protegida amb bloquejos de taula.
     *
     * <p>
     * La connexió espera com a màxim el temps indicat per obtenir els bloquejos.
     * Si no és possible obtenir-los dins del termini, es llança una excepció de
     * timeout i no s'executa el treball.
     * </p>
     *
     * @param locks taules que s'han de bloquejar
     * @param timeoutSeconds segons màxims d'espera per obtenir els bloquejos
     * @param work treball a executar dins de la transacció protegida
     * @param <T> tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si hi ha un error SQL o un timeout de bloqueig
     */
    public <T> T transactionWithTableLocks(
            List<TableLock> locks,
            int timeoutSeconds,
            SqlWork<T> work) throws SQLException {
        return transactionWithTableLocks(Connection.TRANSACTION_READ_COMMITTED, locks, timeoutSeconds, work);
    }

    /**
     * Executa una transacció protegida amb bloquejos de taula i un nivell
     * d'aïllament concret.
     *
     * @param isolationLevel nivell d'aïllament de {@link Connection}
     * @param locks taules que s'han de bloquejar
     * @param timeoutSeconds segons màxims d'espera per obtenir els bloquejos
     * @param work treball a executar dins de la transacció protegida
     * @param <T> tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si hi ha un error SQL o un timeout de bloqueig
     */
    public <T> T transactionWithTableLocks(
            int isolationLevel,
            List<TableLock> locks,
            int timeoutSeconds,
            SqlWork<T> work) throws SQLException {
        Objects.requireNonNull(work, "El treball transaccional no pot ser nul");
        validateTableLocks(locks, timeoutSeconds);

        try (Connection conn = pool.getDataSource().getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            int previousIsolation = conn.getTransactionIsolation();
            int previousLockWaitTimeout = readSessionInt(conn, "lock_wait_timeout");
            int previousInnodbLockWaitTimeout = readSessionInt(conn, "innodb_lock_wait_timeout");
            boolean locked = false;

            try {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(isolationLevel);
                configureLockTimeouts(conn, timeoutSeconds);
                lockTables(conn, locks, timeoutSeconds);
                locked = true;

                T result = work.execute(using(conn, false));

                conn.commit();
                return result;
            } catch (Exception ex) {
                rollbackQuietly(conn);

                if (isLockTimeout(ex)) {
                    throw new TableLockTimeoutException(
                            "No s'ha pogut obtenir el bloqueig de taula dins del temps permès",
                            ex);
                }

                if (ex instanceof SQLException sqlEx) {
                    throw sqlEx;
                }

                throw new SQLException("Error executant la transacció protegida amb bloqueig de taula", ex);
            } finally {
                if (locked) {
                    unlockTablesQuietly(conn);
                }

                restoreLockTimeoutsQuietly(conn, previousLockWaitTimeout, previousInnodbLockWaitTimeout);
                conn.setTransactionIsolation(previousIsolation);
                conn.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private void validateTableLocks(List<TableLock> locks, int timeoutSeconds) {
        Objects.requireNonNull(locks, "La llista de bloquejos no pot ser nul·la");

        if (locks.isEmpty()) {
            throw new IllegalArgumentException("Cal indicar com a mínim una taula per bloquejar");
        }

        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("El timeout de bloqueig ha de ser positiu");
        }

        for (TableLock lock : locks) {
            Objects.requireNonNull(lock, "El bloqueig de taula no pot ser nul");
        }
    }

    private void configureLockTimeouts(Connection conn, int timeoutSeconds) throws SQLException {
        executeStatement(conn, "SET SESSION lock_wait_timeout = " + timeoutSeconds, timeoutSeconds);
        executeStatement(conn, "SET SESSION innodb_lock_wait_timeout = " + timeoutSeconds, timeoutSeconds);
    }

    private void restoreLockTimeoutsQuietly(
            Connection conn,
            int lockWaitTimeout,
            int innodbLockWaitTimeout) {
        try {
            executeStatement(conn, "SET SESSION lock_wait_timeout = " + lockWaitTimeout, 0);
            executeStatement(conn, "SET SESSION innodb_lock_wait_timeout = " + innodbLockWaitTimeout, 0);
        } catch (SQLException ignored) {
            // No es pot fer res útil sense ocultar l'error original de la transacció.
        }
    }

    private int readSessionInt(Connection conn, String variableName) throws SQLException {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT @@session." + variableName)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void lockTables(Connection conn, List<TableLock> locks, int timeoutSeconds) throws SQLException {
        StringBuilder sql = new StringBuilder("LOCK TABLES ");

        for (int i = 0; i < locks.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }

            sql.append(locks.get(i).toSql());
        }

        executeStatement(conn, sql.toString(), timeoutSeconds);
    }

    private void unlockTablesQuietly(Connection conn) {
        try {
            executeStatement(conn, "UNLOCK TABLES", 0);
        } catch (SQLException ignored) {
            // El bloqueig s'allibera igualment quan es tanca la connexió.
        }
    }

    private void executeStatement(Connection conn, String sql, int timeoutSeconds) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            if (timeoutSeconds > 0) {
                stmt.setQueryTimeout(timeoutSeconds);
            }

            stmt.execute(sql);
        }
    }

    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // Es conserva l'error original.
        }
    }

    private boolean isLockTimeout(Throwable error) {
        Throwable current = error;

        while (current != null) {
            if (current instanceof SQLTimeoutException) {
                return true;
            }

            if (current instanceof SQLException sqlEx && sqlEx.getErrorCode() == 1205) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private Context context() {
        return using(pool.getDataSource());
    }

    /**
     * Context fluït per executar operacions utilitzant una connexió o un
     * {@link DataSource} concret.
     */
    public class Context implements DbExecutor {

        private final DataSource src;
        private final Connection conn;
        private final boolean closeConnection;

        private Context(DataSource src, boolean closeConnection) {
            if (src == null) {
                throw new IllegalArgumentException("El DataSource no pot ser nul.");
            }

            this.src = src;
            this.conn = null;
            this.closeConnection = closeConnection;
        }

        private Context(Connection conn, boolean closeConnection) {
            if (conn == null) {
                throw new IllegalArgumentException("La connexió no pot ser nul·la.");
            }

            this.src = null;
            this.conn = conn;
            this.closeConnection = closeConnection;
        }

        /**
         * Executa una consulta SQL que espera obtenir una única fila sense paràmetres.
         *
         * @param sql    consulta SQL completa
         * @param mapper funció que converteix la fila en un objecte
         * @param <T>    tipus d'objecte retornat
         * @return objecte trobat o {@code null}
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public <T> T one(String sql, RowMapper<T> mapper) throws SQLException {
            return one(sql, Params.none(), mapper);
        }

        /**
         * Executa una consulta SQL que espera obtenir una única fila.
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix la fila en un objecte
         * @param <T>    tipus d'objecte retornat
         * @return objecte trobat o {@code null}
         * @throws SQLException si hi ha un error SQL
         */
        public <T> T one(String sql, Params params, RowMapper<T> mapper) throws SQLException {
            validateQuery(sql, mapper);

            return withConnection(c -> {
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    bind(stmt, params);

                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next() ? mapper.map(rs) : null;
                    }
                }
            });
        }

        /**
         * Sobrecàrrega de {@link #one(String, Params, RowMapper)} per a consultes
         * amb un únic paràmetre.
         *
         * @param sql    consulta SQL completa
         * @param param  paràmetre que substituirà el {@code ?}
         * @param mapper funció que converteix la fila en un objecte
         * @param <T>    tipus d'objecte retornat
         * @return objecte trobat o {@code null}
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public <T> T one(String sql, Object param, RowMapper<T> mapper) throws SQLException {
            return one(sql, Params.of(param), mapper);
        }

        /**
         * Sobrecàrrega de {@link #one(String, Params, RowMapper)} per a consultes
         * amb diversos paràmetres.
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix la fila en un objecte
         * @param <T>    tipus d'objecte retornat
         * @return objecte trobat o {@code null}
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public <T> T one(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
            return one(sql, Params.of(params), mapper);
        }

        /**
         * Executa una consulta SQL que retorna diverses files sense paràmetres.
         *
         * @param sql    consulta SQL completa
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T>    tipus d'objecte de cada element de la llista
         * @return llista d'objectes trobats
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public <T> List<T> list(String sql, RowMapper<T> mapper) throws SQLException {
            return list(sql, Params.none(), mapper);
        }

        /**
         * Executa una consulta SQL que retorna diverses files.
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T>    tipus d'objecte de cada element de la llista
         * @return llista d'objectes trobats
         * @throws SQLException si hi ha un error SQL
         */
        public <T> List<T> list(String sql, Params params, RowMapper<T> mapper) throws SQLException {
            validateQuery(sql, mapper);

            return withConnection(c -> {
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    bind(stmt, params);

                    try (ResultSet rs = stmt.executeQuery()) {
                        List<T> result = new ArrayList<>();

                        while (rs.next()) {
                            result.add(mapper.map(rs));
                        }

                        return result;
                    }
                }
            });
        }

        /**
         * Sobrecàrrega de {@link #list(String, Params, RowMapper)} per a consultes
         * amb un únic paràmetre.
         *
         * @param sql    consulta SQL completa
         * @param param  paràmetre que substituirà el {@code ?}
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T>    tipus d'objecte de cada element de la llista
         * @return llista d'objectes trobats
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public <T> List<T> list(String sql, Object param, RowMapper<T> mapper) throws SQLException {
            return list(sql, Params.of(param), mapper);
        }

        /**
         * Sobrecàrrega de {@link #list(String, Params, RowMapper)} per a consultes
         * amb diversos paràmetres.
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T>    tipus d'objecte de cada element de la llista
         * @return llista d'objectes trobats
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public <T> List<T> list(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
            return list(sql, Params.of(params), mapper);
        }

        /**
         * Executa una sentència SQL de modificació.
         *
         * <p>
         * Es pot utilitzar per a {@code UPDATE}, {@code DELETE} o altres sentències
         * que retornin un nombre de files afectades.
         * </p>
         *
         * @param sql    sentència SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return nombre de files afectades
         * @throws SQLException si hi ha un error SQL
         */
        public int update(String sql, Params params) throws SQLException {
            validateSql(sql);

            return withConnection(c -> {
                try (PreparedStatement stmt = c.prepareStatement(sql)) {
                    bind(stmt, params);
                    return stmt.executeUpdate();
                }
            });
        }

        /**
         * Sobrecàrrega de {@link #update(String, Params)} que permet passar
         * els paràmetres directament.
         *
         * @param sql    sentència SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return nombre de files afectades
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public int update(String sql, Object... params) throws SQLException {
            return update(sql, Params.of(params));
        }

        /**
         * Executa una sentència {@code INSERT} i retorna la clau generada.
         *
         * <p>
         * Si no es genera cap clau automàtica, retorna {@code -1}.
         * </p>
         *
         * @param sql    sentència {@code INSERT} completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return clau generada o {@code -1}
         * @throws SQLException si hi ha un error SQL
         */
        public long insert(String sql, Params params) throws SQLException {
            validateSql(sql);

            return withConnection(c -> {
                try (PreparedStatement stmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    bind(stmt, params);
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        return keys.next() ? keys.getLong(1) : -1;
                    }
                }
            });
        }

        /**
         * Sobrecàrrega de {@link #insert(String, Params)} que permet passar
         * els paràmetres directament.
         *
         * @param sql    sentència {@code INSERT} completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return clau generada o {@code -1}
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public long insert(String sql, Object... params) throws SQLException {
            return insert(sql, Params.of(params));
        }

        /**
         * Executa una sentència {@code DELETE}.
         *
         * @param sql    sentència {@code DELETE} completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return nombre de files eliminades
         * @throws SQLException si hi ha un error SQL
         */
        public int delete(String sql, Params params) throws SQLException {
            return update(sql, params);
        }

        /**
         * Sobrecàrrega de {@link #delete(String, Params)} que permet passar
         * els paràmetres directament.
         *
         * @param sql    sentència {@code DELETE} completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return nombre de files eliminades
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public int delete(String sql, Object... params) throws SQLException {
            return delete(sql, Params.of(params));
        }

        /**
         * Comprova si una consulta retorna almenys una fila.
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return {@code true} si la consulta retorna alguna fila
         * @throws SQLException si hi ha un error SQL
         */
        public boolean exists(String sql, Params params) throws SQLException {
            Boolean result = one(sql, params, rs -> true);
            return result != null;
        }

        /**
         * Sobrecàrrega de {@link #exists(String, Params)} que permet passar
         * els paràmetres directament.
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return {@code true} si la consulta retorna alguna fila
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public boolean exists(String sql, Object... params) throws SQLException {
            return exists(sql, Params.of(params));
        }

        /**
         * Executa una consulta que retorna un comptador.
         *
         * <p>
         * La consulta ha de retornar un valor numèric en la primera columna.
         * Normalment s'utilitza amb {@code COUNT(*)}.
         * </p>
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return valor numèric de la primera columna, o {@code 0} si no hi ha resultat
         * @throws SQLException si hi ha un error SQL
         */
        public long count(String sql, Params params) throws SQLException {
            Long result = one(sql, params, rs -> rs.getLong(1));
            return result == null ? 0 : result;
        }

        /**
         * Sobrecàrrega de {@link #count(String, Params)} que permet passar
         * els paràmetres directament.
         *
         * @param sql    consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return valor numèric de la primera columna, o {@code 0} si no hi ha resultat
         * @throws SQLException si hi ha un error SQL
         */
        @Override
        public long count(String sql, Object... params) throws SQLException {
            return count(sql, Params.of(params));
        }

        private <T> T withConnection(SqlFunction<Connection, T> action) throws SQLException {
            if (conn != null) {
                if (!closeConnection) {
                    return action.apply(conn);
                }

                try (conn) {
                    return action.apply(conn);
                }
            }

            Connection c = src.getConnection();

            if (!closeConnection) {
                return action.apply(c);
            }

            try (c) {
                return action.apply(c);
            }
        }

        /**
         * Assigna automàticament els valors rebuts als símbols {@code ?}
         * del {@link PreparedStatement}.
         *
         * @param stmt   sentència preparada
         * @param params paràmetres a assignar
         * @throws SQLException si hi ha un error assignant els valors
         */
        private void bind(PreparedStatement stmt, Params params) throws SQLException {
            Object[] values = params == null ? new Object[0] : params.values();

            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }
        }

        private void validateSql(String sql) {
            if (sql == null || sql.isBlank()) {
                throw new IllegalArgumentException("La consulta SQL no pot estar buida.");
            }
        }

        private <T> void validateQuery(String sql, RowMapper<T> mapper) {
            validateSql(sql);

            if (mapper == null) {
                throw new IllegalArgumentException("El mapper no pot ser nul.");
            }
        }
    }

    /**
     * Comprova si la connexió a la base de dades és vàlida.
     *
     * @return {@code true} si es pot establir una connexió vàlida, {@code false} en
     *         cas contrari
     */
    public boolean isConnected() {
        try (Connection conn = pool.getDataSource().getConnection()) {
            return conn.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    @FunctionalInterface
    private interface SqlFunction<I, O> {
        O apply(I input) throws SQLException;
    }
}