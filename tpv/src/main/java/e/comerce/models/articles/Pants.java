package e.comerce.models.articles;

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
            int pantsLength,
            int waistSize,
            double basePrice,
            int iva,
            int stock) {

        super(
                id,
                name,
                ArticleType.PANTS.type(),
                null,
                pantsLength,
                waistSize,
                null,
                basePrice,
                iva,
                stock);
    }

    /**
     * Calcula el cost unitari segons la fórmula definida per als pantalons.
     */
    @Override
    public double getCostPrice() {
        return getBasePrice() * 0.30 + getPantsLength() * 0.2;
    }
}
