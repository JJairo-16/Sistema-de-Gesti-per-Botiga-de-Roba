package e.comerce.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe encarregada de gestionar les operacions CRUD dels clients a la base de dades.
 */
public class ClientManager {

    /**
     * Representa l'entitat Client.
     * 
     * @param dni Clau primària del client.
     * @param nom Nom de la persona o empresa.
     * @param email Correu electrònic de contacte.
     * @param telefon Telèfon de contacte.
     */
    public record Client(String dni, String nom, String email, String telefon) {}

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/botiga", "root", "");
    }

    /**
     * Insereix un nou client a la base de dades.
     * 
     * @param client L'objecte Client a registrar.
     * @throws SQLException Si hi ha un error en l'execució SQL.
     */
    public void insertar(Client client) throws SQLException {
        String sql = "INSERT INTO clients (dni, nom, email, telefon) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, client.dni());
            ps.setString(2, client.nom());
            ps.setString(3, client.email());
            ps.setString(4, client.telefon());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un client de la base de dades segons el seu DNI.
     * No permet eliminar el client genèric amb codi "000".
     * 
     * @param dni El DNI del client a esborrar.
     * @throws SQLException Si el DNI és "000" o hi ha un error de base de dades.
     */
    public void eliminar(String dni) throws SQLException {
        if (dni.equals("000")) throw new SQLException("No es pot eliminar el client genèric.");
        String sql = "DELETE FROM clients WHERE dni = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.executeUpdate();
        }
    }

    /**
     * Actualitza les dades d'un client existent.
     * 
     * @param client L'objecte Client amb les dades actualitzades.
     * @throws SQLException Si hi ha un error en l'execució SQL.
     */
    public void modificar(Client client) throws SQLException {
        String sql = "UPDATE clients SET nom = ?, email = ?, telefon = ? WHERE dni = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, client.nom());
            ps.setString(2, client.email());
            ps.setString(3, client.telefon());
            ps.setString(4, client.dni());
            ps.executeUpdate();
        }
    }

    /**
     * Cerca un client a la base de dades pel seu DNI.
     * 
     * @param dni El DNI a cercar.
     * @return L'objecte Client si es troba, null en cas contrari.
     * @throws SQLException Si hi ha un error en l'execució SQL.
     */
    public Client buscarPerDni(String dni) throws SQLException {
        String sql = "SELECT * FROM clients WHERE dni = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Client(
                    rs.getString("dni"), 
                    rs.getString("nom"), 
                    rs.getString("email"), 
                    rs.getString("telefon")
                );
            }
        }
        return null;
    }

    /**
     * Obté una llista de tots els clients registrats.
     * 
     * @return Llista d'objectes Client.
     * @throws SQLException Si hi ha un error en l'execució SQL.
     */
    public List<Client> llistarTots() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(new Client(
                    rs.getString("dni"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("telefon")
                ));
            }
        }
        return clients;
    }
}