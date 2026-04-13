package appli.ui.controller;

import appli.model.User;
import appli.security.RoleGuard;
import appli.security.RoleGuard.Fonctionnalite;
import appli.security.SessionManager;
import appli.service.AlerteService;
import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controleur du tableau de bord principal (dashboard.fxml).
 * A l'initialisation, affiche le nom de l'utilisateur connecte et masque
 * les boutons de navigation auxquels son role ne donne pas acces.
 * Affiche aussi un badge si des alertes de stock non resolues existent.
 */
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    /** Badge affiche uniquement si des alertes de stock sont en attente. */
    @FXML private Label lblAlerteBadge;

    @FXML private Button btnPatients;
    @FXML private Button btnHospitalisations;
    @FXML private Button btnStock;
    @FXML private Button btnDemandes;
    @FXML private Button btnUtilisateurs;
    @FXML private Button btnJournal;
    @FXML private Button btnStatistiques;
    @FXML private Button btnRendezVous;
    @FXML private Button btnCommandes;
    @FXML private Button btnOrdonnances;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnChambres;
    @FXML private Button btnLoginLog;

    /**
     * Initialise le tableau de bord : affiche le message de bienvenue,
     * configure la visibilite des boutons selon le role et charge le badge alertes.
     */
    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();

        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        configurerBoutonsParRole();
        chargerBadgeAlertes();
    }

    /**
     * Affiche ou masque chaque bouton en fonction des permissions du role courant.
     * Utilise setManaged(false) pour eviter que les boutons invisibles occupent de l'espace.
     */
    private void configurerBoutonsParRole() {
        btnPatients.setVisible(RoleGuard.hasPermission(Fonctionnalite.CONSULTATION_PATIENTS)
                || RoleGuard.hasPermission(Fonctionnalite.GESTION_PATIENTS));
        btnPatients.setManaged(btnPatients.isVisible());

        btnHospitalisations.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_HOSPITALISATIONS));
        btnHospitalisations.setManaged(btnHospitalisations.isVisible());

        btnStock.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_STOCK));
        btnStock.setManaged(btnStock.isVisible());

        btnDemandes.setVisible(RoleGuard.hasPermission(Fonctionnalite.DEMANDE_PRODUITS)
                || RoleGuard.hasPermission(Fonctionnalite.VALIDATION_DEMANDES));
        btnDemandes.setManaged(btnDemandes.isVisible());

        btnUtilisateurs.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_UTILISATEURS));
        btnUtilisateurs.setManaged(btnUtilisateurs.isVisible());

        btnJournal.setVisible(RoleGuard.hasPermission(Fonctionnalite.CONSULTATION_JOURNAL));
        btnJournal.setManaged(btnJournal.isVisible());

        btnStatistiques.setVisible(RoleGuard.hasPermission(Fonctionnalite.CONSULTATION_STATISTIQUES));
        btnStatistiques.setManaged(btnStatistiques.isVisible());

        btnRendezVous.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_RENDEZ_VOUS));
        btnRendezVous.setManaged(btnRendezVous.isVisible());

        btnCommandes.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_COMMANDES));
        btnCommandes.setManaged(btnCommandes.isVisible());

        btnOrdonnances.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_ORDONNANCES));
        btnOrdonnances.setManaged(btnOrdonnances.isVisible());

        btnFournisseurs.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_FOURNISSEURS));
        btnFournisseurs.setManaged(btnFournisseurs.isVisible());

        btnChambres.setVisible(RoleGuard.hasPermission(Fonctionnalite.GESTION_CHAMBRES));
        btnChambres.setManaged(btnChambres.isVisible());

        btnLoginLog.setVisible(RoleGuard.hasPermission(Fonctionnalite.CONSULTATION_LOGIN_LOG));
        btnLoginLog.setManaged(btnLoginLog.isVisible());
    }

    /**
     * Charge et affiche le badge du nombre d'alertes de stock non resolues.
     * Visible uniquement pour le gestionnaire et l'admin.
     */
    private void chargerBadgeAlertes() {
        SessionManager session = SessionManager.getInstance();
        if (session.isGestionnaire() || session.isAdmin()) {
            try {
                AlerteService alerteService = new AlerteService();
                int count = alerteService.countNonResolues();
                if (count > 0) {
                    lblAlerteBadge.setText(count + " alerte" + (count > 1 ? "s" : ""));
                    lblAlerteBadge.setVisible(true);
                    lblAlerteBadge.setManaged(true);
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement badge alertes : " + e.getMessage());
            }
        }
    }

    // --- Actions de navigation ---

    @FXML
    private void goToPatients() {
        Router.goTo(Route.PATIENTS);
    }

    @FXML
    private void goToHospitalisations() {
        Router.goTo(Route.HOSPITALISATIONS);
    }

    @FXML
    private void goToStock() {
        Router.goTo(Route.STOCK);
    }

    @FXML
    private void goToDemandes() {
        Router.goTo(Route.DEMANDES);
    }

    @FXML
    private void goToUtilisateurs() {
        Router.goTo(Route.UTILISATEURS);
    }

    @FXML
    private void goToJournal() {
        Router.goTo(Route.JOURNAL);
    }

    @FXML
    private void goToStatistiques() {
        Router.goTo(Route.STATISTIQUES);
    }

    @FXML
    private void goToRendezVous() {
        Router.goTo(Route.RENDEZ_VOUS);
    }

    @FXML
    private void goToCommandes() {
        Router.goTo(Route.COMMANDES);
    }

    @FXML
    private void goToOrdonnances() {
        Router.goTo(Route.ORDONNANCES);
    }

    @FXML
    private void goToFournisseurs() {
        Router.goTo(Route.FOURNISSEURS);
    }

    @FXML
    private void goToChambres() {
        Router.goTo(Route.CHAMBRES);
    }

    @FXML
    private void goToLoginLog() {
        Router.goTo(Route.LOGIN_LOG);
    }

    @FXML
    private void goToTotpSetup() {
        Router.goTo(Route.TOTP_SETUP);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }
}
