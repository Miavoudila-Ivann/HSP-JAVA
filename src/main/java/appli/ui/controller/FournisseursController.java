package appli.ui.controller;

import appli.model.Fournisseur;
import appli.model.User;
import appli.service.StockService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

/**
 * Controleur de la vue de gestion des fournisseurs (fournisseurs.fxml).
 * Accessible au gestionnaire et a l'admin.
 * Permet le CRUD complet des fournisseurs et la recherche par nom/code.
 */
public class FournisseursController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private TextField tfRecherche;
    @FXML private TableView<Fournisseur> tableFournisseurs;
    @FXML private TableColumn<Fournisseur, String> colCode;
    @FXML private TableColumn<Fournisseur, String> colNom;
    @FXML private TableColumn<Fournisseur, String> colVille;
    @FXML private TableColumn<Fournisseur, String> colTelephone;
    @FXML private TableColumn<Fournisseur, String> colEmail;
    @FXML private TableColumn<Fournisseur, String> colContact;
    @FXML private TableColumn<Fournisseur, String> colDelai;

    private final StockService stockService = new StockService();

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupTable();
        chargerFournisseurs();

        tfRecherche.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.isBlank()) {
                chargerFournisseurs();
            } else {
                rechercher(val.trim());
            }
        });
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCode()));
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        colVille.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getVille() != null ? c.getValue().getVille() : ""));
        colTelephone.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTelephone() != null ? c.getValue().getTelephone() : ""));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmail() != null ? c.getValue().getEmail() : ""));
        colContact.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getContactNom() != null ? c.getValue().getContactNom() : ""));
        colDelai.setCellValueFactory(c -> {
            Integer d = c.getValue().getDelaiLivraisonJours();
            return new SimpleStringProperty(d != null ? d + " j" : "-");
        });
    }

    private void chargerFournisseurs() {
        try {
            List<Fournisseur> liste = stockService.getAllFournisseurs();
            tableFournisseurs.setItems(FXCollections.observableArrayList(liste));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les fournisseurs : " + e.getMessage());
        }
    }

    private void rechercher(String terme) {
        try {
            List<Fournisseur> liste = stockService.rechercherFournisseurs(terme);
            tableFournisseurs.setItems(FXCollections.observableArrayList(liste));
        } catch (Exception e) {
            chargerFournisseurs();
        }
    }

    @FXML
    private void handleNouveauFournisseur() {
        Fournisseur f = new Fournisseur();
        if (ouvrirDialogFournisseur(f, "Nouveau fournisseur")) {
            try {
                stockService.creerFournisseur(f);
                chargerFournisseurs();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Fournisseur cree avec succes.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleModifierFournisseur() {
        Fournisseur selected = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection requise", "Selectionnez un fournisseur a modifier.");
            return;
        }
        if (ouvrirDialogFournisseur(selected, "Modifier fournisseur")) {
            try {
                stockService.modifierFournisseur(selected);
                chargerFournisseurs();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Fournisseur modifie avec succes.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleSupprimerFournisseur() {
        Fournisseur selected = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection requise", "Selectionnez un fournisseur a supprimer.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer le fournisseur \"" + selected.getNom() + "\" ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                stockService.supprimerFournisseur(selected.getId());
                chargerFournisseurs();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Fournisseur supprime.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    private boolean ouvrirDialogFournisseur(Fournisseur f, String titre) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titre);
        dialog.setHeaderText(null);

        ButtonType okBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField tfCode = new TextField(f.getCode() != null ? f.getCode() : "");
        tfCode.setPromptText("FOUR-001");
        TextField tfNom = new TextField(f.getNom() != null ? f.getNom() : "");
        tfNom.setPromptText("Nom du fournisseur");
        TextField tfVille = new TextField(f.getVille() != null ? f.getVille() : "");
        TextField tfTelephone = new TextField(f.getTelephone() != null ? f.getTelephone() : "");
        TextField tfEmail = new TextField(f.getEmail() != null ? f.getEmail() : "");
        TextField tfContact = new TextField(f.getContactNom() != null ? f.getContactNom() : "");
        TextField tfContactTel = new TextField(f.getContactTelephone() != null ? f.getContactTelephone() : "");
        TextField tfAdresse = new TextField(f.getAdresse() != null ? f.getAdresse() : "");
        TextField tfDelai = new TextField(f.getDelaiLivraisonJours() != null ? String.valueOf(f.getDelaiLivraisonJours()) : "");
        tfDelai.setPromptText("Jours");

        grid.add(new Label("Code *"), 0, 0); grid.add(tfCode, 1, 0);
        grid.add(new Label("Nom *"), 0, 1); grid.add(tfNom, 1, 1);
        grid.add(new Label("Adresse"), 0, 2); grid.add(tfAdresse, 1, 2);
        grid.add(new Label("Ville"), 0, 3); grid.add(tfVille, 1, 3);
        grid.add(new Label("Telephone"), 0, 4); grid.add(tfTelephone, 1, 4);
        grid.add(new Label("Email"), 0, 5); grid.add(tfEmail, 1, 5);
        grid.add(new Label("Contact"), 0, 6); grid.add(tfContact, 1, 6);
        grid.add(new Label("Tel. contact"), 0, 7); grid.add(tfContactTel, 1, 7);
        grid.add(new Label("Delai livraison (j)"), 0, 8); grid.add(tfDelai, 1, 8);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okBtn) {
            if (tfCode.getText().isBlank() || tfNom.getText().isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", "Le code et le nom sont obligatoires.");
                return false;
            }
            f.setCode(tfCode.getText().trim());
            f.setNom(tfNom.getText().trim());
            f.setAdresse(tfAdresse.getText().trim());
            f.setVille(tfVille.getText().trim());
            f.setTelephone(tfTelephone.getText().trim());
            f.setEmail(tfEmail.getText().trim());
            f.setContactNom(tfContact.getText().trim());
            f.setContactTelephone(tfContactTel.getText().trim());
            if (!tfDelai.getText().isBlank()) {
                try { f.setDelaiLivraisonJours(Integer.parseInt(tfDelai.getText().trim())); }
                catch (NumberFormatException ignored) {}
            }
            return true;
        }
        return false;
    }

    @FXML
    private void handleRetour() {
        Router.goTo(Route.DASHBOARD);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
