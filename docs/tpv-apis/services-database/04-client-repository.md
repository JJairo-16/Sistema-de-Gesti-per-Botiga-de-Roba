# `ClientRepository`

## Classe

```java
services.database.repository.ClientRepository
```

## Responsabilitat

Gestiona la persistència dels clients.

## API pública

```java
public boolean insert(Client client) throws SQLException
public boolean save(Client client) throws SQLException
public boolean update(Client client) throws SQLException
public boolean delete(String dni) throws SQLException

public Client findByDni(String dni) throws SQLException
public List<Client> findAll() throws SQLException
public boolean exists(String dni) throws SQLException
```

## Crear un client

```java
Client client = new Client(
    "12345678A",
    "Maria Garcia",
    "maria@example.com",
    "600123123"
);

try (ShopDatabase database = new ShopDatabase()) {
    database.clients().insert(client);
}
```

## Crear o actualitzar

```java
try (ShopDatabase database = new ShopDatabase()) {
    database.clients().save(client);
}
```

## Buscar un client

```java
try (ShopDatabase database = new ShopDatabase()) {
    Client client = database.clients().findByDni("12345678A");
}
```
