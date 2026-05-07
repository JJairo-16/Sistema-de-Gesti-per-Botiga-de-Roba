package e.comerce.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa la capçalera d'un tiquet de venda en el sistema TPV.
 *
 * Aquesta classe emmagatzema la informació principal d'una venda:
 * identificador, client associat, data i imports totals.
 *
 * @param id Identificador únic i incremental del tiquet.
 * @param dniClient DNI/NIF del client associat a la compra.
 *                  El valor "000" representa un client genèric.
 * @param date Data de la venda en format dd/MM/yyyy.
 * @param totalBase Import total de la venda sense IVA.
 * @param totalIva Import total corresponent a l'IVA.
 * @param totalFinal Import final de la venda amb IVA inclòs.
 */
public record Ticket(
        int id,
        String dniClient,
        String date,
        double totalBase,
        double totalIva,
        double totalFinal
) {

    /**
     * Constructor compacte amb validacions bàsiques.
     *
     * @throws IllegalArgumentException Si el DNI del client és nul o buit.
     * @throws IllegalArgumentException Si algun import és negatiu.
     */
    public Ticket {

        if (dniClient == null || dniClient.isBlank()) {
            throw new IllegalArgumentException(
                    "El DNI del client no pot estar buit");
        }

        if (totalBase < 0) {
            throw new IllegalArgumentException(
                    "El total base no pot ser negatiu");
        }

        if (totalIva < 0) {
            throw new IllegalArgumentException(
                    "El total IVA no pot ser negatiu");
        }

        if (totalFinal < 0) {
            throw new IllegalArgumentException(
                    "El total final no pot ser negatiu");
        }
    }

    /**
     * Genera la data actual del sistema en format dd/MM/yyyy.
     *
     * @return Data actual formatada.
     */
    public static String getCurrentDateFormatted() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}