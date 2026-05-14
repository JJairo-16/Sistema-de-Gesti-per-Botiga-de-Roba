package e.comerce.models.articles;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Tipus d'articles disponibles.
 */
public enum ArticleType {
    /** Camisa. */
    SHIRT("camisa"),

    /** Pantaló. */
    PANTS("pantaló"),

    /** Article genèric. */
    GENERIC("gèneric");

    private final String type;

    /**
     * Crea un tipus d'article.
     *
     * @param type nom del tipus
     */
    ArticleType(String type) {
        this.type = type;
    }

    /**
     * Retorna el nom del tipus d'article.
     *
     * @return nom del tipus
     */
    public String type() {
        return type;
    }

    /**
     * Retorna el tipus d'article segons el nom.
     *
     * @param type nom del tipus
     * @return tipus d'article corresponent
     */
    public static ArticleType getType(String type) {
        String normalizedType = normalize(type);

        if (normalizedType.isBlank()) {
            return GENERIC;
        }

        if (normalizedType.matches("camisas?")) {
            return SHIRT;
        }

        if (normalizedType.matches("pantal(o|on|ons|ones)")) {
            return PANTS;
        }

        return GENERIC;
    }

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{M}+");

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return DIACRITICS_PATTERN
                .matcher(Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFD))
                .replaceAll("");

    }
}