package e.comerce.libs.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Carregador JSON simple per a POJOs.
 *
 * <p>
 * Permet llegir i escriure objectes JSON i arrays JSON sense aplicar
 * cap lògica polimòrfica.
 * </p>
 *
 * <pre>
 * JsonLoader&lt;User&gt; loader = new JsonLoader&lt;&gt;(User.class);
 * User user = loader.loadObject(json);
 * </pre>
 *
 * @param <T> tipus d'objecte gestionat pel loader
 */
public class JsonLoader<T> {

    private final Class<T> targetType;
    private final Gson gson;

    /**
     * Crea un loader JSON simple.
     *
     * <p>
     * Aquesta versió està pensada per a classes concretes que Gson pot
     * instanciar directament.
     * </p>
     *
     * <pre>
     * new JsonLoader&lt;&gt;(User.class)
     * </pre>
     *
     * @param targetType classe concreta que es vol carregar o desar
     */
    public JsonLoader(Class<T> targetType) {
        this(targetType, new GsonBuilder().setPrettyPrinting().create());
    }

    /**
     * Crea un loader JSON amb una instància de Gson personalitzada.
     *
     * <p>
     * Aquest constructor està pensat perquè les classes filles puguin
     * reutilitzar tota la lògica de lectura i escriptura.
     * </p>
     *
     * @param targetType tipus gestionat pel loader
     * @param gson instància de Gson que s'utilitzarà internament
     */
    protected JsonLoader(Class<T> targetType, Gson gson) {
        this.targetType = Objects.requireNonNull(targetType, "El tipus objectiu no pot ser nul.");
        this.gson = Objects.requireNonNull(gson, "La instància de Gson no pot ser nul·la.");
    }

    /**
     * Llegeix un objecte JSON des d'una cadena de text.
     *
     * <pre>
     * User user = loader.loadObject(json);
     * </pre>
     *
     * @param json text JSON que representa un objecte
     * @return una instància del tipus configurat
     */
    public T loadObject(String json) {
        requireText(json, "El JSON");
        return gson.fromJson(json, targetType);
    }

    /**
     * Llegeix un array JSON des d'una cadena de text.
     *
     * <pre>
     * List&lt;User&gt; users = loader.loadArray(json);
     * </pre>
     *
     * @param json text JSON que representa un array
     * @return una llista d'objectes del tipus configurat
     */
    public List<T> loadArray(String json) {
        requireText(json, "El JSON");
        return gson.fromJson(json, listType());
    }

    /**
     * Llegeix un objecte JSON des d'un fitxer.
     *
     * <pre>
     * User user = loader.loadObject(Path.of("user.json"));
     * </pre>
     *
     * @param path ruta del fitxer JSON
     * @return una instància del tipus configurat
     * @throws IOException si no es pot llegir el fitxer
     */
    public T loadObject(Path path) throws IOException {
        Objects.requireNonNull(path, "La ruta no pot ser nul·la.");
        return loadObject(Files.readString(path));
    }

    /**
     * Llegeix un array JSON des d'un fitxer.
     *
     * <pre>
     * List&lt;User&gt; users = loader.loadArray(Path.of("users.json"));
     * </pre>
     *
     * @param path ruta del fitxer JSON
     * @return una llista d'objectes del tipus configurat
     * @throws IOException si no es pot llegir el fitxer
     */
    public List<T> loadArray(Path path) throws IOException {
        Objects.requireNonNull(path, "La ruta no pot ser nul·la.");
        return loadArray(Files.readString(path));
    }

    /**
     * Desa un objecte com a JSON.
     *
     * <pre>
     * String json = loader.saveObject(user);
     * </pre>
     *
     * @param object objecte que es vol convertir a JSON
     * @return representació JSON de l'objecte
     */
    public String saveObject(T object) {
        Objects.requireNonNull(object, "L'objecte no pot ser nul.");
        return gson.toJson(object, targetType);
    }

    /**
     * Desa una llista d'objectes com a array JSON.
     *
     * <pre>
     * String json = loader.saveArray(users);
     * </pre>
     *
     * @param objects llista d'objectes que es vol convertir a JSON
     * @return representació JSON de la llista
     */
    public String saveArray(List<? extends T> objects) {
        Objects.requireNonNull(objects, "La llista d'objectes no pot ser nul·la.");
        return gson.toJson(objects, listType());
    }

    /**
     * Desa un objecte JSON en un fitxer.
     *
     * <pre>
     * loader.saveObject(user, Path.of("user.json"));
     * </pre>
     *
     * @param object objecte que es vol desar
     * @param path ruta del fitxer de sortida
     * @throws IOException si no es pot escriure el fitxer
     */
    public void saveObject(T object, Path path) throws IOException {
        Objects.requireNonNull(path, "La ruta no pot ser nul·la.");
        Files.writeString(path, saveObject(object));
    }

    /**
     * Desa una llista d'objectes com a array JSON en un fitxer.
     *
     * <pre>
     * loader.saveArray(users, Path.of("users.json"));
     * </pre>
     *
     * @param objects llista d'objectes que es vol desar
     * @param path ruta del fitxer de sortida
     * @throws IOException si no es pot escriure el fitxer
     */
    public void saveArray(List<? extends T> objects, Path path) throws IOException {
        Objects.requireNonNull(path, "La ruta no pot ser nul·la.");
        Files.writeString(path, saveArray(objects));
    }

    /**
     * Retorna el tipus gestionat pel loader.
     *
     * @return classe objectiu del loader
     */
    protected final Class<T> targetType() {
        return targetType;
    }

    /**
     * Retorna la instància de Gson utilitzada pel loader.
     *
     * @return instància de Gson
     */
    protected final Gson gson() {
        return gson;
    }

    private Type listType() {
        return TypeToken
                .getParameterized(List.class, targetType)
                .getType();
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " no pot ser nul ni buit.");
        }
    }
}