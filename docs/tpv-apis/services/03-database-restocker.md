# Servei `DatabaseRestocker`

## Classe

```java
services.stock.DatabaseRestocker
```

## Responsabilitat

Gestiona la importació automàtica d'articles des d'un fitxer JSON.

El procés permet:

- llegir els articles del JSON
- mostrar una previsualització
- comptar camises i pantalons
- confirmar la importació
- inserir o actualitzar articles a la base de dades

## Fitxer JSON per defecte

```text
tpv/data/articles.json
```

## API pública habitual

```java
public static RestockPreview preview() throws IOException
public static RestockPreview preview(Path jsonPath) throws IOException

public static RestockResult commit(ShopDatabase database, RestockPreview preview) throws SQLException

public static RestockResult restock(ShopDatabase database) throws IOException, SQLException
public static void printPreview(RestockPreview preview)
public static void printResult(RestockResult result)
```

## Flux directe

```java
try (ShopDatabase database = new ShopDatabase()) {
    DatabaseRestocker.restock(database);
}
```

## Flux manual en dues fases

```java
try (ShopDatabase database = new ShopDatabase()) {
    RestockPreview preview = DatabaseRestocker.preview();

    DatabaseRestocker.printPreview(preview);

    RestockResult result = DatabaseRestocker.commit(database, preview);

    DatabaseRestocker.printResult(result);
}
```

## Resultat esperat

El resultat indica:

- total d'articles llegits
- nombre de camises
- nombre de pantalons
- articles afegits
- articles actualitzats
