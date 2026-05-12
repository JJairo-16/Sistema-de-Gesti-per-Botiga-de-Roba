package e.comerce;

import e.comerce.main.MainManager;
import e.comerce.utils.ui.Prettier;

public class App {
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        try {
            MainManager manager = new MainManager();
            manager.run();
        } catch (RuntimeException e) {
            Prettier.error("L'aplicació no es pot iniciar.");
        }
    }
}
