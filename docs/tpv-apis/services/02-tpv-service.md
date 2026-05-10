# Servei `TPVService`

## Classe

```java
services.TPVService
```

## Responsabilitat

`TPVService` conté lògica pròpia del TPV que no pertany directament a cap model concret.

Serveix per calcular totals d'una venda a partir de les línies de factura.

## API pública habitual

```java
public double calculateTotalBase(List<InvoiceLine> lines)
public double calculateTotalIva(List<InvoiceLine> lines)
public double calculateTotalFinal(List<InvoiceLine> lines)
```

## Exemple d'ús

```java
TPVService tpvService = new TPVService();

double totalBase = tpvService.calculateTotalBase(lines);
double totalIva = tpvService.calculateTotalIva(lines);
double totalFinal = tpvService.calculateTotalFinal(lines);
```

## Ús dins del procés de venda

```java
TPVService tpvService = new TPVService();

List<InvoiceLine> lines = new ArrayList<>();

// Afegir línies segons els articles comprats.

double totalBase = tpvService.calculateTotalBase(lines);
double totalIva = tpvService.calculateTotalIva(lines);
double totalFinal = tpvService.calculateTotalFinal(lines);
```

Després d'això es pot crear el `Ticket` i registrar la venda amb `SaleService`.

## Recomanació

No facis aquests càlculs directament al menú. Centralitza'ls a `TPVService` per evitar duplicació de codi.
