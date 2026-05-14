package e.comerce.services.database.report;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.DbExecutor;

/** Consultes agregades de vendes i beneficis. */
public class SalesReportRepository {
    private final DbExecutor db;

    public SalesReportRepository(DbExecutor db) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
    }

    public ClientSalesSummary summarizeClient(String dniClient) throws SQLException {
        if (dniClient == null || dniClient.isBlank()) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }

        return db.one(
                "SELECT c.dni, c.nom, COUNT(t.id) AS num_tiquets, COALESCE(SUM(t.total_final), 0) AS despesa_total "
                        + "FROM clients c LEFT JOIN tiquets t ON t.dni_client = c.dni "
                        + "WHERE c.dni = ? GROUP BY c.dni, c.nom",
                dniClient,
                rs -> new ClientSalesSummary(
                        rs.getString("dni"),
                        rs.getString("nom"),
                        rs.getInt("num_tiquets"),
                        rs.getDouble("despesa_total")));
    }

    public ArticleSalesSummary summarizeArticle(int articleId) throws SQLException {
        if (articleId <= 0) {
            throw new IllegalArgumentException("L'id de l'article ha de ser positiu");
        }

        return db.one(
                "SELECT a.id, a.nom, COALESCE(SUM(l.quantitat), 0) AS quantitat_venuda "
                        + "FROM articles a LEFT JOIN linies_factura l ON l.id_article = a.id "
                        + "WHERE a.id = ? GROUP BY a.id, a.nom",
                articleId,
                rs -> new ArticleSalesSummary(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("quantitat_venuda")));
    }

    public List<ArticleProfitSummary> summarizeProfits(boolean ascending) throws SQLException {
        String order = ascending ? "ASC" : "DESC";

        return db.list(
                "SELECT a.id, a.nom, a.familia, a.preu_base, a.talla_coll, a.llargada_camal, "
                        + "COALESCE(SUM(l.quantitat), 0) AS unitats_venudes, "
                        + "COALESCE(SUM(l.preu_base), 0) AS import_vendes_base "
                        + "FROM articles a LEFT JOIN linies_factura l ON l.id_article = a.id "
                        + "GROUP BY a.id, a.nom, a.familia, a.preu_base, a.talla_coll, a.llargada_camal "
                        + "ORDER BY "
                        + "(COALESCE(SUM(l.preu_base), 0) - "
                        + "CASE WHEN a.familia = 'camisa' THEN (a.preu_base * 0.35 + a.talla_coll * 0.3) "
                        + "ELSE (a.preu_base * 0.30 + a.llargada_camal * 0.2) END * COALESCE(SUM(l.quantitat), 0)) "
                        + order,
                rs -> {
                    double costUnitari = "camisa".equals(rs.getString("familia"))
                            ? rs.getDouble("preu_base") * 0.35 + rs.getInt("talla_coll") * 0.3
                            : rs.getDouble("preu_base") * 0.30 + rs.getInt("llargada_camal") * 0.2;
                    int unitats = rs.getInt("unitats_venudes");
                    double vendesBase = rs.getDouble("import_vendes_base");
                    double costTotal = costUnitari * unitats;

                    return new ArticleProfitSummary(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("familia"),
                            unitats,
                            costUnitari,
                            costTotal,
                            vendesBase,
                            vendesBase - costTotal);
                });
    }

    public record ClientSalesSummary(String dni, String nom, int nombreTiquets, double despesaTotal) {
    }

    public record ArticleSalesSummary(int idArticle, String nom, int quantitatVenuda) {
    }

    public record ArticleProfitSummary(
            int idArticle,
            String nom,
            String familia,
            int unitatsVenudes,
            double costUnitari,
            double costTotal,
            double vendesBase,
            double benefici) {
    }
}
