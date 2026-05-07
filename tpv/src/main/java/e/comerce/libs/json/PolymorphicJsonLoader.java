package e.comerce.libs.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

/**
 * Carregador JSON amb suport per a tipus polimòrfics.
 *
 * <p>
 * Aquesta classe estén {@link JsonLoader} i reutilitza la seva API de lectura
 * i escriptura, però afegeix un adaptador de Gson per resoldre subtipus
 * mitjançant un camp discriminador.
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
 * @param <T> tipus base dels objectes gestionats pel loader
 */
public final class PolymorphicJsonLoader<T> extends JsonLoader<T> {

    private PolymorphicJsonLoader(Class<T> baseType, Gson gson) {
        super(baseType, gson);
    }

    /**
     * Crea un constructor per configurar un loader polimòrfic.
     *
     * <p>
     * El tipus base acostuma a ser una interfície, una classe abstracta
     * o una classe pare amb diversos subtipus registrats.
     * </p>
     *
     * <pre>
     * PolymorphicJsonLoader.forBaseType(Animal.class)
     * </pre>
     *
     * @param baseType tipus base que gestionarà el loader
     * @param <T> tipus base dels objectes
     * @return un constructor de {@code PolymorphicJsonLoader}
     */
    public static <T> PolymorphicJsonLoaderBuilder<T> forBaseType(Class<T> baseType) {
        return new PolymorphicJsonLoaderBuilder<>(baseType);
    }

    static <T> PolymorphicJsonLoader<T> create(
            Class<T> baseType,
            PolymorphicTypeRegistry<T> registry,
            boolean prettyPrinting
    ) {
        Objects.requireNonNull(baseType, "El tipus base no pot ser nul.");
        Objects.requireNonNull(registry, "El registre de tipus no pot ser nul.");

        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(baseType, new PolymorphicTypeAdapter<>(registry));

        if (prettyPrinting) {
            builder.setPrettyPrinting();
        }

        return new PolymorphicJsonLoader<>(baseType, builder.create());
    }
}