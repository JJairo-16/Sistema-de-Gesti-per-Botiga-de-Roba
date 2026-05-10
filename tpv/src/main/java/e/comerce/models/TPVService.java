package e.comerce.models;

import e.comerce.models.LiniaFacturaManager.LiniaFactura;

import java.util.List;

public class TPVService {

    /**
     * Calcula el total base d'una venda.
     */
    public double calcularTotalBase(List<LiniaFactura> linies) {

        double total = 0;

        for (LiniaFactura linia : linies) {
            total += linia.preuBase();
        }

        return total;
    }

    /**
     * Calcula el total IVA d'una venda.
     */
    public double calcularTotalIva(List<LiniaFactura> linies) {

        double total = 0;

        for (LiniaFactura linia : linies) {
            total += linia.iva();
        }

        return total;
    }

    /**
     * Calcula el total final d'una venda.
     */
    public double calcularTotalFinal(List<LiniaFactura> linies) {

        double total = 0;

        for (LiniaFactura linia : linies) {
            total += linia.preuFinal();
        }

        return total;
    }

    /**
     * Genera una línia de factura.
     */
    public LiniaFactura crearLiniaFactura(
            int idTiquet,
            Article article,
            int quantitat) {

        double preuBase =
                article.getBasePrice() * quantitat;

        double ivaImport =
                preuBase * article.getIva() / 100.0;

        double preuFinal =
                preuBase + ivaImport;

        return new LiniaFactura(
                idTiquet,
                article.getId(),
                quantitat,
                preuBase,
                ivaImport,
                preuFinal
        );
    }

    /**
     * Genera el tiquet final.
     */
    public Ticket crearTicket(
            int id,
            String dniClient,
            List<LiniaFactura> linies) {

        double totalBase =
                calcularTotalBase(linies);

        double totalIva =
                calcularTotalIva(linies);

        double totalFinal =
                calcularTotalFinal(linies);

        return new Ticket(
                id,
                dniClient,
                Ticket.getCurrentDateFormatted(),
                totalBase,
                totalIva,
                totalFinal
        );
    }
}