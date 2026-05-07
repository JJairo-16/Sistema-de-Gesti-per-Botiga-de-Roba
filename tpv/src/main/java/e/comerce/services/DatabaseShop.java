package e.comerce.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import e.comerce.libs.db.Database;
import e.comerce.libs.db.Pool;
import e.comerce.utils.io.EnvReader;

/**
 * Gestiona la connexió amb la base de dades de la botiga.
 */
public class DatabaseShop {
    private static final Path DATABASE_CONFIG_PATH = Path.of("tpv/db.env");

    private static final String NAME_KEY = "NAME";
    private static final String USER_KEY = "USER";
    private static final String PASSWORD_KEY = "PASSWORD";

    private final Pool pool;
    private final Database db;

    /**
     * Crea la connexió a partir del fitxer de configuració.
     *
     * @throws IOException si no es pot llegir el fitxer
     */
    public DatabaseShop() throws IOException {
        Map<String, String> data = EnvReader.read(DATABASE_CONFIG_PATH);
        validateData(data);

        System.setProperty("org.slf4j.simpleLogger.log.com.zaxxer.hikari", "off");
        this.pool = Pool.builder()
                .localConnection(data.get(NAME_KEY))
                .credentials(data.get(USER_KEY), data.get(PASSWORD_KEY))
                .build();

        this.db = new Database(pool);
    }

    /**
     * Tanca el pool de connexions.
     */
    public void close() {
        pool.close();
    }

    /**
     * Valida les dades de configuració.
     *
     * @param data dades llegides del fitxer
     */
    private static void validateData(Map<String, String> data) {
        validateString(data, NAME_KEY);
        validateString(data, USER_KEY);
        validateString(data, PASSWORD_KEY);
    }

    /**
     * Valida que una clau existeixi i tingui valor.
     *
     * @param data dades de configuració
     * @param key clau a validar
     */
    private static void validateString(Map<String, String> data, String key) {
        if (!data.containsKey(key)) {
            throw new IllegalArgumentException("No s'ha trobat la clau del nom de la base de dades (" + key + ")");
        }

        String value = data.get(key);
        if (value.isBlank()) {
            throw new IllegalArgumentException("La clau " + key + " es troba en blanc");
        }
    }
}