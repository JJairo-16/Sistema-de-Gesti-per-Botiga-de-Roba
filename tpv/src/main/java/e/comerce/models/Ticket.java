package e.comerce.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public record Ticket(
    int id,
    String dniClient,
    String date,
    double totalBase,
    double totalIva,
    double totalFinal
) {


    public Ticket {
        if (dniClient == null || dniClient.isBlank()) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }
    }

    

    
    public static String getCurrentDateFormatted() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}