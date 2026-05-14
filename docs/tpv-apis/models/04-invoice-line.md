[Tornar a l'índex](../README.md)

# Model `InvoiceLine`

## Classe

```java
models.InvoiceLine
```

## Responsabilitat

Representa una línia de factura o una línia de tiquet.

Cada línia indica quin article s'ha venut, quina quantitat s'ha venut i quins imports s'han aplicat.

## Camps habituals

```java
int ticketId
int articleId
int quantity
double basePrice
int iva
double finalPrice
```

## Exemple

```java
InvoiceLine line = new InvoiceLine(
    0,
    3,
    2,
    40.0,
    21,
    48.4
);
```

## Ús dins d'una venda

Normalment no es desa una línia directament des del menú principal.

El flux recomanat és:

```text
1. Crear una llista d'InvoiceLine.
2. Crear el Ticket corresponent.
3. Passar el Ticket i les línies a SaleService.
```

```java
List<InvoiceLine> lines = List.of(line);
SaleService saleService = new SaleService(database);
long ticketId = saleService.registerSale(ticket, lines);
```

## Recomanació

No utilitzis `LiniaFactura` en codi nou. La nova classe és `InvoiceLine`.
