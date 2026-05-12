package e.comerce.services.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.DbExecutor;
import e.comerce.models.articles.Article;
import e.comerce.models.articles.ArticleType;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;

/** Gestiona totes les operacions de base de dades relacionades amb articles. */
public class ArticleRepository {
    private final DbExecutor db;

    public ArticleRepository(DbExecutor db) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
    }

    public long insert(Article article) throws SQLException {
        validateArticle(article);
        ArticleDbValues values = ArticleDbValues.from(article);

        return db.insert(
                "INSERT INTO articles "
                        + "(id, nom, familia, talla_coll, amplada_pit, talla_cintura, llargada_camal, preu_base, iva, stock) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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

    public boolean update(Article article) throws SQLException {
        validateArticle(article);
        ArticleDbValues values = ArticleDbValues.from(article);

        int affectedRows = db.update(
                "UPDATE articles SET nom = ?, familia = ?, talla_coll = ?, amplada_pit = ?, "
                        + "talla_cintura = ?, llargada_camal = ?, preu_base = ?, iva = ?, stock = ? "
                        + "WHERE id = ?",
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

    public boolean save(Article article) throws SQLException {
        validateArticle(article);
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
                article.getType(),
                values.neckSize,
                values.chestWidth,
                values.waistSize,
                values.pantsLength,
                article.getBasePrice(),
                article.getIva(),
                article.getStock());

        return affectedRows > 0;
    }

    public boolean delete(int id) throws SQLException {
        validateId(id);
        return db.delete("DELETE FROM articles WHERE id = ?", id) > 0;
    }

    public Article findById(int id) throws SQLException {
        validateId(id);
        return db.one(ArticleMapper.SELECT_ARTICLES + " WHERE id = ?", id, ArticleMapper::map);
    }

    /**
     * Cerca un article i bloqueja la seva fila fins al final de la transacció.
     *
     * <p>
     * Aquest mètode només té sentit dins d'una transacció. És útil quan cal
     * llegir l'article, prendre decisions amb les seves dades i evitar canvis
     * concurrents fins que es faci {@code commit} o {@code rollback}.
     * </p>
     *
     * @param id identificador de l'article
     * @return article trobat o {@code null}
     * @throws SQLException si la base de dades retorna un error
     */
    public Article findByIdForUpdate(int id) throws SQLException {
        validateId(id);
        return db.one(ArticleMapper.SELECT_ARTICLES + " WHERE id = ? FOR UPDATE", id, ArticleMapper::map);
    }

    public List<Article> findAll() throws SQLException {
        return db.list(ArticleMapper.SELECT_ARTICLES + " ORDER BY id", ArticleMapper::map);
    }

    public List<Article> findByType(ArticleType type) throws SQLException {
        Objects.requireNonNull(type, "El tipus d'article no pot ser nul");
        return db.list(
                ArticleMapper.SELECT_ARTICLES + " WHERE familia = ? ORDER BY id",
                type.type(),
                ArticleMapper::map);
    }

    /**
     * Cerca articles per identificador, nom o família.
     *
     * @param query text de cerca
     * @return articles trobats
     * @throws SQLException si la base de dades retorna un error
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
                            + " WHERE id = ? OR LOWER(nom) LIKE ? OR LOWER(familia) LIKE ? "
                            + "ORDER BY id",
                    new Object[] { Integer.parseInt(cleanQuery), pattern, pattern },
                    ArticleMapper::map);
        }

        return db.list(
                ArticleMapper.SELECT_ARTICLES
                        + " WHERE LOWER(nom) LIKE ? OR LOWER(familia) LIKE ? "
                        + "ORDER BY id",
                new Object[] { pattern, pattern },
                ArticleMapper::map);
    }

    /**
     * Cerca articles d'un tipus concret per identificador o nom.
     *
     * @param type tipus d'article
     * @param query text de cerca
     * @return articles trobats
     * @throws SQLException si la base de dades retorna un error
     */
    public List<Article> searchByType(ArticleType type, String query) throws SQLException {
        Objects.requireNonNull(type, "El tipus d'article no pot ser nul");

        if (query == null || query.isBlank()) {
            return findByType(type);
        }

        String cleanQuery = query.trim();
        String pattern = "%" + cleanQuery.toLowerCase() + "%";

        if (isInteger(cleanQuery)) {
            return db.list(
                    ArticleMapper.SELECT_ARTICLES
                            + " WHERE familia = ? AND (id = ? OR LOWER(nom) LIKE ?) "
                            + "ORDER BY id",
                    new Object[] { type.type(), Integer.parseInt(cleanQuery), pattern },
                    ArticleMapper::map);
        }

        return db.list(
                ArticleMapper.SELECT_ARTICLES
                        + " WHERE familia = ? AND LOWER(nom) LIKE ? "
                        + "ORDER BY id",
                new Object[] { type.type(), pattern },
                ArticleMapper::map);
    }

    public boolean exists(int id) throws SQLException {
        validateId(id);
        return db.exists("SELECT 1 FROM articles WHERE id = ?", id);
    }

    public boolean updateStock(int id, int stock) throws SQLException {
        validateId(id);
        if (stock < 0) {
            throw new IllegalArgumentException("L'estoc no pot ser negatiu");
        }

        return db.update("UPDATE articles SET stock = ? WHERE id = ?", stock, id) > 0;
    }

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

    public List<Article> findBelowStock(int threshold) throws SQLException {
        if (threshold < 0) {
            throw new IllegalArgumentException("El llindar d'estoc no pot ser negatiu");
        }

        return db.list(
                ArticleMapper.SELECT_ARTICLES + " WHERE stock < ? ORDER BY stock ASC, id ASC",
                threshold,
                ArticleMapper::map);
    }

    /**
     * Comprova si un text representa un nombre enter.
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

    private static void validateArticle(Article article) {
        Objects.requireNonNull(article, "L'article no pot ser nul");
        validateId(article.getId());
    }

    private static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'id de l'article ha de ser positiu");
        }
    }

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

        private static ArticleDbValues from(Article article) {
            if (article.getTypeKey() == ArticleType.SHIRT) {
                Shirt shirt = (Shirt) article;
                return new ArticleDbValues(shirt.getNeckSize(), shirt.getChestWidth(), null, null);
            }

            if (article.getTypeKey() == ArticleType.PANTS) {
                Pants pants = (Pants) article;
                return new ArticleDbValues(null, null, pants.getWaistSize(), pants.getPantsLength());
            }

            throw new IllegalArgumentException("Tipus d'article no suportat");
        }
    }
}
