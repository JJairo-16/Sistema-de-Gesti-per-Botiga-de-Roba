package e.comerce.services;

import java.util.List;
import java.util.Objects;

import e.comerce.models.articles.Article;
import e.comerce.models.InvoiceLine;
import e.comerce.models.Ticket;

/**
 * Servei de domini per construir tiquets i línies de venda del TPV.
 *
 * <p>Aquesta classe no accedeix a la base de dades. Només calcula imports i
 * genera objectes preparats per ser desats amb els serveis de persistència.</p>
 */
public final class TPVService {

    /**
     * Calcula l'import base total d'una venda.
     *
     * @param invoiceLines línies de factura de la venda
     * @return suma dels imports sense IVA
     */
    public double calculateTotalBase(List<InvoiceLine> invoiceLines) {
        return validateLines(invoiceLines).stream()
                .mapToDouble(InvoiceLine::baseAmount)
                .sum();
    }

    /**
     * Calcula l'IVA total d'una venda.
     *
     * @param invoiceLines línies de factura de la venda
     * @return suma dels imports d'IVA
     */
    public double calculateTotalVat(List<InvoiceLine> invoiceLines) {
        return validateLines(invoiceLines).stream()
                .mapToDouble(InvoiceLine::vatAmount)
                .sum();
    }

    /**
     * Calcula l'import final total d'una venda.
     *
     * @param invoiceLines línies de factura de la venda
     * @return suma dels imports amb IVA inclòs
     */
    public double calculateTotalFinal(List<InvoiceLine> invoiceLines) {
        return validateLines(invoiceLines).stream()
                .mapToDouble(InvoiceLine::finalAmount)
                .sum();
    }

    /**
     * Crea una línia de factura a partir d'un article i una quantitat.
     *
     * @param ticketId identificador del tiquet, o {@code 0} si encara no està desat
     * @param article article venut
     * @param quantity unitats venudes
     * @return línia de factura calculada
     */
    public InvoiceLine createInvoiceLine(int ticketId, Article article, int quantity) {
        Objects.requireNonNull(article, "L'article no pot ser nul");

        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantitat ha de ser superior a 0");
        }

        if (article.getStock() < quantity) {
            throw new IllegalArgumentException("No hi ha prou estoc per a l'article " + article.getId());
        }

        double baseAmount = article.getBasePrice() * quantity;
        double vatAmount = article.getIvaAmount() * quantity;
        double finalAmount = article.getFinalPrice() * quantity;

        return new InvoiceLine(
                ticketId,
                article.getId(),
                quantity,
                baseAmount,
                vatAmount,
                finalAmount);
    }

    /**
     * Crea un tiquet amb data actual a partir de les línies de factura.
     *
     * @param ticketId identificador del tiquet, o {@code 0} abans de desar-lo
     * @param customerDni DNI/NIF del client
     * @param invoiceLines línies de factura de la venda
     * @return tiquet preparat per ser registrat
     */
    public Ticket createTicket(int ticketId, String customerDni, List<InvoiceLine> invoiceLines) {
        return new Ticket(
                ticketId,
                customerDni,
                Ticket.getCurrentDateFormatted(),
                calculateTotalBase(invoiceLines),
                calculateTotalVat(invoiceLines),
                calculateTotalFinal(invoiceLines));
    }

    /**
     * Crea un tiquet nou sense identificador assignat.
     *
     * @param customerDni DNI/NIF del client
     * @param invoiceLines línies de factura de la venda
     * @return tiquet amb id {@code 0}
     */
    public Ticket createTicket(String customerDni, List<InvoiceLine> invoiceLines) {
        return createTicket(0, customerDni, invoiceLines);
    }

    private static List<InvoiceLine> validateLines(List<InvoiceLine> invoiceLines) {
        Objects.requireNonNull(invoiceLines, "Les línies de factura no poden ser nul·les");

        if (invoiceLines.isEmpty()) {
            throw new IllegalArgumentException("Una venda ha de tenir com a mínim una línia");
        }

        return invoiceLines;
    }
}
