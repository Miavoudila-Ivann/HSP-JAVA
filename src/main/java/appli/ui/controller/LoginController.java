package appli.ui.controller;

import appli.model.User;
import appli.service.AuthService;
import appli.util.Router;
import appli.util.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

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

        Optional<User> userOpt = authService.login(email, password);

        if (userOpt.isEmpty()) {
            showError("Email ou mot de passe incorrect");
            passwordField.clear();
            return;
        }

        User user = userOpt.get();
        navigateToDashboard(user);
    }

    private void navigateToDashboard(User user) {
        Router.setTitle("HSP - " + user.getRole().getLibelle());
        Router.navigateTo("dashboard", user);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleKeyPressed(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            handleLogin();
        }
    }
}
