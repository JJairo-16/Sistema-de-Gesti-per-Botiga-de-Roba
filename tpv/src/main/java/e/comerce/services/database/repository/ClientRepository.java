package e.comerce.services.database.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.DbExecutor;
import e.comerce.models.Client;

/** Gestiona totes les operacions de base de dades relacionades amb clients. */
public class ClientRepository {
    private final DbExecutor db;

    public ClientRepository(DbExecutor db) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
    }

    public boolean insert(Client client) throws SQLException {
        validateClient(client);
        return db.update(
                "INSERT INTO clients (dni, nom, email, telefon) VALUES (?, ?, ?, ?)",
                client.dni(),
                client.name(),
                client.email(),
                client.phone()) > 0;
    }

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

    public boolean update(Client client) throws SQLException {
        validateClient(client);
        return db.update(
                "UPDATE clients SET nom = ?, email = ?, telefon = ? WHERE dni = ?",
                client.name(),
                client.email(),
                client.phone(),
                client.dni()) > 0;
    }

    public boolean delete(String dni) throws SQLException {
        validateDni(dni);
        if ("000".equals(dni)) {
            throw new IllegalArgumentException("No es pot eliminar el client genèric");
        }

        return db.delete("DELETE FROM clients WHERE dni = ?", dni) > 0;
    }

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
     * @throws SQLException si la base de dades retorna un error
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

    public boolean exists(String dni) throws SQLException {
        validateDni(dni);
        return db.exists("SELECT 1 FROM clients WHERE dni = ?", dni);
    }

    private static void validateClient(Client client) {
        Objects.requireNonNull(client, "El client no pot ser nul");
        validateDni(client.dni());
    }

    private static void validateDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }
    }
}
