# Paquet `services`

## Responsabilitat

El paquet `services` conté serveis d'aplicació que coordinen lògica de negoci o processos del projecte.

A diferència dels models, aquests serveis sí que poden coordinar operacions entre models i repositoris, però no haurien de contenir SQL directe.

## Serveis principals

| Classe              | Paquet           | Funció                                                         |
| ------------------- | ---------------- | -------------------------------------------------------------- |
| `TPVService`        | `services`       | Calcula totals de vendes i ajuda a construir línies de tiquet. |
| `SaleService`       | `services.sales` | Registra una venda completa.                                   |
| `DatabaseRestocker` | `services.stock` | Gestiona la importació d'articles des d'un JSON.               |

## Regla general

- Si una classe només representa dades, ha d'estar a `models`.
- Si una classe coordina una operació de l'aplicació, pot estar a `services` o en un subpaquet temàtic.
- Si una classe executa consultes SQL o gestiona persistència, ha d'estar a `services.database.repository` o `services.database.report`.
