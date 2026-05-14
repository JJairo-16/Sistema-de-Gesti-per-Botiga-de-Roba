package e.comerce.services.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.DbExecutor;
import e.comerce.models.Client;

/** Gestiona les operacions de base de dades relacionades amb clients. */
public class ClientRepository {
    private final DbExecutor db;

    /**
     * Crea un repositori de clients.
     *
     * @param db executor de base de dades
     */
    public ClientRepository(DbExecutor db) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
    }

    /**
     * Insereix un client.
     *
     * @param client client a inserir
     * @return {@code true} si s'ha inserit
     * @throws SQLException si falla l'operació
     */
    public boolean insert(Client client) throws SQLException {
        validateClient(client);
        return db.update(
                "INSERT INTO clients (dni, nom, email, telefon) VALUES (?, ?, ?, ?)",
                client.dni(),
                client.name(),
                client.email(),
                client.phone()) > 0;
    }

    /**
     * Insereix o actualitza un client.
     *
     * @param client client a guardar
     * @return {@code true} si s'ha inserit o actualitzat
     * @throws SQLException si falla l'operació
     */
    public boolean save(Client client) throws SQLException {
        validateClient(client);
        return db.update(
                "INSERT INTO clients (dni, nom, email, telefon) VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE nom = VALUES(nom), email = VALUES(email), telefon = VALUES(telefon)",
                client.dni(),
                client.name(),
                client.email(),
                client.phone()) > 0;
    }

    /**
     * Actualitza un client.
     *
     * @param client client amb les dades noves
     * @return {@code true} si s'ha actualitzat
     * @throws SQLException si falla l'operació
     */
    public boolean update(Client client) throws SQLException {
        validateClient(client);
        return db.update(
                "UPDATE clients SET nom = ?, email = ?, telefon = ? WHERE dni = ?",
                client.name(),
                client.email(),
                client.phone(),
                client.dni()) > 0;
    }

    /**
     * Elimina un client pel seu DNI.
     *
     * @param dni DNI del client
     * @return {@code true} si s'ha eliminat
     * @throws SQLException si falla l'operació
     */
    public boolean delete(String dni) throws SQLException {
        validateDni(dni);
        if ("000".equals(dni)) {
            throw new IllegalArgumentException("No es pot eliminar el client genèric");
        }

        return db.delete("DELETE FROM clients WHERE dni = ?", dni) > 0;
    }

    /**
     * Cerca un client pel seu DNI.
     *
     * @param dni DNI del client
     * @return client trobat o {@code null}
     * @throws SQLException si falla la consulta
     */
    public Client findByDni(String dni) throws SQLException {
        validateDni(dni);
        return db.one(
                "SELECT dni, nom, email, telefon FROM clients WHERE dni = ?",
                dni,
                rs -> new Client(
                        rs.getString("dni"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("telefon")));
    }

    /**
     * Retorna tots els clients.
     *
     * @return llista de clients
     * @throws SQLException si falla la consulta
     */
    public List<Client> findAll() throws SQLException {
        return db.list(
                "SELECT dni, nom, email, telefon FROM clients ORDER BY dni",
                rs -> new Client(
                        rs.getString("dni"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("telefon")));
    }

    /**
     * Cerca clients per DNI, nom, correu o telèfon.
     *
     * @param query text de cerca
     * @return clients trobats
     * @throws SQLException si falla la consulta
     */
    public List<Client> search(String query) throws SQLException {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String pattern = "%" + query.trim().toLowerCase() + "%";

        return db.list(
                "SELECT dni, nom, email, telefon FROM clients "
                        + "WHERE LOWER(dni) LIKE ? OR LOWER(nom) LIKE ? "
                        + "OR LOWER(email) LIKE ? OR LOWER(telefon) LIKE ? "
                        + "ORDER BY nom, dni",
                new Object[] { pattern, pattern, pattern, pattern },
                rs -> new Client(
                        rs.getString("dni"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("telefon")));
    }

    /**
     * Comprova si existeix un client.
     *
     * @param dni DNI del client
     * @return {@code true} si existeix
     * @throws SQLException si falla la consulta
     */
    public boolean exists(String dni) throws SQLException {
        validateDni(dni);
        return db.exists("SELECT 1 FROM clients WHERE dni = ?", dni);
    }

    /**
     * Valida un client.
     *
     * @param client client a validar
     */
    private static void validateClient(Client client) {
        Objects.requireNonNull(client, "El client no pot ser nul");
        validateDni(client.dni());
    }

    /**
     * Valida que el DNI no estigui buit.
     *
     * @param dni DNI a validar
     */
    private static void validateDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }
    }
}