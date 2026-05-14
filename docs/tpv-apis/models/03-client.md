[Tornar a l'índex](../README.md)

# Model `Client`

## Classe

```java
models.Client
```

## Responsabilitat

Representa un client de la botiga.

És un model de dades simple i no conté lògica de base de dades.

## Camps

```java
String dni
String name
String email
String phone
```

## Exemple

```java
Client client = new Client(
    "12345678A",
    "Maria Garcia",
    "maria@example.com",
    "600123123"
);

System.out.println(client.name());
```

## Client genèric

El client genèric de la pràctica s'identifica amb el DNI:

```text
000
```

Aquest client es pot utilitzar quan la venda no està associada a un client registrat.
