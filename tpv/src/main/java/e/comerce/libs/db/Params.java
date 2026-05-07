package e.comerce.libs.db;

/**
 * Representa els paràmetres que es faran servir en una consulta SQL preparada.
 *
 * <p>
 * Cada valor d'aquest objecte substitueix, en ordre, un símbol {@code ?}
 * dins d'un {@code PreparedStatement}.
 * </p>
 *
 * <pre>
 * Params params = Params.of("jairo@test.com", true);
 * </pre>
 */
public final class Params {

    private final Object[] values;

    private Params(Object... values) {
        this.values = values == null ? new Object[0] : values;
    }

    /**
     * Crea un conjunt de paràmetres.
     *
     * <p>
     * L'ordre dels valors ha de coincidir amb l'ordre dels símbols {@code ?}
     * de la consulta SQL.
     * </p>
     *
     * <pre>
     * Params.of("Jairo", "jairo@test.com", true)
     * </pre>
     *
     * @param values valors que substituiran els {@code ?} de la consulta
     * @return una instància de {@code Params}
     */
    public static Params of(Object... values) {
        return new Params(values);
    }

    /**
     * Crea un conjunt de paràmetres buit.
     *
     * <p>
     * És útil per a consultes que no tenen cap {@code ?}.
     * </p>
     *
     * <pre>
     * Params.none()
     * </pre>
     *
     * @return una instància de {@code Params} sense valors
     */
    public static Params none() {
        return new Params();
    }

    /**
     * Retorna els valors interns dels paràmetres.
     *
     * @return array amb els valors dels paràmetres
     */
    Object[] values() {
        return values;
    }
}