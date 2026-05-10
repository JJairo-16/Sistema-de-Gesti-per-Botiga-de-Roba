package e.comerce.services.database.repository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import e.comerce.libs.db.Database;
import e.comerce.models.Ticket;

/** Gestiona les capçaleres dels tiquets de venda. */
public class TicketRepository {
    private static final DateTimeFormatter APP_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Database db;

    public TicketRepository(Database db) {
        this.db = Objects.requireNonNull(db, "La base de dades no pot ser nul·la");
    }

    public long insert(Ticket ticket) throws SQLException {
        Objects.requireNonNull(ticket, "El tiquet no pot ser nul");

        return db.insert(
                "INSERT INTO tiquets (data_compra, dni_client, total_base, total_iva, total_final) "
                        + "VALUES (?, ?, ?, ?, ?)",
                toSqlDate(ticket.date()),
                ticket.dniClient(),
                ticket.totalBase(),
                ticket.totalIva(),
                ticket.totalFinal());
    }

    public boolean update(Ticket ticket) throws SQLException {
        Objects.requireNonNull(ticket, "El tiquet no pot ser nul");
        validateId(ticket.id());

        return db.update(
                "UPDATE tiquets SET data_compra = ?, dni_client = ?, total_base = ?, total_iva = ?, total_final = ? "
                        + "WHERE id = ?",
                toSqlDate(ticket.date()),
                ticket.dniClient(),
                ticket.totalBase(),
                ticket.totalIva(),
                ticket.totalFinal(),
                ticket.id()) > 0;
    }

    public boolean delete(int id) throws SQLException {
        validateId(id);
        return db.delete("DELETE FROM tiquets WHERE id = ?", id) > 0;
    }

    public Ticket findById(int id) throws SQLException {
        validateId(id);
        return db.one(
                "SELECT id, data_compra, dni_client, total_base, total_iva, total_final FROM tiquets WHERE id = ?",
                id,
                rs -> new Ticket(
                        rs.getInt("id"),
                        rs.getString("dni_client"),
                        fromSqlDate(rs.getDate("data_compra").toLocalDate()),
                        rs.getDouble("total_base"),
                        rs.getDouble("total_iva"),
                        rs.getDouble("total_final")));
    }

    public List<Ticket> findByClient(String dniClient) throws SQLException {
        if (dniClient == null || dniClient.isBlank()) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }

        return db.list(
                "SELECT id, data_compra, dni_client, total_base, total_iva, total_final "
                        + "FROM tiquets WHERE dni_client = ? ORDER BY data_compra DESC, id DESC",
                dniClient,
                rs -> new Ticket(
                        rs.getInt("id"),
                        rs.getString("dni_client"),
                        fromSqlDate(rs.getDate("data_compra").toLocalDate()),
                        rs.getDouble("total_base"),
                        rs.getDouble("total_iva"),
                        rs.getDouble("total_final")));
    }

    private static java.sql.Date toSqlDate(String appDate) {
        return java.sql.Date.valueOf(LocalDate.parse(appDate, APP_FORMAT));
    }

    private static String fromSqlDate(LocalDate date) {
        return APP_FORMAT.format(date);
    }

    private static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'id del tiquet ha de ser positiu");
        }
    }
}
