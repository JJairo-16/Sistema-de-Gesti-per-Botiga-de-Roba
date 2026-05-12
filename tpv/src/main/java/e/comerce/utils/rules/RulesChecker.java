package e.comerce.utils.rules;

import java.util.regex.Pattern;

/**
 * Centralitza les validacions de dades.
 */
public final class RulesChecker {
    private RulesChecker() {
    }

    // #region Clients

    private static final Pattern DNI_CHECKER = Pattern.compile("^\\d{8}[A-Za-z]$");
    private static final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static final int DNI_NUMBER_LENGTH = 8;

    private static final int MIN_NAME_LEN = 2;
    private static final int MAX_NAME_LEN = 20;
    private static final Pattern NAME_CHECKER = Pattern.compile(
            "^[A-Za-zÀ-ÿ]++(?:[ '-][A-Za-zÀ-ÿ]++)*+$");

    private static final int MIN_EMAIL_LEN = 5;
    private static final int MAX_EMAIL_LEN = 100;
    private static final Pattern EMAIL_CHECKER = Pattern.compile(
            "^[A-Za-z0-9._%+-]++@(?:[A-Za-z0-9-]++\\.)++[A-Za-z]{2,}$");

    private static final Pattern PHONE_CHECKER = Pattern.compile(
            "^(?:\\+34)?[6-9]\\d{8}$");

    // #endregion

    // #region Articles

    private static final int MIN_ARTICLE_ID = 1;

    private static final int MIN_ARTICLE_NAME_LEN = 2;
    private static final int MAX_ARTICLE_NAME_LEN = 40;
    private static final Pattern ARTICLE_NAME_CHECKER = Pattern.compile(
            "^[A-Za-zÀ-ÿ0-9]++(?:[ '\\-][A-Za-zÀ-ÿ0-9]++)*+$");

    private static final double MIN_BASE_PRICE = 0.0;
    private static final double MAX_BASE_PRICE = 9999.99;

    private static final int MIN_IVA = 4;
    private static final int MAX_IVA = 21;

    private static final int MIN_STOCK = 0;
    private static final int MAX_STOCK = 99999;

    private static final int MIN_NECK_SIZE = 36;
    private static final int MAX_NECK_SIZE = 52;

    private static final int MIN_CHEST_WIDTH = 10;
    private static final int MAX_CHEST_WIDTH = 15;

    private static final int MIN_PANTS_LENGTH = 32;
    private static final int MAX_PANTS_LENGTH = 46;

    private static final int MIN_WAIST_SIZE = 24;
    private static final int MAX_WAIST_SIZE = 56;

    // #endregion

    /**
     * Valida el DNI i la lletra.
     *
     * @param dni DNI a validar
     * @return resultat amb DNI normalitzat
     */
    public static CheckResult checkDni(String dni) {
        if (dni == null || dni.isBlank()) {
            return CheckResult.error("El DNI no pot estar buit.");
        }

        String normalizedDni = dni.trim().toUpperCase();

        if (!DNI_CHECKER.matcher(normalizedDni).matches()) {
            return CheckResult.error("El format del DNI no és correcte. Ha de tenir 8 números i una lletra.");
        }

        String numberPart = normalizedDni.substring(0, DNI_NUMBER_LENGTH);
        char actualLetter = normalizedDni.charAt(DNI_NUMBER_LENGTH);

        int dniNumber = Integer.parseInt(numberPart);
        char expectedLetter = DNI_LETTERS.charAt(dniNumber % DNI_LETTERS.length());

        if (actualLetter != expectedLetter) {
            return CheckResult.error("La lletra del DNI no és correcta.");
        }

        return CheckResult.ok(normalizedDni);
    }

    /**
     * Valida el nom del client.
     *
     * @param name nom a validar
     * @return resultat amb nom normalitzat
     */
    public static CheckResult checkName(String name) {
        if (name == null || name.isBlank()) {
            return CheckResult.error("El nom no pot estar buit.");
        }

        String normalizedName = normalizeSpaces(name);

        if (normalizedName.length() < MIN_NAME_LEN) {
            return CheckResult.error("El nom ha de tenir com a mínim " + MIN_NAME_LEN + " caràcters.");
        }

        if (normalizedName.length() > MAX_NAME_LEN) {
            return CheckResult.error("El nom no pot superar els " + MAX_NAME_LEN + " caràcters.");
        }

        if (!NAME_CHECKER.matcher(normalizedName).matches()) {
            return CheckResult.error("El nom només pot contenir lletres, espais, apòstrofs o guions.");
        }

        return CheckResult.ok(normalizedName);
    }

    /**
     * Valida el correu electrònic.
     *
     * @param email correu a validar
     * @return resultat amb correu normalitzat
     */
    public static CheckResult checkEmail(String email) {
        if (email == null || email.isBlank()) {
            return CheckResult.error("El correu electrònic no pot estar buit.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (normalizedEmail.length() < MIN_EMAIL_LEN) {
            return CheckResult.error("El correu electrònic és massa curt.");
        }

        if (normalizedEmail.length() > MAX_EMAIL_LEN) {
            return CheckResult.error("El correu electrònic no pot superar els " + MAX_EMAIL_LEN + " caràcters.");
        }

        if (!EMAIL_CHECKER.matcher(normalizedEmail).matches()) {
            return CheckResult.error("El format del correu electrònic no és correcte.");
        }

        return CheckResult.ok(normalizedEmail);
    }

    /**
     * Valida el número de telèfon.
     *
     * @param phone telèfon a validar
     * @return resultat amb telèfon normalitzat
     */
    public static CheckResult checkPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return CheckResult.error("El telèfon no pot estar buit.");
        }

        String normalizedPhone = phone.trim()
                .replace(" ", "")
                .replace("-", "");

        if (!PHONE_CHECKER.matcher(normalizedPhone).matches()) {
            return CheckResult.error("El telèfon ha de tenir 9 dígits i començar per 6, 7, 8 o 9.");
        }

        return CheckResult.ok(normalizedPhone);
    }

    /**
     * Valida l'identificador de l'article.
     *
     * @param id identificador a validar
     * @return resultat de la validació
     */
    public static CheckResult checkArticleId(int id) {
        if (id < MIN_ARTICLE_ID) {
            return CheckResult.error("L'identificador de l'article ha de ser positiu.");
        }

        return CheckResult.ok();
    }

    /**
     * Valida el nom de l'article.
     *
     * @param name nom a validar
     * @return resultat amb nom normalitzat
     */
    public static CheckResult checkArticleName(String name) {
        if (name == null || name.isBlank()) {
            return CheckResult.error("El nom de l'article no pot estar buit.");
        }

        String normalizedName = normalizeSpaces(name);

        if (normalizedName.length() < MIN_ARTICLE_NAME_LEN) {
            return CheckResult.error(
                    "El nom de l'article ha de tenir com a mínim " + MIN_ARTICLE_NAME_LEN + " caràcters.");
        }

        if (normalizedName.length() > MAX_ARTICLE_NAME_LEN) {
            return CheckResult.error(
                    "El nom de l'article no pot superar els " + MAX_ARTICLE_NAME_LEN + " caràcters.");
        }

        if (!ARTICLE_NAME_CHECKER.matcher(normalizedName).matches()) {
            return CheckResult.error(
                    "El nom de l'article només pot contenir lletres, números, espais, apòstrofs o guions.");
        }

        return CheckResult.ok(normalizedName);
    }

    /**
     * Valida el preu base.
     *
     * @param basePrice preu base a validar
     * @return resultat de la validació
     */
    public static CheckResult checkBasePrice(double basePrice) {
        if (!Double.isFinite(basePrice)) {
            return CheckResult.error("El preu base ha de ser un número vàlid.");
        }

        if (basePrice < MIN_BASE_PRICE) {
            return CheckResult.error("El preu base no pot ser negatiu.");
        }

        if (basePrice > MAX_BASE_PRICE) {
            return CheckResult.error("El preu base no pot superar els " + MAX_BASE_PRICE + " €.");
        }

        return CheckResult.ok();
    }

    /**
     * Valida l'IVA.
     *
     * @param iva IVA a validar
     * @return resultat de la validació
     */
    public static CheckResult checkIva(int iva) {
        if (iva < MIN_IVA || iva > MAX_IVA) {
            return CheckResult.error("L'IVA ha d'estar entre " + MIN_IVA + " i " + MAX_IVA + ".");
        }

        return CheckResult.ok();
    }

    /**
     * Valida el stock.
     *
     * @param stock stock a validar
     * @return resultat de la validació
     */
    public static CheckResult checkStock(int stock) {
        if (stock < MIN_STOCK) {
            return CheckResult.error("El stock no pot ser negatiu.");
        }

        if (stock > MAX_STOCK) {
            return CheckResult.error("El stock no pot superar les " + MAX_STOCK + " unitats.");
        }

        return CheckResult.ok();
    }

    /**
     * Valida la talla del coll.
     *
     * @param neckSize talla a validar
     * @return resultat de la validació
     */
    public static CheckResult checkNeckSize(int neckSize) {
        if (neckSize < MIN_NECK_SIZE || neckSize > MAX_NECK_SIZE) {
            return CheckResult.error(
                    "La talla del coll ha d'estar entre " + MIN_NECK_SIZE + " i " + MAX_NECK_SIZE + ".");
        }

        return CheckResult.ok();
    }

    /**
     * Valida l'amplada del pit.
     *
     * @param chestWidth amplada a validar
     * @return resultat de la validació
     */
    public static CheckResult checkChestWidth(int chestWidth) {
        if (chestWidth < MIN_CHEST_WIDTH || chestWidth > MAX_CHEST_WIDTH) {
            return CheckResult.error(
                    "L'amplada del pit ha d'estar entre " + MIN_CHEST_WIDTH + " i " + MAX_CHEST_WIDTH + ".");
        }

        return CheckResult.ok();
    }

    /**
     * Valida la llargada dels pantalons.
     *
     * @param pantsLength llargada a validar
     * @return resultat de la validació
     */
    public static CheckResult checkPantsLength(int pantsLength) {
        if (pantsLength < MIN_PANTS_LENGTH || pantsLength > MAX_PANTS_LENGTH) {
            return CheckResult.error(
                    "La llargada dels pantalons ha d'estar entre "
                            + MIN_PANTS_LENGTH + " i " + MAX_PANTS_LENGTH + ".");
        }

        return CheckResult.ok();
    }

    /**
     * Valida la talla de cintura.
     *
     * @param waistSize talla a validar
     * @return resultat de la validació
     */
    public static CheckResult checkWaistSize(int waistSize) {
        if (waistSize < MIN_WAIST_SIZE || waistSize > MAX_WAIST_SIZE) {
            return CheckResult.error(
                    "La talla de cintura ha d'estar entre " + MIN_WAIST_SIZE + " i " + MAX_WAIST_SIZE + ".");
        }

        return CheckResult.ok();
    }

    /**
     * Normalitza els espais interns.
     *
     * @param value text a normalitzar
     * @return text normalitzat
     */
    private static String normalizeSpaces(String value) {
        return value.trim().replaceAll("\\s++", " ");
    }

    /**
     * Resultat d'una validació.
     *
     * @param result     indica si és correcte
     * @param msg        missatge d'error
     * @param normalized valor normalitzat
     */
    public record CheckResult(boolean result, String msg, String normalized) {
        public static CheckResult ok() {
            return new CheckResult(true, "", null);
        }

        public static CheckResult ok(String normalized) {
            return new CheckResult(true, "", normalized);
        }

        public static CheckResult error(String msg) {
            return new CheckResult(false, msg, null);
        }
    }
}