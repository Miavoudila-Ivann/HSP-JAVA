package appli.ui.controller;

import appli.model.DossierPriseEnCharge;
import appli.model.DossierPriseEnCharge.ModeArrivee;
import appli.model.DossierPriseEnCharge.NiveauGravite;
import appli.model.Patient;
import appli.model.User;
import appli.service.PatientService;
import appli.service.TriageService;
import appli.util.Route;
import appli.util.Router;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controleur de la vue de creation d'un nouveau dossier de triage (triage.fxml).
 * La secretaire selectionne un patient, saisit le motif, les symptomes,
 * le niveau de gravite et le mode d'arrivee pour creer le dossier.
 */
public class NewTriageCaseController {

    // --- Navigation bar ---
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // --- Section patient ---
    @FXML private TextField   tfSearchPatient;
    @FXML private ListView<Patient> lvPatients;
    @FXML private HBox        boxPatientSelectionne;
    @FXML private Label       labelPatientSelectionne;

    // --- Section arrivée ---
    @FXML private ComboBox<ModeArrivee> cbModeArrivee;

    // --- Section motif / symptômes ---
    @FXML private TextField tfMotif;
    @FXML private TextArea  taSymptomes;

    // --- Section gravité ---
    @FXML private Button btnNiveau5;
    @FXML private Button btnNiveau4;
    @FXML private Button btnNiveau3;
    @FXML private Button btnNiveau2;
    @FXML private Button btnNiveau1;
    @FXML private Label  labelNiveauSelectionne;

    // --- Feedback ---
    @FXML private Label labelErreur;

    // --- État interne ---
    private Patient      patientSelectionne;
    private NiveauGravite niveauSelectionne;

    private final PatientService patientService = new PatientService();
    private final TriageService  triageService  = new TriageService();

    // Styles des boutons de niveau
    private static final String STYLE_N5_ACTIF  = "-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12; -fx-border-color: #B71C1C; -fx-border-width: 3; -fx-border-radius: 6;";
    private static final String STYLE_N5_INACTIF = "-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12;";
    private static final String STYLE_N4_ACTIF  = "-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12; -fx-border-color: #E65100; -fx-border-width: 3; -fx-border-radius: 6;";
    private static final String STYLE_N4_INACTIF = "-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12;";
    private static final String STYLE_N3_ACTIF  = "-fx-background-color: #FFC107; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12; -fx-border-color: #F57F17; -fx-border-width: 3; -fx-border-radius: 6;";
    private static final String STYLE_N3_INACTIF = "-fx-background-color: #FFC107; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12;";
    private static final String STYLE_N2_ACTIF  = "-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12; -fx-border-color: #33691E; -fx-border-width: 3; -fx-border-radius: 6;";
    private static final String STYLE_N2_INACTIF = "-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12;";
    private static final String STYLE_N1_ACTIF  = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12; -fx-border-color: #1B5E20; -fx-border-width: 3; -fx-border-radius: 6;";
    private static final String STYLE_N1_INACTIF = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12;";

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

        configurerComboModeArrivee();
        configurerListePatients();
        recupererPatientPreselectionne();
    }

    private void configurerComboModeArrivee() {
        cbModeArrivee.getItems().addAll(ModeArrivee.values());
        cbModeArrivee.setConverter(new StringConverter<>() {
            @Override public String toString(ModeArrivee m) { return m != null ? m.getLibelle() : ""; }
            @Override public ModeArrivee fromString(String s) { return null; }
        });
        cbModeArrivee.getSelectionModel().selectFirst();
    }

    private void configurerListePatients() {
        lvPatients.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Patient p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    String nss = p.getNumeroSecuriteSociale() != null ? p.getNumeroSecuriteSociale() : "NSS N/A";
                    setText(p.getNomComplet() + "  —  " + nss);
                }
            }
        });

        lvPatients.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                selectionnerPatient(newVal);
            }
        });
    }

    private void recupererPatientPreselectionne() {
        Object data = Router.getNavigationData("triage");
        if (data instanceof Patient p) {
            selectionnerPatient(p);
            Router.clearNavigationData("triage");
        }
    }

    // =========================================================================
    // Recherche patient
    // =========================================================================

    @FXML
    private void handleRecherchePatient() {
        String terme = tfSearchPatient.getText().trim();
        labelErreur.setText("");

        if (terme.isEmpty()) {
            masquerListePatients();
            return;
        }

        try {
            List<Patient> resultats = patientService.search(terme);
            if (resultats.isEmpty()) {
                labelErreur.setText("Aucun patient trouve pour : \"" + terme + "\"");
                masquerListePatients();
            } else if (resultats.size() == 1) {
                // Sélection automatique si un seul résultat
                selectionnerPatient(resultats.get(0));
            } else {
                lvPatients.setItems(FXCollections.observableArrayList(resultats));
                lvPatients.setVisible(true);
                lvPatients.setManaged(true);
            }
        } catch (Exception e) {
            labelErreur.setText("Erreur de recherche : " + e.getMessage());
        }
    }

    private void masquerListePatients() {
        lvPatients.setVisible(false);
        lvPatients.setManaged(false);
    }

    private void selectionnerPatient(Patient p) {
        patientSelectionne = p;
        String nss = p.getNumeroSecuriteSociale() != null ? p.getNumeroSecuriteSociale() : "NSS non renseigne";
        labelPatientSelectionne.setText(p.getNomComplet() + "  |  " + nss + "  |  " + p.getAge() + " ans");
        boxPatientSelectionne.setVisible(true);
        boxPatientSelectionne.setManaged(true);
        masquerListePatients();
        tfSearchPatient.clear();
        labelErreur.setText("");
    }

    // =========================================================================
    // Sélection du niveau de gravité
    // =========================================================================

    @FXML private void handleSelectNiveau5() { selectionnerNiveau(NiveauGravite.NIVEAU_5); }
    @FXML private void handleSelectNiveau4() { selectionnerNiveau(NiveauGravite.NIVEAU_4); }
    @FXML private void handleSelectNiveau3() { selectionnerNiveau(NiveauGravite.NIVEAU_3); }
    @FXML private void handleSelectNiveau2() { selectionnerNiveau(NiveauGravite.NIVEAU_2); }
    @FXML private void handleSelectNiveau1() { selectionnerNiveau(NiveauGravite.NIVEAU_1); }

    private void selectionnerNiveau(NiveauGravite niveau) {
        niveauSelectionne = niveau;

        // Réinitialiser tous les styles
        btnNiveau5.setStyle(STYLE_N5_INACTIF);
        btnNiveau4.setStyle(STYLE_N4_INACTIF);
        btnNiveau3.setStyle(STYLE_N3_INACTIF);
        btnNiveau2.setStyle(STYLE_N2_INACTIF);
        btnNiveau1.setStyle(STYLE_N1_INACTIF);

        // Activer le bouton sélectionné
        switch (niveau) {
            case NIVEAU_5 -> { btnNiveau5.setStyle(STYLE_N5_ACTIF); }
            case NIVEAU_4 -> { btnNiveau4.setStyle(STYLE_N4_ACTIF); }
            case NIVEAU_3 -> { btnNiveau3.setStyle(STYLE_N3_ACTIF); }
            case NIVEAU_2 -> { btnNiveau2.setStyle(STYLE_N2_ACTIF); }
            case NIVEAU_1 -> { btnNiveau1.setStyle(STYLE_N1_ACTIF); }
        }

        labelNiveauSelectionne.setText(
                "Niveau " + niveau.getCode() + " — " + niveau.getLibelle() + " selectionne");
        labelNiveauSelectionne.setStyle("-fx-font-size: 12; -fx-text-fill: " + niveau.getCouleur() + "; -fx-font-weight: bold;");
        labelErreur.setText("");
    }

    // =========================================================================
    // Enregistrement
    // =========================================================================

    @FXML
    private void handleEnregistrer() {
        labelErreur.setText("");

        if (patientSelectionne == null) {
            labelErreur.setText("Veuillez selectionner un patient.");
            return;
        }
        if (taSymptomes.getText().trim().isEmpty()) {
            labelErreur.setText("Les symptomes sont obligatoires.");
            return;
        }
        if (niveauSelectionne == null) {
            labelErreur.setText("Veuillez selectionner un niveau de gravite.");
            return;
        }

        String motif = tfMotif.getText().trim().isEmpty() ? null : tfMotif.getText().trim();

        try {
            DossierPriseEnCharge dossier = triageService.creerDossier(
                    patientSelectionne.getId(),
                    motif,
                    niveauSelectionne,
                    cbModeArrivee.getValue(),
                    taSymptomes.getText().trim()
            );

            // Succès : retour à la vue patients avec confirmation
            Router.goTo(Route.PATIENTS);

        } catch (Exception e) {
            labelErreur.setText("Erreur : " + e.getMessage());
        }
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    @FXML
    private void handleRetour() {
        Router.goTo(Route.PATIENTS);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }
}
