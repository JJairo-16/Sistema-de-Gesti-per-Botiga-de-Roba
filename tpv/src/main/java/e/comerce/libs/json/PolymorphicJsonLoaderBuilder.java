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
     * <p>
     * El tipus base representa la classe, interfície o classe abstracta
     * sobre la qual es definiran els subtipus.
     * </p>
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
     * <p>
     * Aquest camp s'utilitza per identificar quin subtipus s'ha de crear
     * durant la deserialització.
     * </p>
     *
     * <pre>
     * .typeField("type")
     * </pre>
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
     * El valor {@code typeName} és el que apareixerà al JSON per indicar
     * quin tipus concret s'ha d'utilitzar.
     * </p>
     *
     * <pre>
     * .subtype("dog", Dog.class)
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
     * Activa o desactiva el format llegible del JSON.
     *
     * <p>
     * Quan està activat, el JSON es genera amb indentació per facilitar-ne
     * la lectura.
     * </p>
     *
     * <pre>
     * .prettyPrinting(true)
     * </pre>
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
     * <p>
     * Valida que s'hagin registrat correctament els subtipus abans de crear
     * el loader final.
     * </p>
     *
     * <pre>
     * PolymorphicJsonLoader&lt;Animal&gt; loader = builder.build();
     * </pre>
     *
     * @return una instància configurada de {@code PolymorphicJsonLoader}
     */
    public PolymorphicJsonLoader<T> build() {
        registry.validate();
        return PolymorphicJsonLoader.create(baseType, registry, prettyPrinting);
    }
}