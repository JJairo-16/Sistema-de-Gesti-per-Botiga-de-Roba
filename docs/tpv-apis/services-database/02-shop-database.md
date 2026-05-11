[Tornar a l'índex](../README.md)

# `ShopDatabase`

## Classe

```java
services.database.ShopDatabase
```

## Responsabilitat

És el punt d'entrada principal a la base de dades.

Centralitza la connexió i exposa els repositoris necessaris per treballar amb les dades de la botiga.

També permet executar operacions dins d'una transacció i, quan cal, amb bloquejos de taula i timeout controlat.

## API pública

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

## Exemple bàsic

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> articles = database.articles().findAll();
}
```

## Exemple amb diversos repositoris

```java
try (ShopDatabase database = new ShopDatabase()) {
    Article article = database.articles().findById(1);
    Client client = database.clients().findByDni("000");
}
```

## Transaccions

Quan una operació ha de modificar diverses taules com una sola unitat, es pot executar dins d'una transacció.

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.transaction(tx -> {
        long ticketId = tx.tickets().insert(ticket);
        tx.invoiceLines().insert(line.withTicketId((int) ticketId));
        return ticketId;
    });
}
```

Dins de la transacció es rep un `ShopTransaction`, no un altre `ShopDatabase`. Això evita crear gestors complets de base de dades per cada transacció.

## Bloquejos de taula

Per operacions crítiques es poden demanar bloquejos de taula.

```java
List<TableLock> locks = List.of(
    TableLock.write("articles"),
    TableLock.write("tiquets"),
    TableLock.write("linies_factura")
);

try (ShopDatabase database = new ShopDatabase()) {
    database.transactionWithTableLocks(locks, 10, tx -> {
        // Operació crítica.
        return null;
    });
}
```

Si alguna taula està bloquejada, l'operació espera fins al timeout indicat. Si no pot continuar dins d'aquest temps, es llança una excepció controlada.

## Quan no s'ha d'utilitzar directament

La capa d'aplicació no hauria d'utilitzar transaccions manuals per a operacions que ja tenen un servei propi.

```text
Venda completa
  -> SaleService

Importació d'articles
  -> DatabaseRestocker
```

Això manté el menú senzill i concentra la lògica crítica en serveis revisables.

## Recomanació

Utilitza sempre `try-with-resources` per assegurar que el pool es tanca correctament.

```java
try (ShopDatabase database = new ShopDatabase()) {
    // Operacions de la botiga.
}
```
