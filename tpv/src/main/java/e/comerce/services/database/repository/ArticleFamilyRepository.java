package e.comerce.services.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.DbExecutor;
import e.comerce.models.ArticleFamily;

/**
 * Gestiona totes les operacions de base de dades relacionades amb famílies
 * d'articles.
 */
public class ArticleFamilyRepository {
    private static final int MAX_FAMILY_LENGTH = 20;
    private static final String DEFAULT_FAMILY = "gèneric";

    private final DbExecutor db;

    public ArticleFamilyRepository(DbExecutor db) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
    }

    public long insert(String family) throws SQLException {
        String cleanFamily = validateFamilyForInsert(family);

        return db.insert(
                "INSERT INTO familias (nom) VALUES (?)",
                cleanFamily);
    }

    public boolean update(int id, String family) throws SQLException {
        validateId(id);
        String cleanFamily = validateFamilyForUpdate(id, family);

        return db.update(
                "UPDATE familias SET nom = ? WHERE id = ?",
                cleanFamily,
                id) > 0;
    }

    public boolean update(String oldFamily, String newFamily) throws SQLException {
        String cleanOldFamily = validateFamily(oldFamily);
        ArticleFamily currentFamily = findExactByName(cleanOldFamily);

        if (currentFamily == null) {
            throw new IllegalArgumentException("La família d'articles no existeix");
        }

        return update(currentFamily.id(), newFamily);
    }

    public boolean save(String family) throws SQLException {
        String cleanFamily = validateFamily(family);

        return db.update(
                "INSERT INTO familias (nom) VALUES (?) "
                        + "ON DUPLICATE KEY UPDATE nom = VALUES(nom)",
                cleanFamily) > 0;
    }

    public boolean delete(int id) throws SQLException {
        validateFamilyForDelete(id);

        return db.delete(
                "DELETE FROM familias WHERE id = ?",
                id) > 0;
    }

    public boolean delete(String family) throws SQLException {
        String cleanFamily = validateFamily(family);
        ArticleFamily currentFamily = findExactByName(cleanFamily);

        if (currentFamily == null) {
            throw new IllegalArgumentException("La família d'articles no existeix");
        }

        return delete(currentFamily.id());
    }

    public ArticleFamily findById(int id) throws SQLException {
        validateId(id);

        return db.one(
                "SELECT id, nom FROM familias WHERE id = ?",
                id,
                rs -> new ArticleFamily(rs.getInt("id"), rs.getString("nom")));
    }

    public ArticleFamily findByName(String family) throws SQLException {
        String cleanFamily = validateFamily(family);

        ArticleFamily result = findExactByName(cleanFamily);

        if (result != null) {
            return result;
        }

        ArticleFamily defaultFamily = findExactByName(DEFAULT_FAMILY);

        if (defaultFamily == null) {
            throw new IllegalArgumentException("La família d'articles no existeix i tampoc existeix la família genèrica");
        }

        return defaultFamily;
    }

    public ArticleFamily findExactByName(String family) throws SQLException {
        String cleanFamily = validateFamily(family);

        return db.one(
                "SELECT id, nom FROM familias WHERE LOWER(nom) = LOWER(?)",
                cleanFamily,
                rs -> new ArticleFamily(rs.getInt("id"), rs.getString("nom")));
    }

    public List<ArticleFamily> findAll() throws SQLException {
        return db.list(
                "SELECT id, nom FROM familias ORDER BY nom, id",
                rs -> new ArticleFamily(rs.getInt("id"), rs.getString("nom")));
    }

    public List<ArticleFamily> search(String query) throws SQLException {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String cleanQuery = query.trim();
        String pattern = "%" + cleanQuery.toLowerCase() + "%";

        if (isInteger(cleanQuery)) {
            return db.list(
                    "SELECT id, nom FROM familias "
                            + "WHERE id = ? OR LOWER(nom) LIKE ? "
                            + "ORDER BY nom, id",
                    new Object[] { Integer.parseInt(cleanQuery), pattern },
                    rs -> new ArticleFamily(rs.getInt("id"), rs.getString("nom")));
        }

        return db.list(
                "SELECT id, nom FROM familias "
                        + "WHERE LOWER(nom) LIKE ? "
                        + "ORDER BY nom, id",
                pattern,
                rs -> new ArticleFamily(rs.getInt("id"), rs.getString("nom")));
    }

    public boolean exists(int id) throws SQLException {
        validateId(id);

        return db.exists(
                "SELECT 1 FROM familias WHERE id = ?",
                id);
    }

    public boolean exists(String family) throws SQLException {
        String cleanFamily = validateFamily(family);

        return db.exists(
                "SELECT 1 FROM familias WHERE LOWER(nom) = LOWER(?)",
                cleanFamily);
    }

    public boolean isUsed(int id) throws SQLException {
        validateId(id);

        return db.exists(
                "SELECT 1 FROM articles WHERE familia = ? LIMIT 1",
                id);
    }

    public long countArticles(int id) throws SQLException {
        validateId(id);

        return db.count(
                "SELECT COUNT(*) FROM articles WHERE familia = ?",
                id);
    }

    private String validateFamilyForInsert(String family) throws SQLException {
        String cleanFamily = validateFamily(family);

        if (exists(cleanFamily)) {
            throw new IllegalArgumentException("La família d'articles ja existeix");
        }

        return cleanFamily;
    }

    private String validateFamilyForUpdate(int id, String family) throws SQLException {
        String cleanFamily = validateFamily(family);

        if (!exists(id)) {
            throw new IllegalArgumentException("La família d'articles no existeix");
        }

        if (db.exists(
                "SELECT 1 FROM familias WHERE LOWER(nom) = LOWER(?) AND id <> ?",
                cleanFamily,
                id)) {
            throw new IllegalArgumentException("Ja existeix una altra família amb aquest nom");
        }

        return cleanFamily;
    }

    private void validateFamilyForDelete(int id) throws SQLException {
        validateId(id);

        if (!exists(id)) {
            throw new IllegalArgumentException("La família d'articles no existeix");
        }

        if (isUsed(id)) {
            throw new IllegalArgumentException("No es pot eliminar una família que té articles associats");
        }
    }

    private static String validateFamily(String family) {
        if (family == null || family.isBlank()) {
            throw new IllegalArgumentException("La família d'articles no pot estar buida");
        }

        String cleanFamily = family.trim();

        if (cleanFamily.length() > MAX_FAMILY_LENGTH) {
            throw new IllegalArgumentException("La família d'articles no pot superar els 20 caràcters");
        }

        return cleanFamily;
    }

    private static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'id de la família d'articles ha de ser positiu");
        }
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}