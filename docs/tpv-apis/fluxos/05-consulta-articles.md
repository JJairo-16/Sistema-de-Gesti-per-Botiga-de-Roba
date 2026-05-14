[Tornar a l'índex](../README.md)

# Flux de consulta i selecció d'articles

Aquest document descriu com obtenir productes i preparar-los per a una venda des de la capa d'aplicació.

## Llistar tots els articles

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> articles = database.articles().findAll();

    for (Article article : articles) {
        System.out.println(article.getId() + " - " + article.getName());
    }
}
```

Aquest flux és adequat per mostrar el catàleg complet al menú o a la interfície.

## Buscar un article per identificador

```java
try (ShopDatabase database = new ShopDatabase()) {
    Article article = database.articles().findById(1);

    if (article == null) {
        System.out.println("Article no trobat.");
        return;
    }
}
```

Aquesta consulta s'ha de fer abans d'afegir una línia a la venda.

## Filtrar per tipus

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> shirts = database.articles().findByType(ArticleType.SHIRT);
}
```

Això permet construir pantalles o menús separats per família d'article.

## Consultar articles amb poc estoc

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> articles = database.articles().findBelowStock(5);
}
```

Aquesta consulta és útil per avisar que cal importar o revisar articles.

## Validació abans d'afegir a la venda

Abans de crear una `InvoiceLine`, valida:

- que l'article existeix;
- que la quantitat és superior a zero;
- que el preu base i l'IVA provenen de l'article, no d'entrada manual;
- que la línia utilitza l'identificador real de l'article;
- que la venda final es registrarà amb `SaleService`.

Es pot mostrar una comprovació d'estoc a la interfície, però la protecció definitiva la fa `SaleService` quan redueix l'estoc a la base de dades.

## Preparar una línia de factura

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

## Per què no s'ha de reduir l'estoc manualment

La capa d'aplicació no hauria de cridar `decreaseStock` directament quan està fent una venda completa.

El motiu és que una venda necessita:

```text
1. crear tiquet;
2. crear línies;
3. reduir estoc;
4. confirmar-ho tot o desfer-ho tot si hi ha error.
```

Aquesta coordinació ja la fa `SaleService` amb transacció i bloqueig controlat.
