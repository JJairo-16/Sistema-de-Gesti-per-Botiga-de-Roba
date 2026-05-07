package e.comerce.models;

/**
 * Representa un article de tipus pantalons.
 */
public class Pants extends Article {

    /**
     * Crea uns pantalons amb les seves dades.
     */
    public Pants(
            int id,
            String name,
            int neckSize,
            int pantsLength,
            int waistSize,
            int chestWidth,
            double basePrice,
            int iva,
            int stock) {
        super(
                id,
                name,
                ArticleType.PANTS.type(),
                neckSize,
                pantsLength,
                waistSize,
                chestWidth,
                basePrice,
                iva,
                stock
        );
    }
}