package e.comerce;

import java.io.IOException;
import java.sql.SQLException;

import e.comerce.services.database.ShopDatabase;
import e.comerce.services.stock.DatabaseRestocker;

public class App {
    public static void main(String[] args) throws IOException, SQLException {
        App app = new App();
        app.run();
    }

    public void run() throws IOException, SQLException {
        try (ShopDatabase shop = new ShopDatabase()) {
            DatabaseRestocker.restock(shop);
        }
    }
}
