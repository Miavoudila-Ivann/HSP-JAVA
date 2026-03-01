package appli.ui.controller;

import appli.model.DossierPriseEnCharge;
import appli.model.Patient;
import appli.security.RoleGuard;
import appli.security.RoleGuard.Fonctionnalite;
import appli.security.SessionManager;
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
import java.util.List;

public class PatientsController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private TextField searchField;

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> colNom;
    @FXML private TableColumn<Patient, String> colPrenom;
    @FXML private TableColumn<Patient, String> colSSN;
    @FXML private TableColumn<Patient, String> colEmail;
    @FXML private TableColumn<Patient, String> colTelephone;
    @FXML private TableColumn<Patient, String> colDateNaissance;

    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnDossier;
    @FXML private Button btnSupprimer;
    @FXML private Label statusLabel;

    private final PatientService patientService = new PatientService();
    private final TriageService triageService = new TriageService();
    private final ObservableList<Patient> patientData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        var user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupTableColumns();
        patientTable.setItems(patientData);
        configurerBoutonsParRole();

        btnModifier.setDisable(true);
        btnDossier.setDisable(true);
        btnSupprimer.setDisable(true);

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            btnModifier.setDisable(newVal == null);
            btnDossier.setDisable(newVal == null);
            btnSupprimer.setDisable(newVal == null);
        });

        loadPatients();
    }

    private void setupTableColumns() {
        colNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));
        colPrenom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrenom()));
        colSSN.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroSecuriteSociale()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colTelephone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelephone()));
        colDateNaissance.setCellValueFactory(cell -> {
            LocalDate dn = cell.getValue().getDateNaissance();
            return new SimpleStringProperty(dn != null ? dn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
    }

    private void configurerBoutonsParRole() {
        boolean canManage = RoleGuard.hasPermission(Fonctionnalite.GESTION_PATIENTS);
        btnAjouter.setVisible(canManage);
        btnAjouter.setManaged(canManage);
        btnModifier.setVisible(canManage);
        btnModifier.setManaged(canManage);
        btnDossier.setVisible(canManage);
        btnDossier.setManaged(canManage);

        boolean isAdmin = SessionManager.getInstance().isAdmin();
        btnSupprimer.setVisible(isAdmin);
        btnSupprimer.setManaged(isAdmin);
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientService.getAll();
            patientData.clear();
            patientData.addAll(patients);
            statusLabel.setText(patients.size() + " patient(s)");
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger les patients : " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) {
            loadPatients();
            return;
        }
        try {
            List<Patient> results = patientService.search(term);
            patientData.clear();
            patientData.addAll(results);
            statusLabel.setText(results.size() + " resultat(s)");
        } catch (Exception e) {
            AlertHelper.showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadPatients();
    }

    @FXML
    private void handleAddPatient() {
        showPatientDialog(null);
    }

    @FXML
    private void handleEditPatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showPatientDialog(selected);
        }
    }

    @FXML
    private void handleDeletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (AlertHelper.showConfirmation("Confirmation",
                "Voulez-vous supprimer le patient " + selected.getNomComplet() + " ?")) {
            try {
                patientService.supprimerPatient(selected.getId());
                AlertHelper.showInfo("Succes", "Patient supprime");
                loadPatients();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCreateDossier() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        showDossierDialog(selected);
    }

    private void showPatientDialog(Patient existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nouveau patient" : "Modifier patient");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField ssnField = new TextField();
        TextField emailField = new TextField();
        TextField telField = new TextField();
        TextField adresseField = new TextField();
        DatePicker dateNaissancePicker = new DatePicker();
        ComboBox<Patient.Sexe> sexeCombo = new ComboBox<>();
        sexeCombo.getItems().addAll(Patient.Sexe.values());

        if (existing != null) {
            nomField.setText(existing.getNom());
            prenomField.setText(existing.getPrenom());
            ssnField.setText(existing.getNumeroSecuriteSociale());
            emailField.setText(existing.getEmail());
            telField.setText(existing.getTelephone());
            adresseField.setText(existing.getAdresse());
            dateNaissancePicker.setValue(existing.getDateNaissance());
            sexeCombo.setValue(existing.getSexe());
        }

        grid.add(new Label("Nom* :"), 0, 0); grid.add(nomField, 1, 0);
        grid.add(new Label("Prenom* :"), 0, 1); grid.add(prenomField, 1, 1);
        grid.add(new Label("N* Secu. Sociale* :"), 0, 2); grid.add(ssnField, 1, 2);
        grid.add(new Label("Email :"), 0, 3); grid.add(emailField, 1, 3);
        grid.add(new Label("Telephone :"), 0, 4); grid.add(telField, 1, 4);
        grid.add(new Label("Adresse :"), 0, 5); grid.add(adresseField, 1, 5);
        grid.add(new Label("Date de naissance :"), 0, 6); grid.add(dateNaissancePicker, 1, 6);
        grid.add(new Label("Sexe :"), 0, 7); grid.add(sexeCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType != ButtonType.OK) return;

            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String ssn = ssnField.getText().trim();

            if (!ValidationUtils.isNotEmpty(nom) || !ValidationUtils.isNotEmpty(prenom)) {
                AlertHelper.showError("Erreur", "Le nom et le prenom sont obligatoires");
                return;
            }
            if (!ValidationUtils.isNotEmpty(ssn)) {
                AlertHelper.showError("Erreur", "Le numero de securite sociale est obligatoire");
                return;
            }

            try {
                Patient patient = existing != null ? existing : new Patient();
                patient.setNom(nom);
                patient.setPrenom(prenom);
                patient.setNumeroSecuriteSociale(ssn);
                patient.setEmail(emailField.getText().trim());
                patient.setTelephone(telField.getText().trim());
                patient.setAdresse(adresseField.getText().trim());
                patient.setDateNaissance(dateNaissancePicker.getValue());
                patient.setSexe(sexeCombo.getValue());

                if (existing == null) {
                    patientService.creerPatient(patient);
                    AlertHelper.showInfo("Succes", "Patient cree avec succes");
                } else {
                    patientService.modifierPatient(patient);
                    AlertHelper.showInfo("Succes", "Patient modifie avec succes");
                }
                loadPatients();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    private void showDossierDialog(Patient patient) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouveau dossier de prise en charge");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        Label patientLabel = new Label(patient.getNomComplet() + " (SSN: " + patient.getNumeroSecuriteSociale() + ")");
        patientLabel.setStyle("-fx-font-weight: bold;");

        TextArea motifArea = new TextArea();
        motifArea.setPromptText("Motif d'admission");
        motifArea.setPrefRowCount(3);

        TextArea symptomesArea = new TextArea();
        symptomesArea.setPromptText("Description des symptomes");
        symptomesArea.setPrefRowCount(3);

        ComboBox<DossierPriseEnCharge.NiveauGravite> graviteCombo = new ComboBox<>();
        for (DossierPriseEnCharge.NiveauGravite ng : DossierPriseEnCharge.NiveauGravite.values()) {
            graviteCombo.getItems().add(ng);
        }

        ComboBox<DossierPriseEnCharge.ModeArrivee> arriveeCombo = new ComboBox<>();
        for (DossierPriseEnCharge.ModeArrivee ma : DossierPriseEnCharge.ModeArrivee.values()) {
            arriveeCombo.getItems().add(ma);
        }

        grid.add(new Label("Patient :"), 0, 0); grid.add(patientLabel, 1, 0);
        grid.add(new Label("Motif* :"), 0, 1); grid.add(motifArea, 1, 1);
        grid.add(new Label("Symptomes* :"), 0, 2); grid.add(symptomesArea, 1, 2);
        grid.add(new Label("Gravite* :"), 0, 3); grid.add(graviteCombo, 1, 3);
        grid.add(new Label("Mode arrivee :"), 0, 4); grid.add(arriveeCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(500);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType != ButtonType.OK) return;

            if (!ValidationUtils.isNotEmpty(motifArea.getText()) || !ValidationUtils.isNotEmpty(symptomesArea.getText())) {
                AlertHelper.showError("Erreur", "Le motif et les symptomes sont obligatoires");
                return;
            }
            if (graviteCombo.getValue() == null) {
                AlertHelper.showError("Erreur", "Veuillez selectionner un niveau de gravite");
                return;
            }

            try {
                triageService.creerDossier(
                        patient.getId(),
                        motifArea.getText().trim(),
                        graviteCombo.getValue(),
                        arriveeCombo.getValue(),
                        symptomesArea.getText().trim()
                );
                AlertHelper.showInfo("Succes", "Dossier de prise en charge cree avec succes");
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
