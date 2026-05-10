package e.comerce.models;

/**
 * Representa una línia de factura d'un tiquet de venda.
 *
 * <p>El camp {@code ticketId} pot valer {@code 0} mentre el tiquet encara no
 * s'ha desat a la base de dades. Quan la venda es registra, el servei de venda
 * substitueix aquest valor per l'identificador real generat.</p>
 *
 * @param ticketId identificador del tiquet associat, o {@code 0} si encara no existeix
 * @param articleId identificador de l'article venut
 * @param quantity unitats venudes
 * @param baseAmount import total sense IVA
 * @param vatAmount import total d'IVA
 * @param finalAmount import total amb IVA inclòs
 */
public record InvoiceLine(
        int ticketId,
        int articleId,
        int quantity,
        double baseAmount,
        double vatAmount,
        double finalAmount) {

    /**
     * Valida les dades bàsiques de la línia.
     */
    public InvoiceLine {
        if (ticketId < 0) {
            throw new IllegalArgumentException("L'id del tiquet no pot ser negatiu");
        }

        if (articleId <= 0) {
            throw new IllegalArgumentException("L'id de l'article ha de ser positiu");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantitat ha de ser superior a 0");
        }

        if (baseAmount < 0) {
            throw new IllegalArgumentException("L'import base no pot ser negatiu");
        }

        if (vatAmount < 0) {
            throw new IllegalArgumentException("L'import d'IVA no pot ser negatiu");
        }

        if (finalAmount < 0) {
            throw new IllegalArgumentException("L'import final no pot ser negatiu");
        }
    }

    /**
     * Crea una còpia de la línia associada a un tiquet ja desat.
     *
     * @param newTicketId identificador real del tiquet
     * @return nova línia amb el tiquet indicat
     */
    public InvoiceLine withTicketId(int newTicketId) {
        if (newTicketId <= 0) {
            throw new IllegalArgumentException("L'id del tiquet ha de ser positiu");
        }

        return new InvoiceLine(
                newTicketId,
                articleId,
                quantity,
                baseAmount,
                vatAmount,
                finalAmount);
    }
}
