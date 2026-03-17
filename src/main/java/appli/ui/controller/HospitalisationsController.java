package appli.ui.controller;

import appli.dao.ChambreDAO;
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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    // Plan des chambres
    @FXML private VBox chambresContainer;
    @FXML private ComboBox<DossierPriseEnCharge> cbDossierPlan;
    @FXML private Label lblDossierPlanInfo;

    private final MedicalService medicalService = new MedicalService();
    private final TriageService triageService = new TriageService();
    private final PatientService patientService = new PatientService();
    private final ChambreDAO chambreDAO = new ChambreDAO();

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

        setupDossierPlanComboBox();
        loadData();
    }

    private void setupDossierPlanComboBox() {
        cbDossierPlan.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(DossierPriseEnCharge d) {
                if (d == null) return "";
                String patient = patientNames.getOrDefault(d.getPatientId(), "Patient #" + d.getPatientId());
                return d.getNumeroDossier() + " - " + patient
                        + " [" + (d.getNiveauGravite() != null ? d.getNiveauGravite().getCode() : "?") + "]";
            }
            @Override public DossierPriseEnCharge fromString(String s) { return null; }
        });
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

        // Mettre a jour le ComboBox du plan avec les dossiers EN_ATTENTE ou EN_COURS
        DossierPriseEnCharge selectedBefore = cbDossierPlan.getValue();
        cbDossierPlan.getItems().setAll(
                attenteData.stream()
                        .filter(d -> d.getStatut() == DossierPriseEnCharge.Statut.EN_ATTENTE
                                || d.getStatut() == DossierPriseEnCharge.Statut.EN_COURS)
                        .toList()
        );
        if (selectedBefore != null && cbDossierPlan.getItems().stream().anyMatch(d -> d.getId() == selectedBefore.getId())) {
            cbDossierPlan.setValue(selectedBefore);
        }

        loadChambres();
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

    // =============== Plan des chambres ===============

    private void loadChambres() {
        chambresContainer.getChildren().clear();
        try {
            List<Chambre> toutes = chambreDAO.findAll();

            // Grouper par etage
            Map<Integer, List<Chambre>> parEtage = new TreeMap<>();
            for (Chambre c : toutes) {
                parEtage.computeIfAbsent(c.getEtage(), k -> new java.util.ArrayList<>()).add(c);
            }

            for (Map.Entry<Integer, List<Chambre>> entry : parEtage.entrySet()) {
                int etage = entry.getKey();
                List<Chambre> chambres = entry.getValue();

                // Titre de l'etage
                Label etageLabel = new Label("Etage " + etage);
                etageLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2D3436;");

                FlowPane flowPane = new FlowPane();
                flowPane.setHgap(12);
                flowPane.setVgap(12);
                flowPane.setPadding(new Insets(5, 0, 10, 0));

                for (Chambre chambre : chambres) {
                    VBox card = createChambreCard(chambre);
                    flowPane.getChildren().add(card);
                }

                chambresContainer.getChildren().addAll(etageLabel, flowPane);
            }

            if (toutes.isEmpty()) {
                Label vide = new Label("Aucune chambre configuree");
                vide.setStyle("-fx-text-fill: #636E72; -fx-font-size: 13;");
                chambresContainer.getChildren().add(vide);
            }
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger les chambres : " + e.getMessage());
        }
    }

    private VBox createChambreCard(Chambre chambre) {
        String bgColor;
        String statusText;

        if (chambre.isEnMaintenance()) {
            bgColor = "#B2BEC3";
            statusText = "Maintenance";
        } else if (!chambre.isActif()) {
            bgColor = "#B2BEC3";
            statusText = "Inactif";
        } else if (chambre.getNbLitsOccupes() >= chambre.getCapacite()) {
            bgColor = "#FF7675";
            statusText = "Complet";
        } else if (chambre.getNbLitsOccupes() > 0) {
            bgColor = "#FDCB6E";
            statusText = chambre.getNbLitsOccupes() + "/" + chambre.getCapacite() + " occupe(s)";
        } else {
            bgColor = "#55EFC4";
            statusText = "Disponible";
        }

        Label numLabel = new Label(chambre.getNumero());
        numLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        Label typeLabel = new Label(chambre.getTypeChambre().getLibelle());
        typeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #2D3436;");

        Label litsLabel = new Label("Lits: " + chambre.getNbLitsOccupes() + "/" + chambre.getCapacite());
        litsLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #2D3436; -fx-font-weight: bold;");

        Label statLabel = new Label(statusText);
        statLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #2D3436; -fx-font-style: italic;");

        VBox card = new VBox(4, numLabel, typeLabel, litsLabel, statLabel);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(140);
        card.setPrefHeight(100);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: " + bgColor + ";" +
                " -fx-background-radius: 10;" +
                " -fx-border-color: #1a1a1a;" +
                " -fx-border-width: 2.5;" +
                " -fx-border-radius: 10;" +
                " -fx-effect: dropshadow(three_pass_box, rgba(0,0,0,0.5), 0, 0, 3, 3);" +
                " -fx-cursor: hand;");

        // Tooltip avec details
        boolean disponible = !chambre.isEnMaintenance() && chambre.isActif()
                && chambre.getNbLitsOccupes() < chambre.getCapacite();
        String hint = disponible ? "\n[Cliquer pour hospitaliser]" : "";
        Tooltip tooltip = new Tooltip(
                "Chambre " + chambre.getNumero() + "\n" +
                "Type: " + chambre.getTypeChambre().getLibelle() + "\n" +
                "Batiment: " + chambre.getBatiment() + "\n" +
                "Capacite: " + chambre.getCapacite() + " lit(s)\n" +
                "Occupes: " + chambre.getNbLitsOccupes() + "\n" +
                "Disponibles: " + chambre.getLitsDisponibles() + "\n" +
                (chambre.getEquipements() != null ? "Equipements: " + chambre.getEquipements() : "") +
                hint
        );
        Tooltip.install(card, tooltip);

        // Clic interactif : hospitaliser directement depuis le plan
        if (disponible) {
            card.setOnMouseClicked(e -> handleHospitaliserDepuisPlan(chambre));
            card.setStyle(card.getStyle() + " -fx-cursor: hand;");
        } else {
            card.setStyle(card.getStyle().replace("-fx-cursor: hand;", "") + " -fx-cursor: default;");
        }

        return card;
    }

    private void handleHospitaliserDepuisPlan(Chambre chambre) {
        DossierPriseEnCharge dossier = cbDossierPlan.getValue();
        if (dossier == null) {
            AlertHelper.showWarning("Aucun patient selectionne",
                    "Selectionnez d'abord un dossier dans la liste deroulante au-dessus du plan.");
            return;
        }

        String patientNom = patientNames.getOrDefault(dossier.getPatientId(), "Patient #" + dossier.getPatientId());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Hospitaliser " + patientNom);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        Label infoLabel = new Label("Chambre : " + chambre.getNumero()
                + " - " + chambre.getTypeChambre().getLibelle()
                + "  (" + chambre.getLitsDisponibles() + " lit(s) dispo.)");
        infoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2D3436;");

        TextField motifField = new TextField();
        motifField.setPromptText("Motif hospitalisation*");
        if (dossier.getMotifAdmission() != null) motifField.setText(dossier.getMotifAdmission());

        TextArea diagnosticArea = new TextArea();
        diagnosticArea.setPromptText("Diagnostic d'entree*");
        diagnosticArea.setPrefRowCount(3);

        DatePicker sortiePicker = new DatePicker(java.time.LocalDate.now().plusDays(3));

        grid.add(infoLabel, 0, 0, 2, 1);
        grid.add(new Label("Motif* :"), 0, 1);     grid.add(motifField, 1, 1);
        grid.add(new Label("Diagnostic* :"), 0, 2); grid.add(diagnosticArea, 1, 2);
        grid.add(new Label("Sortie prevue :"), 0, 3); grid.add(sortiePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            if (!ValidationUtils.isNotEmpty(motifField.getText()) || !ValidationUtils.isNotEmpty(diagnosticArea.getText())) {
                AlertHelper.showError("Erreur", "Le motif et le diagnostic sont obligatoires.");
                return;
            }
            try {
                medicalService.hospitaliser(dossier.getId(), chambre.getId(),
                        motifField.getText().trim(), diagnosticArea.getText().trim(), sortiePicker.getValue());
                AlertHelper.showInfo("Succes", patientNom + " hospitalise(e) en chambre " + chambre.getNumero());
                cbDossierPlan.setValue(null);
                loadPatientNames();
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleRefreshChambres() {
        loadChambres();
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
