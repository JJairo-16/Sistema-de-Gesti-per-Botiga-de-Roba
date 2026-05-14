[Tornar a l'índex](../README.md)

# Guia d'operacions habituals

Aquest document resumeix el flux que ha de seguir el menú, la interfície gràfica o qualsevol capa d'aplicació per fer les operacions principals del TPV.

La idea general és que la capa d'aplicació no ha de conèixer SQL, connexions, transaccions ni bloquejos. Ha de treballar amb `ShopDatabase`, serveis d'aplicació i models.

## Regla general

```text
Operació simple de consulta o manteniment
  -> ShopDatabase
  -> repositori corresponent

Operació composta o crítica
  -> servei d'aplicació
  -> transacció i bloquejos gestionats internament
```

## Gestió de clients

Per crear, modificar, consultar o eliminar clients, es pot utilitzar directament el repositori de clients.

```java
try (ShopDatabase database = new ShopDatabase()) {
    Client client = database.clients().findByDni("12345678A");
}
```

Abans de registrar una venda amb client identificat, comprova que el client existeix. Si la venda és anònima, utilitza el client genèric `000`.

## Obtenció de productes

Per mostrar productes disponibles al menú o a la interfície, utilitza el repositori d'articles.

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Article> articles = database.articles().findAll();
}
```

Per seleccionar un producte concret, consulta'l per identificador i valida que existeix abans de crear la línia de venda.

## Venda

Una venda no s'ha de guardar manualment amb `tickets().insert(...)` i `invoiceLines().insert(...)` des de la capa d'aplicació.

El flux correcte és:

```text
1. Seleccionar o validar el client.
2. Seleccionar articles.
3. Validar quantitats positives.
4. Crear les línies de factura.
5. Calcular totals amb TPVService.
6. Crear el Ticket.
7. Registrar-ho tot amb SaleService.
```

`SaleService` s'encarrega de registrar el tiquet, les línies i la reducció d'estoc dins d'una operació segura.

## Importació d'articles

La importació d'articles des del JSON s'ha de fer amb `DatabaseRestocker`.

```java
try (ShopDatabase database = new ShopDatabase()) {
    RestockPreview preview = DatabaseRestocker.preview();
    DatabaseRestocker.printPreview(preview);

    RestockResult result = DatabaseRestocker.commit(database, preview);
    DatabaseRestocker.printResult(result);
}
```

Aquest procés utilitza bloqueig controlat de la taula d'articles, de manera que no es barreja amb vendes simultànies.

## Informes

Els informes són consultes de lectura. Es poden fer directament des de `database.reports()`.

```java
try (ShopDatabase database = new ShopDatabase()) {
    ClientSalesSummary summary = database.reports().summarizeClient("12345678A");
}
```

## Validacions que ha de fer la capa d'aplicació

La capa d'aplicació hauria de validar:

- que el DNI no sigui buit quan es treballa amb clients reals;
- que l'article existeixi abans d'afegir-lo a la venda;
- que la quantitat sigui superior a zero;
- que la venda tingui com a mínim una línia;
- que el client existeixi o que s'utilitzi el client genèric;
- que els errors de base de dades es mostrin de manera comprensible a l'usuari.

## Validacions que ja protegeix el backend

El backend protegeix:

- la reducció d'estoc amb condició d'estoc suficient;
- el registre de venda dins d'una transacció;
- el bloqueig de taules en operacions crítiques;
- el timeout controlat si una taula està bloquejada massa temps;
- la importació d'articles sense barrejar-la amb vendes simultànies.

Això no elimina la necessitat de validar a la interfície, però evita que la base de dades quedi en un estat inconsistent.
