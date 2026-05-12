package e.comerce;

import e.comerce.utils.input.AppMenus;
import e.comerce.utils.ui.Prettier;

public class App {
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        try {
            AppMenus app = new AppMenus();
            app.mainMenu();
        } catch (RuntimeException e) {
            Prettier.error("L'aplicació no es pot iniciar.");
        }
    }
}
