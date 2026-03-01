package appli.ui.controller;

import appli.model.*;
import appli.model.DossierPriseEnCharge.DestinationSortie;
import appli.model.DossierPriseEnCharge.Statut;
import appli.service.MedicalService;
import appli.service.PatientService;
import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DossierController {

    // Navigation
    @FXML private Label labelTitreDossier;
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // Header patient / dossier
    @FXML private Label labelPatientNom;
    @FXML private Label labelPatientInfo;
    @FXML private Label labelNumeroDossier;
    @FXML private Label labelArrivee;
    @FXML private Label labelGravite;
    @FXML private Label labelModeArrivee;
    @FXML private Label labelStatut;
    @FXML private Label labelSymptomes;

    // Message global
    @FXML private Label labelMessage;

    // Ordonnance
    @FXML private Label    labelOrdonnancesExistantes;
    @FXML private TextArea taNotesOrdonnance;
    @FXML private DatePicker dpDateFinOrdonnance;
    @FXML private Label    labelErreurOrdonnance;
    @FXML private Button   btnCreerOrdonnance;

    // Hospitalisation
    @FXML private VBox   formHospitalisation;
    @FXML private Label  labelHospitalisationInfo;
    @FXML private ComboBox<Chambre> cbChambre;
    @FXML private TextArea taDiagnostic;
    @FXML private DatePicker dpSortiePrevue;
    @FXML private Label  labelErreurHospitalisation;
    @FXML private Button btnHospitaliser;

    // Clôture
    @FXML private VBox   formCloture;
    @FXML private ComboBox<DestinationSortie> cbDestination;
    @FXML private TextArea taNotesCloture;
    @FXML private Label  labelErreurCloture;
    @FXML private Button btnCloturer;
    @FXML private Label  labelDossierClos;

    // État
    private DossierPriseEnCharge dossier;

    private final MedicalService  medicalService  = new MedicalService();
    private final PatientService  patientService  = new PatientService();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

        // Récupérer le dossier passé en navigation data
        Object data = Router.getNavigationData("dossier");
        if (data instanceof DossierPriseEnCharge d) {
            dossier = d;
            Router.clearNavigationData("dossier");
            configurerComboboxes();
            afficherDossier();
            chargerOrdonnances();
            chargerHospitalisation();
            configurerSectionCloture();
        } else {
            labelMessage.setText("Erreur : aucun dossier recu.");
            labelMessage.setStyle("-fx-text-fill: #F44336;");
        }
    }

    private void configurerComboboxes() {
        // ComboBox Destination sortie
        cbDestination.getItems().addAll(DestinationSortie.values());
        cbDestination.setConverter(new StringConverter<>() {
            @Override public String toString(DestinationSortie d) { return d != null ? d.getLibelle() : ""; }
            @Override public DestinationSortie fromString(String s) { return null; }
        });
        cbDestination.getSelectionModel().selectFirst();

        // ComboBox Chambres disponibles
        cbChambre.setConverter(new StringConverter<>() {
            @Override public String toString(Chambre c) { return c != null ? c.getDescription() + " (" + c.getLitsDisponibles() + " lit(s) libre)" : ""; }
            @Override public Chambre fromString(String s) { return null; }
        });
        try {
            List<Chambre> chambres = medicalService.getChambresDisponibles();
            cbChambre.getItems().addAll(chambres);
            if (!chambres.isEmpty()) cbChambre.getSelectionModel().selectFirst();
        } catch (Exception e) {
            labelErreurHospitalisation.setText("Erreur chargement chambres : " + e.getMessage());
        }
    }

    // =========================================================================
    // Affichage header
    // =========================================================================

    private void afficherDossier() {
        labelTitreDossier.setText("Dossier " + dossier.getNumeroDossier());
        labelNumeroDossier.setText(dossier.getNumeroDossier());
        labelArrivee.setText(dossier.getDateAdmission() != null
                ? "Arrivee : " + dossier.getDateAdmission().format(DT_FMT) : "");
        labelModeArrivee.setText(dossier.getModeArrivee() != null
                ? dossier.getModeArrivee().getLibelle() : "—");
        labelStatut.setText(dossier.getStatut() != null
                ? dossier.getStatut().getLibelle() : "—");
        labelSymptomes.setText(dossier.getSymptomes() != null
                ? "Symptomes : " + dossier.getSymptomes() : "");

        // Gravité badge coloré
        if (dossier.getNiveauGravite() != null) {
            DossierPriseEnCharge.NiveauGravite g = dossier.getNiveauGravite();
            labelGravite.setText("N" + g.getCode() + " — " + g.getLibelle());
            labelGravite.setStyle(
                    "-fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-background-radius: 8; "
                    + "-fx-text-fill: white; -fx-background-color: " + g.getCouleur() + ";");
        }

        // Patient
        if (dossier.getPatient() != null) {
            Patient p = dossier.getPatient();
            labelPatientNom.setText(p.getNomComplet());
            labelPatientInfo.setText(p.getAge() + " ans  |  "
                    + (p.getSexe() != null ? p.getSexe().getLibelle() : "")
                    + (p.getGroupeSanguin() != null ? "  |  Gr. " + p.getGroupeSanguin().getLibelle() : ""));
        } else {
            // Essayer de charger le patient via le service
            try {
                Optional<Patient> opt = patientService.getById(dossier.getPatientId());
                opt.ifPresentOrElse(p -> {
                    labelPatientNom.setText(p.getNomComplet());
                    labelPatientInfo.setText(p.getAge() + " ans"
                            + (p.getSexe() != null ? "  |  " + p.getSexe().getLibelle() : "")
                            + (p.getGroupeSanguin() != null ? "  |  Gr. " + p.getGroupeSanguin().getLibelle() : ""));
                }, () -> {
                    labelPatientNom.setText("Patient #" + dossier.getPatientId());
                    labelPatientInfo.setText("");
                });
            } catch (Exception e) {
                labelPatientNom.setText("Patient #" + dossier.getPatientId());
                labelPatientInfo.setText("");
            }
        }
    }

    // =========================================================================
    // Ordonnance
    // =========================================================================

    private void chargerOrdonnances() {
        try {
            List<Ordonnance> ordonnances = medicalService.getOrdonnancesByDossierId(dossier.getId());
            if (ordonnances.isEmpty()) {
                labelOrdonnancesExistantes.setText("Aucune ordonnance pour ce dossier.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Ordonnance o : ordonnances) {
                    sb.append("• ").append(o.getNumeroOrdonnance())
                      .append(" — ").append(o.getStatut().getLibelle());
                    if (o.getNotes() != null && !o.getNotes().isBlank()) {
                        sb.append(" : ").append(o.getNotes(), 0, Math.min(60, o.getNotes().length()));
                        if (o.getNotes().length() > 60) sb.append("...");
                    }
                    sb.append("\n");
                }
                labelOrdonnancesExistantes.setText(sb.toString().trim());
            }
        } catch (Exception e) {
            labelOrdonnancesExistantes.setText("Erreur chargement ordonnances.");
        }

        // Désactiver la création si dossier clos
        boolean actif = dossier.getStatut() != Statut.TERMINE;
        btnCreerOrdonnance.setDisable(!actif);
        taNotesOrdonnance.setDisable(!actif);
        dpDateFinOrdonnance.setDisable(!actif);
    }

    @FXML
    private void handleCreerOrdonnance() {
        labelErreurOrdonnance.setText("");

        if (taNotesOrdonnance.getText().trim().isEmpty()) {
            labelErreurOrdonnance.setText("Les prescriptions sont obligatoires.");
            return;
        }

        User currentUser = Router.getCurrentUser();
        try {
            medicalService.creerOrdonnance(
                    dossier.getId(),
                    currentUser.getId(),
                    taNotesOrdonnance.getText().trim(),
                    dpDateFinOrdonnance.getValue());
            taNotesOrdonnance.clear();
            dpDateFinOrdonnance.setValue(null);
            chargerOrdonnances();
            labelMessage.setText("Ordonnance creee.");
            labelMessage.setStyle("-fx-text-fill: #388E3C; -fx-font-size: 12;");
        } catch (Exception e) {
            labelErreurOrdonnance.setText("Erreur : " + e.getMessage());
        }
    }

    // =========================================================================
    // Hospitalisation
    // =========================================================================

    private void chargerHospitalisation() {
        try {
            List<Hospitalisation> hosps = medicalService.getHospitalisationsByDossierId(dossier.getId());

            if (!hosps.isEmpty()) {
                // Il existe déjà une hospitalisation
                Hospitalisation h = hosps.get(0);
                labelHospitalisationInfo.setText(
                        "Sejour " + h.getNumeroSejour()
                        + " — Statut : " + h.getStatut().getLibelle()
                        + (h.getDiagnosticEntree() != null ? "\nDiagnostic : " + h.getDiagnosticEntree() : ""));
                formHospitalisation.setVisible(false);
                formHospitalisation.setManaged(false);
            } else if (dossier.getStatut() == Statut.TERMINE) {
                labelHospitalisationInfo.setText("Dossier clos — aucune hospitalisation.");
                formHospitalisation.setVisible(false);
                formHospitalisation.setManaged(false);
            } else {
                // Aucune hospitalisation : afficher le formulaire
                labelHospitalisationInfo.setText(cbChambre.getItems().isEmpty()
                        ? "Aucune chambre disponible actuellement."
                        : "");
                formHospitalisation.setVisible(!cbChambre.getItems().isEmpty());
                formHospitalisation.setManaged(!cbChambre.getItems().isEmpty());
                btnHospitaliser.setDisable(cbChambre.getItems().isEmpty());
            }
        } catch (Exception e) {
            labelHospitalisationInfo.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleHospitaliser() {
        labelErreurHospitalisation.setText("");

        if (cbChambre.getValue() == null) {
            labelErreurHospitalisation.setText("Veuillez choisir une chambre.");
            return;
        }
        if (taDiagnostic.getText().trim().isEmpty()) {
            labelErreurHospitalisation.setText("Le diagnostic est obligatoire.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer l'hospitalisation");
        confirm.setHeaderText("Hospitaliser ce patient ?");
        confirm.setContentText("Chambre : " + cbChambre.getValue().getDescription());
        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                medicalService.hospitaliser(
                        dossier.getId(),
                        cbChambre.getValue().getId(),
                        dossier.getMotifAdmission(),
                        taDiagnostic.getText().trim(),
                        dpSortiePrevue.getValue());
                dossier.setStatut(Statut.HOSPITALISE);
                chargerHospitalisation();
                labelStatut.setText(Statut.HOSPITALISE.getLibelle());
                labelMessage.setText("Patient hospitalise avec succes.");
                labelMessage.setStyle("-fx-text-fill: #388E3C; -fx-font-size: 12;");
            } catch (Exception e) {
                labelErreurHospitalisation.setText("Erreur : " + e.getMessage());
            }
        });
    }

    // =========================================================================
    // Clôture
    // =========================================================================

    private void configurerSectionCloture() {
        boolean clos = (dossier.getStatut() == Statut.TERMINE);
        formCloture.setVisible(!clos);
        formCloture.setManaged(!clos);
        labelDossierClos.setVisible(clos);
        labelDossierClos.setManaged(clos);
    }

    @FXML
    private void handleCloturerDossier() {
        labelErreurCloture.setText("");

        if (cbDestination.getValue() == null) {
            labelErreurCloture.setText("Veuillez choisir une destination de sortie.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la cloture");
        confirm.setHeaderText("Cloturer ce dossier ?");
        confirm.setContentText("Destination : " + cbDestination.getValue().getLibelle()
                + "\nCette action ne peut pas etre annulee.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                medicalService.cloturerDossier(
                        dossier.getId(),
                        taNotesCloture.getText().trim().isEmpty() ? null : taNotesCloture.getText().trim(),
                        cbDestination.getValue());
                // Mettre à jour l'UI
                dossier.setStatut(Statut.TERMINE);
                labelStatut.setText(Statut.TERMINE.getLibelle());
                configurerSectionCloture();
                chargerOrdonnances();
                labelMessage.setText("Dossier cloture. Destination : " + cbDestination.getValue().getLibelle());
                labelMessage.setStyle("-fx-text-fill: #388E3C; -fx-font-size: 12;");
            } catch (Exception e) {
                labelErreurCloture.setText("Erreur : " + e.getMessage());
            }
        });
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    @FXML
    private void handleRetour() {
        Router.goTo(Route.HOSPITALISATIONS);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }
}
