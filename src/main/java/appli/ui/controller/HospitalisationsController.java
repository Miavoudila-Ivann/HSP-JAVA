package appli.ui.controller;

import appli.model.DossierPriseEnCharge;
import appli.model.User;
import appli.service.TriageService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class HospitalisationsController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label labelCompteurs;
    @FXML private Label labelStatus;

    @FXML private TableView<DossierPriseEnCharge> tableDossiers;
    @FXML private TableColumn<DossierPriseEnCharge, String> colDossier;
    @FXML private TableColumn<DossierPriseEnCharge, String> colPatient;
    @FXML private TableColumn<DossierPriseEnCharge, String> colArrivee;
    @FXML private TableColumn<DossierPriseEnCharge, String> colAttente;
    @FXML private TableColumn<DossierPriseEnCharge, String> colGravite;
    @FXML private TableColumn<DossierPriseEnCharge, String> colMode;
    @FXML private TableColumn<DossierPriseEnCharge, String> colStatut;
    @FXML private TableColumn<DossierPriseEnCharge, String> colSymptomes;

    @FXML private Button btnPrendreEnCharge;
    @FXML private Button btnVoirDossier;

    private final TriageService triageService = new TriageService();
    private final ObservableList<DossierPriseEnCharge> dossiersList = FXCollections.observableArrayList();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    // =========================================================================
    // Initialisation
    // =========================================================================

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        configurerColonnes();
        configurerSelection();
        chargerDossiers();
    }

    private void configurerColonnes() {
        colDossier.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNumeroDossier()));

        colPatient.setCellValueFactory(data -> {
            DossierPriseEnCharge d = data.getValue();
            String nom = (d.getPatient() != null)
                    ? d.getPatient().getNomComplet()
                    : "Patient #" + d.getPatientId();
            return new SimpleStringProperty(nom);
        });

        colArrivee.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDateAdmission() != null
                        ? data.getValue().getDateAdmission().format(DT_FMT) : ""));

        colAttente.setCellValueFactory(data -> {
            LocalDateTime admission = data.getValue().getDateAdmission();
            if (admission == null) return new SimpleStringProperty("—");
            long minutes = Duration.between(admission, LocalDateTime.now()).toMinutes();
            if (minutes < 60) return new SimpleStringProperty(minutes + " min");
            return new SimpleStringProperty((minutes / 60) + "h" + String.format("%02d", minutes % 60));
        });

        colGravite.setCellValueFactory(data -> {
            DossierPriseEnCharge.NiveauGravite g = data.getValue().getNiveauGravite();
            return new SimpleStringProperty(g != null
                    ? "N" + g.getCode() + " — " + g.getLibelle() : "—");
        });

        colMode.setCellValueFactory(data -> {
            DossierPriseEnCharge.ModeArrivee m = data.getValue().getModeArrivee();
            return new SimpleStringProperty(m != null ? m.getLibelle() : "—");
        });

        colStatut.setCellValueFactory(data -> {
            DossierPriseEnCharge.Statut s = data.getValue().getStatut();
            return new SimpleStringProperty(s != null ? s.getLibelle() : "—");
        });

        colSymptomes.setCellValueFactory(data -> {
            String s = data.getValue().getSymptomes();
            return new SimpleStringProperty(s != null ? s : "");
        });

        // Colorer les lignes selon la gravité
        tableDossiers.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DossierPriseEnCharge item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNiveauGravite() == null) {
                    setStyle("");
                } else {
                    String couleur = item.getNiveauGravite().getCouleur();
                    setStyle("-fx-border-color: " + couleur + "; -fx-border-width: 0 0 0 4;");
                }
            }
        });

        tableDossiers.setItems(dossiersList);
    }

    private void configurerSelection() {
        tableDossiers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            btnVoirDossier.setDisable(!selected);
            btnPrendreEnCharge.setDisable(!selected
                    || newVal.getStatut() != DossierPriseEnCharge.Statut.EN_ATTENTE);
        });
    }

    private void chargerDossiers() {
        try {
            List<DossierPriseEnCharge> dossiers = triageService.getDossiersOuverts();
            // Trier : plus urgent (priorite basse) en premier, puis par date d'arrivée
            dossiers.sort(Comparator
                    .comparingInt(DossierPriseEnCharge::getPrioriteTriage)
                    .thenComparing(d -> d.getDateAdmission() != null
                            ? d.getDateAdmission() : LocalDateTime.MAX));
            dossiersList.setAll(dossiers);

            long enAttente = dossiers.stream()
                    .filter(d -> d.getStatut() == DossierPriseEnCharge.Statut.EN_ATTENTE)
                    .count();
            long enCours = dossiers.stream()
                    .filter(d -> d.getStatut() == DossierPriseEnCharge.Statut.EN_COURS)
                    .count();
            labelCompteurs.setText(
                    enAttente + " en attente  |  " + enCours + " en cours  |  Total : " + dossiers.size());
        } catch (Exception e) {
            labelStatus.setText("Erreur chargement : " + e.getMessage());
        }
    }

    // =========================================================================
    // Actions
    // =========================================================================

    @FXML
    private void handleRafraichir() {
        chargerDossiers();
        labelStatus.setText("Liste actualisee.");
    }

    @FXML
    private void handlePrendreEnCharge() {
        DossierPriseEnCharge selected = tableDossiers.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        User currentUser = Router.getCurrentUser();
        try {
            DossierPriseEnCharge updated = triageService.prendreEnCharge(
                    selected.getId(), currentUser.getId());
            Router.goTo(Route.DOSSIER, updated);
        } catch (Exception e) {
            labelStatus.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleVoirDossier() {
        DossierPriseEnCharge selected = tableDossiers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Router.goTo(Route.DOSSIER, selected);
        }
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    @FXML
    private void goToDashboard() {
        Router.goTo(Route.DASHBOARD);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }
}
