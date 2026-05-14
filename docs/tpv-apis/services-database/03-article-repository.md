[Tornar a l'índex](../README.md)

# `ArticleRepository`

## Classe

```java
services.database.repository.ArticleRepository
```

## Responsabilitat

Gestiona la persistència dels articles.

Des de la capa d'aplicació es pot utilitzar per consultar el catàleg, buscar articles concrets o fer manteniments simples.

Les vendes completes no haurien de reduir l'estoc directament amb aquest repositori, sinó a través de `SaleService`.

## Com s'obté

```java
try (ShopDatabase database = new ShopDatabase()) {
    ArticleRepository articles = database.articles();
}
```

## API pública

```java
public long insert(Article article) throws SQLException
public boolean update(Article article) throws SQLException
public boolean save(Article article) throws SQLException
public boolean delete(int id) throws SQLException

public Article findById(int id) throws SQLException
public Article findByIdForUpdate(int id) throws SQLException
public List<Article> findAll() throws SQLException
public List<Article> findByType(ArticleType type) throws SQLException
public boolean exists(int id) throws SQLException

public boolean updateStock(int id, int stock) throws SQLException
public boolean decreaseStock(int id, int quantity) throws SQLException
public List<Article> findBelowStock(int threshold) throws SQLException
```

## Consultar articles

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> articles = database.articles().findAll();
}
```

## Buscar un article

```java
try (ShopDatabase database = new ShopDatabase()) {
    Article article = database.articles().findById(1);

    if (article == null) {
        System.out.println("Article no trobat.");
    }
}
```

## Inserir o actualitzar

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().save(article);
}
```

`save` decideix automàticament si cal inserir o actualitzar.

## Actualitzar estoc manualment

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().updateStock(1, 20);
}
```

Aquesta operació és útil per manteniments administratius simples.

## Reduir estoc

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().decreaseStock(1, 2);
}
```

`decreaseStock` només redueix l'estoc si hi ha unitats suficients.

Tot i això, en una venda completa s'ha d'utilitzar `SaleService`, perquè també crea el tiquet i les línies dins d'una transacció.

## Consulta amb bloqueig de fila

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.transaction(tx -> {
        Article article = tx.articles().findByIdForUpdate(1);
        return article;
    });
}
```

Aquest mètode només té sentit dins d'una transacció. Per a les vendes i importacions habituals, és preferible utilitzar els serveis preparats.
