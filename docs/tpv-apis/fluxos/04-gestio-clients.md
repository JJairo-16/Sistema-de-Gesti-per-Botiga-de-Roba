[Tornar a l'índex](../README.md)

# Flux de gestió de clients

Aquest document descriu com gestionar clients des del menú o la capa d'aplicació.

## Responsabilitat de la capa d'aplicació

La capa d'aplicació ha de demanar les dades, validar-les de manera bàsica i delegar la persistència al repositori de clients.

No s'ha d'escriure SQL ni obrir connexions directament.

## Crear un client

```java
Client client = new Client(
    "12345678A",
    "Maria Garcia",
    "maria@example.com",
    "600123123"
);

try (ShopDatabase database = new ShopDatabase()) {
    boolean inserted = database.clients().insert(client);

    if (!inserted) {
        System.out.println("No s'ha pogut crear el client.");
    }
}
```

## Crear o actualitzar un client

Si vols que l'operació sigui tolerant a clients ja existents, utilitza `save`.

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.clients().save(client);
}
```

Aquest mètode és útil quan el formulari de client pot servir tant per alta com per modificació.

## Buscar un client

```java
try (ShopDatabase database = new ShopDatabase()) {
    Client client = database.clients().findByDni("12345678A");

    if (client == null) {
        System.out.println("Client no trobat.");
    }
}
```

## Llistar clients

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Client> clients = database.clients().findAll();

    for (Client client : clients) {
        System.out.println(client.dni() + " - " + client.name());
    }
}
```

## Eliminar un client

```java
try (ShopDatabase database = new ShopDatabase()) {
    boolean deleted = database.clients().delete("12345678A");

    if (!deleted) {
        System.out.println("No s'ha eliminat cap client.");
    }
}
```

Abans d'eliminar un client, comprova si la interfície ha d'avisar l'usuari que pot tenir vendes associades.

## Client genèric

Per vendes sense client identificat, utilitza el client genèric:

```text
000
```

El menú no hauria de demanar dades personals quan la venda és anònima. Ha de crear el `Ticket` amb `dniClient = "000"`.

## Validacions recomanades

Abans de desar un client, valida com a mínim:

- que el DNI no sigui buit;
- que el nom no sigui buit;
- que el correu tingui un format acceptable si s'ha informat;
- que el telèfon no sigui buit si el programa el considera obligatori;
- que no s'intenti eliminar el client genèric `000` des del menú normal.

Les validacions de format més estrictes poden viure en una capa de formulari o en un servei específic si el projecte creix.
