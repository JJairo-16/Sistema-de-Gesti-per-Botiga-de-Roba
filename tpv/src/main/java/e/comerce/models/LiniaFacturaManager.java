package e.comerce.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de gestió de dades per a les línies de detall de cada tiquet.
 * S'encarrega de la persistència en la taula 'linies_factura'.
 */
public class LiniaFacturaManager {

    /**
     * Representa una línia individual de producte dins d'una venda.
     * 
     * @param idTiquet Identificador del tiquet al qual pertany la línia.
     * @param idArticle Identificador de l'article venut.
     * @param quantitat Nombre de peces venudes.
     * @param preuBase Preu total de la línia sense IVA.
     * @param iva Percentatge d'IVA aplicat.
     * @param preuFinal Preu total de la línia amb l'IVA inclòs.
     */
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

    /**
     * Registra una nova línia de factura a la base de dades.
     * 
     * @param linia Objecte LiniaFactura amb les dades a persistir.
     * @throws SQLException Si es produeix un error en l'accés a les dades.
     */
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

    /**
     * Recupera totes les línies associades a un identificador de tiquet concret.
     * 
     * @param idTiquet L'identificador del tiquet a consultar.
     * @return Una llista d'objectes LiniaFactura.
     * @throws SQLException Si es produeix un error en l'accés a les dades.
     */
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