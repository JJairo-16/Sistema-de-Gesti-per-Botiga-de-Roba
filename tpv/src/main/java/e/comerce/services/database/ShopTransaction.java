package e.comerce.services.database;

import java.util.Objects;

import e.comerce.libs.db.DbExecutor;
import e.comerce.services.database.report.SalesReportRepository;
import e.comerce.services.database.repository.ArticleFamilyRepository;
import e.comerce.services.database.repository.ArticleRepository;
import e.comerce.services.database.repository.ClientRepository;
import e.comerce.services.database.repository.InvoiceLineRepository;
import e.comerce.services.database.repository.TicketRepository;

/**
 * Context de treball d'una transacció de botiga.
 *
 * <p>
 * Aquesta classe agrupa els repositoris que comparteixen una mateixa connexió
 * transaccional. No crea cap pool, no obre connexions pel seu compte i no s'ha
 * de tancar manualment.
 * </p>
 */
public final class ShopTransaction {
    private final ArticleFamilyRepository families;
    private final ArticleRepository articles;
    private final ClientRepository clients;
    private final TicketRepository tickets;
    private final InvoiceLineRepository invoiceLines;
    private final SalesReportRepository reports;

    /**
     * Crea un context transaccional sobre un executor SQL compartit.
     *
     * @param db executor SQL associat a la connexió transaccional
     */
    public ShopTransaction(DbExecutor db) {
        Objects.requireNonNull(db, "L'executor de base de dades no pot ser nul");

        this.families = new ArticleFamilyRepository(db);
        this.articles = new ArticleRepository(db, families);
        this.clients = new ClientRepository(db);
        this.tickets = new TicketRepository(db);
        this.invoiceLines = new InvoiceLineRepository(db);
        this.reports = new SalesReportRepository(db);
    }

    /**
     * Retorna el repositori de famílies.
     *
     * @return repositori de famílies
     */
    public ArticleFamilyRepository families() {
        return families;
    }

    /**
     * Retorna el repositori d'articles.
     *
     * @return repositori d'articles
     */
    public ArticleRepository articles() {
        return articles;
    }

    /**
     * Retorna el repositori de clients.
     *
     * @return repositori de clients
     */
    public ClientRepository clients() {
        return clients;
    }

    /**
     * Retorna el repositori de tiquets.
     *
     * @return repositori de tiquets
     */
    public TicketRepository tickets() {
        return tickets;
    }

    /**
     * Retorna el repositori de línies de factura.
     *
     * @return repositori de línies de factura
     */
    public InvoiceLineRepository invoiceLines() {
        return invoiceLines;
    }

    /**
     * Retorna el repositori d'informes de vendes.
     *
     * @return repositori d'informes
     */
    public SalesReportRepository reports() {
        return reports;
    }
}
