package e.comerce.models;

/**
 * Representa un client de la botiga.
 *
 * @param dni clau primària del client. El valor {@code 000} és el client genèric
 * @param name nom de la persona o empresa
 * @param email correu electrònic de contacte
 * @param phone telèfon de contacte
 */
public record Client(String dni, String name, String email, String phone) {
    public Client {
        if (!stringExists(dni)) {
            throw new IllegalArgumentException("El DNI del client no pot estar buit");
        }

        if (!stringExists(phone)) {
            throw new IllegalArgumentException("El nom del client no pot estar buit");
        }

        if (!stringExists(email)) {
            throw new IllegalArgumentException();
        }

        if (!stringExists(phone)) {
            throw new IllegalArgumentException();
        }
    }

    private static boolean stringExists(String text) {
        return text != null && !text.isBlank();
    }
}