package e.comerce.utils.input;

import java.util.List;

import e.comerce.models.Client;
import e.comerce.models.articles.Article;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;
import e.comerce.utils.rules.RulesChecker;
import e.comerce.utils.rules.RulesChecker.CheckResult;
import e.comerce.utils.ui.Prettier;

/**
 * Crea objectes demanant dades per consola.
 */
public final class ObjectInput {
    private static final Getters GETTERS = new Getters();

    private static final List<String> ARTICLE_TYPES = List.of(
            "Camisa",
            "Pantalons",
            "Cancel·lar");

    private static final int SHIRT_OPTION = 1;
    private static final int PANTS_OPTION = 2;
    private static final int CANCEL_OPTION = 3;

    private ObjectInput() {
    }

    /**
     * Demana les dades d'un client.
     *
     * @return client creat o null si es cancel·la
     */
    public static Client askClient() {
        showCancelHint();

        String dni = askValidatedString("DNI: ", "DNI", RulesChecker::checkDni);
        if (dni == null) {
            return null;
        }

        String name = askValidatedString("Nom: ", "Nom", RulesChecker::checkName);
        if (name == null) {
            return null;
        }

        String email = askValidatedString("Correu electrònic: ", "Correu electrònic", RulesChecker::checkEmail);
        if (email == null) {
            return null;
        }

        String phone = askValidatedString("Telèfon: ", "Telèfon", RulesChecker::checkPhone);
        if (phone == null) {
            return null;
        }

        return new Client(dni, name, email, phone);
    }

    /**
     * Demana el tipus i les dades d'un article.
     *
     * @return article creat o null si es cancel·la
     */
    public static Article askArticle() {
        int option = Menu.getOption(ARTICLE_TYPES, "Tipus d'article");

        return switch (option) {
            case SHIRT_OPTION -> askShirt();
            case PANTS_OPTION -> askPants();
            case CANCEL_OPTION -> {
                cancel();
                yield null;
            }
            default -> {
                invalidOption();
                yield null;
            }
        };
    }

    /**
     * Demana les dades d'una camisa.
     *
     * @return camisa creada o null si es cancel·la
     */
    public static Shirt askShirt() {
        showCancelHint();

        Integer id = askValidatedInteger("Identificador: ", "Identificador", RulesChecker::checkArticleId);
        if (id == null) {
            return null;
        }

        String name = askValidatedString("Nom de l'article: ", "Nom de l'article", RulesChecker::checkArticleName);
        if (name == null) {
            return null;
        }

        Integer neckSize = askValidatedInteger("Talla del coll: ", "Talla del coll", RulesChecker::checkNeckSize);
        if (neckSize == null) {
            return null;
        }

        Integer chestWidth = askValidatedInteger("Amplada del pit: ", "Amplada del pit", RulesChecker::checkChestWidth);
        if (chestWidth == null) {
            return null;
        }

        Double basePrice = askValidatedDouble("Preu base: ", "Preu base", RulesChecker::checkBasePrice);
        if (basePrice == null) {
            return null;
        }

        Integer iva = askValidatedInteger("IVA: ", "IVA", RulesChecker::checkIva);
        if (iva == null) {
            return null;
        }

        Integer stock = askValidatedInteger("Stock: ", "Stock", RulesChecker::checkStock);
        if (stock == null) {
            return null;
        }

        return new Shirt(id, name, neckSize, chestWidth, basePrice, iva, stock);
    }

    /**
     * Demana les dades d'uns pantalons.
     *
     * @return pantalons creats o null si es cancel·la
     */
    public static Pants askPants() {
        showCancelHint();

        Integer id = askValidatedInteger("Identificador: ", "Identificador", RulesChecker::checkArticleId);
        if (id == null) {
            return null;
        }

        String name = askValidatedString("Nom de l'article: ", "Nom de l'article", RulesChecker::checkArticleName);
        if (name == null) {
            return null;
        }

        Integer pantsLength = askValidatedInteger(
                "Llargada dels pantalons: ",
                "Llargada dels pantalons",
                RulesChecker::checkPantsLength);
        if (pantsLength == null) {
            return null;
        }

        Integer waistSize = askValidatedInteger("Talla de cintura: ", "Talla de cintura", RulesChecker::checkWaistSize);
        if (waistSize == null) {
            return null;
        }

        Double basePrice = askValidatedDouble("Preu base: ", "Preu base", RulesChecker::checkBasePrice);
        if (basePrice == null) {
            return null;
        }

        Integer iva = askValidatedInteger("IVA: ", "IVA", RulesChecker::checkIva);
        if (iva == null) {
            return null;
        }

        Integer stock = askValidatedInteger("Stock: ", "Stock", RulesChecker::checkStock);
        if (stock == null) {
            return null;
        }

        return new Pants(id, name, pantsLength, waistSize, basePrice, iva, stock);
    }

    /**
     * Demana un text i aplica una validació.
     *
     * @param prompt    missatge d'entrada
     * @param name      nom del camp
     * @param validator validador
     * @return text normalitzat o null si es cancel·la
     */
    private static String askValidatedString(String prompt, String name, StringValidator validator) {
        while (true) {
            String value = GETTERS.getStringAllowEmpty(prompt, name);

            if (value.isEmpty()) {
                cancel();
                return null;
            }

            CheckResult result = validator.validate(value);

            if (result.result()) {
                return result.normalized();
            }

            Prettier.warn(result.msg());
        }
    }

    /**
     * Demana un enter i aplica una validació.
     *
     * @param prompt    missatge d'entrada
     * @param name      nom del camp
     * @param validator validador
     * @return enter validat o null si es cancel·la
     */
    private static Integer askValidatedInteger(String prompt, String name, IntegerValidator validator) {
        while (true) {
            String input = GETTERS.getStringAllowEmpty(prompt, name);

            if (input.isEmpty()) {
                cancel();
                return null;
            }

            try {
                int value = Integer.parseInt(input);
                CheckResult result = validator.validate(value);

                if (result.result()) {
                    return value;
                }

                Prettier.warn(result.msg());
            } catch (NumberFormatException e) {
                Prettier.warn("%s ha de ser un nombre enter.", name);
            }
        }
    }

    /**
     * Demana un decimal i aplica una validació.
     *
     * @param prompt    missatge d'entrada
     * @param name      nom del camp
     * @param validator validador
     * @return decimal validat o null si es cancel·la
     */
    private static Double askValidatedDouble(String prompt, String name, DoubleValidator validator) {
        while (true) {
            String input = GETTERS.getStringAllowEmpty(prompt, name).replace(',', '.');

            if (input.isEmpty()) {
                cancel();
                return null;
            }

            try {
                double value = Double.parseDouble(input);
                CheckResult result = validator.validate(value);

                if (result.result()) {
                    return value;
                }

                Prettier.warn(result.msg());
            } catch (NumberFormatException e) {
                Prettier.warn("%s ha de ser un nombre decimal.", name);
            }
        }
    }

    /**
     * Mostra com cancel·lar l'operació.
     */
    private static void showCancelHint() {
        Prettier.info("Prem Enter sense escriure res per cancel·lar.");
    }

    /**
     * Informa de la cancel·lació.
     */
    private static void cancel() {
        Prettier.info("Operació cancel·lada.");
    }

    /**
     * Informa d'una opció no vàlida.
     */
    private static void invalidOption() {
        Prettier.warn("Opció no vàlida.");
    }

    /**
     * Valida textos.
     */
    @FunctionalInterface
    private interface StringValidator {
        CheckResult validate(String value);
    }

    /**
     * Valida enters.
     */
    @FunctionalInterface
    private interface IntegerValidator {
        CheckResult validate(int value);
    }

    /**
     * Valida decimals.
     */
    @FunctionalInterface
    private interface DoubleValidator {
        CheckResult validate(double value);
    }
}