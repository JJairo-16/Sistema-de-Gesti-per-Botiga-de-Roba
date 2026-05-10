package e.comerce.models.articles;

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
            String type,
            int neckSize,
            int chestWidth,
            double basePrice,
            int iva,
            int stock) {

        super(
                id,
                name,
                type,
                neckSize,
                null,
                null,
                chestWidth,
                basePrice,
                iva,
                stock
        );
    }

    /**
     * Crea una camisa fent servir el tipus estàndard del model.
     */
    public Shirt(
            int id,
            String name,
            int neckSize,
            int chestWidth,
            double basePrice,
            int iva,
            int stock) {

        this(id, name, ArticleType.SHIRT.type(), neckSize, chestWidth, basePrice, iva, stock);
    }

    /**
     * Calcula el cost unitari segons la fórmula definida per a les camises.
     */
    @Override
    public double getCostPrice() {
        return getBasePrice() * 0.35 + getNeckSize() * 0.3;
    }
}
