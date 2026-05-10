# `TicketRepository`

## Classe

```java
services.database.repository.TicketRepository
```

## Responsabilitat

Gestiona la persistència dels tiquets.

## API pública

```java
public long insert(Ticket ticket) throws SQLException
public boolean update(Ticket ticket) throws SQLException
public boolean delete(int id) throws SQLException

public Ticket findById(int id) throws SQLException
public List<Ticket> findByClient(String dniClient) throws SQLException
```

## Crear un tiquet

Normalment no s'hauria d'inserir un tiquet manualment des del menú.

És millor utilitzar `SaleService`, perquè també desa les línies i actualitza l'stock.

```java
SaleService saleService = new SaleService(database);
long ticketId = saleService.registerSale(ticket, lines);
```

## Consultar tiquets d'un client

```java
try (ShopDatabase database = new ShopDatabase()) {
    List<Ticket> tickets = database.tickets().findByClient("12345678A");
}
```
