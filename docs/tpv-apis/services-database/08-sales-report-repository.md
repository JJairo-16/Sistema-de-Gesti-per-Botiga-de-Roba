# `SalesReportRepository`

## Classe

```java
services.database.report.SalesReportRepository
```

## Responsabilitat

Genera informes agregats de vendes i beneficis.

## API pública

```java
public ClientSalesSummary summarizeClient(String dniClient) throws SQLException
public ArticleSalesSummary summarizeArticle(int articleId) throws SQLException
public List<ArticleProfitSummary> summarizeProfits(boolean ascending) throws SQLException
```

## Venda total per client

```java
try (ShopDatabase database = new ShopDatabase()) {
    ClientSalesSummary summary = database.reports().summarizeClient("12345678A");

    System.out.println(summary.name());
    System.out.println(summary.ticketCount());
    System.out.println(summary.totalSpent());
}
```

## Venda total per article

```java
try (ShopDatabase database = new ShopDatabase()) {
    ArticleSalesSummary summary = database.reports().summarizeArticle(1);

    System.out.println(summary.articleName());
    System.out.println(summary.quantitySold());
}
```

## Beneficis

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<ArticleProfitSummary> profits = database.reports().summarizeProfits(false);
}
```

`false` ordena de manera descendent per benefici.

`true` ordena de manera ascendent per benefici.
