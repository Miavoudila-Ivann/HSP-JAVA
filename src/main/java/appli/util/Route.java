package appli.util;

/**
 * Enumeration centrale de toutes les routes de l'application.
 * Chaque route correspond a un fichier FXML dans /appli/view/.
 */
public enum Route {

    LOGIN("login", "HSP - Connexion"),
    DASHBOARD("dashboard", "HSP - Tableau de bord"),
    PATIENTS("patients", "HSP - Gestion des Patients"),
    HOSPITALISATIONS("hospitalisations", "HSP - Hospitalisations"),
    STOCK("stock", "HSP - Gestion du Stock"),
    DEMANDES("demandes", "HSP - Demandes de Produits"),
    UTILISATEURS("utilisateurs", "HSP - Gestion des Utilisateurs"),
    JOURNAL("journal", "HSP - Journal des Actions");

    private final String viewName;
    private final String title;

    Route(String viewName, String title) {
        this.viewName = viewName;
        this.title = title;
    }

    public String getViewName() {
        return viewName;
    }

    public String getTitle() {
        return title;
    }

    public String getFxmlPath() {
        return "/appli/view/" + viewName + ".fxml";
    }
}
