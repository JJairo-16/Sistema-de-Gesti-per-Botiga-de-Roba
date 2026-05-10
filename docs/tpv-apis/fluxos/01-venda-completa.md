# Flux de venda completa

Aquest document descriu el flux recomanat per registrar una venda.

## Resum

```text
1. Obrir ShopDatabase.
2. Demanar client.
3. Afegir articles i quantitats.
4. Crear InvoiceLine per cada article.
5. Calcular totals amb TPVService.
6. Crear Ticket.
7. Registrar venda amb SaleService.
8. Mostrar el tiquet per pantalla.
```

## Exemple simplificat

```java
try (ShopDatabase database = new ShopDatabase()) {
    Article article = database.articles().findById(1);

    if (article == null) {
        System.out.println("Article no trobat.");
        return;
    }

    int quantity = 2;

    if (article.getStock() < quantity) {
        System.out.println("No hi ha stock suficient.");
        return;
    }

    double basePrice = article.getBasePrice() * quantity;
    int iva = article.getIva();
    double finalPrice = basePrice * (1 + iva / 100.0);

    InvoiceLine line = new InvoiceLine(
        0,
        article.getId(),
        quantity,
        basePrice,
        iva,
        finalPrice
    );

    List<InvoiceLine> lines = List.of(line);

    TPVService tpvService = new TPVService();

    double totalBase = tpvService.calculateTotalBase(lines);
    double totalIva = tpvService.calculateTotalIva(lines);
    double totalFinal = tpvService.calculateTotalFinal(lines);

    Ticket ticket = new Ticket(
        0,
        LocalDate.now(),
        "000",
        totalBase,
        totalIva,
        totalFinal
    );

    SaleService saleService = new SaleService(database);
    long ticketId = saleService.registerSale(ticket, lines);

    System.out.println("Venda registrada amb el tiquet " + ticketId + ".");
}
```

## Classes implicades

| Classe         | Paquet              | Funció                             |
| -------------- | ------------------- | ---------------------------------- |
| `Article`      | `models`            | Dades de l'article venut.          |
| `InvoiceLine`  | `models`            | Línia de venda.                    |
| `Ticket`       | `models`            | Capçalera del tiquet.              |
| `TPVService`   | `services`          | Càlcul de totals.                  |
| `SaleService`  | `services.sales`    | Persistència completa de la venda. |
| `ShopDatabase` | `services.database` | Entrada a la base de dades.        |
