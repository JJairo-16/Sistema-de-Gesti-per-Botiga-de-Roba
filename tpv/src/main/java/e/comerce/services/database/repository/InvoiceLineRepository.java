package e.comerce.services.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.Database;
import e.comerce.models.InvoiceLine;

/**
 * Repositori de línies de factura associades als tiquets de venda.
 */
public class InvoiceLineRepository {
    private final Database db;

    public InvoiceLineRepository(Database db) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
    }

    /**
     * Desa una línia de factura.
     *
     * @param invoiceLine línia que es vol desar
     * @return {@code true} si s'ha inserit correctament
     * @throws SQLException si la base de dades retorna un error
     */
    public boolean insert(InvoiceLine invoiceLine) throws SQLException {
        Objects.requireNonNull(invoiceLine, "La línia de factura no pot ser nul·la");
        validateTicketId(invoiceLine.ticketId());

        return db.update(
                "INSERT INTO linies_factura "
                        + "(id_tiquet, id_article, quantitat, preu_base, iva, preu_final) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                invoiceLine.ticketId(),
                invoiceLine.articleId(),
                invoiceLine.quantity(),
                invoiceLine.baseAmount(),
                invoiceLine.vatAmount(),
                invoiceLine.finalAmount()) > 0;
    }

    /**
     * Elimina totes les línies d'un tiquet.
     *
     * @param ticketId identificador del tiquet
     * @return {@code true} si s'ha eliminat com a mínim una línia
     * @throws SQLException si la base de dades retorna un error
     */
    public boolean deleteByTicket(int ticketId) throws SQLException {
        validateTicketId(ticketId);
        return db.delete("DELETE FROM linies_factura WHERE id_tiquet = ?", ticketId) > 0;
    }

    /**
     * Cerca les línies associades a un tiquet.
     *
     * @param ticketId identificador del tiquet
     * @return llista de línies del tiquet
     * @throws SQLException si la base de dades retorna un error
     */
    public List<InvoiceLine> findByTicket(int ticketId) throws SQLException {
        validateTicketId(ticketId);

        return db.list(
                "SELECT id_tiquet, id_article, quantitat, preu_base, iva, preu_final "
                        + "FROM linies_factura WHERE id_tiquet = ? ORDER BY id_article",
                ticketId,
                rs -> new InvoiceLine(
                        rs.getInt("id_tiquet"),
                        rs.getInt("id_article"),
                        rs.getInt("quantitat"),
                        rs.getDouble("preu_base"),
                        rs.getDouble("iva"),
                        rs.getDouble("preu_final")));
    }

    private static void validateTicketId(int ticketId) {
        if (ticketId <= 0) {
            throw new IllegalArgumentException("L'id del tiquet ha de ser positiu");
        }
    }
}
