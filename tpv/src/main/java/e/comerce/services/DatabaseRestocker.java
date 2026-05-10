package e.comerce.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import e.comerce.libs.json.PolymorphicJsonLoader;
import e.comerce.models.Article;
import e.comerce.models.ArticleType;
import e.comerce.models.Pants;
import e.comerce.models.Shirt;

public final class DatabaseRestocker {
    private DatabaseRestocker() {
    }

    private static final Path RESTOCK_PATH = Path.of("tpv/data/articles.json");
    private static final PolymorphicJsonLoader<Article> loader = PolymorphicJsonLoader.forBaseType(Article.class)
            .typeField("familia")
            .subtype(ArticleType.SHIRT.type(), Shirt.class)
            .subtype(ArticleType.PANTS.type(), Pants.class)
            .build();

    public static void restock(DatabaseShop db) throws IOException, SQLException {
        if (!Files.exists(RESTOCK_PATH))
            throw new IllegalArgumentException(RESTOCK_PATH.toString() + " no existeix");

        List<Article> articles = loader.loadArray(RESTOCK_PATH);
        for (Article article : articles) {
            System.out.println(article.getType());
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        try (DatabaseShop shop = new DatabaseShop()) {
            DatabaseRestocker.restock(shop);
        }
    }
}
