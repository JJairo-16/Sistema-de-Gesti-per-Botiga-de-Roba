package e.comerce.utils.input;

import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import e.comerce.models.ArticleFamily;
import e.comerce.models.Client;
import e.comerce.models.articles.Article;
import e.comerce.models.articles.GenericArticle;
import e.comerce.models.articles.Pants;
import e.comerce.models.articles.Shirt;
import e.comerce.services.database.repository.ArticleFamilyRepository;
import e.comerce.utils.rules.RulesChecker;
import e.comerce.utils.rules.RulesChecker.CheckResult;
import e.comerce.utils.ui.Prettier;

/** Crea objectes demanant dades per consola. */
public final class ObjectInput {
    private final Getters getters;
    private final ArticleFamilyRepository familyRepository;

    /**
     * Crea un lector d'objectes.
     *
     * @param familyRepository repositori de famílies
     */
    public ObjectInput(ArticleFamilyRepository familyRepository) {
        this.getters = new Getters();
        this.familyRepository = familyRepository;
    }

    /**
     * Demana les dades d'un client.
     *
     * @return client creat o {@code null} si es cancel·la
     */
    public Client askClient() {
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
     * @return article creat o {@code null} si es cancel·la
     * @throws SQLException si falla la consulta de famílies
     */
    public Article askArticle() throws SQLException {
        List<ArticleFamily> families = familyRepository.findAll();

        if (families.isEmpty()) {
            Prettier.warn("No hi ha famílies d'articles a la base de dades.");
            return null;
        }

        List<String> options = new ArrayList<>();

        for (ArticleFamily family : families) {
            options.add(family.name());
        }

        options.add("Cancel·lar");

        int option = Menu.getOption(options, "Tipus d'article");

        if (option == options.size()) {
            cancel();
            return null;
        }

        if (option < 1 || option > families.size()) {
            invalidOption();
            return null;
        }

        String family = families.get(option - 1).name();

        if (isShirt(family)) {
            return askShirt(family);
        }

        if (isPants(family)) {
            return askPants();
        }

        return askGenericArticle(family);
    }

    /**
     * Demana les dades d'una camisa.
     *
     * @param family família de l'article
     * @return camisa creada o {@code null} si es cancel·la
     */
    public Shirt askShirt(String family) {
        showCancelHint();

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

        return new Shirt(-1, name, family, neckSize, chestWidth, basePrice, iva, stock);
    }

    /**
     * Demana les dades d'uns pantalons.
     *
     * @return pantalons creats o {@code null} si es cancel·la
     */
    public Pants askPants() {
        showCancelHint();

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

        return new Pants(-1, name, pantsLength, waistSize, basePrice, iva, stock);
    }

    /**
     * Demana les dades d'un article genèric.
     *
     * @param family família de l'article
     * @return article creat o {@code null} si es cancel·la
     */
    public GenericArticle askGenericArticle(String family) {
        showCancelHint();

        String name = askValidatedString("Nom de l'article: ", "Nom de l'article", RulesChecker::checkArticleName);
        if (name == null) {
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

        return new GenericArticle(-1, name, family, basePrice, iva, stock);
    }

    /**
     * Demana un text validat.
     *
     * @param prompt missatge d'entrada
     * @param name nom del camp
     * @param validator validador
     * @return text normalitzat o {@code null} si es cancel·la
     */
    private String askValidatedString(String prompt, String name, StringValidator validator) {
        while (true) {
            String value = getters.getStringAllowEmpty(prompt, name);

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
     * Demana un enter validat.
     *
     * @param prompt missatge d'entrada
     * @param name nom del camp
     * @param validator validador
     * @return enter validat o {@code null} si es cancel·la
     */
    private Integer askValidatedInteger(String prompt, String name, IntegerValidator validator) {
        while (true) {
            String input = getters.getStringAllowEmpty(prompt, name);

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
     * Demana un decimal validat.
     *
     * @param prompt missatge d'entrada
     * @param name nom del camp
     * @param validator validador
     * @return decimal validat o {@code null} si es cancel·la
     */
    private Double askValidatedDouble(String prompt, String name, DoubleValidator validator) {
        while (true) {
            String input = getters.getStringAllowEmpty(prompt, name).replace(',', '.');

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
     * Comprova si una família és de camises.
     *
     * @param family família a comprovar
     * @return {@code true} si és camisa
     */
    private static boolean isShirt(String family) {
        return normalize(family).equals("camisa");
    }

    /**
     * Comprova si una família és de pantalons.
     *
     * @param family família a comprovar
     * @return {@code true} si és pantalons
     */
    private static boolean isPants(String family) {
        String normalizedFamily = normalize(family);
        return normalizedFamily.equals("pantalo")
                || normalizedFamily.equals("pantalons")
                || normalizedFamily.equals("pantalon")
                || normalizedFamily.equals("pantalones");
    }

    /**
     * Normalitza un text per comparar-lo.
     *
     * @param value text original
     * @return text normalitzat
     */
    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    /** Mostra com cancel·lar l'operació. */
    private static void showCancelHint() {
        Prettier.info("Prem Enter sense escriure res per cancel·lar.");
    }

    /** Informa de la cancel·lació. */
    private static void cancel() {
        Prettier.info("Operació cancel·lada.");
    }

    /** Informa d'una opció no vàlida. */
    private static void invalidOption() {
        Prettier.warn("Opció no vàlida.");
    }

    /** Valida textos. */
    @FunctionalInterface
    private interface StringValidator {
        CheckResult validate(String value);
    }

    /** Valida enters. */
    @FunctionalInterface
    private interface IntegerValidator {
        CheckResult validate(int value);
    }

    /** Valida decimals. */
    @FunctionalInterface
    private interface DoubleValidator {
        CheckResult validate(double value);
    }
}