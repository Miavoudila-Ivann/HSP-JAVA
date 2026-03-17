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
    JOURNAL("journal", "HSP - Journal des Actions"),
    TRIAGE("triage", "HSP - Nouveau Cas de Triage"),
    DOSSIER("dossier", "HSP - Detail du Dossier"),
    STATISTIQUES("statistiques", "HSP - Statistiques"),
    RENDEZ_VOUS("rendezvous", "HSP - Rendez-vous"),
    TOTP_VERIFY("totp_verify", "HSP - Verification 2FA"),
    TOTP_SETUP("totp_setup", "HSP - Configuration 2FA");

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
