package e.comerce.models;

    public record ArticleFamily(int id, String name) {
        public ArticleFamily {
            if (id <= 0) {
                throw new IllegalArgumentException("L'id de la família d'articles ha de ser positiu");
            }

            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("La família d'articles no pot estar buida");
            }
        }
    }