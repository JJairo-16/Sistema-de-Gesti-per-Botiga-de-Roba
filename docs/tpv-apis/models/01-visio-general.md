[Tornar a l'índex](../README.md)

# Paquet `models`

## Responsabilitat

El paquet `models` conté les classes i records que representen les dades principals del domini de la botiga.

Aquest paquet no ha de contenir connexions a base de dades, SQL, lectures de fitxers ni interacció amb l'usuari.

## Classes principals

| Classe        | Funció                                      |
| ------------- | ------------------------------------------- |
| `Article`     | Classe base dels articles de la botiga.     |
| `Shirt`       | Especialització d'article per a camises.    |
| `Pants`       | Especialització d'article per a pantalons.  |
| `Client`      | Representa un client de la botiga.          |
| `InvoiceLine` | Representa una línia de factura o tiquet.   |
| `Ticket`      | Representa el tiquet principal d'una venda. |

## Normes de disseny

- Els noms de classes, atributs i mètodes estan en anglès.
- Els JavaDocs estan en català.
- Els models només representen dades i càlculs propis del domini.
- Els models no han de saber com es desen a la base de dades.

## Exemple d'ús

```java
Article article = new Shirt(
    1,
    "Camisa blanca",
    39,
    12,
    25.0,
    21,
    10
);

double finalPrice = article.getFinalPrice();
double costPrice = article.getCostPrice();
```
