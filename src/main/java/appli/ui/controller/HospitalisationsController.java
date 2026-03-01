package appli.ui.controller;

import appli.model.*;
import appli.security.SessionManager;
import appli.service.MedicalService;
import appli.service.PatientService;
import appli.service.TriageService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalisationsController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // Salle d'attente
    @FXML private TableView<DossierPriseEnCharge> attenteTable;
    @FXML private TableColumn<DossierPriseEnCharge, String> colDossierNum;
    @FXML private TableColumn<DossierPriseEnCharge, String> colDossierPatient;
    @FXML private TableColumn<DossierPriseEnCharge, String> colDossierMotif;
    @FXML private TableColumn<DossierPriseEnCharge, String> colDossierGravite;
    @FXML private TableColumn<DossierPriseEnCharge, String> colDossierDate;
    @FXML private TableColumn<DossierPriseEnCharge, String> colDossierStatut;

    @FXML private Button btnPrendreEnCharge;
    @FXML private Button btnOrdonnance;
    @FXML private Button btnHospitaliser;

    // Hospitalisations en cours
    @FXML private TableView<Hospitalisation> hospTable;
    @FXML private TableColumn<Hospitalisation, String> colHospSejour;
    @FXML private TableColumn<Hospitalisation, String> colHospChambre;
    @FXML private TableColumn<Hospitalisation, String> colHospDiagnostic;
    @FXML private TableColumn<Hospitalisation, String> colHospDateEntree;
    @FXML private TableColumn<Hospitalisation, String> colHospDateSortie;
    @FXML private TableColumn<Hospitalisation, String> colHospStatut;

    @FXML private Button btnSortie;
    @FXML private Label statusLabel;

    private final MedicalService medicalService = new MedicalService();
    private final TriageService triageService = new TriageService();
    private final PatientService patientService = new PatientService();

    private final ObservableList<DossierPriseEnCharge> attenteData = FXCollections.observableArrayList();
    private final ObservableList<Hospitalisation> hospData = FXCollections.observableArrayList();
    private final Map<Integer, String> patientNames = new HashMap<>();

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        var user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        loadPatientNames();
        setupAttenteColumns();
        setupHospColumns();

        attenteTable.setItems(attenteData);
        hospTable.setItems(hospData);

        btnPrendreEnCharge.setDisable(true);
        btnOrdonnance.setDisable(true);
        btnHospitaliser.setDisable(true);
        btnSortie.setDisable(true);

        attenteTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            btnPrendreEnCharge.setDisable(newVal == null || newVal.getStatut() != DossierPriseEnCharge.Statut.EN_ATTENTE);
            btnOrdonnance.setDisable(newVal == null || newVal.getStatut() == DossierPriseEnCharge.Statut.EN_ATTENTE);
            btnHospitaliser.setDisable(newVal == null || newVal.getStatut() == DossierPriseEnCharge.Statut.EN_ATTENTE);
        });

        hospTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            btnSortie.setDisable(newVal == null);
        });

        loadData();
    }

    private void loadPatientNames() {
        for (Patient p : patientService.getAll()) {
            patientNames.put(p.getId(), p.getNomComplet());
        }
    }

    private void setupAttenteColumns() {
        colDossierNum.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroDossier()));

        colDossierPatient.setCellValueFactory(cell -> {
            int pid = cell.getValue().getPatientId();
            return new SimpleStringProperty(patientNames.getOrDefault(pid, "Patient #" + pid));
        });

        colDossierMotif.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMotifAdmission()));

        colDossierGravite.setCellValueFactory(cell -> {
            var ng = cell.getValue().getNiveauGravite();
            return new SimpleStringProperty(ng != null ? ng.getCode() + " - " + ng.getLibelle() : "");
        });

        colDossierDate.setCellValueFactory(cell -> {
            var dt = cell.getValue().getDateAdmission();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "");
        });

        colDossierStatut.setCellValueFactory(cell -> {
            var s = cell.getValue().getStatut();
            return new SimpleStringProperty(s != null ? s.getLibelle() : "");
        });
    }

    private void setupHospColumns() {
        colHospSejour.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroSejour()));
        colHospChambre.setCellValueFactory(cell -> new SimpleStringProperty("Chambre " + cell.getValue().getChambreId()));
        colHospDiagnostic.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDiagnosticEntree()));
        colHospDateEntree.setCellValueFactory(cell -> {
            var dt = cell.getValue().getDateEntree();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "");
        });
        colHospDateSortie.setCellValueFactory(cell -> {
            var dt = cell.getValue().getDateSortiePrevue();
            return new SimpleStringProperty(dt != null ? dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        colHospStatut.setCellValueFactory(cell -> {
            var s = cell.getValue().getStatut();
            return new SimpleStringProperty(s != null ? s.getLibelle() : "");
        });
    }

    private void loadData() {
        try {
            List<DossierPriseEnCharge> dossiers = triageService.getDossiersOuverts();
            attenteData.clear();
            attenteData.addAll(dossiers);

            List<Hospitalisation> hosps = medicalService.getHospitalisationsEnCours();
            hospData.clear();
            hospData.addAll(hosps);

            statusLabel.setText(dossiers.size() + " dossier(s) ouvert(s) | " + hosps.size() + " hospitalisation(s)");
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger les donnees : " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadPatientNames();
        loadData();
    }

    @FXML
    private void handlePrendreEnCharge() {
        DossierPriseEnCharge selected = attenteTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        User currentUser = SessionManager.getInstance().getCurrentUser();
        try {
            triageService.prendreEnCharge(selected.getId(), currentUser.getId());
            AlertHelper.showInfo("Succes", "Dossier pris en charge");
            loadData();
        } catch (Exception e) {
            AlertHelper.showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleOrdonnance() {
        DossierPriseEnCharge selected = attenteTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Creer ordonnance et cloturer");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes de l'ordonnance");
        notesArea.setPrefRowCount(3);

        DatePicker dateFinPicker = new DatePicker(LocalDate.now().plusDays(7));

        TextArea clotureArea = new TextArea();
        clotureArea.setPromptText("Notes de cloture du dossier");
        clotureArea.setPrefRowCount(2);

        grid.add(new Label("Notes ordonnance :"), 0, 0); grid.add(notesArea, 1, 0);
        grid.add(new Label("Date fin traitement :"), 0, 1); grid.add(dateFinPicker, 1, 1);
        grid.add(new Label("Notes de cloture :"), 0, 2); grid.add(clotureArea, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                medicalService.creerOrdonnance(selected.getId(), currentUser.getId(),
                        notesArea.getText(), dateFinPicker.getValue());
                medicalService.cloturerDossier(selected.getId(), clotureArea.getText(),
                        DossierPriseEnCharge.DestinationSortie.DOMICILE);
                AlertHelper.showInfo("Succes", "Ordonnance creee et dossier cloture - Patient sort a domicile");
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleHospitaliser() {
        DossierPriseEnCharge selected = attenteTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        List<Chambre> chambresDispos;
        try {
            chambresDispos = medicalService.getChambresDisponibles();
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger les chambres : " + e.getMessage());
            return;
        }

        if (chambresDispos.isEmpty()) {
            AlertHelper.showWarning("Aucune chambre", "Aucune chambre n'est disponible actuellement");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Hospitaliser le patient");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        ComboBox<String> chambreCombo = new ComboBox<>();
        Map<String, Chambre> chambreMap = new HashMap<>();
        for (Chambre c : chambresDispos) {
            String label = c.getNumero() + " - " + c.getTypeChambre().getLibelle() +
                    " (" + c.getLitsDisponibles() + " lit(s) dispo.)";
            chambreCombo.getItems().add(label);
            chambreMap.put(label, c);
        }

        TextField motifField = new TextField();
        TextArea diagnosticArea = new TextArea();
        diagnosticArea.setPromptText("Diagnostic d'entree");
        diagnosticArea.setPrefRowCount(3);

        DatePicker sortiePicker = new DatePicker(LocalDate.now().plusDays(3));

        grid.add(new Label("Chambre* :"), 0, 0); grid.add(chambreCombo, 1, 0);
        grid.add(new Label("Motif* :"), 0, 1); grid.add(motifField, 1, 1);
        grid.add(new Label("Diagnostic* :"), 0, 2); grid.add(diagnosticArea, 1, 2);
        grid.add(new Label("Date sortie prevue :"), 0, 3); grid.add(sortiePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;

            Chambre chambre = chambreCombo.getValue() != null ? chambreMap.get(chambreCombo.getValue()) : null;
            if (chambre == null) {
                AlertHelper.showError("Erreur", "Veuillez selectionner une chambre");
                return;
            }
            if (!ValidationUtils.isNotEmpty(motifField.getText()) || !ValidationUtils.isNotEmpty(diagnosticArea.getText())) {
                AlertHelper.showError("Erreur", "Le motif et le diagnostic sont obligatoires");
                return;
            }

            try {
                medicalService.hospitaliser(selected.getId(), chambre.getId(),
                        motifField.getText().trim(), diagnosticArea.getText().trim(), sortiePicker.getValue());
                AlertHelper.showInfo("Succes", "Patient hospitalise en chambre " + chambre.getNumero());
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleSortie() {
        Hospitalisation selected = hospTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sortie du patient");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextArea diagnosticArea = new TextArea();
        diagnosticArea.setPromptText("Diagnostic de sortie");
        diagnosticArea.setPrefRowCount(3);

        ComboBox<Hospitalisation.TypeSortie> typeSortieCombo = new ComboBox<>();
        typeSortieCombo.getItems().addAll(Hospitalisation.TypeSortie.values());
        typeSortieCombo.setValue(Hospitalisation.TypeSortie.GUERISON);

        TextArea observationsArea = new TextArea();
        observationsArea.setPromptText("Observations");
        observationsArea.setPrefRowCount(2);

        grid.add(new Label("Diagnostic sortie* :"), 0, 0); grid.add(diagnosticArea, 1, 0);
        grid.add(new Label("Type de sortie* :"), 0, 1); grid.add(typeSortieCombo, 1, 1);
        grid.add(new Label("Observations :"), 0, 2); grid.add(observationsArea, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            if (!ValidationUtils.isNotEmpty(diagnosticArea.getText())) {
                AlertHelper.showError("Erreur", "Le diagnostic de sortie est obligatoire");
                return;
            }
            try {
                medicalService.sortiePatient(selected.getId(), diagnosticArea.getText().trim(),
                        typeSortieCombo.getValue(), observationsArea.getText().trim());
                AlertHelper.showInfo("Succes", "Patient sorti - Chambre liberee");
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
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
