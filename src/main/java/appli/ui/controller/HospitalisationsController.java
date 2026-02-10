package appli.ui.controller;

import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HospitalisationsController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML
    public void initialize() {
        var user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }
    }

    @FXML
    private void goToDashboard() {
        Router.goTo(Route.DASHBOARD);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }
}
