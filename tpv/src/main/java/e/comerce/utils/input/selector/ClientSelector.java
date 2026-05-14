package e.comerce.utils.input.selector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import e.comerce.models.Client;
import e.comerce.services.database.ShopDatabase;
import e.comerce.services.database.repository.ClientRepository;
import e.comerce.utils.input.Getters;
import e.comerce.utils.input.Menu;
import e.comerce.utils.input.SelectionUtils;
import e.comerce.utils.rules.RulesChecker;
import e.comerce.utils.rules.RulesChecker.CheckResult;
import e.comerce.utils.ui.Prettier;

/**
 * Cerca i selecciona clients de la base de dades.
 */
public final class ClientSelector {
    private static final Getters GETTERS = new Getters();

    private static final List<String> SELECT_OPTIONS = List.of(
            "Introduir DNI",
            "Cercar client",
            "Mostrar tots els clients",
            "Cancel·lar");

    private static final int DNI_OPTION = 1;
    private static final int SEARCH_OPTION = 2;
    private static final int SHOW_ALL_OPTION = 3;
    private static final int CANCEL_OPTION = 4;

    private final ClientRepository clients;

    /**
     * Inicialitza el selector amb la base de dades.
     *
     * @param shop base de dades
     */
    public ClientSelector(ShopDatabase shop) {
        this.clients = shop.clients();
    }

    /**
     * Selecciona un client i retorna el seu DNI.
     *
     * @return DNI seleccionat o null si es cancel·la
     */
    public String askClientDni() {
        Client client = askClient();
        return client == null ? null : client.dni();
    }

    /**
     * Selecciona un client.
     *
     * @return client seleccionat o null si es cancel·la
     */
    public Client askClient() {
        while (true) {
            int option = Menu.getOption(SELECT_OPTIONS, "Seleccionar client");

            switch (option) {
                case DNI_OPTION:
                    return askClientByDni();

                case SEARCH_OPTION:
                    return searchClient();

                case SHOW_ALL_OPTION:
                    return showAllClients();

                case CANCEL_OPTION:
                    if (SelectionUtils.confirmCancel()) {
                        SelectionUtils.cancel();
                        return null;
                    }
                    break;

                default:
                    Prettier.warn("Opció no vàlida.");
                    break;
            }
        }
    }

    /**
     * Demana un DNI i comprova que existeixi.
     *
     * @return client trobat o null si es cancel·la
     */
    private Client askClientByDni() {
        printSearchHeader("Introducció directa de DNI");
        SelectionUtils.showCancelHint();

        while (true) {
            String dni = GETTERS.getStringAllowEmpty("DNI del client: ", "DNI");

            if (dni.isEmpty()) {
                if (SelectionUtils.shouldCancelEmptyInput()) {
                    SelectionUtils.cancel();
                    return null;
                }

                continue;
            }

            CheckResult result = RulesChecker.checkDni(dni);

            if (!result.result()) {
                Prettier.warn(result.msg());
                continue;
            }

            try {
                Client client = clients.findByDni(result.normalized());

                if (client == null) {
                    Prettier.warn("No s'ha trobat cap client amb aquest DNI.");
                    Prettier.info("Pots utilitzar l'opció de cerca si no recordes el DNI complet.");
                    continue;
                }

                showSelectedClient(client);
                return client;
            } catch (SQLException e) {
                SelectionUtils.databaseError();
                return null;
            } catch (Exception e) {
                SelectionUtils.unexpectedError();
                return null;
            }
        }
    }

    /**
     * Cerca clients a la base de dades.
     *
     * @return client seleccionat o null si es cancel·la
     */
    private Client searchClient() {
        printSearchHeader("Cerca de client");
        Prettier.info("Pots cercar per DNI parcial, nom, correu o telèfon.");
        SelectionUtils.showCancelHint();

        while (true) {
            String query = GETTERS.getStringAllowEmpty("Cerca: ", "Cerca");

            if (query.isEmpty()) {
                if (SelectionUtils.shouldCancelEmptyInput()) {
                    SelectionUtils.cancel();
                    return null;
                }

                continue;
            }

            try {
                List<Client> matches = clients.search(query);

                if (matches.isEmpty()) {
                    Prettier.warn("No s'ha trobat cap client amb aquesta cerca.");
                    Prettier.info("Prova amb una altra dada del client.");
                    continue;
                }

                return chooseFromList(matches, "Clients trobats");
            } catch (SQLException e) {
                SelectionUtils.databaseError();
                return null;
            } catch (Exception e) {
                SelectionUtils.unexpectedError();
                return null;
            }
        }
    }

    /**
     * Mostra tots els clients.
     *
     * @return client seleccionat o null si es cancel·la
     */
    private Client showAllClients() {
        printSearchHeader("Llistat de clients");

        try {
            List<Client> allClients = clients.findAll();

            if (allClients.isEmpty()) {
                Prettier.warn("No hi ha clients registrats.");
                return null;
            }

            return chooseFromList(allClients, "Tots els clients");
        } catch (SQLException e) {
            SelectionUtils.databaseError();
            return null;
        } catch (Exception e) {
            SelectionUtils.unexpectedError();
            return null;
        }
    }

    /**
     * Permet seleccionar un client d'una llista.
     *
     * @param foundClients clients trobats
     * @param title        títol del menú
     * @return client seleccionat o null si es cancel·la
     */
    private Client chooseFromList(List<Client> foundClients, String title) {
        List<String> options = new ArrayList<>();

        for (Client client : foundClients) {
            options.add(formatClient(client));
        }

        options.add("Cancel·lar");

        int option = Menu.getOption(options, title);

        if (option == options.size()) {
            if (SelectionUtils.confirmCancel()) {
                SelectionUtils.cancel();
                return null;
            }

            return chooseFromList(foundClients, title);
        }

        if (option < 1 || option > foundClients.size()) {
            Prettier.warn("Opció no vàlida.");
            return null;
        }

        Client selected = foundClients.get(option - 1);
        showSelectedClient(selected);
        return selected;
    }

    /**
     * Mostra una capçalera simple.
     *
     * @param title títol
     */
    private void printSearchHeader(String title) {
        System.out.println();
        Prettier.info("=== " + title + " ===");
        System.out.println();
    }

    /**
     * Mostra el client seleccionat.
     *
     * @param client client seleccionat
     */
    private void showSelectedClient(Client client) {
        System.out.println();
        Prettier.info("Client seleccionat:");
        System.out.println(formatClient(client));
    }

    /**
     * Formata un client per mostrar-lo.
     *
     * @param client client
     * @return text del client
     */
    private String formatClient(Client client) {
        return String.format(
                "%s - %s - %s - %s",
                safe(client.dni()),
                safe(client.nom()),
                safe(client.email()),
                safe(client.telefon()));
    }

    /**
     * Evita valors null.
     *
     * @param value text
     * @return text segur
     */
    private String safe(String value) {
        return value == null ? "" : value;
    }
}