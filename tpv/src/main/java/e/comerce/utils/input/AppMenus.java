package e.comerce.utils.input;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import e.comerce.services.database.ShopDatabase;
import e.comerce.services.stock.DatabaseRestocker;
import e.comerce.utils.ui.Cleaner;
import e.comerce.utils.ui.Prettier;

/**
 * Gestiona els menús principals de l'aplicació.
 */
public final class AppMenus {
    private static final int MAIN_EXIT_OPTION = 9;
    private static final int MANAGER_EXIT_OPTION = 5;

    private static final List<String> MAIN_OPTIONS = List.of(
            "Importació articles.",
            "Gestió d'articles",
            "Gestió de clients",
            "TPV.",
            "Consultes vendes per client.",
            "Consultes vendes per article.",
            "Calcula els beneficis totals.",
            "Recompra automàtica articles.",
            "Sortir");

    private static final List<String> MANAGER_OPTIONS = List.of(
            "Altes",
            "Baixes",
            "Mod.",
            "Consultes",
            "Tornar al menú principal.");

    private static final List<String> MANAGER_CUSTOMER_OPTIONS = List.of(
            "Altes",
            "Baixes",
            "Mod.",
            "Consultes",
            "Tornar al menú principal.");

    private final ShopDatabase shop;
    private final Cleaner cleaner;

    /**
     * Inicialitza el menú i la connexió amb la base de dades.
     */
    public AppMenus() {
        this.cleaner = new Cleaner();

        try {
            this.shop = new ShopDatabase();
        } catch (IOException e) {
            throw new InitException("No s'ha pogut inicialitzar l'aplicació.", e);
        }
    }

    /**
     * Mostra el menú principal.
     */
    public void mainMenu() {
        boolean loop = true;

        while (loop) {
            int option = Menu.getOption(MAIN_OPTIONS, "Menú principal");

            if (option == MAIN_EXIT_OPTION) {
                loop = false;
                continue;
            }

            cleaner.clear();
            handleMainOption(option);
        }
    }

    /**
     * Executa l'opció triada al menú principal.
     *
     * @param option opció seleccionada
     */
    private void handleMainOption(int option) {
        switch (option) {
            case 1:
                importItems();
                Menu.pause();
                break;

            case 2:
                itemManagement();
                break;

            case 3:
                customerManagement();
                break;

            case 4:
                tpv();
                Menu.pause();
                break;

            case 5:
                salesQueriesCustomer();
                Menu.pause();
                break;

            case 6:
                inquiriesSalesItems();
                Menu.pause();
                break;

            case 7:
                calculateTotalBenefits();
                Menu.pause();
                break;

            case 8:
                automaticRepurchaseItems();
                Menu.pause();
                break;

            default:
                break;
        }
    }

    /**
     * Importa articles a la base de dades.
     */
    public void importItems() {
        Prettier.info("Restaquejant articles, espereu si us plau...");

        try {
            DatabaseRestocker.restock(shop);
            Prettier.info("Articles importats correctament.");
        } catch (IOException e) {
            Prettier.error(
                    "Error al llegir el fitxer. Si us plau, comprova que el fitxer existeix i que el format és correcte.");
        } catch (SQLException e) {
            Prettier.error("Error en la base de dades. Si us plau, torni a intentar-ho més tard.");
        } catch (Exception e) {
            Prettier.error("Error inesperat. Si us plau, torni a intentar-ho més tard.");
        }
    }

    /**
     * Mostra el menú de gestió d'articles.
     */
    public void itemManagement() {
        managementMenu(MANAGER_OPTIONS, "Opcions de gestió d'articles");
    }

    /**
     * Mostra el menú de gestió de clients.
     */
    public void customerManagement() {
        managementMenu(MANAGER_CUSTOMER_OPTIONS, "Opcions de gestió de clients");
    }

    /**
     * Mostra un menú de gestió genèric.
     *
     * @param options opcions del menú
     * @param title   títol del menú
     */
    private void managementMenu(List<String> options, String title) {
        boolean loop = true;

        while (loop) {
            int option = Menu.getOption(options, title);

            if (option == MANAGER_EXIT_OPTION) {
                loop = false;
                continue;
            }

            cleaner.clear();
            handleManagementOption(option);
        }
    }

    /**
     * Executa l'opció triada en un menú de gestió.
     *
     * @param option opció seleccionada
     */
    private void handleManagementOption(int option) {
        switch (option) {
            case 1:
                registerItems();
                Menu.pause();
                break;

            case 2:
                descontinueItems();
                Menu.pause();
                break;

            case 3:
                mod();
                Menu.pause();
                break;

            case 4:
                consults();
                Menu.pause();
                break;

            default:
                break;
        }
    }

    /**
     * Inicia el punt de venda.
     */
    public void tpv() { // ? pendent

    }

    /**
     * Consulta vendes per client.
     */
    public void salesQueriesCustomer() { // ? pendent

    }

    /**
     * Consulta vendes per article.
     */
    public void inquiriesSalesItems() { // ? pendent

    }

    /**
     * Calcula els beneficis totals.
     */
    public void calculateTotalBenefits() { // ? pendent

    }

    /**
     * Recompra articles automàticament.
     */
    public void automaticRepurchaseItems() { // ? pendent

    }

    /**
     * Dona d'alta articles.
     */
    public void registerItems() { // ? pendent

    }

    /**
     * Dona de baixa articles.
     */
    public void descontinueItems() { // ? pendent

    }

    /**
     * Modifica dades.
     */
    public void mod() { // ? pendent

    }

    /**
     * Mostra consultes.
     */
    public void consults() { // ? pendent

    }

    public class InitException extends RuntimeException {
        public InitException(String msg, Exception e) {
            super(msg, e);
        }
    }
}
