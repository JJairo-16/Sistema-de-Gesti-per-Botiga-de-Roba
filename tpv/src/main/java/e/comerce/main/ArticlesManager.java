package e.comerce.main;

import java.sql.SQLException;
import java.util.List;

import e.comerce.models.articles.Article;
import e.comerce.services.database.ShopDatabase;
import e.comerce.services.database.repository.ArticleRepository;
import e.comerce.utils.input.Menu;
import e.comerce.utils.input.ObjectInput;
import e.comerce.utils.input.selector.ArticleSelector;
import e.comerce.utils.ui.Cleaner;
import e.comerce.utils.ui.Prettier;

/**
 * Gestiona el menú d'articles.
 */
public final class ArticlesManager {
    private static final List<String> ARTICLE_OPTIONS = List.of(
            "Altes",
            "Baixes",
            "Mod.",
            "Consultes",
            "Tornar al menú principal.");

    private static final int EXIT_OPTION = ARTICLE_OPTIONS.size();

    private final ArticleRepository repo;
    private final ArticleSelector selector;
    private final Cleaner cleaner;

    /**
     * Inicialitza el gestor d'articles.
     *
     * @param shop    base de dades
     * @param cleaner netejador de pantalla
     */
    public ArticlesManager(ShopDatabase shop, Cleaner cleaner) {
        this.repo = shop.articles();
        this.selector = new ArticleSelector(shop);
        this.cleaner = cleaner;
    }

    /**
     * Mostra el menú d'articles.
     */
    public void showMenu() {
        boolean loop = true;

        while (loop) {
            int option = Menu.getOption(ARTICLE_OPTIONS, "Gestió d'articles");

            if (option == EXIT_OPTION) {
                loop = false;
                continue;
            }

            cleaner.clear();
            handleOption(option);
        }
    }

    /**
     * Executa una opció d'articles.
     *
     * @param option opció seleccionada
     */
    private void handleOption(int option) {
        switch (option) {
            case 1:
                registerArticle();
                Menu.pause();
                break;

            case 2:
                discontinueArticle();
                Menu.pause();
                break;

            case 3:
                modifyArticle();
                Menu.pause();
                break;

            case 4:
                consultArticles();
                Menu.pause();
                break;

            default:
                Prettier.warn("Opció no vàlida.");
                break;
        }
    }

    /**
     * Dona d'alta un article.
     */
    private void registerArticle() {
        Prettier.info("Si us plau, ompli les dades del nou article:");

        Article article = ObjectInput.askArticle();

        if (article == null) {
            return;
        }

        try {
            if (repo.findById(article.getId()) != null) {
                Prettier.warn("Ja existeix un article amb aquest ID.");
                return;
            }

            boolean inserted = repo.insert(article) > 0;

            if (inserted) {
                Prettier.info("S'ha afegit el nou article a la base de dades.");
            } else {
                Prettier.warn("No s'ha pogut afegir l'article.");
            }
        } catch (SQLException e) {
            databaseError();
        } catch (IllegalArgumentException e) {
            Prettier.warn(e.getMessage());
        } catch (Exception e) {
            unexpectedError();
        }
    }

    /**
     * Dona de baixa un article.
     */
    private void discontinueArticle() {
        Article article = selector.askArticle();

        if (article == null) {
            return;
        }

        try {
            boolean deleted = repo.delete(article.getId());

            if (deleted) {
                Prettier.info("Article eliminat correctament.");
            } else {
                Prettier.warn("No s'ha trobat l'article a la base de dades.");
            }
        } catch (SQLException e) {
            databaseError();
        } catch (IllegalArgumentException e) {
            Prettier.warn(e.getMessage());
        } catch (Exception e) {
            unexpectedError();
        }
    }

    /**
     * Modifica un article.
     */
    private void modifyArticle() {
        Article oldArticle = selector.askArticle();

        if (oldArticle == null) {
            return;
        }

        Prettier.info("Introdueix les noves dades de l'article.");
        Prettier.info("L'ID s'ha de mantenir igual: %d", oldArticle.getId());

        Article newArticle = ObjectInput.askArticle();

        if (newArticle == null) {
            return;
        }

        if (oldArticle.getId() != newArticle.getId()) {
            Prettier.warn("No es pot modificar l'ID de l'article en aquesta operació.");
            return;
        }

        try {
            boolean updated = repo.update(newArticle);

            if (updated) {
                Prettier.info("Article modificat correctament.");
            } else {
                Prettier.warn("No s'ha pogut modificar l'article.");
            }
        } catch (SQLException e) {
            databaseError();
        } catch (IllegalArgumentException e) {
            Prettier.warn(e.getMessage());
        } catch (Exception e) {
            unexpectedError();
        }
    }

    /**
     * Consulta articles.
     */
    private void consultArticles() {
        Article article = selector.askArticle();

        if (article == null) {
            return;
        }

        Prettier.info("Article seleccionat:");
        System.out.println(formatArticle(article));
    }

    /**
     * Formata les dades d'un article.
     *
     * @param article article a mostrar
     * @return text formatat
     */
    private String formatArticle(Article article) {
        return String.format(
                "ID: %d%nNom: %s%nTipus: %s%nPreu base: %.2f €%nPreu final: %.2f €%nIVA: %d%%%nStock: %d%nBenefici/unitat: %.2f €",
                article.getId(),
                article.getName(),
                article.getType(),
                article.getBasePrice(),
                article.getFinalPrice(),
                article.getIva(),
                article.getStock(),
                article.getProfitPerUnit());
    }

    /**
     * Informa d'un error de base de dades.
     */
    private void databaseError() {
        Prettier.error("Error en la base de dades. Si us plau, torni a intentar-ho més tard.");
    }

    /**
     * Informa d'un error inesperat.
     */
    private void unexpectedError() {
        Prettier.error("Error inesperat. Si us plau, torni a intentar-ho més tard.");
    }
}