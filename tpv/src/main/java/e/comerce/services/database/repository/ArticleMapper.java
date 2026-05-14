package e.comerce.services.database.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import e.comerce.models.articles.Article;
import e.comerce.models.articles.ArticleType;
import e.comerce.models.articles.GenericArticle;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;

/** Converteix files SQL en articles del model de domini. */
final class ArticleMapper {
    /** Consulta base per obtenir articles amb la seva família. */
    static final String SELECT_ARTICLES = """
            SELECT articles.id, articles.nom, familias.nom AS familia,
                   articles.talla_coll, articles.amplada_pit,
                   articles.talla_cintura, articles.llargada_camal,
                   articles.preu_base, articles.iva, articles.stock
            FROM articles
            JOIN familias ON familias.id = articles.familia
            """;

    private ArticleMapper() {
    }

    /**
     * Crea un article a partir de la fila actual del ResultSet.
     *
     * @param rs ResultSet posicionat en una fila vàlida
     * @return article corresponent a la família indicada
     * @throws SQLException si falla la lectura de dades SQL
     */
    static Article map(ResultSet rs) throws SQLException {
        String family = getFamily(rs);
        ArticleType type = ArticleType.getType(family);

        return switch (type) {
            case SHIRT -> new Shirt(
                    getId(rs),
                    getName(rs),
                    family,
                    rs.getInt("talla_coll"),
                    rs.getInt("amplada_pit"),
                    getBasePrice(rs),
                    getVat(rs),
                    getStock(rs));

            case PANTS -> new Pants(
                    getId(rs),
                    getName(rs),
                    rs.getInt("llargada_camal"),
                    rs.getInt("talla_cintura"),
                    getBasePrice(rs),
                    getVat(rs),
                    getStock(rs));

            default -> new GenericArticle(
                    getId(rs),
                    getName(rs),
                    family,
                    getBasePrice(rs),
                    getVat(rs),
                    getStock(rs));
        };
    }

    /**
     * Llegeix l'identificador de l'article.
     *
     * @param rs ResultSet actual
     * @return identificador de l'article
     * @throws SQLException si falla la lectura
     */
    private static int getId(ResultSet rs) throws SQLException {
        return rs.getInt("id");
    }

    /**
     * Llegeix el nom de l'article.
     *
     * @param rs ResultSet actual
     * @return nom de l'article
     * @throws SQLException si falla la lectura
     */
    private static String getName(ResultSet rs) throws SQLException {
        return rs.getString("nom");
    }

    /**
     * Llegeix la família de l'article.
     *
     * @param rs ResultSet actual
     * @return família de l'article
     * @throws SQLException si falla la lectura
     */
    private static String getFamily(ResultSet rs) throws SQLException {
        return rs.getString("familia");
    }

    /**
     * Llegeix el preu base.
     *
     * @param rs ResultSet actual
     * @return preu base de l'article
     * @throws SQLException si falla la lectura
     */
    private static double getBasePrice(ResultSet rs) throws SQLException {
        return rs.getDouble("preu_base");
    }

    /**
     * Llegeix l'IVA de l'article.
     *
     * @param rs ResultSet actual
     * @return percentatge d'IVA
     * @throws SQLException si falla la lectura
     */
    private static int getVat(ResultSet rs) throws SQLException {
        return rs.getInt("iva");
    }

    /**
     * Llegeix l'estoc de l'article.
     *
     * @param rs ResultSet actual
     * @return unitats en estoc
     * @throws SQLException si falla la lectura
     */
    private static int getStock(ResultSet rs) throws SQLException {
        return rs.getInt("stock");
    }
}