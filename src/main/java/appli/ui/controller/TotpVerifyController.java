package appli.ui.controller;

import appli.model.User;
import appli.service.AuthService;
import appli.service.AuthService.LoginResult;
import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controleur de la vue de verification 2FA (totp_verify.fxml).
 * Affiche a l'utilisateur qui a active le 2FA un champ de saisie du code OTP.
 * Appele apres la verification du mot de passe, avant l'acces au dashboard.
 */
public class TotpVerifyController {

    @FXML private TextField codeField;
    @FXML private Label errorLabel;
    @FXML private Label emailLabel;

    private final AuthService authService = new AuthService();
    private User pendingUser;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        pendingUser = (User) Router.getNavigationData("totp_verify");
        if (pendingUser != null) {
            emailLabel.setText(pendingUser.getEmail());
        }
    }

    @FXML
    private void handleVerify() {
        String code = codeField.getText().trim();

        if (code.isEmpty()) {
            showError("Veuillez saisir le code a 6 chiffres");
            return;
        }

        if (pendingUser == null) {
            showError("Session expiree. Reconnectez-vous.");
            return;
        }

        LoginResult result = authService.completeTotpLogin(pendingUser, code);

        switch (result.status()) {
            case SUCCESS -> {
                Router.clearNavigationData("totp_verify");
                Router.goTo(Route.DASHBOARD);
            }
            case TOTP_INVALIDE -> {
                showError("Code incorrect. Verifiez l'heure de votre appareil.");
                codeField.clear();
            }
            default -> showError("Erreur d'authentification.");
        }
    }

    @FXML
    private void handleCancel() {
        Router.clearNavigationData("totp_verify");
        Router.goTo(Route.LOGIN);
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleVerify();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
