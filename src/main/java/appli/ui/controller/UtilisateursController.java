package appli.ui.controller;

import appli.model.User;
import appli.repository.UserRepository;
import appli.security.PasswordHasher;
import appli.service.AuthService;
import appli.ui.util.AlertHelper;
import appli.util.Route;
import appli.util.Router;
import appli.util.ValidationUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controleur de la vue de gestion des utilisateurs (utilisateurs.fxml).
 * Accessible a l'admin uniquement.
 * Permet de creer, modifier, activer/desactiver et supprimer les comptes utilisateurs.
 */
public class UtilisateursController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPrenom;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colActif;
    @FXML private TableColumn<User, String> colDerniereConnexion;

    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnToggleActif;
    @FXML private Label statusLabel;

    private final AuthService authService = new AuthService();
    private final UserRepository userRepository = new UserRepository();
    private final ObservableList<User> userData = FXCollections.observableArrayList();

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        var user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupTableColumns();
        userTable.setItems(userData);

        btnModifier.setDisable(true);
        btnToggleActif.setDisable(true);

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            btnModifier.setDisable(newVal == null);
            btnToggleActif.setDisable(newVal == null);
            if (newVal != null) {
                btnToggleActif.setText(newVal.isActif() ? "Desactiver" : "Activer");
            }
        });

        loadUsers();
    }

    private void setupTableColumns() {
        colNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));
        colPrenom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrenom()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colRole.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole().getLibelle()));
        colActif.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isActif() ? "Oui" : "Non"));
        colDerniereConnexion.setCellValueFactory(cell -> {
            var dt = cell.getValue().getDerniereConnexion();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "Jamais");
        });
    }

    private void loadUsers() {
        try {
            List<User> users = userRepository.getAll();
            userData.clear();
            userData.addAll(users);
            statusLabel.setText(users.size() + " utilisateur(s)");
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }

    @FXML
    private void handleEditUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showUserDialog(selected);
        }
    }

    @FXML
    private void handleToggleActif() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String action = selected.isActif() ? "desactiver" : "activer";
        if (AlertHelper.showConfirmation("Confirmation",
                "Voulez-vous " + action + " l'utilisateur " + selected.getNomComplet() + " ?")) {
            try {
                selected.setActif(!selected.isActif());
                userRepository.save(selected);
                AlertHelper.showInfo("Succes", "Utilisateur " + action + " avec succes");
                loadUsers();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        }
    }

    private void showUserDialog(User existingUser) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existingUser == null ? "Nouvel utilisateur" : "Modifier utilisateur");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        ComboBox<User.Role> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(User.Role.values());

        Label passwordHint = new Label();
        passwordHint.setWrapText(true);
        passwordHint.setMaxWidth(250);

        Label confirmHint = new Label();
        confirmHint.setWrapText(true);
        confirmHint.setMaxWidth(250);

        if (existingUser != null) {
            nomField.setText(existingUser.getNom());
            prenomField.setText(existingUser.getPrenom());
            emailField.setText(existingUser.getEmail());
            roleCombo.setValue(existingUser.getRole());
            passwordField.setPromptText("Laisser vide pour ne pas changer");
            confirmPasswordField.setPromptText("Confirmer le nouveau mot de passe");
        } else {
            passwordField.setPromptText("Mot de passe (min 8 car.)");
            confirmPasswordField.setPromptText("Confirmer le mot de passe");
        }

        passwordField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String error = ValidationUtils.validatePasswordStrength(newVal);
                passwordHint.setText(error != null ? error : "Mot de passe valide");
                passwordHint.setStyle(error != null
                        ? "-fx-text-fill: #F44336; -fx-font-size: 11;"
                        : "-fx-text-fill: #4CAF50; -fx-font-size: 11;");
            } else {
                passwordHint.setText("");
            }
            updateConfirmHint(confirmHint, newVal, confirmPasswordField.getText());
        });

        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> {
            updateConfirmHint(confirmHint, passwordField.getText(), newVal);
        });

        grid.add(new Label("Nom :"), 0, 0);           grid.add(nomField, 1, 0);
        grid.add(new Label("Prenom :"), 0, 1);        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email :"), 0, 2);         grid.add(emailField, 1, 2);
        grid.add(new Label("Role :"), 0, 3);          grid.add(roleCombo, 1, 3);
        grid.add(new Label("Mot de passe :"), 0, 4);  grid.add(passwordField, 1, 4);
        grid.add(passwordHint, 1, 5);
        grid.add(new Label("Confirmer :"), 0, 6);     grid.add(confirmPasswordField, 1, 6);
        grid.add(confirmHint, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType != ButtonType.OK) return;

            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            User.Role role = roleCombo.getValue();

            if (!ValidationUtils.isNotEmpty(nom) || !ValidationUtils.isNotEmpty(prenom)) {
                AlertHelper.showError("Erreur", "Le nom et le prenom sont obligatoires");
                return;
            }
            if (!ValidationUtils.isValidEmail(email)) {
                AlertHelper.showError("Erreur", "Adresse email invalide");
                return;
            }
            if (role == null) {
                AlertHelper.showError("Erreur", "Veuillez selectionner un role");
                return;
            }

            try {
                if (existingUser == null) {
                    if (password.isEmpty()) {
                        AlertHelper.showError("Erreur", "Le mot de passe est obligatoire");
                        return;
                    }
                    String pwdError = ValidationUtils.validatePasswordStrength(password);
                    if (pwdError != null) {
                        AlertHelper.showError("Mot de passe faible", pwdError);
                        return;
                    }
                    if (!password.equals(confirmPassword)) {
                        AlertHelper.showError("Erreur", "Les mots de passe ne correspondent pas");
                        return;
                    }
                    authService.createUser(email, password, nom, prenom, role);
                    AlertHelper.showInfo("Succes", "Utilisateur cree avec succes");
                } else {
                    existingUser.setNom(nom);
                    existingUser.setPrenom(prenom);
                    existingUser.setEmail(email);
                    existingUser.setRole(role);
                    if (!password.isEmpty()) {
                        String pwdError = ValidationUtils.validatePasswordStrength(password);
                        if (pwdError != null) {
                            AlertHelper.showError("Mot de passe faible", pwdError);
                            return;
                        }
                        if (!password.equals(confirmPassword)) {
                            AlertHelper.showError("Erreur", "Les mots de passe ne correspondent pas");
                            return;
                        }
                        existingUser.setPasswordHash(PasswordHasher.hash(password));
                    }
                    userRepository.save(existingUser);
                    AlertHelper.showInfo("Succes", "Utilisateur modifie avec succes");
                }
                loadUsers();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    private void updateConfirmHint(Label hint, String password, String confirm) {
        if (confirm == null || confirm.isEmpty()) {
            hint.setText("");
            return;
        }
        if (password != null && password.equals(confirm)) {
            hint.setText("Les mots de passe correspondent");
            hint.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 11;");
        } else {
            hint.setText("Les mots de passe ne correspondent pas");
            hint.setStyle("-fx-text-fill: #F44336; -fx-font-size: 11;");
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
