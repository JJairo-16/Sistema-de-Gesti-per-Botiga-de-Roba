package e.comerce.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    public record Client(String dni, String nom, String email, String telefon) {}

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/botiga", "root", "");
    }

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

    public void eliminar(String dni) throws SQLException {
        if (dni.equals("000")) throw new SQLException("No es pot eliminar el client genèric.");
        String sql = "DELETE FROM clients WHERE dni = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.executeUpdate();
        }
    }

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