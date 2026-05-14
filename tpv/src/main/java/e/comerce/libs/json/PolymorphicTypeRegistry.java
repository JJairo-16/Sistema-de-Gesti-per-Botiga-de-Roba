package e.comerce.libs.json;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Registre intern de subtipus polimòrfics.
 *
 * <p>
 * Manté la relació entre els identificadors que apareixen al JSON i les
 * classes concretes que representen cada subtipus.
 * </p>
 *
 * <p>
 * La deserialització suporta tres nivells de coincidència:
 * </p>
 *
 * <ol>
 *     <li>Coincidència exacta amb el nom registrat.</li>
 *     <li>Coincidència normalitzada: ignora majúscules, accents i plural simple.</li>
 *     <li>Coincidència per expressió regular registrada explícitament.</li>
 * </ol>
 *
 * <p>
 * També pot tenir un subtipus per defecte per quan no hi ha cap coincidència.
 * </p>
 *
 * <pre>
 * registry.register("dog", Dog.class);
 * registry.registerPattern("dogs?", "dog", Dog.class);
 * registry.registerDefault("generic", GenericAnimal.class);
 * </pre>
 *
 * @param <T> tipus base dels objectes registrats
 */
final class PolymorphicTypeRegistry<T> {

    private String typeField;

    private final Map<String, Class<? extends T>> exactTypeNameToClass = new HashMap<>();
    private final Map<String, Class<? extends T>> normalizedTypeNameToClass = new HashMap<>();
    private final Map<Class<? extends T>, String> classToTypeName = new HashMap<>();
    private final List<PatternRegistration<T>> patternRegistrations = new ArrayList<>();

    private Class<? extends T> defaultSubtype;
    private String defaultTypeName;

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
     * A més de la coincidència exacta, el nom també es registra de forma
     * normalitzada. Això permet que valors com {@code Camisa}, {@code camisa}
     * o {@code camisas} puguin resoldre el mateix subtipus sense necessitat
     * d'una expressió regular.
     * </p>
     *
     * @param typeName identificador del subtipus dins del JSON
     * @param subtype classe concreta del subtipus
     */
    void register(String typeName, Class<? extends T> subtype) {
        requireText(typeName, "El nom del tipus");
        Objects.requireNonNull(subtype, "El subtipus no pot ser nul.");
        ensureTypeNameIsFree(typeName);
        ensureSubtypeIsFree(subtype);

        exactTypeNameToClass.put(typeName, subtype);
        normalizedTypeNameToClass.put(normalize(typeName), subtype);
        classToTypeName.put(subtype, typeName);
    }

    /**
     * Registra un subtipus amb una expressió regular.
     *
     * <p>
     * El paràmetre {@code serializedTypeName} és el nom que s'escriurà quan
     * es serialitzi una instància d'aquest subtipus. El paràmetre
     * {@code typePattern} és la regla que s'utilitzarà en deserialitzar.
     * </p>
     *
     * @param typePattern expressió regular per reconèixer el tipus del JSON
     * @param serializedTypeName nom canònic que s'escriurà en serialitzar
     * @param subtype classe concreta del subtipus
     */
    void registerPattern(
            String typePattern,
            String serializedTypeName,
            Class<? extends T> subtype
    ) {
        requireText(typePattern, "El patró del tipus");
        requireText(serializedTypeName, "El nom serialitzat del tipus");
        Objects.requireNonNull(subtype, "El subtipus no pot ser nul.");
        ensureSubtypeIsFree(subtype);

        Pattern pattern;

        try {
            pattern = Pattern.compile(typePattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("El patró del tipus no és vàlid: " + typePattern, e);
        }

        patternRegistrations.add(new PatternRegistration<>(pattern, subtype));
        classToTypeName.put(subtype, serializedTypeName);
    }

    /**
     * Registra el subtipus per defecte.
     *
     * <p>
     * Aquest subtipus s'utilitza quan el valor del camp discriminador no
     * coincideix amb cap registre exacte, normalitzat ni regex.
     * </p>
     *
     * @param serializedTypeName nom que s'escriurà quan se serialitzi aquest subtipus
     * @param subtype classe concreta per defecte
     */
    void registerDefault(String serializedTypeName, Class<? extends T> subtype) {
        requireText(serializedTypeName, "El nom serialitzat del tipus per defecte");
        Objects.requireNonNull(subtype, "El subtipus per defecte no pot ser nul.");

        if (defaultSubtype != null) {
            throw new IllegalArgumentException("El subtipus per defecte ja està registrat: " + defaultSubtype.getName());
        }

        if (classToTypeName.containsKey(subtype)) {
            throw new IllegalArgumentException("El subtipus ja està registrat: " + subtype.getName());
        }

        this.defaultSubtype = subtype;
        this.defaultTypeName = serializedTypeName;
        classToTypeName.put(subtype, serializedTypeName);
    }

    /**
     * Retorna el nom del camp discriminador.
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
     * Si no hi ha coincidència, retorna el subtipus per defecte si existeix.
     * </p>
     *
     * @param typeName identificador del subtipus dins del JSON
     * @return classe concreta associada, subtipus per defecte o {@code null}
     */
    Class<? extends T> subtypeOf(String typeName) {
        if (typeName == null) {
            return defaultSubtype;
        }

        Class<? extends T> exactSubtype = exactTypeNameToClass.get(typeName);

        if (exactSubtype != null) {
            return exactSubtype;
        }

        Class<? extends T> normalizedSubtype = normalizedTypeNameToClass.get(normalize(typeName));

        if (normalizedSubtype != null) {
            return normalizedSubtype;
        }

        for (PatternRegistration<T> registration : patternRegistrations) {
            if (registration.matches(typeName)) {
                return registration.subtype();
            }
        }

        return defaultSubtype;
    }

    /**
     * Busca el nom de tipus associat a una classe concreta.
     *
     * @param subtype classe concreta del subtipus
     * @return identificador associat, o {@code null} si no existeix
     */
    String typeNameOf(Class<?> subtype) {
        return classToTypeName.get(subtype);
    }

    /**
     * Indica si el registre té subtipus per defecte.
     *
     * @return {@code true} si existeix un subtipus per defecte
     */
    boolean hasDefaultSubtype() {
        return defaultSubtype != null && defaultTypeName != null;
    }

    /**
     * Valida que el registre estigui preparat per utilitzar-se.
     *
     * @throws IllegalStateException si no hi ha cap subtipus registrat
     */
    void validate() {
        if (exactTypeNameToClass.isEmpty() && patternRegistrations.isEmpty() && !hasDefaultSubtype()) {
            throw new IllegalStateException("S'ha de registrar com a mínim un subtipus.");
        }
    }

    private void ensureTypeNameIsFree(String typeName) {
        if (exactTypeNameToClass.containsKey(typeName)) {
            throw new IllegalArgumentException("El nom del tipus ja està registrat: " + typeName);
        }

        String normalizedTypeName = normalize(typeName);

        if (normalizedTypeNameToClass.containsKey(normalizedTypeName)) {
            throw new IllegalArgumentException("El nom del tipus normalitzat ja està registrat: " + typeName);
        }
    }

    private void ensureSubtypeIsFree(Class<? extends T> subtype) {
        if (classToTypeName.containsKey(subtype)) {
            throw new IllegalArgumentException("El subtipus ja està registrat: " + subtype.getName());
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer
                .normalize(value.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        if (normalized.length() > 1 && normalized.endsWith("s")) {
            return normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " no pot ser nul ni buit.");
        }
    }

    private static final class PatternRegistration<T> {
        private final Pattern pattern;
        private final Class<? extends T> subtype;

        private PatternRegistration(Pattern pattern, Class<? extends T> subtype) {
            this.pattern = pattern;
            this.subtype = subtype;
        }

        private boolean matches(String typeName) {
            return pattern.matcher(typeName).matches()
                    || pattern.matcher(normalize(typeName)).matches();
        }

        private Class<? extends T> subtype() {
            return subtype;
        }
    }
}
