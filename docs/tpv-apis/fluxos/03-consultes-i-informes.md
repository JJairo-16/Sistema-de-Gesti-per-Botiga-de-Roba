# Flux de consultes i informes

Aquest document descriu com fer consultes agregades de vendes.

## Consulta de vendes per client

```java
try (ShopDatabase database = new ShopDatabase()) {
    ClientSalesSummary summary = database.reports().summarizeClient("12345678A");

    System.out.println("Client: " + summary.name());
    System.out.println("Tiquets: " + summary.ticketCount());
    System.out.println("Despesa total: " + summary.totalSpent());
}
```

## Consulta de vendes per article

```java
try (ShopDatabase database = new ShopDatabase()) {
    ArticleSalesSummary summary = database.reports().summarizeArticle(1);

    System.out.println("Article: " + summary.articleName());
    System.out.println("Quantitat venuda: " + summary.quantitySold());
}
```

## Informe de beneficis

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<ArticleProfitSummary> summaries = database.reports().summarizeProfits(false);

    for (ArticleProfitSummary summary : summaries) {
        System.out.println(summary.articleName());
        System.out.println(summary.profit());
    }
}
```

## Ordenació de beneficis

```java
summarizeProfits(true)
```

Ordenació ascendent.

```java
summarizeProfits(false)
```

Ordenació descendent.
