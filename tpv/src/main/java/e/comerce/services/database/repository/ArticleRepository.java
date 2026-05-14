package e.comerce.services.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.DbExecutor;
import e.comerce.models.articles.Article;
import e.comerce.models.articles.ArticleType;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;

/** Gestiona les operacions de base de dades relacionades amb articles. */
public class ArticleRepository {
    private final DbExecutor db;
    private final ArticleFamilyRepository families;

    /**
     * Crea un repositori d'articles.
     *
     * @param db executor de base de dades
     * @param family repositori de famílies
     */
    public ArticleRepository(DbExecutor db, ArticleFamilyRepository family) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
        this.families = Objects.requireNonNull(family, "El repositori de families no pot ser nul.");
    }

    /**
     * Insereix un article, amb id si ja en té.
     *
     * @param article article a inserir
     * @return id generat o retornat per la base de dades
     * @throws SQLException si falla l'operació
     */
    public long insert(Article article) throws SQLException {
        if (article != null && article.getId() == -1) {
            return insertWithoutId(article);
        }

        validateExistingArticle(article);
        ArticleDbValues values = ArticleDbValues.from(article);

        return db.insert(
                "INSERT INTO articles "
                        + "(id, nom, familia, talla_coll, amplada_pit, talla_cintura, llargada_camal, preu_base, iva, stock) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                article.getId(),
                article.getName(),
                getTypeId(article),
                values.neckSize,
                values.chestWidth,
                values.waistSize,
                values.pantsLength,
                article.getBasePrice(),
                article.getIva(),
                article.getStock());
    }

    /**
     * Insereix un article sense id.
     *
     * @param article article a inserir
     * @return id generat
     * @throws SQLException si falla l'operació
     */
    public long insertWithoutId(Article article) throws SQLException {
        validateNewArticle(article);
        ArticleDbValues values = ArticleDbValues.from(article);

        return db.insert(
                "INSERT INTO articles "
                        + "(nom, familia, talla_coll, amplada_pit, talla_cintura, llargada_camal, preu_base, iva, stock) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                article.getName(),
                getTypeId(article),
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
     * @param article article amb les dades noves
     * @return {@code true} si s'ha modificat alguna fila
     * @throws SQLException si falla l'operació
     */
    public boolean update(Article article) throws SQLException {
        validateExistingArticle(article);
        ArticleDbValues values = ArticleDbValues.from(article);

        int affectedRows = db.update(
                "UPDATE articles SET nom = ?, familia = ?, talla_coll = ?, amplada_pit = ?, "
                        + "talla_cintura = ?, llargada_camal = ?, preu_base = ?, iva = ?, stock = ? "
                        + "WHERE id = ?",
                article.getName(),
                getTypeId(article),
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
     * @return {@code true} si s'ha inserit o actualitzat
     * @throws SQLException si falla l'operació
     */
    public boolean save(Article article) throws SQLException {
        validateExistingArticle(article);
        ArticleDbValues values = ArticleDbValues.from(article);

        int affectedRows = db.update(
                "INSERT INTO articles "
                        + "(id, nom, familia, talla_coll, amplada_pit, talla_cintura, llargada_camal, preu_base, iva, stock) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "nom = VALUES(nom), familia = VALUES(familia), talla_coll = VALUES(talla_coll), "
                        + "amplada_pit = VALUES(amplada_pit), talla_cintura = VALUES(talla_cintura), "
                        + "llargada_camal = VALUES(llargada_camal), preu_base = VALUES(preu_base), "
                        + "iva = VALUES(iva), stock = VALUES(stock)",
                article.getId(),
                article.getName(),
                getTypeId(article),
                values.neckSize,
                values.chestWidth,
                values.waistSize,
                values.pantsLength,
                article.getBasePrice(),
                article.getIva(),
                article.getStock());

        return affectedRows > 0;
    }

    /**
     * Elimina un article pel seu id.
     *
     * @param id identificador de l'article
     * @return {@code true} si s'ha eliminat
     * @throws SQLException si falla l'operació
     */
    public boolean delete(int id) throws SQLException {
        validateId(id);
        return db.delete("DELETE FROM articles WHERE id = ?", id) > 0;
    }

    /**
     * Cerca un article pel seu id.
     *
     * @param id identificador de l'article
     * @return article trobat o {@code null}
     * @throws SQLException si falla la consulta
     */
    public Article findById(int id) throws SQLException {
        validateId(id);
        return db.one(ArticleMapper.SELECT_ARTICLES + " WHERE articles.id = ?", id, ArticleMapper::map);
    }

    /**
     * Cerca un article i bloqueja la seva fila fins al final de la transacció.
     *
     * @param id identificador de l'article
     * @return article trobat o {@code null}
     * @throws SQLException si falla la consulta
     */
    public Article findByIdForUpdate(int id) throws SQLException {
        validateId(id);
        return db.one(ArticleMapper.SELECT_ARTICLES + " WHERE articles.id = ? FOR UPDATE", id, ArticleMapper::map);
    }

    /**
     * Retorna tots els articles.
     *
     * @return llista d'articles
     * @throws SQLException si falla la consulta
     */
    public List<Article> findAll() throws SQLException {
        return db.list(ArticleMapper.SELECT_ARTICLES + " ORDER BY articles.id", ArticleMapper::map);
    }

    /**
     * Retorna els articles d'un tipus concret.
     *
     * @param type tipus d'article
     * @return articles del tipus indicat
     * @throws SQLException si falla la consulta
     */
    public List<Article> findByType(ArticleType type) throws SQLException {
        Objects.requireNonNull(type, "El tipus d'article no pot ser nul");
        return db.list(
                ArticleMapper.SELECT_ARTICLES + " WHERE articles.familia = ? ORDER BY articles.id",
                getTypeId(type.type()),
                ArticleMapper::map);
    }

    /**
     * Cerca articles per id, nom o família.
     *
     * @param query text de cerca
     * @return articles trobats
     * @throws SQLException si falla la consulta
     */
    public List<Article> search(String query) throws SQLException {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String cleanQuery = query.trim();
        String pattern = "%" + cleanQuery.toLowerCase() + "%";

        if (isInteger(cleanQuery)) {
            return db.list(
                    ArticleMapper.SELECT_ARTICLES
                            + " WHERE articles.id = ? OR LOWER(articles.nom) LIKE ? OR LOWER(familias.nom) LIKE ? "
                            + "ORDER BY articles.id",
                    new Object[] { Integer.parseInt(cleanQuery), pattern, pattern },
                    ArticleMapper::map);
        }

        return db.list(
                ArticleMapper.SELECT_ARTICLES
                        + " WHERE LOWER(articles.nom) LIKE ? OR LOWER(familias.nom) LIKE ? "
                        + "ORDER BY articles.id",
                new Object[] { pattern, pattern },
                ArticleMapper::map);
    }

    /**
     * Cerca articles d'un tipus concret per id o nom.
     *
     * @param type tipus d'article
     * @param query text de cerca
     * @return articles trobats
     * @throws SQLException si falla la consulta
     */
    public List<Article> searchByType(ArticleType type, String query) throws SQLException {
        Objects.requireNonNull(type, "El tipus d'article no pot ser nul");

        if (query == null || query.isBlank()) {
            return findByType(type);
        }

        int typeId = getTypeId(type.type());
        String cleanQuery = query.trim();
        String pattern = "%" + cleanQuery.toLowerCase() + "%";

        if (isInteger(cleanQuery)) {
            return db.list(
                    ArticleMapper.SELECT_ARTICLES
                            + " WHERE articles.familia = ? AND (articles.id = ? OR LOWER(articles.nom) LIKE ?) "
                            + "ORDER BY articles.id",
                    new Object[] { typeId, Integer.parseInt(cleanQuery), pattern },
                    ArticleMapper::map);
        }

        return db.list(
                ArticleMapper.SELECT_ARTICLES
                        + " WHERE articles.familia = ? AND LOWER(articles.nom) LIKE ? "
                        + "ORDER BY articles.id",
                new Object[] { typeId, pattern },
                ArticleMapper::map);
    }

    /**
     * Comprova si existeix un article.
     *
     * @param id identificador de l'article
     * @return {@code true} si existeix
     * @throws SQLException si falla la consulta
     */
    public boolean exists(int id) throws SQLException {
        validateId(id);
        return db.exists("SELECT 1 FROM articles WHERE id = ?", id);
    }

    /**
     * Actualitza l'estoc d'un article.
     *
     * @param id identificador de l'article
     * @param stock nou estoc
     * @return {@code true} si s'ha actualitzat
     * @throws SQLException si falla l'operació
     */
    public boolean updateStock(int id, int stock) throws SQLException {
        validateId(id);
        if (stock < 0) {
            throw new IllegalArgumentException("L'estoc no pot ser negatiu");
        }

        return db.update("UPDATE articles SET stock = ? WHERE id = ?", stock, id) > 0;
    }

    /**
     * Redueix l'estoc si hi ha unitats suficients.
     *
     * @param id identificador de l'article
     * @param quantity quantitat a restar
     * @return {@code true} si s'ha reduït l'estoc
     * @throws SQLException si falla l'operació
     */
    public boolean decreaseStock(int id, int quantity) throws SQLException {
        validateId(id);
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantitat ha de ser positiva");
        }

        return db.update(
                "UPDATE articles SET stock = stock - ? WHERE id = ? AND stock >= ?",
                quantity,
                id,
                quantity) > 0;
    }

    /**
     * Cerca articles amb estoc inferior al llindar.
     *
     * @param threshold llindar d'estoc
     * @return articles amb estoc baix
     * @throws SQLException si falla la consulta
     */
    public List<Article> findBelowStock(int threshold) throws SQLException {
        if (threshold < 0) {
            throw new IllegalArgumentException("El llindar d'estoc no pot ser negatiu");
        }

        return db.list(
                ArticleMapper.SELECT_ARTICLES + " WHERE articles.stock < ? ORDER BY articles.stock ASC, articles.id ASC",
                threshold,
                ArticleMapper::map);
    }

    /**
     * Comprova si un text representa un enter.
     *
     * @param value text a comprovar
     * @return {@code true} si és enter
     */
    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Obté l'id d'una família pel seu nom.
     *
     * @param type nom de la família
     * @return id de la família
     * @throws SQLException si falla la consulta
     */
    private int getTypeId(String type) throws SQLException {
        return families.findByName(type).id();
    }

    /**
     * Obté l'id del tipus d'un article.
     *
     * @param article article
     * @return id de la família
     * @throws SQLException si falla la consulta
     */
    private int getTypeId(Article article) throws SQLException {
        return getTypeId(article.getType());
    }

    /**
     * Valida un article nou.
     *
     * @param article article a validar
     */
    private static void validateNewArticle(Article article) {
        Objects.requireNonNull(article, "L'article no pot ser nul");

        if (article.getId() != -1) {
            validateId(article.getId());
        }
    }

    /**
     * Valida un article existent.
     *
     * @param article article a validar
     */
    private static void validateExistingArticle(Article article) {
        Objects.requireNonNull(article, "L'article no pot ser nul");
        validateId(article.getId());
    }

    /**
     * Valida que un id sigui positiu.
     *
     * @param id identificador a validar
     */
    private static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'id de l'article ha de ser positiu");
        }
    }

    /** Valors específics segons el tipus d'article. */
    private static final class ArticleDbValues {
        private final Integer neckSize;
        private final Integer chestWidth;
        private final Integer waistSize;
        private final Integer pantsLength;

        private ArticleDbValues(Integer neckSize, Integer chestWidth, Integer waistSize, Integer pantsLength) {
            this.neckSize = neckSize;
            this.chestWidth = chestWidth;
            this.waistSize = waistSize;
            this.pantsLength = pantsLength;
        }

        /**
         * Extreu els valors específics d'un article.
         *
         * @param article article origen
         * @return valors per guardar a la base de dades
         */
        private static ArticleDbValues from(Article article) {
            if (article.getTypeKey() == ArticleType.SHIRT) {
                Shirt shirt = (Shirt) article;
                return new ArticleDbValues(shirt.getNeckSize(), shirt.getChestWidth(), null, null);
            }

            if (article.getTypeKey() == ArticleType.PANTS) {
                Pants pants = (Pants) article;
                return new ArticleDbValues(null, null, pants.getWaistSize(), pants.getPantsLength());
            }

            return new ArticleDbValues(null, null, null, null);
        }
    }
}