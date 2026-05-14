[Tornar a l'índex](../README.md)

# Resum d'APIs públiques

## `models`

### `Article`

```java
public double getFinalPrice()
public double getIvaAmount()
public abstract double getCostPrice()
public double getProfitPerUnit()
```

### `Shirt`

```java
public double getCostPrice()
```

### `Pants`

```java
public double getCostPrice()
```

### `Client`

```java
public record Client(String dni, String name, String email, String phone)
```

### `InvoiceLine`

```java
public record InvoiceLine(
    int ticketId,
    int articleId,
    int quantity,
    double basePrice,
    int iva,
    double finalPrice
)
```

## `services`

### `TPVService`

```java
public double calculateTotalBase(List<InvoiceLine> lines)
public double calculateTotalIva(List<InvoiceLine> lines)
public double calculateTotalFinal(List<InvoiceLine> lines)
```

### `services.stock.DatabaseRestocker`

```java
public static RestockPreview preview() throws IOException
public static RestockPreview preview(Path jsonPath) throws IOException
public static RestockResult commit(ShopDatabase database, RestockPreview preview) throws SQLException
public static RestockResult commit(ShopDatabase database, RestockPreview preview, int timeoutSeconds) throws SQLException
public static RestockResult restock(ShopDatabase database) throws IOException, SQLException
public static RestockResult restock(ShopDatabase database, int timeoutSeconds) throws IOException, SQLException
public static void printPreview(RestockPreview preview)
public static void printResult(RestockResult result)
```

### `services.sales.SaleService`

```java
public SaleService(ShopDatabase database)
public long registerSale(Ticket ticket, List<InvoiceLine> lines) throws SQLException
```

## `services.database`

### `ShopDatabase`

```java
public ShopDatabase() throws IOException
public ShopDatabase(Path configPath) throws IOException
public ArticleRepository articles()
public ClientRepository clients()
public TicketRepository tickets()
public InvoiceLineRepository invoiceLines()
public SalesReportRepository reports()
public <T> T transaction(ShopWork<T> work) throws SQLException
public void transaction(ShopRunnable work) throws SQLException
public <T> T transactionWithTableLocks(List<TableLock> locks, ShopWork<T> work) throws SQLException
public <T> T transactionWithTableLocks(List<TableLock> locks, int timeoutSeconds, ShopWork<T> work) throws SQLException
public void close()
```

### `ShopTransaction`

```java
public ArticleRepository articles()
public ClientRepository clients()
public TicketRepository tickets()
public InvoiceLineRepository invoiceLines()
public SalesReportRepository reports()
```

### `TableLock`

```java
public static TableLock read(String table)
public static TableLock write(String table)
```

### `services.database.repository.ArticleRepository`

```java
public long insert(Article article) throws SQLException
public boolean update(Article article) throws SQLException
public boolean save(Article article) throws SQLException
public boolean delete(int id) throws SQLException
public Article findById(int id) throws SQLException
public Article findByIdForUpdate(int id) throws SQLException
public List<Article> findAll() throws SQLException
public List<Article> findByType(ArticleType type) throws SQLException
public boolean exists(int id) throws SQLException
public boolean updateStock(int id, int stock) throws SQLException
public boolean decreaseStock(int id, int quantity) throws SQLException
public List<Article> findBelowStock(int threshold) throws SQLException
```

### `services.database.repository.ClientRepository`

```java
public boolean insert(Client client) throws SQLException
public boolean save(Client client) throws SQLException
public boolean update(Client client) throws SQLException
public boolean delete(String dni) throws SQLException
public Client findByDni(String dni) throws SQLException
public List<Client> findAll() throws SQLException
public boolean exists(String dni) throws SQLException
```

### `services.database.repository.TicketRepository`

```java
public long insert(Ticket ticket) throws SQLException
public boolean update(Ticket ticket) throws SQLException
public boolean delete(int id) throws SQLException
public Ticket findById(int id) throws SQLException
public List<Ticket> findByClient(String dniClient) throws SQLException
```

### `services.database.repository.InvoiceLineRepository`

```java
public boolean insert(InvoiceLine line) throws SQLException
public boolean deleteByTicket(int ticketId) throws SQLException
public List<InvoiceLine> findByTicket(int ticketId) throws SQLException
```

## `services.database.report`

### `SalesReportRepository`

```java
public ClientSalesSummary summarizeClient(String dniClient) throws SQLException
public ArticleSalesSummary summarizeArticle(int articleId) throws SQLException
public List<ArticleProfitSummary> summarizeProfits(boolean ascending) throws SQLException
```
