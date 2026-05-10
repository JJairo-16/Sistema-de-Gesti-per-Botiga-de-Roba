package e.comerce.services.stock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.json.PolymorphicJsonLoader;
import e.comerce.models.articles.Article;
import e.comerce.models.articles.ArticleType;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;
import e.comerce.services.database.ShopDatabase;

/**
 * Gestiona la importació inicial d'articles des del JSON de central cap a la
 * base de dades de la botiga.
 */
public final class DatabaseRestocker {
    private DatabaseRestocker() {
    }

    private static final Path RESTOCK_PATH = Path.of("tpv/data/articles.json");
    private static final PolymorphicJsonLoader<Article> loader = PolymorphicJsonLoader.forBaseType(Article.class)
            .typeField("familia")
            .subtype(ArticleType.SHIRT.type(), Shirt.class)
            .subtype(ArticleType.PANTS.type(), Pants.class)
            .build();

    /**
     * Llegeix el JSON i prepara el resum de la importació sense escriure res a
     * la base de dades.
     */
    public static RestockPreview preview() throws IOException {
        return preview(RESTOCK_PATH);
    }

    /**
     * Llegeix un JSON concret i prepara el resum de la importació sense escriure
     * res a la base de dades.
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
     * <p>Si l'article ja existeix, se'n reinicialitzen tots els valors amb els
     * valors del JSON. Si no existeix, es dona d'alta.</p>
     */
    public static RestockResult commit(ShopDatabase db, RestockPreview preview) throws SQLException {
        Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
        Objects.requireNonNull(preview, "La previsualització no pot ser nul·la");

        int inserted = 0;
        int updated = 0;

        for (Article article : preview.articles()) {
            boolean alreadyExists = db.articles().exists(article.getId());
            db.articles().save(article);

            if (alreadyExists) {
                updated++;
            } else {
                inserted++;
            }
        }

        return new RestockResult(preview.total(), preview.shirts(), preview.pants(), inserted, updated);
    }

    /**
     * Carrega el JSON per defecte i el desa directament a la base de dades.
     *
     * <p>Aquest mètode es manté per compatibilitat amb el codi existent. Per a
     * fluxos amb confirmació prèvia, fes servir {@link #preview()} i
     * {@link #commit(ShopDatabase, RestockPreview)}.</p>
     */
    public static RestockResult restock(ShopDatabase db) throws IOException, SQLException {
        RestockPreview preview = preview();
        printPreview(preview);
        RestockResult result = commit(db, preview);
        printResult(result);
        return result;
    }

    /**
     * Mostra el resum dels articles carregats des del JSON abans de confirmar la
     * importació.
     */
    public static void printPreview(RestockPreview preview) {
        Objects.requireNonNull(preview, "La previsualització no pot ser nul·la");

        System.out.println("Articles preparats per importar: " + preview.total());
        System.out.println("Camises: " + preview.shirts());
        System.out.println("Pantalons: " + preview.pants());
    }

    /**
     * Mostra el resum final de la importació.
     */
    public static void printResult(RestockResult result) {
        Objects.requireNonNull(result, "El resultat no pot ser nul");

        System.out.println("Articles afegits: " + result.inserted());
        System.out.println("Articles actualitzats: " + result.updated());
    }

    private static int countByType(List<Article> articles, ArticleType type) {
        return (int) articles.stream()
                .filter(article -> article.getTypeKey() == type)
                .count();
    }

    /**
     * Dades carregades del JSON abans de modificar la base de dades.
     */
    public record RestockPreview(List<Article> articles, int total, int shirts, int pants) {
        public RestockPreview {
            articles = List.copyOf(Objects.requireNonNull(articles, "La llista d'articles no pot ser nul·la"));
        }
    }

    /**
     * Resum final del procés d'importació d'articles.
     */
    public record RestockResult(int total, int shirts, int pants, int inserted, int updated) {
    }
}
