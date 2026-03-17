package appli.ui.controller;

import appli.dao.RendezVousDAO;
import appli.dao.PatientDAO;
import appli.dao.UserDAO;
import appli.model.Patient;
import appli.model.Rendezvous;
import appli.model.User;
import appli.security.SessionManager;
import appli.ui.util.AlertHelper;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RendezVousController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private ToggleButton tglAVenir;

    @FXML private TableView<Rendezvous> rdvTable;
    @FXML private TableColumn<Rendezvous, String> colNumero;
    @FXML private TableColumn<Rendezvous, String> colPatient;
    @FXML private TableColumn<Rendezvous, String> colMedecin;
    @FXML private TableColumn<Rendezvous, String> colDate;
    @FXML private TableColumn<Rendezvous, String> colDuree;
    @FXML private TableColumn<Rendezvous, String> colType;
    @FXML private TableColumn<Rendezvous, String> colStatut;
    @FXML private TableColumn<Rendezvous, String> colMotif;
    @FXML private TableColumn<Rendezvous, String> colLieu;

    @FXML private Button btnNouveauRdv;
    @FXML private Button btnModifier;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;
    @FXML private Button btnRealise;
    @FXML private Button btnReporter;
    @FXML private Button btnSupprimer;

    @FXML private Label statusLabel;

    private final RendezVousDAO rdvDAO = new RendezVousDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final UserDAO userDAO = new UserDAO();

    private final ObservableList<Rendezvous> rdvData = FXCollections.observableArrayList();
    private List<Rendezvous> allRdv;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupColumns();
        setupFiltreStatut();
        rdvTable.setItems(rdvData);
        setupButtonsState();
        loadData();
    }

    private void setupColumns() {
        colNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroRdv()));
        colPatient.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPatientNomComplet()));
        colMedecin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedecinNomComplet()));
        colDate.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getDateHeure();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "");
        });
        colDuree.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDureeMinutes() + " min"));
        colType.setCellValueFactory(c -> {
            Rendezvous.TypeRdv t = c.getValue().getTypeRdv();
            return new SimpleStringProperty(t != null ? t.getLibelle() : "");
        });
        colStatut.setCellValueFactory(c -> {
            Rendezvous.Statut s = c.getValue().getStatut();
            return new SimpleStringProperty(s != null ? s.getLibelle() : "");
        });
        colMotif.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotif()));
        colLieu.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLieu() != null ? c.getValue().getLieu() : ""));

        // Coloration des lignes selon statut
        rdvTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Rendezvous item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    setStyle(switch (item.getStatut()) {
                        case CONFIRME  -> "-fx-background-color: #d4efdf;";
                        case REALISE   -> "-fx-background-color: #d6eaf8;";
                        case ANNULE    -> "-fx-background-color: #fadbd8;";
                        case REPORTE   -> "-fx-background-color: #fdebd0;";
                        default        -> "";
                    });
                }
            }
        });
    }

    private void setupFiltreStatut() {
        cbFiltreStatut.getItems().add("Tous");
        for (Rendezvous.Statut s : Rendezvous.Statut.values()) cbFiltreStatut.getItems().add(s.getLibelle());
        cbFiltreStatut.setValue("Tous");
    }

    private void setupButtonsState() {
        btnModifier.setDisable(true);
        btnConfirmer.setDisable(true);
        btnAnnuler.setDisable(true);
        btnRealise.setDisable(true);
        btnReporter.setDisable(true);
        btnSupprimer.setDisable(true);

        rdvTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean none = sel == null;
            btnModifier.setDisable(none || sel.getStatut() == Rendezvous.Statut.ANNULE || sel.getStatut() == Rendezvous.Statut.REALISE);
            btnConfirmer.setDisable(none || sel.getStatut() != Rendezvous.Statut.PLANIFIE);
            btnAnnuler.setDisable(none || sel.getStatut() == Rendezvous.Statut.ANNULE || sel.getStatut() == Rendezvous.Statut.REALISE);
            btnRealise.setDisable(none || sel.getStatut() == Rendezvous.Statut.REALISE || sel.getStatut() == Rendezvous.Statut.ANNULE);
            btnReporter.setDisable(none || sel.getStatut() == Rendezvous.Statut.ANNULE || sel.getStatut() == Rendezvous.Statut.REALISE);
            btnSupprimer.setDisable(none);
        });
    }

    private void loadData() {
        try {
            allRdv = rdvDAO.findAll();
            applyFiltres();
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger les rendez-vous : " + e.getMessage());
        }
    }

    private void applyFiltres() {
        String search = searchField.getText().trim().toLowerCase();
        String filtreStatut = cbFiltreStatut.getValue();
        boolean aVenirOnly = tglAVenir != null && tglAVenir.isSelected();

        List<Rendezvous> filtered = allRdv.stream()
                .filter(r -> {
                    if (!search.isEmpty()) {
                        String patient = r.getPatientNomComplet().toLowerCase();
                        String medecin = r.getMedecinNomComplet().toLowerCase();
                        String num = r.getNumeroRdv() != null ? r.getNumeroRdv().toLowerCase() : "";
                        if (!patient.contains(search) && !medecin.contains(search) && !num.contains(search))
                            return false;
                    }
                    if (filtreStatut != null && !filtreStatut.equals("Tous")) {
                        if (!r.getStatut().getLibelle().equals(filtreStatut)) return false;
                    }
                    if (aVenirOnly) {
                        if (r.getDateHeure() == null || r.getDateHeure().isBefore(LocalDateTime.now())) return false;
                        if (r.getStatut() == Rendezvous.Statut.ANNULE || r.getStatut() == Rendezvous.Statut.REALISE) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        rdvData.setAll(filtered);
        statusLabel.setText(filtered.size() + " rendez-vous affiche(s) | Total : " + allRdv.size());
    }

    @FXML private void handleSearch() { applyFiltres(); }
    @FXML private void handleFiltreChange() { applyFiltres(); }
    @FXML private void handleToggleAVenir() { applyFiltres(); }
    @FXML private void handleRefresh() { loadData(); }

    @FXML
    private void handleNouveauRdv() {
        List<Patient> patients = patientDAO.findAll();
        List<User> medecins = userDAO.findByRole(User.Role.MEDECIN);

        if (patients.isEmpty()) { AlertHelper.showWarning("Attention", "Aucun patient enregistre."); return; }
        if (medecins.isEmpty()) { AlertHelper.showWarning("Attention", "Aucun medecin enregistre."); return; }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouveau rendez-vous");
        GridPane grid = buildFormGrid(patients, medecins, null);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                Rendezvous rdv = extractFromForm(grid, patients, medecins);
                rdv.setNumeroRdv(rdvDAO.generateNumeroRdv());
                User current = SessionManager.getInstance().getCurrentUser();
                if (current != null) rdv.setCreePar(current.getId());
                rdvDAO.save(rdv);
                AlertHelper.showInfo("Succes", "Rendez-vous " + rdv.getNumeroRdv() + " cree.");
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleModifier() {
        Rendezvous sel = rdvTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        List<Patient> patients = patientDAO.findAll();
        List<User> medecins = userDAO.findByRole(User.Role.MEDECIN);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier le rendez-vous");
        GridPane grid = buildFormGrid(patients, medecins, sel);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                Rendezvous updated = extractFromForm(grid, patients, medecins);
                updated.setId(sel.getId());
                updated.setNumeroRdv(sel.getNumeroRdv());
                rdvDAO.update(updated);
                AlertHelper.showInfo("Succes", "Rendez-vous modifie.");
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleConfirmer() {
        Rendezvous sel = rdvTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            rdvDAO.updateStatut(sel.getId(), Rendezvous.Statut.CONFIRME);
            AlertHelper.showInfo("Succes", "Rendez-vous confirme.");
            loadData();
        } catch (Exception e) { AlertHelper.showError("Erreur", e.getMessage()); }
    }

    @FXML
    private void handleAnnuler() {
        Rendezvous sel = rdvTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Annuler ce rendez-vous ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.YES) return;
            try {
                rdvDAO.updateStatut(sel.getId(), Rendezvous.Statut.ANNULE);
                AlertHelper.showInfo("Succes", "Rendez-vous annule.");
                loadData();
            } catch (Exception e) { AlertHelper.showError("Erreur", e.getMessage()); }
        });
    }

    @FXML
    private void handleRealise() {
        Rendezvous sel = rdvTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            rdvDAO.updateStatut(sel.getId(), Rendezvous.Statut.REALISE);
            AlertHelper.showInfo("Succes", "Rendez-vous marque comme realise.");
            loadData();
        } catch (Exception e) { AlertHelper.showError("Erreur", e.getMessage()); }
    }

    @FXML
    private void handleReporter() {
        Rendezvous sel = rdvTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reporter le rendez-vous");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 20, 10, 10));

        DatePicker datePicker = new DatePicker(sel.getDateHeure() != null ? sel.getDateHeure().toLocalDate() : LocalDate.now().plusDays(1));
        Spinner<Integer> heureSpin = new Spinner<>(0, 23, sel.getDateHeure() != null ? sel.getDateHeure().getHour() : 9);
        Spinner<Integer> minuteSpin = new Spinner<>(0, 59, 0, 5);

        grid.add(new Label("Nouvelle date* :"), 0, 0); grid.add(datePicker, 1, 0);
        grid.add(new Label("Heure* :"), 0, 1); grid.add(heureSpin, 1, 1);
        grid.add(new Label("Minute :"), 0, 2); grid.add(minuteSpin, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            if (datePicker.getValue() == null) { AlertHelper.showError("Erreur", "Date obligatoire."); return; }
            try {
                LocalDateTime nouvelleDate = LocalDateTime.of(datePicker.getValue(),
                        LocalTime.of(heureSpin.getValue(), minuteSpin.getValue()));
                sel.setDateHeure(nouvelleDate);
                sel.setStatut(Rendezvous.Statut.REPORTE);
                rdvDAO.update(sel);
                AlertHelper.showInfo("Succes", "Rendez-vous reporte au " + nouvelleDate.format(DT_FORMAT));
                loadData();
            } catch (Exception e) { AlertHelper.showError("Erreur", e.getMessage()); }
        });
    }

    @FXML
    private void handleSupprimer() {
        Rendezvous sel = rdvTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer definitivement le rendez-vous " + sel.getNumeroRdv() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.YES) return;
            try {
                rdvDAO.delete(sel.getId());
                AlertHelper.showInfo("Succes", "Rendez-vous supprime.");
                loadData();
            } catch (Exception e) { AlertHelper.showError("Erreur", e.getMessage()); }
        });
    }

    // ──────────────────────────────────────────────
    // Formulaire de creation / modification
    // ──────────────────────────────────────────────

    private GridPane buildFormGrid(List<Patient> patients, List<User> medecins, Rendezvous existing) {
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10); grid.setPadding(new Insets(20, 25, 10, 10));

        // Patient
        ComboBox<Patient> cbPatient = new ComboBox<>();
        cbPatient.getItems().addAll(patients);
        cbPatient.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Patient p) { return p == null ? "" : p.getPrenom() + " " + p.getNom(); }
            @Override public Patient fromString(String s) { return null; }
        });
        cbPatient.setPrefWidth(260);

        // Medecin
        ComboBox<User> cbMedecin = new ComboBox<>();
        cbMedecin.getItems().addAll(medecins);
        cbMedecin.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(User u) { return u == null ? "" : "Dr. " + u.getPrenom() + " " + u.getNom(); }
            @Override public User fromString(String s) { return null; }
        });
        cbMedecin.setPrefWidth(260);

        // Date / heure
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        Spinner<Integer> heureSpin = new Spinner<>(0, 23, 9);
        Spinner<Integer> minuteSpin = new Spinner<>(0, 59, 0, 5);

        // Type
        ComboBox<Rendezvous.TypeRdv> cbType = new ComboBox<>();
        cbType.getItems().addAll(Rendezvous.TypeRdv.values());
        cbType.setValue(Rendezvous.TypeRdv.CONSULTATION);

        // Duree
        Spinner<Integer> dureeSpin = new Spinner<>(5, 240, 30, 5);

        // Motif
        TextArea motifArea = new TextArea();
        motifArea.setPrefRowCount(3);
        motifArea.setPromptText("Motif du rendez-vous*");

        // Notes
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(2);
        notesArea.setPromptText("Notes (optionnel)");

        // Lieu
        TextField lieuField = new TextField();
        lieuField.setPromptText("Ex: Salle 12, Bloc B");

        // Pré-remplissage si modification
        if (existing != null) {
            patients.stream().filter(p -> p.getId() == existing.getPatientId()).findFirst().ifPresent(cbPatient::setValue);
            medecins.stream().filter(u -> u.getId() == existing.getMedecinId()).findFirst().ifPresent(cbMedecin::setValue);
            if (existing.getDateHeure() != null) {
                datePicker.setValue(existing.getDateHeure().toLocalDate());
                heureSpin.getValueFactory().setValue(existing.getDateHeure().getHour());
                minuteSpin.getValueFactory().setValue(existing.getDateHeure().getMinute());
            }
            if (existing.getTypeRdv() != null) cbType.setValue(existing.getTypeRdv());
            dureeSpin.getValueFactory().setValue(existing.getDureeMinutes());
            if (existing.getMotif() != null) motifArea.setText(existing.getMotif());
            if (existing.getNotes() != null) notesArea.setText(existing.getNotes());
            if (existing.getLieu() != null) lieuField.setText(existing.getLieu());
        }

        int row = 0;
        grid.add(new Label("Patient* :"), 0, row); grid.add(cbPatient, 1, row++);
        grid.add(new Label("Medecin* :"), 0, row); grid.add(cbMedecin, 1, row++);
        grid.add(new Label("Date* :"), 0, row); grid.add(datePicker, 1, row++);
        grid.add(new Label("Heure* :"), 0, row); grid.add(heureSpin, 1, row++);
        grid.add(new Label("Minute :"), 0, row); grid.add(minuteSpin, 1, row++);
        grid.add(new Label("Type :"), 0, row); grid.add(cbType, 1, row++);
        grid.add(new Label("Duree (min) :"), 0, row); grid.add(dureeSpin, 1, row++);
        grid.add(new Label("Motif* :"), 0, row); grid.add(motifArea, 1, row++);
        grid.add(new Label("Notes :"), 0, row); grid.add(notesArea, 1, row++);
        grid.add(new Label("Lieu :"), 0, row); grid.add(lieuField, 1, row);

        // On stocke les composants avec setId pour les récupérer dans extractFromForm
        cbPatient.setId("cbPatient"); cbMedecin.setId("cbMedecin");
        datePicker.setId("datePicker"); heureSpin.setId("heureSpin"); minuteSpin.setId("minuteSpin");
        cbType.setId("cbType"); dureeSpin.setId("dureeSpin");
        motifArea.setId("motifArea"); notesArea.setId("notesArea"); lieuField.setId("lieuField");

        return grid;
    }

    @SuppressWarnings("unchecked")
    private Rendezvous extractFromForm(GridPane grid, List<Patient> patients, List<User> medecins) {
        ComboBox<Patient>         cbPatient  = (ComboBox<Patient>) grid.lookup("#cbPatient");
        ComboBox<User>            cbMedecin  = (ComboBox<User>)    grid.lookup("#cbMedecin");
        DatePicker                datePicker = (DatePicker)        grid.lookup("#datePicker");
        Spinner<Integer>          heureSpin  = (Spinner<Integer>)  grid.lookup("#heureSpin");
        Spinner<Integer>          minuteSpin = (Spinner<Integer>)  grid.lookup("#minuteSpin");
        ComboBox<Rendezvous.TypeRdv> cbType  = (ComboBox<Rendezvous.TypeRdv>) grid.lookup("#cbType");
        Spinner<Integer>          dureeSpin  = (Spinner<Integer>)  grid.lookup("#dureeSpin");
        TextArea                  motifArea  = (TextArea)          grid.lookup("#motifArea");
        TextArea                  notesArea  = (TextArea)          grid.lookup("#notesArea");
        TextField                 lieuField  = (TextField)         grid.lookup("#lieuField");

        if (cbPatient.getValue() == null) throw new RuntimeException("Veuillez selectionner un patient.");
        if (cbMedecin.getValue() == null) throw new RuntimeException("Veuillez selectionner un medecin.");
        if (datePicker.getValue() == null) throw new RuntimeException("Veuillez choisir une date.");
        if (motifArea.getText().isBlank()) throw new RuntimeException("Le motif est obligatoire.");

        Rendezvous rdv = new Rendezvous();
        rdv.setPatientId(cbPatient.getValue().getId());
        rdv.setMedecinId(cbMedecin.getValue().getId());
        rdv.setDateHeure(LocalDateTime.of(datePicker.getValue(),
                LocalTime.of(heureSpin.getValue(), minuteSpin.getValue())));
        rdv.setTypeRdv(cbType.getValue());
        rdv.setDureeMinutes(dureeSpin.getValue());
        rdv.setMotif(motifArea.getText().trim());
        rdv.setNotes(notesArea.getText().isBlank() ? null : notesArea.getText().trim());
        rdv.setLieu(lieuField.getText().isBlank() ? null : lieuField.getText().trim());
        rdv.setStatut(Rendezvous.Statut.PLANIFIE);
        return rdv;
    }

    @FXML private void goToDashboard() { Router.goTo(Route.DASHBOARD); }
    @FXML private void handleLogout() { Router.logout(); }
}
