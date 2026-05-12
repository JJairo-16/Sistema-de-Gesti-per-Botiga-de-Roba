package e.comerce.libs.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Classe encarregada de gestionar el pool de connexions a la base de dades.
 *
 * <p>
 * Aquesta classe encapsula la configuració i creació d'un
 * {@link HikariDataSource}
 * utilitzant el patró Builder per facilitar la seva configuració.
 * </p>
 *
 * <p>
 * Exemple d'ús:
 * </p>
 *
 * <pre>
 * Pool pool = Pool.builder()
 *         .localConnection("test_db")
 *         .credentials("root", "")
 *         .poolSize(10, 2)
 *         .build();
 * </pre>
 */
public class Pool {

    private final HikariDataSource dataSource;

    /**
     * Constructor privat. Es força l'ús del {@link Builder}.
     *
     * @param dataSource instància de {@link HikariDataSource} configurada
     */
    private Pool(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Retorna el {@link HikariDataSource} associat a aquest pool.
     *
     * @return dataSource configurat
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Tanca el {@link HikariDataSource} associat a aquest pool.
     *
     * <p>
     * Allibera tots els recursos i connexions gestionades pel pool.
     * Un cop tancat, aquest pool no es pot reutilitzar.
     * </p>
     */
    public void close() {
        dataSource.close();
    }

    /**
     * Punt d'entrada per crear una nova instància del {@link Builder}.
     *
     * @return nova instància de {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Classe Builder per configurar i crear instàncies de {@link Pool}.
     *
     * <p>
     * Permet definir els paràmetres de connexió i del pool de manera fluida.
     * </p>
     */
    public static class Builder {
        private static final String BASE_URL = "jdbc:mysql://";

        private String host = "localhost";
        private int port = 3306;
        private String database = "";
        private String user = "root";
        private String password = "";

        // Configuració opcional del pool
        private int maxPoolSize = 10;
        private int minIdle = 2;

        /**
         * Configura una connexió local amb host i port per defecte.
         *
         * <p>
         * Equivalent a:
         * host = localhost, port = 3306
         * </p>
         *
         * @param database nom de la base de dades
         * @return el mateix builder per encadenar crides
         */
        public Builder localConnection(String database) {
            this.host = "localhost";
            this.port = 3306;
            this.database = database;
            return this;
        }

        /**
         * Configura la connexió especificant host, port i base de dades.
         *
         * @param host     adreça del servidor de base de dades
         * @param port     port del servidor
         * @param database nom de la base de dades
         * @return el mateix builder per encadenar crides
         */
        public Builder connection(String host, int port, String database) {
            this.host = host;
            this.port = port;
            this.database = database;
            return this;
        }

        /**
         * Defineix les credencials d'accés a la base de dades.
         *
         * @param user     nom d'usuari
         * @param password contrasenya
         * @return el mateix builder per encadenar crides
         */
        public Builder credentials(String user, String password) {
            this.user = user;
            this.password = password;
            return this;
        }

        /**
         * Configura la mida del pool de connexions.
         *
         * @param maxPoolSize nombre màxim de connexions simultànies
         * @param minIdle     nombre mínim de connexions inactives
         * @return el mateix builder per encadenar crides
         */
        public Builder poolSize(int maxPoolSize, int minIdle) {
            this.maxPoolSize = maxPoolSize;
            this.minIdle = minIdle;
            return this;
        }

        /**
         * Construeix una instància de {@link Pool} amb la configuració definida.
         *
         * <p>
         * Internament crea un {@link HikariConfig} i un {@link HikariDataSource}.
         * </p>
         *
         * @return nova instància de {@link Pool}
         */
        public Pool build() {
            String jdbcUrl = BASE_URL + host + ":" + port + "/" + database +
                    "?useSSL=false" +
                    "&allowPublicKeyRetrieval=true" +
                    "&serverTimezone=UTC";

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(password);

            config.setMaximumPoolSize(maxPoolSize);
            config.setMinimumIdle(minIdle);

            config.setConnectionTimeout(5000);
            config.setValidationTimeout(3000);
            config.setIdleTimeout(60000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(10000);

            config.setInitializationFailTimeout(-1);
            config.setKeepaliveTime(30000);
            HikariDataSource ds = new HikariDataSource(config);

            return new Pool(ds);
        }
    }
}