package appli.ui.controller;

import appli.model.User;
import appli.security.RoleGuard;
import appli.security.RoleGuard.Fonctionnalite;
import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private Button btnPatients;
    @FXML private Button btnHospitalisations;
    @FXML private Button btnStock;
    @FXML private Button btnDemandes;
    @FXML private Button btnUtilisateurs;
    @FXML private Button btnJournal;

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();

        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        configurerBoutonsParRole();
    }

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
    private void handleLogout() {
        Router.logout();
    }
}
