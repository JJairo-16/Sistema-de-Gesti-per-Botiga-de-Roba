package e.comerce;

import java.io.IOException;

import e.comerce.services.database.ShopDatabase;

public class App {
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        try {
            ShopDatabase db = new ShopDatabase();
        } catch (IOException e) {
            System.err.println("Could not connect to the data source: " + e.getMessage());
        }
    }
}
