# `ArticleRepository`

## Classe

```java
services.database.repository.ArticleRepository
```

## Responsabilitat

Gestiona la persistència dels articles.

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
public List<Article> findAll() throws SQLException
public List<Article> findByType(ArticleType type) throws SQLException
public boolean exists(int id) throws SQLException

public boolean updateStock(int id, int stock) throws SQLException
public boolean decreaseStock(int id, int quantity) throws SQLException
public List<Article> findBelowStock(int threshold) throws SQLException
```

## Inserir o actualitzar

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().save(article);
}
```

`save` decideix automàticament si cal inserir o actualitzar.

## Consultar articles

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> articles = database.articles().findAll();
}
```

## Actualitzar stock

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().updateStock(1, 20);
}
```

## Reduir stock

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.articles().decreaseStock(1, 2);
}
```
