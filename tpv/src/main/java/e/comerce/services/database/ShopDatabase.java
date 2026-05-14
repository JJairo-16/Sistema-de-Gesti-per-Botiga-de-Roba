package e.comerce.services.database;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import e.comerce.libs.db.Database;
import e.comerce.libs.db.Pool;
import e.comerce.libs.db.table.TableLock;
import e.comerce.services.database.report.SalesReportRepository;
import e.comerce.services.database.repository.ArticleFamilyRepository;
import e.comerce.services.database.repository.ArticleRepository;
import e.comerce.services.database.repository.ClientRepository;
import e.comerce.services.database.repository.InvoiceLineRepository;
import e.comerce.services.database.repository.TicketRepository;
import e.comerce.utils.io.EnvReader;

/**
 * Punt únic de connexió i accés a dades de la botiga.
 *
 * <p>
 * Aquesta classe crea el pool de connexions i exposa només repositoris de
 * negoci. La resta de l'aplicació no hauria d'obrir connexions SQL pròpies.
 * </p>
 */
public class ShopDatabase implements AutoCloseable {
    private static final Path DEFAULT_CONFIG_PATH = Path.of("tpv/db.env");

    private static final String NAME_KEY = "NAME";
    private static final String USER_KEY = "USER";
    private static final String PASSWORD_KEY = "PASSWORD";

    public static final int DEFAULT_LOCK_TIMEOUT_SECONDS = 5;

    private final Pool pool;
    private final Database database;

    private final ArticleFamilyRepository families;
    private final ArticleRepository articles;
    private final ClientRepository clients;
    private final TicketRepository tickets;
    private final InvoiceLineRepository invoiceLines;
    private final SalesReportRepository reports;

    /**
     * Crea la connexió fent servir el fitxer per defecte {@code tpv/db.env}.
     *
     * @throws IOException si no es pot llegir la configuració
     */
    public ShopDatabase() throws IOException {
        this(DEFAULT_CONFIG_PATH);
    }

    /**
     * Crea la connexió fent servir un fitxer de configuració concret.
     *
     * @param configPath ruta del fitxer de configuració
     * @throws IOException si no es pot llegir la configuració
     */
    public ShopDatabase(Path configPath) throws IOException {
        Map<String, String> data = EnvReader.read(configPath);
        validateData(data);

        System.setProperty("org.slf4j.simpleLogger.log.com.zaxxer.hikari", "off");

        this.pool = Pool.builder()
                .localConnection(data.get(NAME_KEY))
                .credentials(data.get(USER_KEY), data.get(PASSWORD_KEY))
                .build();

        this.database = new Database(pool);

        this.families = new ArticleFamilyRepository(database);
        this.articles = new ArticleRepository(database, families);
        this.clients = new ClientRepository(database);
        this.tickets = new TicketRepository(database);
        this.invoiceLines = new InvoiceLineRepository(database);
        this.reports = new SalesReportRepository(database);
    }

    /**
     * Executa un conjunt d'operacions de botiga dins d'una única transacció.
     *
     * <p>
     * Si el treball acaba correctament, es confirma la transacció. Si qualsevol
     * operació falla, es desfà tot el treball fet dins del bloc.
     * </p>
     *
     * @param work treball a executar dins de la transacció
     * @param <T>  tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si la base de dades retorna un error
     */
    public <T> T transaction(ShopWork<T> work) throws SQLException {
        Objects.requireNonNull(work, "El treball transaccional no pot ser nul");

        return database.transaction(tx -> {
            ShopTransaction transaction = new ShopTransaction(tx);
            return work.execute(transaction);
        });
    }

    /**
     * Executa una transacció de botiga amb un nivell d'aïllament concret.
     *
     * @param isolationLevel nivell d'aïllament de {@link java.sql.Connection}
     * @param work           treball a executar dins de la transacció
     * @param <T>            tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si la base de dades retorna un error
     */
    public <T> T transaction(int isolationLevel, ShopWork<T> work) throws SQLException {
        Objects.requireNonNull(work, "El treball transaccional no pot ser nul");

        return database.transaction(isolationLevel, tx -> work.execute(new ShopTransaction(tx)));
    }

    /**
     * Executa una transacció protegida amb bloquejos de taula.
     *
     * <p>
     * Si alguna taula ja està bloquejada, la connexió espera el temps indicat.
     * Si passat aquest temps no pot obtenir tots els bloquejos, es llança un
     * timeout i no s'executa el treball.
     * </p>
     *
     * @param locks          taules que cal bloquejar
     * @param timeoutSeconds segons màxims d'espera
     * @param work           treball a executar amb les taules bloquejades
     * @param <T>            tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si la base de dades retorna un error
     */
    public <T> T transactionWithTableLocks(
            List<TableLock> locks,
            int timeoutSeconds,
            ShopWork<T> work) throws SQLException {
        Objects.requireNonNull(work, "El treball transaccional no pot ser nul");

        return database.transactionWithTableLocks(
                locks,
                timeoutSeconds,
                tx -> work.execute(new ShopTransaction(tx)));
    }

    /**
     * Executa una transacció protegida amb bloquejos de taula i el timeout per
     * defecte.
     *
     * @param locks taules que cal bloquejar
     * @param work  treball a executar amb les taules bloquejades
     * @param <T>   tipus de valor retornat
     * @return valor retornat pel treball
     * @throws SQLException si la base de dades retorna un error
     */
    public <T> T transactionWithTableLocks(List<TableLock> locks, ShopWork<T> work) throws SQLException {
        return transactionWithTableLocks(locks, DEFAULT_LOCK_TIMEOUT_SECONDS, work);
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

    /**
     * Tanca el pool de connexions.
     */
    @Override
    public void close() {
        pool.close();
    }

    private static void validateData(Map<String, String> data) {
        Objects.requireNonNull(data, "Les dades de configuració no poden ser nul·les");
        validateString(data, NAME_KEY);
        validateString(data, USER_KEY);
        validateString(data, PASSWORD_KEY);
    }

    private static void validateString(Map<String, String> data, String key) {
        if (!data.containsKey(key)) {
            throw new IllegalArgumentException("No s'ha trobat la clau de configuració: " + key);
        }

        String value = data.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La clau " + key + " es troba en blanc");
        }
    }
}
