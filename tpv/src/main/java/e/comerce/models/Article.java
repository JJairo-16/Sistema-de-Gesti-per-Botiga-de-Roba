package e.comerce.models;

/**
 * Representa un article de la botiga.
 */
public abstract class Article {
    private final int id;
    private String name;
    private final String type;
    private final ArticleType typeKey;

    private int neckSize; // talla_coll: 36 - 52
    private Integer pantsLength; // llargada_camal: 32 - 46 o null
    private int waistSize; // talla_cintura: 24 - 56
    private int chestWidth; // amplada_pit: 10 - 15

    private double basePrice; // preu_base: preu sense IVA
    private int iva; // IVA: 4 - 21
    private int stock; // stock >= 0

    private static final int MIN_NECK_SIZE = 36;
    private static final int MAX_NECK_SIZE = 52;

    private static final int MIN_PANTS_LENGTH = 32;
    private static final int MAX_PANTS_LENGTH = 46;

    private static final int MIN_WAIST_SIZE = 24;
    private static final int MAX_WAIST_SIZE = 56;

    private static final int MIN_CHEST_WIDTH = 10;
    private static final int MAX_CHEST_WIDTH = 15;

    private static final int MIN_IVA = 4;
    private static final int MAX_IVA = 21;

    private static final int MIN_STOCK = 0;
    private static final double MIN_BASE_PRICE = 0;

    /**
     * Crea un article amb les seves dades bàsiques.
     */
    protected Article(
            int id,
            String name,
            String type,
            int neckSize,
            Integer pantsLength,
            int waistSize,
            int chestWidth,
            double basePrice,
            int iva,
            int stock) {
        this.id = id;
        setName(name);

        this.typeKey = ArticleType.getType(type);
        this.type = type;

        setNeckSize(neckSize);
        setPantsLength(pantsLength);
        setWaistSize(waistSize);
        setChestWidth(chestWidth);
        setBasePrice(basePrice);
        setIva(iva);
        setStock(stock);
    }

    /**
     * Retorna l'identificador.
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna el nom comercial.
     */
    public String getName() {
        return name;
    }

    /**
     * Defineix el nom comercial.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nom de l'article no pot estar buit");
        }

        this.name = name;
    }

    /**
     * Retorna el tipus d'article.
     */
    public String getType() {
        return type;
    }

    /**
     * Retorna el tipus d'article com a enum.
     */
    public ArticleType getTypeKey() {
        return typeKey;
    }

    /**
     * Retorna la talla de coll.
     */
    public int getNeckSize() {
        return neckSize;
    }

    /**
     * Defineix la talla de coll.
     */
    public void setNeckSize(int neckSize) {
        if (neckSize < MIN_NECK_SIZE || neckSize > MAX_NECK_SIZE) {
            throw new IllegalArgumentException(
                    "La talla de coll ha d'estar entre " + MIN_NECK_SIZE + " i " + MAX_NECK_SIZE);
        }

        this.neckSize = neckSize;
    }

    /**
     * Retorna la llargada del camal.
     */
    public Integer getPantsLength() {
        return pantsLength;
    }

    /**
     * Defineix la llargada del camal.
     */
    public void setPantsLength(Integer pantsLength) {
        if (pantsLength != null &&
                (pantsLength < MIN_PANTS_LENGTH || pantsLength > MAX_PANTS_LENGTH)) {
            throw new IllegalArgumentException(
                    "La llargada del camal ha d'estar entre " + MIN_PANTS_LENGTH + " i " + MAX_PANTS_LENGTH);
        }

        this.pantsLength = pantsLength;
    }

    /**
     * Retorna la talla de cintura.
     */
    public int getWaistSize() {
        return waistSize;
    }

    /**
     * Defineix la talla de cintura.
     */
    public void setWaistSize(int waistSize) {
        if (waistSize < MIN_WAIST_SIZE || waistSize > MAX_WAIST_SIZE) {
            throw new IllegalArgumentException(
                    "La talla de cintura ha d'estar entre " + MIN_WAIST_SIZE + " i " + MAX_WAIST_SIZE);
        }

        this.waistSize = waistSize;
    }

    /**
     * Retorna l'amplada de pit.
     */
    public int getChestWidth() {
        return chestWidth;
    }

    /**
     * Defineix l'amplada de pit.
     */
    public void setChestWidth(int chestWidth) {
        if (chestWidth < MIN_CHEST_WIDTH || chestWidth > MAX_CHEST_WIDTH) {
            throw new IllegalArgumentException(
                    "L'amplada de pit ha d'estar entre " + MIN_CHEST_WIDTH + " i " + MAX_CHEST_WIDTH);
        }

        this.chestWidth = chestWidth;
    }

    /**
     * Retorna el preu base.
     */
    public double getBasePrice() {
        return basePrice;
    }

    /**
     * Defineix el preu base.
     */
    public void setBasePrice(double basePrice) {
        if (basePrice < MIN_BASE_PRICE) {
            throw new IllegalArgumentException("El preu base no pot ser negatiu");
        }

        this.basePrice = basePrice;
    }

    /**
     * Retorna l'IVA.
     */
    public int getIva() {
        return iva;
    }

    /**
     * Defineix l'IVA.
     */
    public void setIva(int iva) {
        if (iva < MIN_IVA || iva > MAX_IVA) {
            throw new IllegalArgumentException(
                    "L'IVA ha d'estar entre " + MIN_IVA + " i " + MAX_IVA);
        }

        this.iva = iva;
    }

    /**
     * Retorna l'estoc.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Defineix l'estoc.
     */
    public void setStock(int stock) {
        if (stock < MIN_STOCK) {
            throw new IllegalArgumentException("L'estoc no pot ser negatiu");
        }

        this.stock = stock;
    }
}