package e.comerce.services.stock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.table.TableLock;
import e.comerce.libs.json.PolymorphicJsonLoader;
import e.comerce.models.articles.Article;
import e.comerce.models.articles.ArticleType;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;
import e.comerce.services.database.ShopDatabase;
import e.comerce.services.database.ShopTransaction;

/**
 * Gestiona la importació d'articles des del JSON de central cap a la base de
 * dades de la botiga.
 *
 * <p>
 * La confirmació del reaprovisionament bloqueja la taula d'articles en mode
 * escriptura per evitar que una venda i una actualització de catàleg modifiquin
 * l'estoc alhora.
 * </p>
 */
public final class DatabaseRestocker {
    private DatabaseRestocker() {
    }

    private static final Path RESTOCK_PATH = Path.of("tpv/data/articles.json");

    private static final List<TableLock> RESTOCK_TABLE_LOCKS = List.of(
            TableLock.write("articles"));

    private static final PolymorphicJsonLoader<Article> loader = PolymorphicJsonLoader.forBaseType(Article.class)
            .typeField("familia")
            .subtype(ArticleType.SHIRT.type(), Shirt.class)
            .subtype(ArticleType.PANTS.type(), Pants.class)
            .build();

    /**
     * Llegeix el JSON per defecte i prepara el resum de la importació sense
     * escriure res a la base de dades.
     *
     * @return previsualització del reaprovisionament
     * @throws IOException si no es pot llegir el fitxer
     */
    public static RestockPreview preview() throws IOException {
        return preview(RESTOCK_PATH);
    }

    /**
     * Llegeix un JSON concret i prepara el resum de la importació sense escriure
     * res a la base de dades.
     *
     * @param jsonPath ruta del fitxer JSON
     * @return previsualització del reaprovisionament
     * @throws IOException si no es pot llegir el fitxer
     */
    public static RestockPreview preview(Path jsonPath) throws IOException {
        Objects.requireNonNull(jsonPath, "La ruta del JSON no pot ser nul·la");

        if (!Files.exists(jsonPath)) {
            throw new IllegalArgumentException(jsonPath + " no existeix");
        }

        List<Article> articles = loader.loadArray(jsonPath);
        int shirts = countByType(articles, ArticleType.SHIRT);
        int pants = countByType(articles, ArticleType.PANTS);

        return new RestockPreview(articles, articles.size(), shirts, pants);
    }

    /**
     * Desa a la base de dades els articles prèviament preparats.
     *
     * <p>
     * Aquesta operació és transaccional i bloqueja la taula {@code articles} en
     * mode escriptura. Això evita que les vendes puguin reduir estoc mentre el
     * reaprovisionament està actualitzant el catàleg.
     * </p>
     *
     * <p>
     * Si l'article ja existeix, se'n reinicialitzen els valors amb els valors
     * del JSON. Si no existeix, es dona d'alta.
     * </p>
     *
     * @param db base de dades de la botiga
     * @param preview articles preparats prèviament
     * @return resum final del reaprovisionament
     * @throws SQLException si la base de dades retorna un error o si no es pot
     * obtenir el bloqueig dins del temps configurat
     */
    public static RestockResult commit(ShopDatabase db, RestockPreview preview) throws SQLException {
        Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
        Objects.requireNonNull(preview, "La previsualització no pot ser nul·la");

        return db.transactionWithTableLocks(RESTOCK_TABLE_LOCKS, transaction -> commitLocked(transaction, preview));
    }

    /**
     * Desa a la base de dades els articles prèviament preparats fent servir un
     * timeout concret per obtenir el bloqueig de taula.
     *
     * @param db base de dades de la botiga
     * @param preview articles preparats prèviament
     * @param timeoutSeconds segons màxims d'espera per obtenir el bloqueig
     * @return resum final del reaprovisionament
     * @throws SQLException si la base de dades retorna un error o si no es pot
     * obtenir el bloqueig dins del temps indicat
     */
    public static RestockResult commit(
            ShopDatabase db,
            RestockPreview preview,
            int timeoutSeconds) throws SQLException {
        Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
        Objects.requireNonNull(preview, "La previsualització no pot ser nul·la");

        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("El timeout ha de ser superior a zero segons");
        }

        return db.transactionWithTableLocks(
                RESTOCK_TABLE_LOCKS,
                timeoutSeconds,
                transaction -> commitLocked(transaction, preview));
    }

    /**
     * Carrega el JSON per defecte i el desa directament a la base de dades.
     *
     * <p>
     * Aquest mètode es manté per compatibilitat amb el codi existent. Per a
     * fluxos amb confirmació prèvia, fes servir {@link #preview()} i
     * {@link #commit(ShopDatabase, RestockPreview)}.
     * </p>
     *
     * @param db base de dades de la botiga
     * @return resum final del reaprovisionament
     * @throws IOException si no es pot llegir el JSON
     * @throws SQLException si la base de dades retorna un error o si no es pot
     * obtenir el bloqueig dins del temps configurat
     */
    public static RestockResult restock(ShopDatabase db) throws IOException, SQLException {
        RestockPreview preview = preview();
        printPreview(preview);

        RestockResult result = commit(db, preview);
        printResult(result);

        return result;
    }

    /**
     * Carrega el JSON per defecte i el desa directament a la base de dades fent
     * servir un timeout concret per obtenir el bloqueig de taula.
     *
     * @param db base de dades de la botiga
     * @param timeoutSeconds segons màxims d'espera per obtenir el bloqueig
     * @return resum final del reaprovisionament
     * @throws IOException si no es pot llegir el JSON
     * @throws SQLException si la base de dades retorna un error o si no es pot
     * obtenir el bloqueig dins del temps indicat
     */
    public static RestockResult restock(ShopDatabase db, int timeoutSeconds) throws IOException, SQLException {
        RestockPreview preview = preview();
        printPreview(preview);

        RestockResult result = commit(db, preview, timeoutSeconds);
        printResult(result);

        return result;
    }

    /**
     * Mostra el resum dels articles carregats des del JSON abans de confirmar la
     * importació.
     *
     * @param preview previsualització del reaprovisionament
     */
    public static void printPreview(RestockPreview preview) {
        Objects.requireNonNull(preview, "La previsualització no pot ser nul·la");

        System.out.println("Articles preparats per importar: " + preview.total());
        System.out.println("Camises: " + preview.shirts());
        System.out.println("Pantalons: " + preview.pants());
    }

    /**
     * Mostra el resum final de la importació.
     *
     * @param result resultat del reaprovisionament
     */
    public static void printResult(RestockResult result) {
        Objects.requireNonNull(result, "El resultat no pot ser nul");

        System.out.println("Articles afegits: " + result.inserted());
        System.out.println("Articles actualitzats: " + result.updated());
    }

    private static RestockResult commitLocked(ShopTransaction transaction, RestockPreview preview) throws SQLException {
        int inserted = 0;
        int updated = 0;

        for (Article article : preview.articles()) {
            boolean alreadyExists = transaction.articles().exists(article.getId());

            transaction.articles().save(article);

            if (alreadyExists) {
                updated++;
            } else {
                inserted++;
            }
        }

        return new RestockResult(preview.total(), preview.shirts(), preview.pants(), inserted, updated);
    }

    private static int countByType(List<Article> articles, ArticleType type) {
        return (int) articles.stream()
                .filter(article -> article.getTypeKey() == type)
                .count();
    }

    /**
     * Dades carregades del JSON abans de modificar la base de dades.
     *
     * @param articles articles carregats
     * @param total nombre total d'articles
     * @param shirts nombre de camises
     * @param pants nombre de pantalons
     */
    public record RestockPreview(List<Article> articles, int total, int shirts, int pants) {
        public RestockPreview {
            articles = List.copyOf(Objects.requireNonNull(articles, "La llista d'articles no pot ser nul·la"));
        }
    }

    /**
     * Resum final del procés d'importació d'articles.
     *
     * @param total nombre total d'articles llegits
     * @param shirts nombre de camises llegides
     * @param pants nombre de pantalons llegits
     * @param inserted articles afegits
     * @param updated articles actualitzats
     */
    public record RestockResult(int total, int shirts, int pants, int inserted, int updated) {
    }
}
