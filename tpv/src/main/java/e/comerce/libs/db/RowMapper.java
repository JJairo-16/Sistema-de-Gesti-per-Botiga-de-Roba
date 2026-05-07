package e.comerce.libs.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interfície funcional per convertir una fila d'un {@link ResultSet}
 * en un objecte Java.
 *
 * <p>
 * Aquesta interfície s'utilitza en els mètodes de consulta de {@link Database}.
 * Cada vegada que es llegeix una fila, es crida el mètode {@link #map(ResultSet)}.
 * </p>
 *
 * <pre>
 * RowMapper&lt;User&gt; mapper = rs -&gt; new User(
 *     rs.getInt("id"),
 *     rs.getString("name"),
 *     rs.getBoolean("active")
 * );
 * </pre>
 *
 * @param <T> tipus d'objecte que es vol obtenir a partir de la fila
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * Converteix la fila actual del {@link ResultSet} en un objecte.
     *
     * @param rs resultat SQL posicionat sobre una fila concreta
     * @return objecte creat a partir de la fila actual
     * @throws SQLException si hi ha un error llegint el {@code ResultSet}
     */
    T map(ResultSet rs) throws SQLException;
}