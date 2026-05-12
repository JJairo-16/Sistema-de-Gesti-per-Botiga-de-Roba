package e.comerce.utils.input;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import e.comerce.utils.ui.Cleaner;

public final class AppMenus {
    private AppMenus() {
    }

    private static final Cleaner cleaner = new Cleaner();
    private static final List<String> MAIN_OPTIONS = List.of("Importació articles.", "Gestió d'articles",
            "Gestió de clients", "TPV.", "Consultes vendes per client.", "Consultes vendes per article.",
            "Calcula els beneficis totals.", "Recompra automàtica articles.");
    private static final List<String> MANAGER_OPTIONS = List.of("Altes", "Baixes", "Mod.", "Consultes", "Tornar al menú principal.");
    private static final List<String> MANAGER_CUSTOMER_OPTIONS = List.of("Altes", "Baixes", "Mod.", "Consultes", "Tornar al menú principal.");

    public static void mainMenu() {
        boolean loop = true;

        while (loop) {
            int option = Menu.getOption(MAIN_OPTIONS, "Menu principal");

            if (option == 9) {
                loop = false;
                continue;
            }

            cleaner.clear();

            switch (option) {
                case 1:
                    importItems();
                    System.out.println();
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
    }

    public static void main(String[] args) {
        mainMenu();
    }

    public static void importItems() {
        System.out.println("Restaquejant articles, espereu si us plau...\n");
        try {
            importExceptions();
        } catch (IOException e) {
            System.out.println("Error al llegir el fitxer. Si us plau, comprova que el fitxer existeix i que el format és correcte.");
        } catch (SQLException e) {
            System.out.println("Error en la base de dades. Si us plau, torni a intentar-ho més tard.");
        } catch (Exception e) {
            System.out.println("Error inesperat. Si us plau, torni a intentar-ho més tard.");
        }

        System.out.println("Articles importats correctament."); 
    }

    private static void importExceptions() throws IOException, SQLException {

    }

    public static void itemManagement() {

        boolean loop = true;

        while (loop) {
            int option = Menu.getOption(MANAGER_OPTIONS, "Item management options");

            if (option == 5) {
                loop = false;
                continue;
            }

            cleaner.clear();

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

    }

    public static void customerManagement() {

         boolean loop = true;

        while (loop) {
            int option = Menu.getOption(MANAGER_CUSTOMER_OPTIONS, "Customer management options");

            if (option == 5) {
                loop = false;
                continue;
            }

            cleaner.clear();

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
    }

    public static void tpv() {

    }

    public static void salesQueriesCustomer() {

    }

    public static void inquiriesSalesItems() {

    }

    public static void calculateTotalBenefits() {

    }

    public static void automaticRepurchaseItems() {

    }

    public static void registerItems() {

    }

    public static void descontinueItems() {

    }

    public static void mod() {

    }

    public static void consults() {

    }

}
