package e.comerce.main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import e.comerce.services.database.ShopDatabase;
import e.comerce.services.stock.DatabaseRestocker;
import e.comerce.utils.input.Menu;
import e.comerce.utils.ui.Cleaner;
import e.comerce.utils.ui.Prettier;

/**
 * Gestiona el menú principal de l'aplicació.
 */
public final class MainManager {
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

    private static final int ARTICLES_OPTION = 2;
    private static final int CLIENTS_OPTION = 3;
    private static final int EXIT_OPTION = MAIN_OPTIONS.size();

    private final ShopDatabase shop;
    private final Cleaner cleaner;
    private final ArticlesManager articlesManager;
    private final ClientsManager clientsManager;

    /**
     * Inicialitza els serveis principals.
     */
    public MainManager() {
        this.cleaner = new Cleaner();

        try {
            this.shop = new ShopDatabase();
        } catch (IOException e) {
            throw new InitException("No s'ha pogut inicialitzar l'aplicació.", e);
        }

        this.articlesManager = new ArticlesManager(shop, cleaner);
        this.clientsManager = new ClientsManager(shop, cleaner);
    }

    /**
     * Mostra el menú principal.
     */
    public void run() {
        boolean loop = true;

        while (loop) {
            int option = Menu.getOption(MAIN_OPTIONS, "Menú principal");

            if (option == EXIT_OPTION) {
                loop = false;
                continue;
            }

            if (shouldClearBeforeHandling(option)) {
                cleaner.clear();
            }

            handleMainOption(option);
        }
    }

    /**
     * Indica si cal netejar abans d'executar l'opció seleccionada.
     * Les opcions que obren un submenú no ho necessiten perquè el menú es neteja
     * quan es mostra, evitant dobles neteges i parpelleigs.
     *
     * @param option opció seleccionada
     * @return true si cal netejar abans d'executar l'opció
     */
    private boolean shouldClearBeforeHandling(int option) {
        return option != ARTICLES_OPTION && option != CLIENTS_OPTION;
    }

    /**
     * Executa una opció del menú principal.
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
                articlesManager.showMenu();
                break;

            case 3:
                clientsManager.showMenu();
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
    private void importItems() {
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
     * Inicia el punt de venda.
     */
    private void tpv() {
        // TODO pendent
    }

    /**
     * Consulta vendes per client.
     */
    private void salesQueriesCustomer() {
        // TODO pendent
    }

    /**
     * Consulta vendes per article.
     */
    private void inquiriesSalesItems() {
        // TODO pendent
    }

    /**
     * Calcula els beneficis totals.
     */
    private void calculateTotalBenefits() {
        // TODO pendent
    }

    /**
     * Recompra articles automàticament.
     */
    private void automaticRepurchaseItems() {
        // TODO pendent
    }

    /**
     * Error d'inicialització de l'aplicació.
     */
    public static final class InitException extends RuntimeException {
        public InitException(String message, Exception cause) {
            super(message, cause);
        }
    }
}