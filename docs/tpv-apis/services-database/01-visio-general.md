# Paquet `services.database`

## Responsabilitat

El paquet `services.database` conté el punt d'entrada centralitzat a la base de dades. Els seus subpaquets separen repositoris i informes.

Això inclou:

- connexió centralitzada
- repositoris de persistència
- informes agregats de lectura

## Classes principals

| Classe                  | Paquet                         | Funció                                          |
| ----------------------- | ------------------------------ | ----------------------------------------------- |
| `ShopDatabase`          | `services.database`            | Punt d'entrada centralitzat a la base de dades. |
| `ArticleRepository`     | `services.database.repository` | Accés a dades d'articles.                       |
| `ClientRepository`      | `services.database.repository` | Accés a dades de clients.                       |
| `TicketRepository`      | `services.database.repository` | Accés a dades de tiquets.                       |
| `InvoiceLineRepository` | `services.database.repository` | Accés a dades de línies de factura.             |
| `SalesReportRepository` | `services.database.report`     | Consultes agregades i informes.                 |

## Regla important

Qualsevol operació SQL ha d'estar dins d'aquest paquet o dels seus subpaquets.

Els menús i serveis d'alt nivell no haurien d'utilitzar `DriverManager`, `Connection`, `PreparedStatement` o SQL directament.
