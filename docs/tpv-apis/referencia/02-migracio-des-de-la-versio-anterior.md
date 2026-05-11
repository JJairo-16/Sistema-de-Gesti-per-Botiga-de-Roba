[Tornar a l'índex](../README.md)

# Migració des de la versió anterior

Aquest document resumeix els canvis importants respecte de la versió anterior del projecte.

## Classes antigues eliminades

Aquestes classes ja no s'han d'utilitzar:

```text
models.LiniaFactura
models.LiniaFacturaManager
models.TPVService
models.ClientManager
services.DatabaseShop
```

## Substitucions

| Abans | Ara |
| ----- | --- |
| `models.LiniaFactura` | `models.InvoiceLine` |
| `models.TPVService` | `services.TPVService` |
| `models.ClientManager` | `services.database.repository.ClientRepository` |
| `models.LiniaFacturaManager` | `services.database.repository.InvoiceLineRepository` |
| `services.DatabaseShop` | `services.database.ShopDatabase` |

## Exemple de canvi: línia de factura

Abans:

```java
LiniaFactura line = new LiniaFactura(...);
```

Ara:

```java
InvoiceLine line = new InvoiceLine(...);
```

## Exemple de canvi: TPVService

Abans:

```java
import models.TPVService;
```

Ara:

```java
import services.TPVService;
```

## Exemple de canvi: accés a clients

Abans:

```java
ClientManager manager = new ClientManager();
Client client = manager.buscarPerDni("12345678A");
```

Ara:

```java
try (ShopDatabase database = new ShopDatabase()) {
    Client client = database.clients().findByDni("12345678A");
}
```

## Exemple de canvi: accés a línies de factura

Abans:

```java
LiniaFacturaManager manager = new LiniaFacturaManager();
List<LiniaFactura> lines = manager.llistarPerTiquet(1);
```

Ara:

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<InvoiceLine> lines = database.invoiceLines().findByTicket(1);
}
```

## Exemple de canvi: venda completa

Abans, una venda podia quedar repartida entre diferents gestors o consultes manuals.

Ara, el flux recomanat és utilitzar `SaleService`.

```java
try (ShopDatabase database = new ShopDatabase()) {
    SaleService saleService = new SaleService(database);
    long ticketId = saleService.registerSale(ticket, lines);
}
```

Això garanteix que el tiquet, les línies i la reducció d'estoc es gestionin de manera coordinada.

## Exemple de canvi: importació d'articles

La importació del JSON s'ha de fer amb `DatabaseRestocker`.

```java
try (ShopDatabase database = new ShopDatabase()) {
    RestockPreview preview = DatabaseRestocker.preview();
    RestockResult result = DatabaseRestocker.commit(database, preview);
}
```

Aquest servei utilitza transacció i bloqueig controlat de la taula d'articles.

## Recomanació final

En codi nou, utilitza sempre `ShopDatabase` com a punt d'entrada a la base de dades i evita crear classes gestores dins del paquet `models`.

Per operacions compostes, utilitza serveis d'aplicació:

```text
Venda completa
  -> SaleService

Importació d'articles
  -> DatabaseRestocker
```
