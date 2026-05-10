package e.comerce.services.database.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import e.comerce.models.articles.Article;
import e.comerce.models.articles.ArticleType;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;

/** Converteix files SQL en articles del model de domini. */
final class ArticleMapper {
    static final String SELECT_ARTICLES = "SELECT id, nom, familia, talla_coll, amplada_pit, "
            + "talla_cintura, llargada_camal, preu_base, iva, stock FROM articles";

    private ArticleMapper() {
    }

    static Article map(ResultSet rs) throws SQLException {
        ArticleType type = ArticleType.getType(rs.getString("familia"));

        return switch (type) {
            case SHIRT -> new Shirt(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    ArticleType.SHIRT.type(),
                    rs.getInt("talla_coll"),
                    rs.getInt("amplada_pit"),
                    rs.getDouble("preu_base"),
                    rs.getInt("iva"),
                    rs.getInt("stock"));
            case PANTS -> new Pants(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("llargada_camal"),
                    rs.getInt("talla_cintura"),
                    rs.getDouble("preu_base"),
                    rs.getInt("iva"),
                    rs.getInt("stock"));
        };
    }
}
