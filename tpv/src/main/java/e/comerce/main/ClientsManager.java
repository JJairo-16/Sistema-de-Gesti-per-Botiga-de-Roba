package e.comerce.main;

import java.sql.SQLException;
import java.util.List;

import e.comerce.models.Client;
import e.comerce.services.database.ShopDatabase;
import e.comerce.services.database.repository.ClientRepository;
import e.comerce.utils.input.Menu;
import e.comerce.utils.input.ObjectInput;
import e.comerce.utils.input.selector.ClientSelector;
import e.comerce.utils.ui.Cleaner;
import e.comerce.utils.ui.Prettier;

/**
 * Gestiona el menú de clients.
 */
public final class ClientsManager {
    private static final List<String> CLIENT_OPTIONS = List.of(
            "Altes",
            "Baixes",
            "Mod.",
            "Consultes",
            "Tornar al menú principal.");

    private static final int EXIT_OPTION = CLIENT_OPTIONS.size();

    private final ClientRepository repo;
    private final ClientSelector selector;
    private final Cleaner cleaner;

    /**
     * Inicialitza el gestor de clients.
     *
     * @param shop    base de dades
     * @param cleaner netejador de pantalla
     */
    public ClientsManager(ShopDatabase shop, Cleaner cleaner) {
        this.repo = shop.clients();
        this.selector = new ClientSelector(shop);
        this.cleaner = cleaner;
    }

    /**
     * Mostra el menú de clients.
     */
    public void showMenu() {
        boolean loop = true;

        while (loop) {
            int option = Menu.getOption(CLIENT_OPTIONS, "Gestió de clients");

            if (option == EXIT_OPTION) {
                loop = false;
                continue;
            }

            cleaner.clear();
            handleOption(option);
        }
    }

    /**
     * Executa una opció de clients.
     *
     * @param option opció seleccionada
     */
    private void handleOption(int option) {
        switch (option) {
            case 1:
                registerClient();
                Menu.pause();
                break;

            case 2:
                deleteClient();
                Menu.pause();
                break;

            case 3:
                modifyClient();
                Menu.pause();
                break;

            case 4:
                consultClients();
                Menu.pause();
                break;

            default:
                Prettier.warn("Opció no vàlida.");
                break;
        }
    }

    /**
     * Dona d'alta un client.
     */
    private void registerClient() {
        Prettier.info("Si us plau, ompli les dades del nou client:");

        Client client = ObjectInput.askClient();

        if (client == null) {
            return;
        }

        try {
            if (repo.findByDni(client.dni()) != null) {
                Prettier.warn("Ja existeix un client amb aquest DNI.");
                return;
            }

            boolean inserted = repo.insert(client);

            if (inserted) {
                Prettier.info("S'ha afegit el nou client a la base de dades.");
            } else {
                Prettier.warn("No s'ha pogut afegir el client.");
            }
        } catch (SQLException e) {
            databaseError();
        } catch (IllegalArgumentException e) {
            Prettier.warn(e.getMessage());
        } catch (Exception e) {
            unexpectedError();
        }
    }

    /**
     * Dona de baixa un client.
     */
    private void deleteClient() {
        Client client = selector.askClient();

        if (client == null) {
            return;
        }

        try {
            boolean deleted = repo.delete(client.dni());

            if (deleted) {
                Prettier.info("Client eliminat correctament.");
            } else {
                Prettier.warn("No s'ha trobat el client a la base de dades.");
            }
        } catch (SQLException e) {
            databaseError();
        } catch (IllegalArgumentException e) {
            Prettier.warn(e.getMessage());
        } catch (Exception e) {
            unexpectedError();
        }
    }

    /**
     * Modifica un client.
     */
    private void modifyClient() {
        Client oldClient = selector.askClient();

        if (oldClient == null) {
            return;
        }

        Prettier.info("Introdueix les noves dades del client.");
        Prettier.info("El DNI s'ha de mantenir igual: %s", oldClient.dni());

        Client newClient = ObjectInput.askClient();

        if (newClient == null) {
            return;
        }

        if (!oldClient.dni().equals(newClient.dni())) {
            Prettier.warn("No es pot modificar el DNI del client en aquesta operació.");
            return;
        }

        try {
            boolean updated = repo.update(newClient);

            if (updated) {
                Prettier.info("Client modificat correctament.");
            } else {
                Prettier.warn("No s'ha pogut modificar el client.");
            }
        } catch (SQLException e) {
            databaseError();
        } catch (IllegalArgumentException e) {
            Prettier.warn(e.getMessage());
        } catch (Exception e) {
            unexpectedError();
        }
    }

    /**
     * Consulta clients.
     */
    private void consultClients() {
        Client client = selector.askClient();

        if (client == null) {
            return;
        }

        Prettier.info("Client seleccionat:");
        System.out.println(formatClient(client));
    }

    /**
     * Formata les dades d'un client.
     *
     * @param client client a mostrar
     * @return text formatat
     */
    private String formatClient(Client client) {
        return String.format(
                "DNI: %s%nNom: %s%nCorreu electrònic: %s%nTelèfon: %s",
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

    /**
     * Informa d'un error de base de dades.
     */
    private void databaseError() {
        Prettier.error("Error en la base de dades. Si us plau, torni a intentar-ho més tard.");
    }

    /**
     * Informa d'un error inesperat.
     */
    private void unexpectedError() {
        Prettier.error("Error inesperat. Si us plau, torni a intentar-ho més tard.");
    }
}