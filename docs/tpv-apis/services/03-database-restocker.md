[Tornar a l'índex](../README.md)

# Servei `DatabaseRestocker`

## Classe

```java
services.stock.DatabaseRestocker
```

## Responsabilitat

Gestiona la importació automàtica d'articles des d'un fitxer JSON.

El procés permet:

- llegir els articles del JSON;
- mostrar una previsualització;
- comptar camises i pantalons;
- confirmar la importació;
- inserir o actualitzar articles a la base de dades;
- protegir la importació amb transacció i bloqueig controlat.

## Fitxer JSON per defecte

```text
tpv/data/articles.json
```

## API pública habitual

```java
public static RestockPreview preview() throws IOException
public static RestockPreview preview(Path jsonPath) throws IOException

public static RestockResult commit(ShopDatabase database, RestockPreview preview) throws SQLException
public static RestockResult commit(ShopDatabase database, RestockPreview preview, int timeoutSeconds) throws SQLException

public static RestockResult restock(ShopDatabase database) throws IOException, SQLException
public static RestockResult restock(ShopDatabase database, int timeoutSeconds) throws IOException, SQLException

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

## Flux amb timeout concret

```java
try (ShopDatabase database = new ShopDatabase()) {
    RestockPreview preview = DatabaseRestocker.preview();
    RestockResult result = DatabaseRestocker.commit(database, preview, 10);
}
```

## Bloqueig de taula

`commit` bloqueja la taula `articles` en mode escriptura durant la importació.

Això evita que una venda i una importació puguin modificar l'estoc al mateix temps.

Si la taula està bloquejada per una altra operació, la importació espera fins al timeout configurat. Si no pot obtenir el bloqueig dins d'aquest temps, es llança una excepció controlada.

## Resultat esperat

El resultat indica:

- total d'articles llegits;
- nombre de camises;
- nombre de pantalons;
- articles afegits;
- articles actualitzats.

## Semàntica de l'estoc

Si un article ja existeix, les dades es reinicialitzen amb els valors del JSON. Això inclou l'estoc.

Per tant, aquest servei representa una sincronització de catàleg, no una suma incremental d'unitats rebudes.
