# Flux d'importació d'articles

Aquest document descriu com importar articles des del JSON de central.

## Flux recomanat

```text
1. Llegir el JSON.
2. Mostrar previsualització.
3. Demanar confirmació.
4. Inserir o actualitzar articles.
5. Mostrar resum final.
```

## Ús directe

```java
try (ShopDatabase database = new ShopDatabase()) {
    DatabaseRestocker.restock(database);
}
```

## Ús manual

```java
try (ShopDatabase database = new ShopDatabase()) {
    RestockPreview preview = DatabaseRestocker.preview();

    DatabaseRestocker.printPreview(preview);

    RestockResult result = DatabaseRestocker.commit(database, preview);

    DatabaseRestocker.printResult(result);
}
```

## Comportament de la importació

- Si l'article no existeix, s'insereix.
- Si l'article ja existeix, s'actualitza amb les dades del JSON.
- Al final es mostra el nombre d'articles afegits i actualitzats.

## Classes implicades

| Classe                      | Paquet              | Funció                                    |
| --------------------------- | ------------------- | ----------------------------------------- |
| `DatabaseRestocker`         | `services.stock`               | Llegeix el JSON i coordina la importació. |
| `ShopDatabase`              | `services.database`            | Dona accés al repositori d'articles.      |
| `ArticleRepository`         | `services.database.repository` | Insereix o actualitza articles.           |
| `Article`, `Shirt`, `Pants` | `models`            | Representen els articles importats.       |
