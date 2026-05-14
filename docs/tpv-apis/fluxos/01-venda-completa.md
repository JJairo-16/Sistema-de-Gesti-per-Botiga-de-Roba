[Tornar a l'índex](../README.md)

# Flux de venda completa

Aquest document descriu el flux recomanat per registrar una venda des del menú o la capa d'aplicació.

La venda és una operació crítica. No s'ha de guardar manualment inserint tiquets, línies i reduccions d'estoc per separat. S'ha d'utilitzar `SaleService`.

## Resum

```text
1. Obrir ShopDatabase.
2. Seleccionar o validar el client.
3. Obtenir els articles seleccionats.
4. Validar quantitats.
5. Crear InvoiceLine per cada article.
6. Calcular totals amb TPVService.
7. Crear Ticket.
8. Registrar la venda amb SaleService.
9. Mostrar el tiquet per pantalla.
```

## Validar el client

Si la venda té client identificat, comprova que existeix.

```java
Client client = database.clients().findByDni(dniClient);

if (client == null) {
    System.out.println("Client no trobat.");
    return;
}
```

Si la venda és anònima, utilitza el client genèric:

```java
String dniClient = "000";
```

## Preparar les línies

Per cada article seleccionat, consulta l'article real a la base de dades i crea una línia amb les dades del model.

```java
Article article = database.articles().findById(articleId);

if (article == null) {
    System.out.println("Article no trobat.");
    return;
}

if (quantity <= 0) {
    System.out.println("La quantitat ha de ser superior a zero.");
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
```

La interfície pot avisar si l'estoc visible sembla insuficient, però la comprovació definitiva es fa en registrar la venda.

## Exemple complet simplificat

```java
try (ShopDatabase database = new ShopDatabase()) {
    String dniClient = "000";

    Article article = database.articles().findById(1);

    if (article == null) {
        System.out.println("Article no trobat.");
        return;
    }

    int quantity = 2;

    if (quantity <= 0) {
        System.out.println("La quantitat ha de ser superior a zero.");
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
        dniClient,
        totalBase,
        totalIva,
        totalFinal
    );

    SaleService saleService = new SaleService(database);
    long ticketId = saleService.registerSale(ticket, lines);

    System.out.println("Venda registrada amb el tiquet " + ticketId + ".");
}
```

## Què fa `SaleService`

`SaleService` s'encarrega de:

- validar que el tiquet no sigui nul;
- validar que la llista de línies no sigui nul·la;
- validar que la venda tingui com a mínim una línia;
- inserir el tiquet;
- associar les línies al tiquet generat;
- reduir l'estoc dels articles;
- inserir les línies de factura;
- fer-ho tot dins d'una transacció;
- utilitzar bloqueig controlat de taules quan cal evitar operacions simultànies estranyes.

Si alguna part falla, la transacció es desfà i la base de dades no queda a mitges.

## Errors que ha de tractar la capa d'aplicació

La capa d'aplicació hauria de capturar els errors i mostrar missatges comprensibles.

```java
try {
    long ticketId = saleService.registerSale(ticket, lines);
    System.out.println("Venda registrada amb el tiquet " + ticketId + ".");
} catch (SQLException ex) {
    System.out.println("No s'ha pogut registrar la venda: " + ex.getMessage());
}
```

Un error pot indicar estoc insuficient, timeout esperant un bloqueig o qualsevol altre problema de base de dades.

## Classes implicades

| Classe | Paquet | Funció |
| ------ | ------ | ------ |
| `Article` | `models` | Dades de l'article venut. |
| `InvoiceLine` | `models` | Línia de venda. |
| `Ticket` | `models` | Capçalera del tiquet. |
| `TPVService` | `services` | Càlcul de totals. |
| `SaleService` | `services.sales` | Persistència completa de la venda. |
| `ShopDatabase` | `services.database` | Entrada a la base de dades. |
