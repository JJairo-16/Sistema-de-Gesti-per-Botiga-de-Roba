[Tornar a l'índex](../README.md)

# `SaleService`

## Classe

```java
services.sales.SaleService
```

## Responsabilitat

Registra una venda completa de manera coordinada.

Aquest servei és responsable de:

- inserir el tiquet;
- inserir les línies de factura;
- reduir l'estoc dels articles venuts;
- executar tota la venda dins d'una transacció;
- utilitzar bloqueig controlat de taules per evitar modificacions simultànies problemàtiques.

## API pública

```java
public SaleService(ShopDatabase database)
public long registerSale(Ticket ticket, List<InvoiceLine> lines) throws SQLException
```

## Exemple d'ús

```java
try (ShopDatabase database = new ShopDatabase()) {
    SaleService saleService = new SaleService(database);

    long ticketId = saleService.registerSale(ticket, lines);

    System.out.println("Tiquet creat: " + ticketId);
}
```

## Quan s'ha d'utilitzar

S'ha d'utilitzar quan el client confirma una compra al TPV.

La capa d'aplicació no hauria d'inserir manualment el tiquet i les línies quan està fent una venda real.

## Validacions que fa

`SaleService` valida que:

- el tiquet no sigui nul;
- la llista de línies no sigui nul·la;
- la venda tingui com a mínim una línia;
- cada reducció d'estoc es pugui aplicar correctament.

Si no hi ha prou estoc o es produeix un error, la venda no es confirma parcialment.

## Transacció i bloqueig

El servei registra la venda dins d'una transacció. Això permet confirmar o desfer tota l'operació com una sola unitat.

A més, pot utilitzar bloquejos de taula per evitar que dues operacions crítiques modifiquin les mateixes dades de manera simultània.

Si la taula està bloquejada per una altra operació, el procés espera fins al timeout configurat. Si no pot continuar dins d'aquest temps, es llança un error controlat.

## Avantatge

Evita que el programa:

- desi un tiquet sense línies;
- resti estoc sense haver registrat correctament la venda;
- deixi línies associades a un tiquet que no s'ha confirmat;
- barregi vendes i importacions d'articles en moments crítics.
