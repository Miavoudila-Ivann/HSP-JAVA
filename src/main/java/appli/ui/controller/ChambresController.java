package appli.ui.controller;

import appli.model.Chambre;
import appli.model.User;
import appli.service.MedicalService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ChambresController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private ComboBox<String> cbFiltreType;
    @FXML private TableView<Chambre> tableChambres;
    @FXML private TableColumn<Chambre, String> colNumero;
    @FXML private TableColumn<Chambre, String> colType;
    @FXML private TableColumn<Chambre, String> colEtage;
    @FXML private TableColumn<Chambre, String> colCapacite;
    @FXML private TableColumn<Chambre, String> colOccupes;
    @FXML private TableColumn<Chambre, String> colStatut;
    @FXML private TableColumn<Chambre, String> colTarif;

    private final MedicalService medicalService = new MedicalService();

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupTable();
        setupFiltres();
        chargerChambres();
    }

    private void setupTable() {
        colNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumero()));
        colType.setCellValueFactory(c -> {
            Chambre.TypeChambre t = c.getValue().getTypeChambre();
            return new SimpleStringProperty(t != null ? t.getLibelle() : "");
        });
        colEtage.setCellValueFactory(c -> new SimpleStringProperty(
                "Etage " + c.getValue().getEtage() + " — " + c.getValue().getBatiment()));
        colCapacite.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getCapacite())));
        colOccupes.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNbLitsOccupes() + " / " + c.getValue().getCapacite()));
        colStatut.setCellValueFactory(c -> {
            Chambre ch = c.getValue();
            String s;
            if (!ch.isActif()) s = "Inactive";
            else if (ch.isEnMaintenance()) s = "Maintenance";
            else if (ch.getLitsDisponibles() == 0) s = "Pleine";
            else s = ch.getLitsDisponibles() + " lit(s) libre(s)";
            return new SimpleStringProperty(s);
        });
        colTarif.setCellValueFactory(c -> {
            BigDecimal t = c.getValue().getTarifJournalier();
            return new SimpleStringProperty(t != null ? t.toPlainString() + " EUR/j" : "-");
        });
    }

    private void setupFiltres() {
        cbFiltreType.setItems(FXCollections.observableArrayList("Tous",
                "SIMPLE", "DOUBLE", "SOINS_INTENSIFS", "REANIMATION", "URGENCE", "PEDIATRIE", "MATERNITE"));
        cbFiltreType.setValue("Tous");
        cbFiltreType.setOnAction(e -> chargerChambres());
    }

    private void chargerChambres() {
        try {
            List<Chambre> toutes = medicalService.getAllChambres();
            String filtre = cbFiltreType.getValue();
            if (filtre != null && !filtre.equals("Tous")) {
                Chambre.TypeChambre type = Chambre.TypeChambre.valueOf(filtre);
                toutes = toutes.stream().filter(c -> c.getTypeChambre() == type).toList();
            }
            tableChambres.setItems(FXCollections.observableArrayList(toutes));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les chambres : " + e.getMessage());
        }
    }

    @FXML
    private void handleNouvelleChambre() {
        Chambre ch = new Chambre();
        if (ouvrirDialogChambre(ch, "Nouvelle chambre")) {
            try {
                medicalService.creerChambre(ch);
                chargerChambres();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Chambre creee avec succes.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleModifierChambre() {
        Chambre selected = tableChambres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection requise", "Selectionnez une chambre a modifier.");
            return;
        }
        if (ouvrirDialogChambre(selected, "Modifier chambre — " + selected.getNumero())) {
            try {
                medicalService.modifierChambre(selected);
                chargerChambres();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Chambre modifiee avec succes.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleToggleMaintenance() {
        Chambre selected = tableChambres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection requise", "Selectionnez une chambre.");
            return;
        }
        boolean nouvelEtat = !selected.isEnMaintenance();
        String msg = nouvelEtat
                ? "Mettre la chambre " + selected.getNumero() + " en maintenance ?"
                : "Retirer la chambre " + selected.getNumero() + " de la maintenance ?";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText(msg);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                medicalService.toggleMaintenanceChambre(selected.getId(), nouvelEtat);
                chargerChambres();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleSupprimerChambre() {
        Chambre selected = tableChambres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection requise", "Selectionnez une chambre a supprimer.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer la chambre " + selected.getNumero() + " ?\nCette action est irreversible.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                medicalService.supprimerChambre(selected.getId());
                chargerChambres();
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Chambre supprimee.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    private boolean ouvrirDialogChambre(Chambre ch, String titre) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titre);
        dialog.setHeaderText(null);

        ButtonType okBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField tfNumero = new TextField(ch.getNumero() != null ? ch.getNumero() : "");
        tfNumero.setPromptText("Ex: 101A");
        TextField tfEtage = new TextField(ch.getEtage() > 0 ? String.valueOf(ch.getEtage()) : "");
        TextField tfBatiment = new TextField(ch.getBatiment() != null ? ch.getBatiment() : "Principal");
        ComboBox<Chambre.TypeChambre> cbType = new ComboBox<>(
                FXCollections.observableArrayList(Chambre.TypeChambre.values()));
        if (ch.getTypeChambre() != null) cbType.setValue(ch.getTypeChambre());
        TextField tfCapacite = new TextField(ch.getCapacite() > 0 ? String.valueOf(ch.getCapacite()) : "1");
        TextField tfTarif = new TextField(ch.getTarifJournalier() != null ? ch.getTarifJournalier().toPlainString() : "");
        tfTarif.setPromptText("EUR/jour");
        TextArea taNotes = new TextArea(ch.getNotes() != null ? ch.getNotes() : "");
        taNotes.setPrefRowCount(2);

        grid.add(new Label("Numero *"), 0, 0); grid.add(tfNumero, 1, 0);
        grid.add(new Label("Type *"), 0, 1); grid.add(cbType, 1, 1);
        grid.add(new Label("Etage"), 0, 2); grid.add(tfEtage, 1, 2);
        grid.add(new Label("Batiment"), 0, 3); grid.add(tfBatiment, 1, 3);
        grid.add(new Label("Capacite (lits)"), 0, 4); grid.add(tfCapacite, 1, 4);
        grid.add(new Label("Tarif journalier"), 0, 5); grid.add(tfTarif, 1, 5);
        grid.add(new Label("Notes"), 0, 6); grid.add(taNotes, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okBtn) {
            if (tfNumero.getText().isBlank() || cbType.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", "Le numero et le type sont obligatoires.");
                return false;
            }
            ch.setNumero(tfNumero.getText().trim());
            ch.setTypeChambre(cbType.getValue());
            ch.setBatiment(tfBatiment.getText().isBlank() ? "Principal" : tfBatiment.getText().trim());
            ch.setNotes(taNotes.getText().trim());
            try { ch.setEtage(Integer.parseInt(tfEtage.getText().trim())); }
            catch (NumberFormatException ignored) {}
            try { ch.setCapacite(Integer.parseInt(tfCapacite.getText().trim())); }
            catch (NumberFormatException ignored) { ch.setCapacite(1); }
            if (!tfTarif.getText().isBlank()) {
                try { ch.setTarifJournalier(new BigDecimal(tfTarif.getText().trim())); }
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
