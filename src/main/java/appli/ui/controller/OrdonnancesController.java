package appli.ui.controller;

import appli.model.LigneOrdonnance;
import appli.model.Ordonnance;
import appli.model.User;
import appli.security.RoleGuard;
import appli.security.SessionManager;
import appli.service.MedicalService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdonnancesController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private TableView<Ordonnance> tableOrdonnances;
    @FXML private TableColumn<Ordonnance, String> colNumero;
    @FXML private TableColumn<Ordonnance, String> colDate;
    @FXML private TableColumn<Ordonnance, String> colStatut;
    @FXML private TableColumn<Ordonnance, String> colDossier;
    @FXML private TableColumn<Ordonnance, String> colDateFin;
    @FXML private TableColumn<Ordonnance, String> colNotes;

    @FXML private VBox sectionLignes;
    @FXML private Label lblTitreDetail;
    @FXML private TableView<LigneOrdonnance> tableLignes;
    @FXML private TableColumn<LigneOrdonnance, String> colProduit;
    @FXML private TableColumn<LigneOrdonnance, String> colPosologie;
    @FXML private TableColumn<LigneOrdonnance, String> colQuantite;
    @FXML private TableColumn<LigneOrdonnance, String> colVoie;
    @FXML private TableColumn<LigneOrdonnance, String> colDuree;
    @FXML private TableColumn<LigneOrdonnance, String> colInstructions;

    private final MedicalService medicalService = new MedicalService();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupTable();
        setupLignesTable();
        setupFiltres();
        chargerOrdonnances();

        tableOrdonnances.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        afficherLignes(selected);
                    } else {
                        sectionLignes.setVisible(false);
                        sectionLignes.setManaged(false);
                    }
                });
    }

    private void setupTable() {
        colNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroOrdonnance()));
        colDate.setCellValueFactory(c -> {
            Ordonnance o = c.getValue();
            return new SimpleStringProperty(o.getDatePrescription() != null ? o.getDatePrescription().format(dtf) : "");
        });
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut().getLibelle()));
        colDossier.setCellValueFactory(c -> new SimpleStringProperty("Dossier #" + c.getValue().getDossierId()));
        colDateFin.setCellValueFactory(c -> {
            Ordonnance o = c.getValue();
            return new SimpleStringProperty(o.getDateFin() != null ? o.getDateFin().format(df) : "-");
        });
        colNotes.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNotes() != null ? c.getValue().getNotes() : ""));
    }

    private void setupLignesTable() {
        colProduit.setCellValueFactory(c -> {
            LigneOrdonnance l = c.getValue();
            String nom = l.getProduit() != null ? l.getProduit().getNom() : ("Produit #" + l.getProduitId());
            return new SimpleStringProperty(nom);
        });
        colPosologie.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPosologie() != null ? c.getValue().getPosologie() : ""));
        colQuantite.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getQuantite())));
        colVoie.setCellValueFactory(c -> {
            LigneOrdonnance.VoieAdministration v = c.getValue().getVoieAdministration();
            return new SimpleStringProperty(v != null ? v.getLibelle() : "");
        });
        colDuree.setCellValueFactory(c -> {
            Integer d = c.getValue().getDureeJours();
            return new SimpleStringProperty(d != null ? d + " j" : "-");
        });
        colInstructions.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getInstructions() != null ? c.getValue().getInstructions() : ""));
    }

    private void setupFiltres() {
        cbFiltreStatut.setItems(FXCollections.observableArrayList(
                "Tous", "ACTIVE", "TERMINEE", "ANNULEE", "SUSPENDUE"));
        cbFiltreStatut.setValue("Tous");
        cbFiltreStatut.setOnAction(e -> chargerOrdonnances());
    }

    private void chargerOrdonnances() {
        try {
            List<Ordonnance> ordonnances;
            SessionManager session = SessionManager.getInstance();
            User current = session.getCurrentUser();

            if (session.isMedecin()) {
                ordonnances = medicalService.getOrdonnancesByMedecin(current.getId());
            } else {
                ordonnances = medicalService.getAllOrdonnances();
            }

            String filtre = cbFiltreStatut.getValue();
            if (filtre != null && !filtre.equals("Tous")) {
                Ordonnance.Statut statutFiltre = Ordonnance.Statut.valueOf(filtre);
                ordonnances = ordonnances.stream()
                        .filter(o -> o.getStatut() == statutFiltre)
                        .toList();
            }

            tableOrdonnances.setItems(FXCollections.observableArrayList(ordonnances));
            sectionLignes.setVisible(false);
            sectionLignes.setManaged(false);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les ordonnances : " + e.getMessage());
        }
    }

    private void afficherLignes(Ordonnance ordonnance) {
        try {
            List<LigneOrdonnance> lignes = medicalService.getLignesOrdonnance(ordonnance.getId());
            if (lignes.isEmpty()) {
                sectionLignes.setVisible(false);
                sectionLignes.setManaged(false);
                return;
            }
            lblTitreDetail.setText("Lignes de " + ordonnance.getNumeroOrdonnance()
                    + " (" + lignes.size() + " medicament(s))");
            tableLignes.setItems(FXCollections.observableArrayList(lignes));
            sectionLignes.setVisible(true);
            sectionLignes.setManaged(true);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les lignes : " + e.getMessage());
        }
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
