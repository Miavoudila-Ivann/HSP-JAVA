package appli.ui.controller;

import appli.model.*;
import appli.model.DossierPriseEnCharge.DestinationSortie;
import appli.model.DossierPriseEnCharge.Statut;
import appli.service.MedicalService;
import appli.service.PDFExportService;
import appli.service.PatientService;
import appli.service.StockService;
import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.time.LocalDate;
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

    // Export
    @FXML private Button btnExporterPDF;

    // État
    private DossierPriseEnCharge dossier;

    private final MedicalService  medicalService  = new MedicalService();
    private final PatientService  patientService  = new PatientService();
    private final PDFExportService pdfExportService = new PDFExportService();
    private final StockService    stockService    = new StockService();

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
                    // Afficher le nombre de lignes
                    try {
                        int nbLignes = medicalService.getLignesOrdonnance(o.getId()).size();
                        sb.append(" : ").append(nbLignes).append(" ligne(s)");
                    } catch (Exception ignored) {}
                    if (o.getNotes() != null && !o.getNotes().isBlank()) {
                        sb.append(" — ").append(o.getNotes(), 0, Math.min(50, o.getNotes().length()));
                        if (o.getNotes().length() > 50) sb.append("...");
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
            Ordonnance ordonnance = medicalService.creerOrdonnance(
                    dossier.getId(),
                    currentUser.getId(),
                    taNotesOrdonnance.getText().trim(),
                    dpDateFinOrdonnance.getValue());
            taNotesOrdonnance.clear();
            dpDateFinOrdonnance.setValue(null);
            chargerOrdonnances();
            labelMessage.setText("Ordonnance creee. Ajoutez les medicaments ci-dessous.");
            labelMessage.setStyle("-fx-text-fill: #388E3C; -fx-font-size: 12;");
            // Step 2 : ouvrir le dialog d'ajout de lignes de prescription
            ouvrirDialogLignesOrdonnance(ordonnance.getId());
            chargerOrdonnances();
        } catch (Exception e) {
            labelErreurOrdonnance.setText("Erreur : " + e.getMessage());
        }
    }

    private void ouvrirDialogLignesOrdonnance(int ordonnanceId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lignes de prescription");
        dialog.setHeaderText("Ajouter des medicaments a l'ordonnance");
        dialog.getDialogPane().setPrefWidth(620);

        ButtonType btnTerminer = new ButtonType("Terminer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(btnTerminer);

        VBox content = new VBox(12);
        content.setPadding(new Insets(15));

        // Liste des lignes deja ajoutees
        Label lblLignes = new Label("Medicaments prescrits :");
        lblLignes.setStyle("-fx-font-weight: bold;");
        ListView<String> listeLignes = new ListView<>();
        listeLignes.setPrefHeight(110);

        Runnable rafraichirListe = () -> {
            try {
                var lignes = medicalService.getLignesOrdonnance(ordonnanceId);
                listeLignes.getItems().clear();
                for (LigneOrdonnance l : lignes) {
                    String nom = "Produit #" + l.getProduitId();
                    try {
                        var opt = stockService.getProduitById(l.getProduitId());
                        if (opt.isPresent()) nom = opt.get().getNom();
                    } catch (Exception ignored) {}
                    String voie = l.getVoieAdministration() != null ? l.getVoieAdministration().name() : "";
                    int duree = l.getDureeJours() != null ? l.getDureeJours() : 0;
                    listeLignes.getItems().add(
                            nom + "  —  " + l.getPosologie()
                            + "  x" + l.getQuantite()
                            + "  " + duree + "j"
                            + "  [" + voie + "]"
                    );
                }
            } catch (Exception ignored) {}
        };
        rafraichirListe.run();

        // Formulaire ajout ligne
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        ComboBox<Produit> cbProduit = new ComboBox<>();
        cbProduit.setPrefWidth(240);
        cbProduit.setConverter(new StringConverter<>() {
            @Override public String toString(Produit p) { return p != null ? "[" + p.getCode() + "] " + p.getNom() : ""; }
            @Override public Produit fromString(String s) { return null; }
        });
        try {
            cbProduit.getItems().addAll(stockService.getAllProduits());
            if (!cbProduit.getItems().isEmpty()) cbProduit.getSelectionModel().selectFirst();
        } catch (Exception e) {
            content.getChildren().add(new Label("Erreur chargement produits : " + e.getMessage()));
        }

        TextField tfPosologie = new TextField();
        tfPosologie.setPromptText("ex: 1 comprime matin et soir");

        Spinner<Integer> spQuantite = new Spinner<>(1, 9999, 1);
        spQuantite.setEditable(true);

        Spinner<Integer> spDuree = new Spinner<>(1, 365, 7);
        spDuree.setEditable(true);

        ComboBox<LigneOrdonnance.VoieAdministration> cbVoie = new ComboBox<>();
        cbVoie.getItems().addAll(LigneOrdonnance.VoieAdministration.values());
        cbVoie.getSelectionModel().selectFirst();

        TextArea taInstructions = new TextArea();
        taInstructions.setPrefRowCount(2);
        taInstructions.setPromptText("Instructions complementaires (optionnel)");

        Label lblErrLigne = new Label();
        lblErrLigne.setStyle("-fx-text-fill: #D63031; -fx-font-size: 12;");

        Button btnAjouter = new Button("+ Ajouter medicament");
        btnAjouter.setStyle("-fx-background-color: #6C5CE7; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-border-color: #1a1a1a; -fx-border-width: 2; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");

        int row = 0;
        grid.add(new Label("Medicament *"), 0, row);
        grid.add(cbProduit, 1, row++);
        grid.add(new Label("Posologie *"), 0, row);
        grid.add(tfPosologie, 1, row++);
        grid.add(new Label("Quantite *"), 0, row);
        grid.add(spQuantite, 1, row++);
        grid.add(new Label("Duree (jours) *"), 0, row);
        grid.add(spDuree, 1, row++);
        grid.add(new Label("Voie d'administration"), 0, row);
        grid.add(cbVoie, 1, row++);
        grid.add(new Label("Instructions"), 0, row);
        grid.add(taInstructions, 1, row++);
        grid.add(lblErrLigne, 0, row, 2, 1);
        row++;
        grid.add(btnAjouter, 1, row);

        btnAjouter.setOnAction(e -> {
            lblErrLigne.setText("");
            if (cbProduit.getValue() == null) { lblErrLigne.setText("Selectionnez un medicament."); return; }
            if (tfPosologie.getText().trim().isEmpty()) { lblErrLigne.setText("La posologie est obligatoire."); return; }
            try {
                medicalService.ajouterLigneOrdonnance(
                        ordonnanceId,
                        cbProduit.getValue().getId(),
                        tfPosologie.getText().trim(),
                        spQuantite.getValue(),
                        spDuree.getValue(),
                        cbVoie.getValue(),
                        taInstructions.getText().trim().isEmpty() ? null : taInstructions.getText().trim()
                );
                rafraichirListe.run();
                tfPosologie.clear();
                spQuantite.getValueFactory().setValue(1);
                spDuree.getValueFactory().setValue(7);
                taInstructions.clear();
            } catch (Exception ex) {
                lblErrLigne.setText("Erreur : " + ex.getMessage());
            }
        });

        content.getChildren().addAll(
                lblLignes, listeLignes,
                new Separator(),
                new Label("Ajouter un medicament :"),
                grid
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(480);
        dialog.getDialogPane().setContent(scroll);
        dialog.showAndWait();
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
    // Export PDF
    // =========================================================================

    @FXML
    private void handleExporterPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le dossier en PDF");
        String nomFichier = "dossier_" + (dossier.getNumeroDossier() != null
                ? dossier.getNumeroDossier().replace("/", "-") : dossier.getId())
                + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        fileChooser.setInitialFileName(nomFichier);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(btnExporterPDF.getScene().getWindow());

        if (file != null) {
            try {
                List<Ordonnance> ordonnances = medicalService.getOrdonnancesByDossierId(dossier.getId());
                List<Hospitalisation> hosps = medicalService.getHospitalisationsByDossierId(dossier.getId());
                Hospitalisation hospitalisation = hosps.isEmpty() ? null : hosps.get(0);

                pdfExportService.exportDossierPriseEnCharge(dossier, ordonnances, hospitalisation, file);

                labelMessage.setText("PDF exporte : " + file.getName());
                labelMessage.setStyle("-fx-text-fill: #388E3C; -fx-font-size: 12;");
            } catch (Exception e) {
                labelMessage.setText("Erreur export PDF : " + e.getMessage());
                labelMessage.setStyle("-fx-text-fill: #F44336; -fx-font-size: 12;");
            }
        }
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
