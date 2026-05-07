package e.comerce.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa la capçalera d'un tiquet de venda en el sistema TPV.
 * 
 * @param id Identificador únic i incremental del tiquet.
 * @param dniClient NIF/DNI del client associat o "000" per a client genèric.
 * @param date Data de la venda en format dd/MM/yyyy.
 * @param totalBase Suma dels preus base de totes les línies.
 * @param totalIva Suma de les quotes d'IVA de totes les línies.
 * @param totalFinal Import total a pagar pel client.
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
     * Constructor compacte que valida que el client estigui identificat.
     * 
     * @throws IllegalArgumentException Si el dniClient és nul o està buit.
     */
    public Ticket {
        if (dniClient == null || dniClient.isBlank()) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }
    }

    /**
     * Genera la data actual del sistema seguint el format requerit pel projecte.
     * 
     * @return String amb la data en format dd/MM/yyyy.
     */
    public static String getCurrentDateFormatted() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}