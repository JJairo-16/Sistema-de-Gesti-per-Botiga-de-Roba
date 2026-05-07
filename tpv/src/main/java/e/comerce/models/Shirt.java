package e.comerce.models;

/**
 * Representa un article de tipus camisa.
 */
public class Shirt extends Article {

    /**
     * Crea una camisa amb les seves dades.
     */
    public Shirt(
            int id,
            String name,
            int neckSize,
            int waistSize,
            int chestWidth,
            double basePrice,
            int iva,
            int stock) {
        super(
                id,
                name,
                ArticleType.SHIRT.type(),
                neckSize,
                null,
                waistSize,
                chestWidth,
                basePrice,
                iva,
                stock
        );
    }
}