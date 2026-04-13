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

/**
 * Controleur de la vue de connexion (login.fxml).
 * Valide les champs, appelle {@link AuthService#login} et redirige selon le statut :
 * tableau de bord, verification 2FA, ou affichage d'un message d'erreur.
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    /** Cache le label d'erreur a l'ouverture de la vue. */
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    /**
     * Declenche la tentative de connexion apres validation locale des champs.
     * Redirige vers TOTP_VERIFY si le 2FA est active, vers DASHBOARD sinon.
     */
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
            case SUCCESS             -> Router.goTo(Route.DASHBOARD);
            case TOTP_REQUIRED       -> Router.goTo(Route.TOTP_VERIFY, result.user());
            case COMPTE_VERROUILLE   -> showError("Compte verrouille - Reessayez dans 30 minutes");
            case COMPTE_DESACTIVE    -> showError("Compte desactive - Contactez l'administrateur");
            case IDENTIFIANTS_INVALIDES -> {
                showError("Email ou mot de passe incorrect");
                passwordField.clear();
            }
            default -> showError("Erreur inattendue");
        }
    }

    /** Affiche un message d'erreur dans le label prevu a cet effet. */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /** Permet de valider le formulaire avec la touche Entree. */
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
}
