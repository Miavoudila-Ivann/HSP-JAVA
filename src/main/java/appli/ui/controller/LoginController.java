package appli.ui.controller;

import appli.service.AuthService;
import appli.service.AuthService.LoginResult;
import appli.util.Route;
import appli.util.Router;
import appli.util.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        errorLabel.setVisible(false);

        if (!ValidationUtils.isNotEmpty(email)) {
            showError("Veuillez saisir votre email");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showError("Format d'email invalide");
            return;
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            showError("Veuillez saisir votre mot de passe");
            return;
        }

        LoginResult result = authService.login(email, password);

        switch (result.status()) {
            case SUCCESS -> Router.goTo(Route.DASHBOARD);
            case COMPTE_VERROUILLE -> showError("Compte verrouille - Reessayez dans 30 minutes");
            case COMPTE_DESACTIVE -> showError("Compte desactive - Contactez l'administrateur");
            case IDENTIFIANTS_INVALIDES -> {
                showError("Email ou mot de passe incorrect");
                passwordField.clear();
            }
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
}
