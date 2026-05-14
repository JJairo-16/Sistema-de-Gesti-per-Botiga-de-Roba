package e.comerce.utils.input.selector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import e.comerce.models.articles.Article;
import e.comerce.models.articles.ArticleType;
import e.comerce.services.database.ShopDatabase;
import e.comerce.services.database.repository.ArticleRepository;
import e.comerce.utils.input.Getters;
import e.comerce.utils.input.Menu;
import e.comerce.utils.input.SelectionUtils;
import e.comerce.utils.rules.RulesChecker;
import e.comerce.utils.rules.RulesChecker.CheckResult;
import e.comerce.utils.ui.Prettier;

/**
 * Cerca i selecciona articles de la base de dades.
 */
public final class ArticleSelector {
    private static final Getters GETTERS = new Getters();

    private static final List<String> SELECT_OPTIONS = List.of(
            "Introduir ID",
            "Cercar article",
            "Mostrar tots els articles",
            "Mostrar camises",
            "Mostrar pantalons",
            "Cancel·lar");

    private static final int ID_OPTION = 1;
    private static final int SEARCH_OPTION = 2;
    private static final int SHOW_ALL_OPTION = 3;
    private static final int SHOW_SHIRTS_OPTION = 4;
    private static final int SHOW_PANTS_OPTION = 5;
    private static final int CANCEL_OPTION = 6;

    private final ArticleRepository articles;

    /**
     * Inicialitza el selector amb la base de dades.
     *
     * @param shop base de dades
     */
    public ArticleSelector(ShopDatabase shop) {
        this.articles = shop.articles();
    }

    /**
     * Selecciona un article i retorna el seu ID.
     *
     * @return ID seleccionat o null si es cancel·la
     */
    public Integer askArticleId() {
        Article article = askArticle();
        return article == null ? null : article.getId();
    }

    /**
     * Selecciona un article.
     *
     * @return article seleccionat o null si es cancel·la
     */
    public Article askArticle() {
        while (true) {
            int option = Menu.getOption(SELECT_OPTIONS, "Seleccionar article");

            switch (option) {
                case ID_OPTION:
                    return askArticleById();

                case SEARCH_OPTION:
                    return searchArticle();

                case SHOW_ALL_OPTION:
                    return showAllArticles();

                case SHOW_SHIRTS_OPTION:
                    return showArticlesByType(ArticleType.SHIRT, "Camises");

                case SHOW_PANTS_OPTION:
                    return showArticlesByType(ArticleType.PANTS, "Pantalons");

                case CANCEL_OPTION:
                    if (SelectionUtils.confirmCancel()) {
                        SelectionUtils.cancel();
                        return null;
                    }
                    break;

                default:
                    Prettier.warn("Opció no vàlida.");
                    break;
            }
        }
    }

    /**
     * Demana un ID i comprova que existeixi.
     *
     * @return article trobat o null si es cancel·la
     */
    private Article askArticleById() {
        printSearchHeader("Introducció directa d'ID");
        SelectionUtils.showCancelHint();

        while (true) {
            String input = GETTERS.getStringAllowEmpty("ID de l'article: ", "ID");

            if (input.isEmpty()) {
                if (SelectionUtils.shouldCancelEmptyInput()) {
                    SelectionUtils.cancel();
                    return null;
                }

                continue;
            }

            int id;

            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                Prettier.warn("L'ID de l'article ha de ser un nombre enter.");
                continue;
            }

            CheckResult result = RulesChecker.checkArticleId(id);

            if (!result.result()) {
                Prettier.warn(result.msg());
                continue;
            }

            try {
                Article article = articles.findById(id);

                if (article == null) {
                    Prettier.warn("No s'ha trobat cap article amb aquest ID.");
                    Prettier.info("Pots utilitzar l'opció de cerca si no recordes l'ID exacte.");
                    continue;
                }

                showSelectedArticle(article);
                return article;
            } catch (SQLException e) {
                SelectionUtils.databaseError();
                return null;
            } catch (Exception e) {
                SelectionUtils.unexpectedError();
                return null;
            }
        }
    }

    /**
     * Cerca articles a la base de dades.
     *
     * @return article seleccionat o null si es cancel·la
     */
    private Article searchArticle() {
        printSearchHeader("Cerca d'article");
        Prettier.info("Pots cercar per ID, nom o tipus.");
        SelectionUtils.showCancelHint();

        while (true) {
            String query = GETTERS.getStringAllowEmpty("Cerca: ", "Cerca");

            if (query.isEmpty()) {
                if (SelectionUtils.shouldCancelEmptyInput()) {
                    SelectionUtils.cancel();
                    return null;
                }

                continue;
            }

            try {
                List<Article> matches = articles.search(query);

                if (matches.isEmpty()) {
                    Prettier.warn("No s'ha trobat cap article amb aquesta cerca.");
                    Prettier.info("Prova amb una altra dada de l'article.");
                    continue;
                }

                return chooseFromList(matches, "Articles trobats");
            } catch (SQLException e) {
                SelectionUtils.databaseError();
                return null;
            } catch (Exception e) {
                SelectionUtils.unexpectedError();
                return null;
            }
        }
    }

    /**
     * Mostra tots els articles.
     *
     * @return article seleccionat o null si es cancel·la
     */
    private Article showAllArticles() {
        printSearchHeader("Llistat d'articles");

        try {
            List<Article> allArticles = articles.findAll();

            if (allArticles.isEmpty()) {
                Prettier.warn("No hi ha articles registrats.");
                return null;
            }

            return chooseFromList(allArticles, "Tots els articles");
        } catch (SQLException e) {
            SelectionUtils.databaseError();
            return null;
        } catch (Exception e) {
            SelectionUtils.unexpectedError();
            return null;
        }
    }

    /**
     * Mostra articles per tipus.
     *
     * @param type  tipus d'article
     * @param title títol del menú
     * @return article seleccionat o null si es cancel·la
     */
    private Article showArticlesByType(ArticleType type, String title) {
        printSearchHeader(title);

        try {
            List<Article> foundArticles = articles.findByType(type);

            if (foundArticles.isEmpty()) {
                Prettier.warn("No hi ha articles d'aquest tipus.");
                return null;
            }

            return chooseFromList(foundArticles, title);
        } catch (SQLException e) {
            SelectionUtils.databaseError();
            return null;
        } catch (Exception e) {
            SelectionUtils.unexpectedError();
            return null;
        }
    }

    /**
     * Permet seleccionar un article d'una llista.
     *
     * @param foundArticles articles trobats
     * @param title         títol del menú
     * @return article seleccionat o null si es cancel·la
     */
    private Article chooseFromList(List<Article> foundArticles, String title) {
        List<String> options = new ArrayList<>();

        for (Article article : foundArticles) {
            options.add(formatArticle(article));
        }

        options.add("Cancel·lar");

        int option = Menu.getOption(options, title);

        if (option == options.size()) {
            if (SelectionUtils.confirmCancel()) {
                SelectionUtils.cancel();
                return null;
            }

            return chooseFromList(foundArticles, title);
        }

        if (option < 1 || option > foundArticles.size()) {
            Prettier.warn("Opció no vàlida.");
            return null;
        }

        Article selected = foundArticles.get(option - 1);
        showSelectedArticle(selected);
        return selected;
    }

    /**
     * Mostra una capçalera simple.
     *
     * @param title títol
     */
    private void printSearchHeader(String title) {
        System.out.println();
        Prettier.info("=== " + title + " ===");
        System.out.println();
    }

    /**
     * Mostra l'article seleccionat.
     *
     * @param article article seleccionat
     */
    private void showSelectedArticle(Article article) {
        System.out.println();
        Prettier.info("Article seleccionat:");
        System.out.println(formatArticle(article));
    }

    /**
     * Formata un article per mostrar-lo.
     *
     * @param article article
     * @return text de l'article
     */
    private String formatArticle(Article article) {
        return String.format(
                "%d - %s - %s - %.2f € - stock: %d",
                article.getId(),
                article.getName(),
                article.getType(),
                article.getFinalPrice(),
                article.getStock());
    }
}