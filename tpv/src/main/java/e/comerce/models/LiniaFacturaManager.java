package e.comerce.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LiniaFacturaManager {

    public record LiniaFactura(
        int idTiquet,
        int idArticle,
        int quantitat,
        double preuBase,
        double iva,
        double preuFinal
    ) {}

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/botiga", "root", "");
    }

    public void insertar(LiniaFactura linia) throws SQLException {
        String sql = "INSERT INTO linies_factura (id_tiquet, id_article, quantitat, preu_base, iva, preu_final) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, linia.idTiquet());
            ps.setInt(2, linia.idArticle());
            ps.setInt(3, linia.quantitat());
            ps.setDouble(4, linia.preuBase());
            ps.setDouble(5, linia.iva());
            ps.setDouble(6, linia.preuFinal());
            ps.executeUpdate();
        }
    }

    public List<LiniaFactura> llistarPerTiquet(int idTiquet) throws SQLException {
        List<LiniaFactura> linies = new ArrayList<>();
        String sql = "SELECT * FROM linies_factura WHERE id_tiquet = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idTiquet);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                linies.add(new LiniaFactura(
                    rs.getInt("id_tiquet"),
                    rs.getInt("id_article"),
                    rs.getInt("quantitat"),
                    rs.getDouble("preu_base"),
                    rs.getDouble("iva"),
                    rs.getDouble("preu_final")
                ));
            }
        }
        return linies;
    }
}