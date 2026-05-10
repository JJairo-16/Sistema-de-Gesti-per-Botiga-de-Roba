# `SaleService`

## Classe

```java
services.sales.SaleService
```

## Responsabilitat

Registra una venda completa de manera coordinada.

Aquest servei és responsable de:

- inserir el tiquet
- inserir les línies de factura
- reduir l'stock dels articles venuts

## API pública

```java
public SaleService(ShopDatabase database)
public long registerSale(Ticket ticket, List<InvoiceLine> lines) throws SQLException
```

## Exemple d'ús

```java
try (ShopDatabase database = new ShopDatabase()) {
    SaleService saleService = new SaleService(database);

    long ticketId = saleService.registerSale(ticket, lines);

    System.out.println("Tiquet creat: " + ticketId);
}
```

## Quan s'ha d'utilitzar

S'ha d'utilitzar quan el client confirma una compra al TPV.

## Avantatge

Evita que el programa desi un tiquet sense línies o que resti stock sense haver registrat correctament la venda.
