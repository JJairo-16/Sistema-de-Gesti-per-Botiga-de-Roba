[Tornar a l'índex](../README.md)

# Paquet `services`

## Responsabilitat

El paquet `services` conté serveis d'aplicació que coordinen lògica de negoci o processos del projecte.

A diferència dels models, aquests serveis sí que poden coordinar operacions entre models i repositoris, però no haurien de contenir SQL directe.

## Serveis principals

| Classe | Paquet | Funció |
| ------ | ------ | ------ |
| `TPVService` | `services` | Calcula totals de vendes i ajuda a construir línies de tiquet. |
| `SaleService` | `services.sales` | Registra una venda completa amb transacció i control d'estoc. |
| `DatabaseRestocker` | `services.stock` | Gestiona la importació d'articles des d'un JSON amb bloqueig controlat. |

## Regla general

- Si una classe només representa dades, ha d'estar a `models`.
- Si una classe coordina una operació de l'aplicació, pot estar a `services` o en un subpaquet temàtic.
- Si una classe executa consultes SQL o gestiona persistència, ha d'estar a `services.database.repository` o `services.database.report`.

## Ús des de la capa d'aplicació

La capa d'aplicació ha d'utilitzar serveis quan l'operació sigui composta o crítica.

```text
Venda completa
  -> SaleService

Importació d'articles
  -> DatabaseRestocker

Càlcul de totals
  -> TPVService
```

Per consultes simples, es pot utilitzar `ShopDatabase` directament.
