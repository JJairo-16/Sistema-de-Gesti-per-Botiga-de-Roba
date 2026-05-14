package e.comerce.libs.json;

import java.util.Objects;

/**
 * Constructor per configurar i crear instàncies de {@link PolymorphicJsonLoader}.
 *
 * <p>
 * Permet definir el camp discriminador i registrar els subtipus que es faran servir
 * durant la serialització i deserialització de JSON polimòrfics.
 * </p>
 *
 * <pre>
 * PolymorphicJsonLoader&lt;Animal&gt; loader = PolymorphicJsonLoader
 *         .forBaseType(Animal.class)
 *         .typeField("type")
 *         .subtype("dog", Dog.class)
 *         .subtype("cat", Cat.class)
 *         .defaultSubtype("generic", GenericAnimal.class)
 *         .build();
 * </pre>
 *
 * @param <T> tipus base dels objectes
 */
public final class PolymorphicJsonLoaderBuilder<T> {

    private static final String DEFAULT_TYPE_FIELD = "type";

    private final Class<T> baseType;
    private final PolymorphicTypeRegistry<T> registry;

    private boolean prettyPrinting = true;

    /**
     * Crea un nou constructor per a un tipus base.
     *
     * @param baseType tipus base del loader
     */
    PolymorphicJsonLoaderBuilder(Class<T> baseType) {
        this.baseType = Objects.requireNonNull(baseType, "El tipus base no pot ser nul.");
        this.registry = new PolymorphicTypeRegistry<>(DEFAULT_TYPE_FIELD);
    }

    /**
     * Defineix el nom del camp discriminador.
     *
     * @param typeField nom del camp discriminador
     * @return el mateix constructor per continuar la configuració
     */
    public PolymorphicJsonLoaderBuilder<T> typeField(String typeField) {
        registry.setTypeField(typeField);
        return this;
    }

    /**
     * Registra un subtipus amb el seu identificador.
     *
     * <p>
     * El nom registrat accepta coincidència exacta i també coincidència
     * normalitzada. Per exemple, si es registra {@code Camisa}, també podrà
     * llegir {@code camisa}, {@code CAMISA} o {@code camisas}.
     * </p>
     *
     * <pre>
     * .subtype("Camisa", Shirt.class)
     * </pre>
     *
     * @param typeName identificador del tipus al JSON
     * @param subtype classe concreta del subtipus
     * @return el mateix constructor per continuar la configuració
     */
    public PolymorphicJsonLoaderBuilder<T> subtype(
            String typeName,
            Class<? extends T> subtype
    ) {
        registry.register(typeName, subtype);
        return this;
    }

    /**
     * Registra un subtipus amb una expressió regular.
     *
     * <p>
     * El patró es compara amb {@code matches()}, per tant ha de coincidir amb
     * tot el valor del camp discriminador. La comparació ignora majúscules i
     * minúscules.
     * </p>
     *
     * <pre>
     * .subtypePattern("camisas?", "Camisa", Shirt.class)
     * .subtypePattern("pantalon(es)?|pantalons", "Pantalons", Pants.class)
     * </pre>
     *
     * @param typePattern expressió regular que reconeix el valor del JSON
     * @param serializedTypeName nom canònic que s'escriurà en serialitzar
     * @param subtype classe concreta del subtipus
     * @return el mateix constructor per continuar la configuració
     */
    public PolymorphicJsonLoaderBuilder<T> subtypePattern(
            String typePattern,
            String serializedTypeName,
            Class<? extends T> subtype
    ) {
        registry.registerPattern(typePattern, serializedTypeName, subtype);
        return this;
    }

    /**
     * Registra el subtipus per defecte.
     *
     * <p>
     * Aquest subtipus s'utilitza quan el valor del camp discriminador no
     * coincideix amb cap subtipus registrat prèviament.
     * </p>
     *
     * <pre>
     * .defaultSubtype("Generic", GenericArticle.class)
     * </pre>
     *
     * @param serializedTypeName nom que s'escriurà en serialitzar aquest subtipus
     * @param subtype classe concreta per defecte
     * @return el mateix constructor per continuar la configuració
     */
    public PolymorphicJsonLoaderBuilder<T> defaultSubtype(
            String serializedTypeName,
            Class<? extends T> subtype
    ) {
        registry.registerDefault(serializedTypeName, subtype);
        return this;
    }

    /**
     * Activa o desactiva el format llegible del JSON.
     *
     * @param enabled {@code true} per activar el format llegible
     * @return el mateix constructor per continuar la configuració
     */
    public PolymorphicJsonLoaderBuilder<T> prettyPrinting(boolean enabled) {
        this.prettyPrinting = enabled;
        return this;
    }

    /**
     * Construeix una instància de {@link PolymorphicJsonLoader}.
     *
     * @return una instància configurada de {@code PolymorphicJsonLoader}
     */
    public PolymorphicJsonLoader<T> build() {
        registry.validate();
        return PolymorphicJsonLoader.create(baseType, registry, prettyPrinting);
    }
}
