[Tornar a l'índex](../README.md)

# Flux d'importació d'articles

Aquest document descriu com importar articles des del JSON de central.

La importació és una operació crítica perquè pot modificar l'estoc dels articles. Per això s'ha de fer amb `DatabaseRestocker`, no escrivint directament sobre el repositori des del menú.

## Flux recomanat

```text
1. Llegir el JSON.
2. Mostrar previsualització.
3. Demanar confirmació.
4. Bloquejar la taula d'articles de manera controlada.
5. Inserir o actualitzar articles.
6. Confirmar la transacció.
7. Mostrar resum final.
```

## Ús directe

```java
try (ShopDatabase database = new ShopDatabase()) {
    DatabaseRestocker.restock(database);
}
```

## Ús manual en dues fases

```java
try (ShopDatabase database = new ShopDatabase()) {
    RestockPreview preview = DatabaseRestocker.preview();

    DatabaseRestocker.printPreview(preview);

    RestockResult result = DatabaseRestocker.commit(database, preview);

    DatabaseRestocker.printResult(result);
}
```

## Ús amb timeout específic

```java
try (ShopDatabase database = new ShopDatabase()) {
    RestockPreview preview = DatabaseRestocker.preview();
    RestockResult result = DatabaseRestocker.commit(database, preview, 10);
}
```

En aquest exemple, el procés espera fins a 10 segons per obtenir el bloqueig de la taula d'articles.

## Comportament de la importació

- Si l'article no existeix, s'insereix.
- Si l'article ja existeix, s'actualitza amb les dades del JSON.
- L'estoc queda amb el valor del JSON.
- Al final es mostra el nombre d'articles afegits i actualitzats.

Aquesta importació funciona com una sincronització de catàleg. No és una suma d'unitats rebudes.

## Bloqueig i timeout

Durant el `commit`, `DatabaseRestocker` bloqueja la taula `articles` en mode escriptura.

Això evita que una venda i una importació modifiquin l'estoc del mateix article alhora.

Si la taula està bloquejada per una altra operació, el procés espera fins al timeout configurat. Si no pot continuar dins d'aquest temps, es llança un error controlat.

## Validacions recomanades

Abans de confirmar la importació, la capa d'aplicació hauria de:

- mostrar el nombre total d'articles;
- mostrar el nombre de camises i pantalons;
- demanar confirmació explícita a l'usuari;
- avisar que els articles existents s'actualitzaran amb les dades del JSON;
- capturar errors de lectura del fitxer i errors de base de dades.

## Classes implicades

| Classe | Paquet | Funció |
| ------ | ------ | ------ |
| `DatabaseRestocker` | `services.stock` | Llegeix el JSON i coordina la importació. |
| `ShopDatabase` | `services.database` | Dona accés transaccional a la base de dades. |
| `ArticleRepository` | `services.database.repository` | Insereix o actualitza articles. |
| `Article`, `Shirt`, `Pants` | `models` | Representen els articles importats. |
