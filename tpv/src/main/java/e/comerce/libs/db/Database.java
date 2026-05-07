package e.comerce.libs.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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
 *     <li>amb mètodes directes, utilitzant el {@link Pool} intern,</li>
 *     <li>amb API fluida, utilitzant una {@link Connection} o un {@link DataSource} extern.</li>
 * </ul>
 *
 * <p>
 * Quan s'utilitza {@link #using(Connection)}, la connexió no es tanca per defecte.
 * Quan s'utilitza {@link #using(DataSource)}, la connexió obtinguda sí que es tanca per defecte.
 * </p>
 */
public class Database {

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
     * @param conn connexió externa
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
     * Per defecte, la connexió obtinguda del {@link DataSource} sí que es tanca automàticament.
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
     * @param src DataSource extern
     * @param closeConnection indica si la connexió obtinguda s'ha de tancar automàticament
     * @return context fluït de base de dades
     */
    public Context using(DataSource src, boolean closeConnection) {
        return new Context(src, closeConnection);
    }

    /**
     * Executa una consulta SQL que espera obtenir una única fila sense paràmetres.
     *
     * @param sql consulta SQL completa
     * @param mapper funció que converteix la fila en un objecte
     * @param <T> tipus d'objecte retornat
     * @return objecte trobat o {@code null}
     * @throws SQLException si hi ha un error SQL
     */
    public <T> T one(String sql, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.none(), mapper);
    }

    /**
     * Executa una consulta SQL que espera obtenir una única fila.
     *
     * @param sql consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix la fila en un objecte
     * @param <T> tipus d'objecte retornat
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
     * @param sql consulta SQL completa
     * @param param paràmetre que substituirà el {@code ?}
     * @param mapper funció que converteix la fila en un objecte
     * @param <T> tipus d'objecte retornat
     * @return objecte trobat o {@code null}
     * @throws SQLException si hi ha un error SQL
     */
    public <T> T one(String sql, Object param, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.of(param), mapper);
    }

    /**
     * Sobrecàrrega de {@link #one(String, Params, RowMapper)} per a consultes
     * amb diversos paràmetres.
     *
     * @param sql consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix la fila en un objecte
     * @param <T> tipus d'objecte retornat
     * @return objecte trobat o {@code null}
     * @throws SQLException si hi ha un error SQL
     */
    public <T> T one(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
        return one(sql, Params.of(params), mapper);
    }

    /**
     * Executa una consulta SQL que retorna diverses files sense paràmetres.
     *
     * @param sql consulta SQL completa
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T> tipus d'objecte de cada element de la llista
     * @return llista d'objectes trobats
     * @throws SQLException si hi ha un error SQL
     */
    public <T> List<T> list(String sql, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.none(), mapper);
    }

    /**
     * Executa una consulta SQL que retorna diverses files.
     *
     * @param sql consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T> tipus d'objecte de cada element de la llista
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
     * @param sql consulta SQL completa
     * @param param paràmetre que substituirà el {@code ?}
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T> tipus d'objecte de cada element de la llista
     * @return llista d'objectes trobats
     * @throws SQLException si hi ha un error SQL
     */
    public <T> List<T> list(String sql, Object param, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.of(param), mapper);
    }

    /**
     * Sobrecàrrega de {@link #list(String, Params, RowMapper)} per a consultes
     * amb diversos paràmetres.
     *
     * @param sql consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @param mapper funció que converteix cada fila en un objecte
     * @param <T> tipus d'objecte de cada element de la llista
     * @return llista d'objectes trobats
     * @throws SQLException si hi ha un error SQL
     */
    public <T> List<T> list(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
        return list(sql, Params.of(params), mapper);
    }

    /**
     * Executa una sentència SQL de modificació.
     *
     * @param sql sentència SQL completa
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
     * @param sql sentència SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return nombre de files afectades
     * @throws SQLException si hi ha un error SQL
     */
    public int update(String sql, Object... params) throws SQLException {
        return update(sql, Params.of(params));
    }

    /**
     * Executa una sentència {@code INSERT} i retorna la clau generada.
     *
     * @param sql sentència {@code INSERT} completa
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
     * @param sql sentència {@code INSERT} completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return clau generada o {@code -1}
     * @throws SQLException si hi ha un error SQL
     */
    public long insert(String sql, Object... params) throws SQLException {
        return insert(sql, Params.of(params));
    }

    /**
     * Executa una sentència {@code DELETE}.
     *
     * @param sql sentència {@code DELETE} completa
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
     * @param sql sentència {@code DELETE} completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return nombre de files eliminades
     * @throws SQLException si hi ha un error SQL
     */
    public int delete(String sql, Object... params) throws SQLException {
        return delete(sql, Params.of(params));
    }

    /**
     * Comprova si una consulta retorna almenys una fila.
     *
     * @param sql consulta SQL completa
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
     * @param sql consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return {@code true} si la consulta retorna alguna fila
     * @throws SQLException si hi ha un error SQL
     */
    public boolean exists(String sql, Object... params) throws SQLException {
        return exists(sql, Params.of(params));
    }

    /**
     * Executa una consulta que retorna un comptador.
     *
     * @param sql consulta SQL completa
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
     * @param sql consulta SQL completa
     * @param params paràmetres que substituiran els {@code ?}
     * @return valor numèric de la primera columna, o {@code 0} si no hi ha resultat
     * @throws SQLException si hi ha un error SQL
     */
    public long count(String sql, Object... params) throws SQLException {
        return count(sql, Params.of(params));
    }

    private Context context() {
        return using(pool.getDataSource());
    }

    /**
     * Context fluït per executar operacions utilitzant una connexió o un
     * {@link DataSource} concret.
     */
    public class Context {

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
         * @param sql consulta SQL completa
         * @param mapper funció que converteix la fila en un objecte
         * @param <T> tipus d'objecte retornat
         * @return objecte trobat o {@code null}
         * @throws SQLException si hi ha un error SQL
         */
        public <T> T one(String sql, RowMapper<T> mapper) throws SQLException {
            return one(sql, Params.none(), mapper);
        }

        /**
         * Executa una consulta SQL que espera obtenir una única fila.
         *
         * @param sql consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix la fila en un objecte
         * @param <T> tipus d'objecte retornat
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
         * @param sql consulta SQL completa
         * @param param paràmetre que substituirà el {@code ?}
         * @param mapper funció que converteix la fila en un objecte
         * @param <T> tipus d'objecte retornat
         * @return objecte trobat o {@code null}
         * @throws SQLException si hi ha un error SQL
         */
        public <T> T one(String sql, Object param, RowMapper<T> mapper) throws SQLException {
            return one(sql, Params.of(param), mapper);
        }

        /**
         * Sobrecàrrega de {@link #one(String, Params, RowMapper)} per a consultes
         * amb diversos paràmetres.
         *
         * @param sql consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix la fila en un objecte
         * @param <T> tipus d'objecte retornat
         * @return objecte trobat o {@code null}
         * @throws SQLException si hi ha un error SQL
         */
        public <T> T one(String sql, Object[] params, RowMapper<T> mapper) throws SQLException {
            return one(sql, Params.of(params), mapper);
        }

        /**
         * Executa una consulta SQL que retorna diverses files sense paràmetres.
         *
         * @param sql consulta SQL completa
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T> tipus d'objecte de cada element de la llista
         * @return llista d'objectes trobats
         * @throws SQLException si hi ha un error SQL
         */
        public <T> List<T> list(String sql, RowMapper<T> mapper) throws SQLException {
            return list(sql, Params.none(), mapper);
        }

        /**
         * Executa una consulta SQL que retorna diverses files.
         *
         * @param sql consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T> tipus d'objecte de cada element de la llista
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
         * @param sql consulta SQL completa
         * @param param paràmetre que substituirà el {@code ?}
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T> tipus d'objecte de cada element de la llista
         * @return llista d'objectes trobats
         * @throws SQLException si hi ha un error SQL
         */
        public <T> List<T> list(String sql, Object param, RowMapper<T> mapper) throws SQLException {
            return list(sql, Params.of(param), mapper);
        }

        /**
         * Sobrecàrrega de {@link #list(String, Params, RowMapper)} per a consultes
         * amb diversos paràmetres.
         *
         * @param sql consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @param mapper funció que converteix cada fila en un objecte
         * @param <T> tipus d'objecte de cada element de la llista
         * @return llista d'objectes trobats
         * @throws SQLException si hi ha un error SQL
         */
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
         * @param sql sentència SQL completa
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
         * @param sql sentència SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return nombre de files afectades
         * @throws SQLException si hi ha un error SQL
         */
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
         * @param sql sentència {@code INSERT} completa
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
         * @param sql sentència {@code INSERT} completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return clau generada o {@code -1}
         * @throws SQLException si hi ha un error SQL
         */
        public long insert(String sql, Object... params) throws SQLException {
            return insert(sql, Params.of(params));
        }

        /**
         * Executa una sentència {@code DELETE}.
         *
         * @param sql sentència {@code DELETE} completa
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
         * @param sql sentència {@code DELETE} completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return nombre de files eliminades
         * @throws SQLException si hi ha un error SQL
         */
        public int delete(String sql, Object... params) throws SQLException {
            return delete(sql, Params.of(params));
        }

        /**
         * Comprova si una consulta retorna almenys una fila.
         *
         * @param sql consulta SQL completa
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
         * @param sql consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return {@code true} si la consulta retorna alguna fila
         * @throws SQLException si hi ha un error SQL
         */
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
         * @param sql consulta SQL completa
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
         * @param sql consulta SQL completa
         * @param params paràmetres que substituiran els {@code ?}
         * @return valor numèric de la primera columna, o {@code 0} si no hi ha resultat
         * @throws SQLException si hi ha un error SQL
         */
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
         * @param stmt sentència preparada
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

    @FunctionalInterface
    private interface SqlFunction<I, O> {
        O apply(I input) throws SQLException;
    }
}