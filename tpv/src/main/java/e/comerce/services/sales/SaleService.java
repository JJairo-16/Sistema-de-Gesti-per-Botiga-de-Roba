package e.comerce.services.sales;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.models.InvoiceLine;
import e.comerce.models.Ticket;
import e.comerce.services.database.ShopDatabase;

/**
 * Servei d'aplicació que registra una venda completa a la base de dades.
 *
 * <p>Centralitza la inserció del tiquet, la inserció de les línies de factura
 * i la reducció d'estoc dels articles venuts.</p>
 */
public class SaleService {
    private final ShopDatabase shopDatabase;

    /**
     * Crea el servei de vendes amb la connexió centralitzada de la botiga.
     *
     * @param shopDatabase accés centralitzat a la base de dades
     */
    public SaleService(ShopDatabase shopDatabase) {
        this.shopDatabase = Objects.requireNonNull(
                shopDatabase,
                "La base de dades de la botiga no pot ser nul·la");
    }

    /**
     * Registra una venda i actualitza l'estoc.
     *
     * @param ticket capçalera del tiquet
     * @param invoiceLines línies de factura associades
     * @return identificador generat del tiquet
     * @throws SQLException si la base de dades retorna un error
     */
    public long registerSale(Ticket ticket, List<InvoiceLine> invoiceLines) throws SQLException {
        
        Objects.requireNonNull(ticket, "El tiquet no pot ser nul");
        Objects.requireNonNull(invoiceLines, "Les línies de factura no poden ser nul·les");

        if (invoiceLines.isEmpty()) {
            throw new IllegalArgumentException("Una venda ha de tenir com a mínim una línia");
        }

        long generatedTicketId = shopDatabase.tickets().insert(ticket);
        int ticketId = resolveTicketId(generatedTicketId, ticket.id());

        for (InvoiceLine invoiceLine : invoiceLines) {
            InvoiceLine invoiceLineToSave = invoiceLine.ticketId() == ticketId
                    ? invoiceLine
                    : invoiceLine.withTicketId(ticketId);

            decreaseStock(invoiceLineToSave);
            shopDatabase.invoiceLines().insert(invoiceLineToSave);
        }

        return generatedTicketId;
    }

    private void decreaseStock(InvoiceLine invoiceLine) throws SQLException {
        boolean stockUpdated = shopDatabase.articles()
                .decreaseStock(invoiceLine.articleId(), invoiceLine.quantity());

        if (!stockUpdated) {
            throw new SQLException("No hi ha prou estoc per a l'article " + invoiceLine.articleId());
        }
    }

    private static int resolveTicketId(long generatedTicketId, int providedTicketId) throws SQLException {
        if (generatedTicketId > 0) {
            return Math.toIntExact(generatedTicketId);
        }

        if (providedTicketId > 0) {
            return providedTicketId;
        }

        throw new SQLException("No s'ha pogut obtenir l'id del tiquet registrat");
    }
}
