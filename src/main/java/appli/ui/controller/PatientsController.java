package appli.ui.controller;

import appli.model.Patient;
import appli.model.User;
import appli.security.RoleGuard;
import appli.security.RoleGuard.Fonctionnalite;
import appli.security.SessionManager;
import appli.service.PatientService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PatientsController {

    // --- Navigation bar ---
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // --- Toolbar ---
    @FXML private TextField tfSearch;
    @FXML private Button btnNouveau;
    @FXML private Button btnTriage;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    // --- Table ---
    @FXML private TableView<Patient> tablePatients;
    @FXML private TableColumn<Patient, String>  colNom;
    @FXML private TableColumn<Patient, String>  colPrenom;
    @FXML private TableColumn<Patient, String>  colDateNaissance;
    @FXML private TableColumn<Patient, String>  colNss;
    @FXML private TableColumn<Patient, String>  colSexe;
    @FXML private TableColumn<Patient, String>  colTelephone;
    @FXML private TableColumn<Patient, Integer> colAge;

    // --- Panneau formulaire ---
    @FXML private ScrollPane scrollFormPane;
    @FXML private Label      formTitle;
    @FXML private Label      labelFormError;

    // Identification
    @FXML private TextField  tfNss;
    @FXML private TextField  tfNom;
    @FXML private TextField  tfPrenom;
    @FXML private DatePicker dpDateNaissance;
    @FXML private ComboBox<Patient.Sexe>          cbSexe;
    @FXML private ComboBox<Patient.GroupeSanguin> cbGroupeSanguin;

    // Coordonnées
    @FXML private TextField tfTelephone;
    @FXML private TextField tfMobile;
    @FXML private TextField tfEmail;
    @FXML private TextField tfAdresse;
    @FXML private TextField tfCodePostal;
    @FXML private TextField tfVille;

    // Médical
    @FXML private TextArea  taAllergies;
    @FXML private TextArea  taAntecedents;
    @FXML private TextField tfMedecinTraitant;

    // Contact urgence
    @FXML private TextField tfContactNom;
    @FXML private TextField tfContactTel;
    @FXML private TextField tfContactLien;

    // --- Statut ---
    @FXML private Label labelStatus;

    // --- État interne ---
    private enum FormMode { NONE, CREATE, EDIT }
    private FormMode formMode = FormMode.NONE;
    private Patient  patientEnEdition;

    private final PatientService patientService = new PatientService();
    private final ObservableList<Patient> patientsList = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        configurerComboBoxes();
        configurerColonnesTable();
        configurerSelectionTable();
        configurerVisibiliteParRole();
        chargerPatients();
    }

    private void configurerComboBoxes() {
        cbSexe.getItems().addAll(Patient.Sexe.values());
        cbSexe.setConverter(new StringConverter<>() {
            @Override public String toString(Patient.Sexe s) { return s != null ? s.getLibelle() : ""; }
            @Override public Patient.Sexe fromString(String s) { return null; }
        });

        cbGroupeSanguin.getItems().add(null);
        cbGroupeSanguin.getItems().addAll(Patient.GroupeSanguin.values());
        cbGroupeSanguin.setConverter(new StringConverter<>() {
            @Override public String toString(Patient.GroupeSanguin gs) { return gs != null ? gs.getLibelle() : "— Non renseigne —"; }
            @Override public Patient.GroupeSanguin fromString(String s) { return null; }
        });
    }

    private void configurerColonnesTable() {
        colNom.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNom()));
        colPrenom.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPrenom()));
        colDateNaissance.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDateNaissance() != null
                        ? data.getValue().getDateNaissance().format(DATE_FMT) : ""));
        colNss.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNumeroSecuriteSociale() != null
                        ? data.getValue().getNumeroSecuriteSociale() : ""));
        colSexe.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getSexe() != null
                        ? data.getValue().getSexe().getLibelle() : ""));
        colTelephone.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getTelephone() != null
                        ? data.getValue().getTelephone() : ""));
        colAge.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getAge()));

        tablePatients.setItems(patientsList);
    }

    private void configurerSelectionTable() {
        tablePatients.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            btnModifier.setDisable(!selected);
            btnSupprimer.setDisable(!selected || !SessionManager.getInstance().isAdmin());
        });
    }

    private void configurerVisibiliteParRole() {
        btnTriage.setVisible(RoleGuard.hasPermission(Fonctionnalite.TRIAGE));
        btnTriage.setManaged(btnTriage.isVisible());

        btnSupprimer.setVisible(SessionManager.getInstance().isAdmin());
        btnSupprimer.setManaged(btnSupprimer.isVisible());
    }

    private void chargerPatients() {
        try {
            List<Patient> liste = patientService.getAll();
            patientsList.setAll(liste);
            labelStatus.setText(liste.size() + " patient(s) charge(s)");
        } catch (Exception e) {
            labelStatus.setText("Erreur lors du chargement : " + e.getMessage());
        }
    }

    // =========================================================================
    // Actions barre d'outils
    // =========================================================================

    @FXML
    private void handleRecherche() {
        String terme = tfSearch.getText().trim();
        if (terme.isEmpty()) {
            chargerPatients();
            return;
        }
        try {
            List<Patient> resultats = patientService.search(terme);
            patientsList.setAll(resultats);
            labelStatus.setText(resultats.size() + " resultat(s) pour \"" + terme + "\"");
        } catch (Exception e) {
            labelStatus.setText("Erreur de recherche : " + e.getMessage());
        }
    }

    @FXML
    private void handleAfficherTous() {
        tfSearch.clear();
        chargerPatients();
    }

    @FXML
    private void handleNouveau() {
        formMode = FormMode.CREATE;
        patientEnEdition = null;
        formTitle.setText("Nouveau Patient");
        viderFormulaire();
        afficherFormulaire();
    }

    @FXML
    private void handleModifier() {
        Patient selectionne = tablePatients.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;
        formMode = FormMode.EDIT;
        patientEnEdition = selectionne;
        formTitle.setText("Modifier Patient");
        remplirFormulaire(selectionne);
        afficherFormulaire();
    }

    @FXML
    private void handleSupprimer() {
        Patient selectionne = tablePatients.getSelectionModel().getSelectedItem();
        if (selectionne == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer " + selectionne.getNomComplet() + " ?");
        confirm.setContentText("Cette action est irreversible (conformite RGPD).");
        Optional<ButtonType> reponse = confirm.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            try {
                patientService.supprimerPatient(selectionne.getId());
                chargerPatients();
                fermerFormulaire();
                labelStatus.setText("Patient supprime.");
            } catch (Exception e) {
                labelStatus.setText("Erreur : " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // Actions formulaire
    // =========================================================================

    @FXML
    private void handleEnregistrer() {
        labelFormError.setText("");

        if (tfNom.getText().trim().isEmpty()) {
            labelFormError.setText("Le nom est obligatoire.");
            return;
        }
        if (tfPrenom.getText().trim().isEmpty()) {
            labelFormError.setText("Le prenom est obligatoire.");
            return;
        }
        if (dpDateNaissance.getValue() == null) {
            labelFormError.setText("La date de naissance est obligatoire.");
            return;
        }
        if (cbSexe.getValue() == null) {
            labelFormError.setText("Le sexe est obligatoire.");
            return;
        }

        Patient patient = (formMode == FormMode.EDIT) ? patientEnEdition : new Patient();
        remplirPatientDepuisFormulaire(patient);

        try {
            if (formMode == FormMode.CREATE) {
                patientService.creerPatient(patient);
                labelStatus.setText("Patient cree : " + patient.getNomComplet());
            } else {
                patientService.modifierPatient(patient);
                labelStatus.setText("Patient modifie : " + patient.getNomComplet());
            }
            chargerPatients();
            fermerFormulaire();
        } catch (Exception e) {
            labelFormError.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFormulaire();
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    @FXML
    private void goToTriage() {
        Patient selectionne = tablePatients.getSelectionModel().getSelectedItem();
        if (selectionne != null) {
            Router.goTo(Route.TRIAGE, selectionne);
        } else {
            Router.goTo(Route.TRIAGE);
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

    // =========================================================================
    // Helpers formulaire
    // =========================================================================

    private void afficherFormulaire() {
        scrollFormPane.setVisible(true);
        scrollFormPane.setManaged(true);
        labelFormError.setText("");
    }

    private void fermerFormulaire() {
        scrollFormPane.setVisible(false);
        scrollFormPane.setManaged(false);
        formMode = FormMode.NONE;
        patientEnEdition = null;
    }

    private void viderFormulaire() {
        tfNss.clear();
        tfNom.clear();
        tfPrenom.clear();
        dpDateNaissance.setValue(null);
        cbSexe.setValue(null);
        cbGroupeSanguin.setValue(null);
        tfTelephone.clear();
        tfMobile.clear();
        tfEmail.clear();
        tfAdresse.clear();
        tfCodePostal.clear();
        tfVille.clear();
        taAllergies.clear();
        taAntecedents.clear();
        tfMedecinTraitant.clear();
        tfContactNom.clear();
        tfContactTel.clear();
        tfContactLien.clear();
        labelFormError.setText("");
    }

    private void remplirFormulaire(Patient p) {
        tfNss.setText(p.getNumeroSecuriteSociale() != null ? p.getNumeroSecuriteSociale() : "");
        tfNom.setText(p.getNom() != null ? p.getNom() : "");
        tfPrenom.setText(p.getPrenom() != null ? p.getPrenom() : "");
        dpDateNaissance.setValue(p.getDateNaissance());
        cbSexe.setValue(p.getSexe());
        cbGroupeSanguin.setValue(p.getGroupeSanguin());
        tfTelephone.setText(p.getTelephone() != null ? p.getTelephone() : "");
        tfMobile.setText(p.getTelephoneMobile() != null ? p.getTelephoneMobile() : "");
        tfEmail.setText(p.getEmail() != null ? p.getEmail() : "");
        tfAdresse.setText(p.getAdresse() != null ? p.getAdresse() : "");
        tfCodePostal.setText(p.getCodePostal() != null ? p.getCodePostal() : "");
        tfVille.setText(p.getVille() != null ? p.getVille() : "");
        taAllergies.setText(p.getAllergiesConnues() != null ? p.getAllergiesConnues() : "");
        taAntecedents.setText(p.getAntecedentsMedicaux() != null ? p.getAntecedentsMedicaux() : "");
        tfMedecinTraitant.setText(p.getMedecinTraitant() != null ? p.getMedecinTraitant() : "");
        tfContactNom.setText(p.getPersonneContactNom() != null ? p.getPersonneContactNom() : "");
        tfContactTel.setText(p.getPersonneContactTelephone() != null ? p.getPersonneContactTelephone() : "");
        tfContactLien.setText(p.getPersonneContactLien() != null ? p.getPersonneContactLien() : "");
        labelFormError.setText("");
    }

    private void remplirPatientDepuisFormulaire(Patient p) {
        p.setNumeroSecuriteSociale(orNull(tfNss.getText()));
        p.setNom(tfNom.getText().trim());
        p.setPrenom(tfPrenom.getText().trim());
        p.setDateNaissance(dpDateNaissance.getValue());
        p.setSexe(cbSexe.getValue());
        p.setGroupeSanguin(cbGroupeSanguin.getValue());
        p.setTelephone(orNull(tfTelephone.getText()));
        p.setTelephoneMobile(orNull(tfMobile.getText()));
        p.setEmail(orNull(tfEmail.getText()));
        p.setAdresse(orNull(tfAdresse.getText()));
        p.setCodePostal(orNull(tfCodePostal.getText()));
        p.setVille(orNull(tfVille.getText()));
        p.setAllergiesConnues(orNull(taAllergies.getText()));
        p.setAntecedentsMedicaux(orNull(taAntecedents.getText()));
        p.setMedecinTraitant(orNull(tfMedecinTraitant.getText()));
        p.setPersonneContactNom(orNull(tfContactNom.getText()));
        p.setPersonneContactTelephone(orNull(tfContactTel.getText()));
        p.setPersonneContactLien(orNull(tfContactLien.getText()));
    }

    private String orNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
