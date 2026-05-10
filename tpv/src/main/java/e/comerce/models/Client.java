package e.comerce.models;

/**
 * Representa un client de la botiga.
 *
 * @param dni clau primària del client. El valor {@code 000} és el client genèric
 * @param nom nom de la persona o empresa
 * @param email correu electrònic de contacte
 * @param telefon telèfon de contacte
 */
public record Client(String dni, String nom, String email, String telefon) {
    public Client {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }

        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("El nom del client no pot estar buit");
        }

        if (email == null) {
            email = "";
        }

        if (telefon == null) {
            telefon = "";
        }
    }
}
