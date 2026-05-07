package e.comerce.services;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import e.comerce.libs.db.Database;
import e.comerce.libs.db.Pool;
import e.comerce.models.Article;
import e.comerce.models.ArticleType;
import e.comerce.models.Pants;
import e.comerce.models.Shirt;
import e.comerce.utils.io.EnvReader;

/**
 * Gestiona l'accés a la base de dades de la botiga.
 */
public class DatabaseShop implements AutoCloseable {
    private static final Path DATABASE_CONFIG_PATH = Path.of("tpv/db.env");

    private static final String NAME_KEY = "NAME";
    private static final String USER_KEY = "USER";
    private static final String PASSWORD_KEY = "PASSWORD";

    private static final String SELECT_ARTICLES = "SELECT id, nom, familia, talla_coll, amplada_pit, " +
            "talla_cintura, llargada_camal, preu_base, iva, stock " +
            "FROM articles";

    private final Pool pool;
    private final Database db;

    /**
     * Crea la connexió amb les dades del fitxer de configuració.
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
     * Insereix un article a la base de dades.
     *
     * @param article article a inserir
     * @return id generat o -1 si no n'hi ha
     */
    public long insertArticle(Article article) throws SQLException {
        validateArticle(article);

        ArticleDbValues values = ArticleDbValues.from(article);

        return db.insert(
                "INSERT INTO articles " +
                        "(id, nom, familia, talla_coll, amplada_pit, talla_cintura, llargada_camal, preu_base, iva, stock) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                article.getId(),
                article.getName(),
                article.getType(),
                values.neckSize,
                values.chestWidth,
                values.waistSize,
                values.pantsLength,
                article.getBasePrice(),
                article.getIva(),
                article.getStock());
    }

    /**
     * Actualitza un article existent.
     *
     * @param article article amb les noves dades
     * @return true si s'ha modificat
     */
    public boolean updateArticle(Article article) throws SQLException {
        validateArticle(article);

        ArticleDbValues values = ArticleDbValues.from(article);

        int affectedRows = db.update(
                "UPDATE articles SET " +
                        "nom = ?, " +
                        "familia = ?, " +
                        "talla_coll = ?, " +
                        "amplada_pit = ?, " +
                        "talla_cintura = ?, " +
                        "llargada_camal = ?, " +
                        "preu_base = ?, " +
                        "iva = ?, " +
                        "stock = ? " +
                        "WHERE id = ?",
                article.getName(),
                article.getType(),
                values.neckSize,
                values.chestWidth,
                values.waistSize,
                values.pantsLength,
                article.getBasePrice(),
                article.getIva(),
                article.getStock(),
                article.getId());

        return affectedRows > 0;
    }

    /**
     * Insereix o actualitza un article.
     *
     * @param article article a guardar
     * @return true si s'ha inserit, false si s'ha actualitzat
     */
    public boolean saveArticle(Article article) throws SQLException {
        validateArticle(article);

        if (existsArticle(article.getId())) {
            updateArticle(article);
            return false;
        }

        insertArticle(article);
        return true;
    }

    /**
     * Elimina un article pel seu id.
     *
     * @param id identificador de l'article
     * @return true si s'ha eliminat
     */
    public boolean deleteArticle(int id) throws SQLException {
        validateId(id);

        int affectedRows = db.delete(
                "DELETE FROM articles WHERE id = ?",
                id);

        return affectedRows > 0;
    }

    /**
     * Cerca un article pel seu id.
     *
     * @param id identificador de l'article
     * @return article trobat o null
     */
    public Article findArticleById(int id) throws SQLException {
        validateId(id);

        return db.one(
                SELECT_ARTICLES + " WHERE id = ?",
                id,
                DatabaseShop::mapArticle);
    }

    /**
     * Retorna tots els articles.
     *
     * @return llista d'articles
     */
    public List<Article> findAllArticles() throws SQLException {
        return db.list(
                SELECT_ARTICLES + " ORDER BY id",
                DatabaseShop::mapArticle);
    }

    /**
     * Retorna els articles d'un tipus.
     *
     * @param type tipus d'article
     * @return llista d'articles
     */
    public List<Article> findArticlesByType(ArticleType type) throws SQLException {
        Objects.requireNonNull(type, "El tipus d'article no pot ser nul");

        return db.list(
                SELECT_ARTICLES + " WHERE familia = ? ORDER BY id",
                type.type(),
                DatabaseShop::mapArticle);
    }

    /**
     * Comprova si existeix un article.
     *
     * @param id identificador de l'article
     * @return true si existeix
     */
    public boolean existsArticle(int id) throws SQLException {
        validateId(id);

        return db.exists(
                "SELECT 1 FROM articles WHERE id = ?",
                id);
    }

    /**
     * Actualitza l'estoc d'un article.
     *
     * @param id identificador de l'article
     * @param stock nou estoc
     * @return true si s'ha modificat
     */
    public boolean updateStock(int id, int stock) throws SQLException {
        validateId(id);

        if (stock < 0) {
            throw new IllegalArgumentException("L'estoc no pot ser negatiu");
        }

        int affectedRows = db.update(
                "UPDATE articles SET stock = ? WHERE id = ?",
                stock,
                id);

        return affectedRows > 0;
    }

    /**
     * Redueix l'estoc si hi ha prou unitats.
     *
     * @param id identificador de l'article
     * @param quantity quantitat a reduir
     * @return true si s'ha reduït
     */
    public boolean decreaseStock(int id, int quantity) throws SQLException {
        validateId(id);

        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantitat ha de ser positiva");
        }

        int affectedRows = db.update(
                "UPDATE articles " +
                        "SET stock = stock - ? " +
                        "WHERE id = ? AND stock >= ?",
                quantity,
                id,
                quantity);

        return affectedRows > 0;
    }

    /**
     * Tanca el pool de connexions.
     */
    @Override
    public void close() {
        pool.close();
    }

    /**
     * Converteix una fila SQL en un article.
     *
     * @param rs resultat SQL
     * @return article creat
     */
    private static Article mapArticle(ResultSet rs) throws SQLException {
        ArticleType type = ArticleType.getType(rs.getString("familia"));

        return switch (type) {
            case SHIRT -> mapShirt(rs);
            case PANTS -> mapPants(rs);
            default -> throw new IllegalArgumentException("Tipus d'article no suportat");
        };
    }

    /**
     * Converteix una fila SQL en una camisa.
     *
     * @param rs resultat SQL
     * @return camisa creada
     */
    private static Shirt mapShirt(ResultSet rs) throws SQLException {
        return new Shirt(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getInt("talla_coll"),
                rs.getInt("amplada_pit"),
                rs.getDouble("preu_base"),
                rs.getInt("iva"),
                rs.getInt("stock"));
    }

    /**
     * Converteix una fila SQL en uns pantalons.
     *
     * @param rs resultat SQL
     * @return pantalons creats
     */
    private static Pants mapPants(ResultSet rs) throws SQLException {
        return new Pants(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getInt("talla_cintura"),
                rs.getInt("llargada_camal"),
                rs.getDouble("preu_base"),
                rs.getInt("iva"),
                rs.getInt("stock"));
    }

    /**
     * Valida que l'article sigui correcte.
     *
     * @param article article a validar
     */
    private static void validateArticle(Article article) {
        Objects.requireNonNull(article, "L'article no pot ser nul");
        validateId(article.getId());
    }

    /**
     * Valida un identificador.
     *
     * @param id identificador a validar
     */
    private static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'id de l'article ha de ser positiu");
        }
    }

    /**
     * Valida les dades de configuració.
     *
     * @param data dades llegides
     */
    private static void validateData(Map<String, String> data) {
        Objects.requireNonNull(data, "Les dades de configuració no poden ser nul·les");

        validateString(data, NAME_KEY);
        validateString(data, USER_KEY);
        validateString(data, PASSWORD_KEY);
    }

    /**
     * Valida una clau de configuració.
     *
     * @param data dades de configuració
     * @param key clau a validar
     */
    private static void validateString(Map<String, String> data, String key) {
        if (!data.containsKey(key)) {
            throw new IllegalArgumentException("No s'ha trobat la clau de configuració: " + key);
        }

        String value = data.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La clau " + key + " es troba en blanc");
        }
    }

    /**
     * Conté les talles que es guarden a la BD.
     */
    private static class ArticleDbValues {
        private final Integer neckSize;
        private final Integer chestWidth;
        private final Integer waistSize;
        private final Integer pantsLength;

        /**
         * Crea els valors específics d'article.
         */
        private ArticleDbValues(
                Integer neckSize,
                Integer chestWidth,
                Integer waistSize,
                Integer pantsLength) {
            this.neckSize = neckSize;
            this.chestWidth = chestWidth;
            this.waistSize = waistSize;
            this.pantsLength = pantsLength;
        }

        /**
         * Extreu les talles segons el tipus d'article.
         *
         * @param article article d'origen
         * @return valors per a la BD
         */
        private static ArticleDbValues from(Article article) {
            if (article.getTypeKey() == ArticleType.SHIRT) {
                Shirt shirt = (Shirt) article;

                return new ArticleDbValues(
                        shirt.getNeckSize(),
                        shirt.getChestWidth(),
                        null,
                        null);
            }

            if (article.getTypeKey() == ArticleType.PANTS) {
                Pants pants = (Pants) article;

                return new ArticleDbValues(
                        null,
                        null,
                        pants.getWaistSize(),
                        pants.getPantsLength());
            }

            throw new IllegalArgumentException("Tipus d'article no suportat");
        }
    }
}