# Models d'articles

## Classes

```java
models.Article
models.Shirt
models.Pants
```

## `Article`

`Article` és la classe base per a tots els articles de la botiga.

Conté les dades comunes:

- identificador
- nom
- família
- preu base
- IVA
- stock

També centralitza càlculs comuns.

## API destacada

```java
public double getFinalPrice()
public double getIvaAmount()
public abstract double getCostPrice()
public double getProfitPerUnit()
```

## Preu final

El preu final es calcula aplicant l'IVA al preu base.

```java
double finalPrice = article.getFinalPrice();
```

## Cost de producció

El cost de producció depèn del tipus d'article.

### Camisa

```text
cost = basePrice * 0.35 + collarSize * 0.3
```

### Pantalons

```text
cost = basePrice * 0.30 + legLength * 0.2
```

## Exemple amb camisa

```java
Shirt shirt = new Shirt(
    1,
    "Camisa formal",
    40,
    12,
    29.99,
    21,
    8
);

double cost = shirt.getCostPrice();
double finalPrice = shirt.getFinalPrice();
```

## Exemple amb pantalons

```java
Pants pants = new Pants(
    2,
    "Pantalons texans",
    42,
    38,
    39.99,
    21,
    5
);

double cost = pants.getCostPrice();
double finalPrice = pants.getFinalPrice();
```

## Recomanació

No calculis el preu final ni el cost fora dels models si el model ja ofereix el mètode corresponent. Això evita duplicar fórmules en diversos punts del projecte.
