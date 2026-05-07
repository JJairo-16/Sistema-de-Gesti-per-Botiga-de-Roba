package e.comerce.utils.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Llegeix fitxers d'entorn.
 */
public final class EnvReader {

    /**
     * Evita la creació d'instàncies.
     */
    private EnvReader() {
    }

    /**
     * Llegeix un fitxer d'entorn i retorna les seves claus i valors.
     *
     * @param path ruta del fitxer
     * @return mapa amb les variables llegides
     * @throws IOException si no es pot llegir el fitxer
     */
    public static Map<String, String> read(Path path) throws IOException {
        Map<String, String> env = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int equalsIndex = line.indexOf('=');

                if (equalsIndex <= 0) {
                    continue;
                }

                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();

                value = removeQuotes(value);

                env.put(key, value);
            }
        }

        return env;
    }

    /**
     * Elimina cometes simples o dobles del valor.
     *
     * @param value valor a netejar
     * @return valor sense cometes externes
     */
    private static String removeQuotes(String value) {
        if (value.length() >= 2) {
            boolean doubleQuoted = value.startsWith("\"") && value.endsWith("\"");
            boolean singleQuoted = value.startsWith("'") && value.endsWith("'");

            if (doubleQuoted || singleQuoted) {
                return value.substring(1, value.length() - 1);
            }
        }

        return value;
    }
}