# `InvoiceLineRepository`

## Classe

```java
services.database.repository.InvoiceLineRepository
```

## Responsabilitat

Gestiona la persistència de les línies de factura.

## API pública

```java
public boolean insert(InvoiceLine line) throws SQLException
public boolean deleteByTicket(int ticketId) throws SQLException
public List<InvoiceLine> findByTicket(int ticketId) throws SQLException
```

## Consultar línies d'un tiquet

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<InvoiceLine> lines = database.invoiceLines().findByTicket(10);
}
```

## Inserció de línies

Tot i que el repositori permet inserir línies individualment, en una venda real és preferible utilitzar `SaleService`.

```java
SaleService saleService = new SaleService(database);
saleService.registerSale(ticket, lines);
```

Això evita inconsistències entre tiquet, línies i stock.
