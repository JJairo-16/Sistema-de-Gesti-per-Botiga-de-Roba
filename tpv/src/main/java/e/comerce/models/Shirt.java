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
                null,
                chestWidth,
                basePrice,
                iva,
                stock
        );
    }
}