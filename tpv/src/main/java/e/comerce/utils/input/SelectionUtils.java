package e.comerce.utils.input;

import e.comerce.utils.ui.Prettier;

/**
 * Utilitats comunes per a selectors.
 */
public final class SelectionUtils {
    private static final Getters GETTERS = new Getters();

    private SelectionUtils() {
    }

    /**
     * Demana confirmació abans de cancel·lar.
     *
     * @return true si es confirma la cancel·lació
     */
    public static boolean confirmCancel() {
        return GETTERS.getBoolean(
                "Vols cancel·lar l'operació? (s/n): ",
                false,
                "s",
                "n");
    }

    /**
     * Gestiona una entrada buida.
     *
     * @return true si s'ha de cancel·lar
     */
    public static boolean shouldCancelEmptyInput() {
        Prettier.warn("No has introduït cap valor.");
        return confirmCancel();
    }

    /**
     * Mostra l'ajuda de cancel·lació.
     */
    public static void showCancelHint() {
        Prettier.info("Deixa el camp buit si vols cancel·lar.");
    }

    /**
     * Informa de la cancel·lació.
     */
    public static void cancel() {
        Prettier.info("Operació cancel·lada.");
    }

    /**
     * Informa d'un error de base de dades.
     */
    public static void databaseError() {
        Prettier.error("Error en la base de dades. Si us plau, torni a intentar-ho més tard.");
    }

    /**
     * Informa d'un error inesperat.
     */
    public static void unexpectedError() {
        Prettier.error("Error inesperat. Si us plau, torni a intentar-ho més tard.");
    }
}