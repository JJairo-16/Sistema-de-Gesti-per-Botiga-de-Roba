[Tornar a l'índex](../README.md)

# Paquet `services.database`

## Responsabilitat

El paquet `services.database` conté el punt d'entrada centralitzat a la base de dades. Els seus subpaquets separen repositoris i informes.

Això inclou:

- connexió centralitzada;
- repositoris de persistència;
- informes agregats de lectura;
- execució de transaccions;
- bloquejos de taula amb timeout controlat per a operacions crítiques.

## Classes principals

| Classe | Paquet | Funció |
| ------ | ------ | ------ |
| `ShopDatabase` | `services.database` | Punt d'entrada centralitzat a la base de dades. |
| `ShopTransaction` | `services.database` | Context lleuger per executar operacions dins d'una transacció. |
| `ShopWork` | `services.database` | Contracte funcional per definir treball transaccional. |
| `TableLock` | `services.database` | Defineix bloquejos de taula de lectura o escriptura. |
| `ArticleRepository` | `services.database.repository` | Accés a dades d'articles. |
| `ClientRepository` | `services.database.repository` | Accés a dades de clients. |
| `TicketRepository` | `services.database.repository` | Accés a dades de tiquets. |
| `InvoiceLineRepository` | `services.database.repository` | Accés a dades de línies de factura. |
| `SalesReportRepository` | `services.database.report` | Consultes agregades i informes. |

## Regla important

Qualsevol operació SQL ha d'estar dins d'aquest paquet o dels seus subpaquets.

Els menús i serveis d'alt nivell no haurien d'utilitzar `DriverManager`, `Connection`, `PreparedStatement` o SQL directament.

## Operacions simples

Per consultes o manteniments simples, utilitza els repositoris exposats per `ShopDatabase`.

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> articles = database.articles().findAll();
}
```

## Operacions crítiques

Per operacions que han de confirmar-se com una sola unitat, utilitza serveis d'aplicació com `SaleService` o `DatabaseRestocker`.

Aquests serveis ja utilitzen transaccions i bloquejos quan cal.

```text
Venda completa
  -> SaleService

Importació d'articles
  -> DatabaseRestocker
```
