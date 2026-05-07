package e.comerce.libs.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registre intern de subtipus polimòrfics.
 *
 * <p>
 * Manté la relació entre els identificadors que apareixen al JSON i les
 * classes concretes que representen cada subtipus.
 * </p>
 *
 * <pre>
 * registry.register("dog", Dog.class);
 * registry.register("cat", Cat.class);
 * </pre>
 *
 * @param <T> tipus base dels objectes registrats
 */
final class PolymorphicTypeRegistry<T> {

    private String typeField;

    private final Map<String, Class<? extends T>> typeNameToClass = new HashMap<>();
    private final Map<Class<? extends T>, String> classToTypeName = new HashMap<>();

    /**
     * Crea un registre de subtipus.
     *
     * <p>
     * El camp de tipus s'utilitza com a discriminador dins del JSON.
     * </p>
     *
     * <pre>
     * new PolymorphicTypeRegistry&lt;&gt;("type")
     * </pre>
     *
     * @param typeField nom del camp discriminador
     */
    PolymorphicTypeRegistry(String typeField) {
        setTypeField(typeField);
    }

    /**
     * Defineix el camp discriminador del JSON.
     *
     * <p>
     * Aquest camp s'utilitza per saber quin subtipus concret s'ha de crear
     * durant la deserialització.
     * </p>
     *
     * <pre>
     * registry.setTypeField("type")
     * </pre>
     *
     * @param typeField nom del camp discriminador
     */
    void setTypeField(String typeField) {
        if (typeField == null || typeField.isBlank()) {
            throw new IllegalArgumentException("El camp de tipus no pot ser nul ni buit.");
        }

        this.typeField = typeField;
    }

    /**
     * Registra un subtipus amb el seu identificador JSON.
     *
     * <p>
     * El nom del tipus ha de ser únic, i cada classe concreta només es pot
     * registrar una vegada.
     * </p>
     *
     * <pre>
     * registry.register("dog", Dog.class)
     * </pre>
     *
     * @param typeName identificador del subtipus dins del JSON
     * @param subtype classe concreta del subtipus
     */
    void register(String typeName, Class<? extends T> subtype) {
        requireText(typeName, "El nom del tipus");
        Objects.requireNonNull(subtype, "El subtipus no pot ser nul.");

        if (typeNameToClass.containsKey(typeName)) {
            throw new IllegalArgumentException("El nom del tipus ja està registrat: " + typeName);
        }

        if (classToTypeName.containsKey(subtype)) {
            throw new IllegalArgumentException(
                    "El subtipus ja està registrat: " + subtype.getName()
            );
        }

        typeNameToClass.put(typeName, subtype);
        classToTypeName.put(subtype, typeName);
    }

    /**
     * Retorna el nom del camp discriminador.
     *
     * <p>
     * Aquest valor és el nom del camp que s'afegirà o es llegirà del JSON.
     * </p>
     *
     * @return nom del camp discriminador
     */
    String typeField() {
        return typeField;
    }

    /**
     * Busca la classe concreta associada a un nom de tipus.
     *
     * <p>
     * Retorna {@code null} si el nom indicat no està registrat.
     * </p>
     *
     * @param typeName identificador del subtipus dins del JSON
     * @return classe concreta associada, o {@code null} si no existeix
     */
    Class<? extends T> subtypeOf(String typeName) {
        return typeNameToClass.get(typeName);
    }

    /**
     * Busca el nom de tipus associat a una classe concreta.
     *
     * <p>
     * Retorna {@code null} si la classe indicada no està registrada.
     * </p>
     *
     * @param subtype classe concreta del subtipus
     * @return identificador associat, o {@code null} si no existeix
     */
    String typeNameOf(Class<?> subtype) {
        return classToTypeName.get(subtype);
    }

    /**
     * Valida que el registre estigui preparat per utilitzar-se.
     *
     * <p>
     * Com a mínim, cal haver registrat un subtipus abans de construir
     * el loader.
     * </p>
     *
     * @throws IllegalStateException si no hi ha cap subtipus registrat
     */
    void validate() {
        if (typeNameToClass.isEmpty()) {
            throw new IllegalStateException("S'ha de registrar com a mínim un subtipus.");
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " no pot ser nul ni buit.");
        }
    }
}