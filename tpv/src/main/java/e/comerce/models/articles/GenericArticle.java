package e.comerce.models.articles;

public class GenericArticle extends Article {
    public GenericArticle(
            int id,
            String name,
            String type,
            double basePrice,
            int iva,
            int stock) {

        super(
                id,
                name,
                type,
                null,
                null,
                null,
                null,
                basePrice,
                iva,
                stock);
    }

    public GenericArticle(
            int id,
            String name,
            double basePrice,
            int iva,
            int stock) {

        this(id, name, ArticleType.GENERIC.type(), basePrice, iva, stock);
    }

    @Override
    public double getCostPrice() {
        return this.getBasePrice();
    }
}
