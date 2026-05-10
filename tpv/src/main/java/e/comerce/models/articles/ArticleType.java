package e.comerce.models.articles;

/**
 * Tipus d'articles disponibles.
 */
public enum ArticleType {
    /** Camisa. */
    SHIRT("camisa"),

    /** Pantaló. */
    PANTS("pantaló");

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
     * @throws IllegalArgumentException si el tipus no existeix
     */
    public static ArticleType getType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("El tipus d'article no pot estar buit");
        }

        for (ArticleType articleType : ArticleType.values()) {
            if (articleType.type().equals(type)) {
                return articleType;
            }
        }

        throw new IllegalArgumentException("El tipus d'article no existeix");
    }
}