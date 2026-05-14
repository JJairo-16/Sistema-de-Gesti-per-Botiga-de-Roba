# Índex de documentació del TPV

Aquesta documentació descriu com utilitzar la nova arquitectura del projecte TPV després de separar els models, els serveis d'aplicació i la capa de base de dades.

L'objectiu principal de la nova versió és mantenir el codi net, separar responsabilitats i evitar que els models tinguin connexions directes amb la base de dades.

## Punt d'entrada recomanat

En codi nou, el punt d'entrada recomanat per treballar amb la base de dades és `ShopDatabase`:

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().findAll();
    database.clients().findAll();
}
```

Els models no han d'obrir connexions ni executar SQL. Els serveis poden coordinar operacions, però l'accés real a la base de dades ha d'anar a través de `ShopDatabase` i dels repositoris de `services.database.repository`.

## Fluxos d'ús des del menú o la capa d'aplicació

Aquests documents expliquen com fer operacions habituals sense entrar en el backend profund ni escriure SQL directament.

| Document | Contingut |
| -------- | --------- |
| [Guia d'operacions habituals](fluxos/00-guia-operacions.md) | Resum dels fluxos principals: clients, articles, vendes, importació i informes. |
| [Venda completa](fluxos/01-venda-completa.md) | Flux recomanat per registrar una venda amb validacions, càlculs i persistència segura. |
| [Importació d'articles](fluxos/02-importacio-articles.md) | Com llegir el JSON, previsualitzar-lo i confirmar la importació. |
| [Consultes i informes](fluxos/03-consultes-i-informes.md) | Com obtenir informes de vendes, articles i beneficis. |
| [Gestió de clients](fluxos/04-gestio-clients.md) | Alta, actualització, consulta i baixa de clients. |
| [Consulta i selecció d'articles](fluxos/05-consulta-articles.md) | Com obtenir productes, filtrar-los i validar stock abans d'una venda. |

## Models

| Document | Contingut |
| -------- | --------- |
| [Visió general dels models](models/01-visio-general.md) | Responsabilitat del paquet `models`. |
| [Articles](models/02-articles.md) | `Article`, `Shirt`, `Pants`, preus, costos i beneficis. |
| [Client](models/03-client.md) | Dades del client i client genèric. |
| [InvoiceLine](models/04-invoice-line.md) | Línies de tiquet i ús dins d'una venda. |

## Serveis d'aplicació

| Document | Contingut |
| -------- | --------- |
| [Visió general dels serveis](services/01-visio-general.md) | Responsabilitat del paquet `services`. |
| [TPVService](services/02-tpv-service.md) | Càlcul de totals de venda. |
| [DatabaseRestocker](services/03-database-restocker.md) | Importació d'articles amb bloqueig controlat. |
| [SaleService](services/04-sale-service.md) | Registre complet d'una venda. |

## Capa de base de dades

| Document | Contingut |
| -------- | --------- |
| [Visió general de la base de dades](services-database/01-visio-general.md) | Responsabilitats de `services.database`. |
| [ShopDatabase](services-database/02-shop-database.md) | Punt d'entrada, repositoris, transaccions i bloquejos. |
| [ArticleRepository](services-database/03-article-repository.md) | Persistència i consultes d'articles. |
| [ClientRepository](services-database/04-client-repository.md) | Persistència i consultes de clients. |
| [TicketRepository](services-database/05-ticket-repository.md) | Persistència i consultes de tiquets. |
| [InvoiceLineRepository](services-database/06-invoice-line-repository.md) | Persistència i consultes de línies de factura. |
| [SalesReportRepository](services-database/08-sales-report-repository.md) | Informes agregats de vendes i beneficis. |

## Referència

| Document | Contingut |
| -------- | --------- |
| [Resum d'APIs públiques](referencia/01-resum-apis-publiques.md) | Llista resumida de mètodes públics importants. |
| [Migració des de la versió anterior](referencia/02-migracio-des-de-la-versio-anterior.md) | Canvis principals respecte a la versió antiga. |

## Paquets principals

| Paquet | Responsabilitat |
| ------ | --------------- |
| `models` | Defineix les dades del domini i els càlculs propis dels objectes. |
| `services` | Conté serveis generals d'aplicació. |
| `services.sales` | Coordina el registre complet de vendes. |
| `services.stock` | Coordina la importació i reposició d'articles. |
| `services.database` | Conté la connexió centralitzada a la base de dades. |
| `services.database.repository` | Conté els repositoris de persistència. |
| `services.database.report` | Conté consultes agregades i informes. |

## Regla general d'ús

Des del menú, la interfície gràfica o qualsevol capa d'aplicació, segueix aquesta regla:

```text
Menú o controlador
  -> servei d'aplicació si l'operació és composta
  -> ShopDatabase i repositoris si és una consulta o una operació simple
  -> models per representar les dades
```

No s'ha d'executar SQL des del menú ni manipular connexions directament. Les vendes i les importacions han d'utilitzar els serveis preparats perquè ja inclouen validacions, transaccions, bloquejos i tractament controlat d'errors.
