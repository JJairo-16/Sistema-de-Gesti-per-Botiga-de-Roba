# Guia d'ús de la nova versió del TPV

Aquesta guia descriu com utilitzar la nova arquitectura del projecte TPV després de separar els models, els serveis d'aplicació i la capa de base de dades.

L'objectiu principal de la nova versió és mantenir el codi net, separar responsabilitats i evitar que els models tinguin connexions directes amb la base de dades.

## Estructura de la documentació

```text
models/
  01-visio-general.md
  02-articles.md
  03-client.md
  04-invoice-line.md

services/
  01-visio-general.md
  02-tpv-service.md
  03-database-restocker.md
  04-sale-service.md

services-database/
  01-visio-general.md
  02-shop-database.md
  03-article-repository.md
  04-client-repository.md
  05-ticket-repository.md
  06-invoice-line-repository.md
  08-sales-report-repository.md

fluxos/
  01-venda-completa.md
  02-importacio-articles.md
  03-consultes-i-informes.md

referencia/
  01-resum-apis-publiques.md
  02-migracio-des-de-la-versio-anterior.md
```

## Paquets principals

| Paquet                         | Responsabilitat                                                             |
| ------------------------------ | --------------------------------------------------------------------------- |
| `models`                       | Defineix les dades del domini i els càlculs propis dels objectes.           |
| `services`                     | Conté serveis generals d'aplicació.                                         |
| `services.sales`               | Coordina el registre complet de vendes.                                     |
| `services.stock`               | Coordina la importació i reposició d'articles.                              |
| `services.database`            | Conté la connexió centralitzada a la base de dades.                         |
| `services.database.repository` | Conté els repositoris de persistència.                                      |
| `services.database.report`     | Conté consultes agregades i informes.                                       |

## Regla general d'ús

En codi nou, el punt d'entrada recomanat per treballar amb la base de dades és:

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().findAll();
    database.clients().findAll();
}
```

Els models no han d'obrir connexions ni executar SQL. Els serveis poden coordinar operacions, però l'accés real a la base de dades ha d'anar a través de `ShopDatabase` i dels repositoris de `services.database.repository`.
