# `ShopDatabase`

## Classe

```java
services.database.ShopDatabase
```

## Responsabilitat

És el punt d'entrada principal a la base de dades.

Centralitza la connexió i exposa els repositoris necessaris per treballar amb les dades de la botiga.

## API pública

```java
public ShopDatabase() throws IOException
public ShopDatabase(Path configPath) throws IOException

public ArticleRepository articles()
public ClientRepository clients()
public TicketRepository tickets()
public InvoiceLineRepository invoiceLines()
public SalesReportRepository reports()

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

## Recomanació

Utilitza sempre `try-with-resources` per assegurar que la connexió es tanca correctament.
