package e.comerce.libs.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Adaptador de Gson per gestionar la serialització i deserialització
 * de tipus polimòrfics.
 *
 * <p>
 * Aquest adaptador permet convertir objectes JSON en instàncies concretes
 * de subtipus a partir d'un camp discriminador (com ara {@code type}),
 * i també afegeix aquest camp durant la serialització.
 * </p>
 *
 * <p>
 * És utilitzat internament per {@link PolymorphicJsonLoader} i no està
 * pensat per a ús directe.
 * </p>
 *
 * @param <T> tipus base dels objectes
 */
final class PolymorphicTypeAdapter<T>
        implements JsonDeserializer<T>, JsonSerializer<T> {

    private final PolymorphicTypeRegistry<T> registry;

    /**
     * Crea un nou adaptador polimòrfic.
     *
     * <p>
     * El registre conté la relació entre els identificadors de tipus
     * i les classes concretes.
     * </p>
     *
     * @param registry registre de subtipus
     */
    PolymorphicTypeAdapter(PolymorphicTypeRegistry<T> registry) {
        this.registry = Objects.requireNonNull(registry, "El registre de tipus no pot ser nul.");
    }

    /**
     * Deserialitza un objecte JSON a un subtipus concret.
     *
     * <p>
     * Llegeix el camp discriminador i resol el subtipus corresponent
     * abans de delegar la deserialització a Gson.
     * </p>
     *
     * @param json element JSON d'entrada
     * @param targetType tipus objectiu
     * @param context context de deserialització
     * @return una instància del subtipus corresponent
     * @throws JsonParseException si el JSON no és vàlid o el tipus no està registrat
     */
    @Override
    public T deserialize(
            JsonElement json,
            Type targetType,
            JsonDeserializationContext context
    ) throws JsonParseException {

        JsonObject jsonObject = asJsonObject(json);

        String typeName = readTypeName(jsonObject);
        Class<? extends T> subtype = registry.subtypeOf(typeName);

        if (subtype == null) {
            throw new JsonParseException("Subtipus desconegut: " + typeName);
        }

        return context.deserialize(jsonObject, subtype);
    }

    /**
     * Serialitza un objecte afegint el camp discriminador.
     *
     * <p>
     * Determina el tipus real de l'objecte i afegeix el seu identificador
     * al JSON generat.
     * </p>
     *
     * @param value objecte a serialitzar
     * @param sourceType tipus font
     * @param context context de serialització
     * @return element JSON amb el camp de tipus inclòs
     */
    @Override
    public JsonElement serialize(
            T value,
            Type sourceType,
            JsonSerializationContext context
    ) {
        Objects.requireNonNull(value, "L'objecte no pot ser nul.");

        Class<?> runtimeType = value.getClass();
        String typeName = registry.typeNameOf(runtimeType);

        if (typeName == null) {
            throw new JsonParseException(
                    "El subtipus no està registrat: " + runtimeType.getName()
            );
        }

        JsonObject jsonObject = context
                .serialize(value, runtimeType)
                .getAsJsonObject();

        jsonObject.addProperty(registry.typeField(), typeName);

        return jsonObject;
    }

    /**
     * Converteix un {@link JsonElement} a {@link JsonObject}.
     *
     * <p>
     * Verifica que l'element sigui un objecte JSON vàlid.
     * </p>
     *
     * @param json element JSON
     * @return objecte JSON corresponent
     * @throws JsonParseException si no és un objecte JSON
     */
    private JsonObject asJsonObject(JsonElement json) {
        if (json == null || !json.isJsonObject()) {
            throw new JsonParseException("S'esperava un objecte JSON.");
        }

        return json.getAsJsonObject();
    }

    /**
     * Llegeix el nom del tipus des del JSON.
     *
     * <p>
     * Obté el valor del camp discriminador i valida que sigui una cadena.
     * </p>
     *
     * @param jsonObject objecte JSON
     * @return nom del tipus
     * @throws JsonParseException si el camp no existeix o no és vàlid
     */
    private String readTypeName(JsonObject jsonObject) {
        String typeField = registry.typeField();

        if (!jsonObject.has(typeField)) {
            throw new JsonParseException(
                    "Falta el camp de tipus: " + typeField
            );
        }

        JsonElement value = jsonObject.get(typeField);

        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            throw new JsonParseException(
                    "El camp de tipus ha de ser una cadena: " + typeField
            );
        }

        return value.getAsString();
    }
}