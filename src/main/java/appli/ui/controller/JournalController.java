package appli.ui.controller;

import appli.model.JournalAction;
import appli.model.User;
import appli.repository.UserRepository;
import appli.service.JournalService;
import appli.ui.util.AlertHelper;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controleur de la vue du journal des actions (journal.fxml).
 * Affiche le journal d'audit RGPD avec filtres par utilisateur, type d'action et periode.
 * Accessible a l'admin uniquement.
 */
public class JournalController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterUser;

    @FXML private TableView<JournalAction> journalTable;
    @FXML private TableColumn<JournalAction, String> colDate;
    @FXML private TableColumn<JournalAction, String> colUser;
    @FXML private TableColumn<JournalAction, String> colType;
    @FXML private TableColumn<JournalAction, String> colDescription;
    @FXML private TableColumn<JournalAction, String> colEntite;
    @FXML private TableColumn<JournalAction, String> colIP;

    @FXML private Label statusLabel;

    private final JournalService journalService = new JournalService();
    private final UserRepository userRepository = new UserRepository();
    private final ObservableList<JournalAction> journalData = FXCollections.observableArrayList();
    private final Map<Integer, String> userNames = new HashMap<>();

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        var user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        loadUserNames();
        setupFilterCombos();
        setupTableColumns();
        journalTable.setItems(journalData);
        loadJournalEntries();
    }

    private void loadUserNames() {
        for (User u : userRepository.getAll()) {
            userNames.put(u.getId(), u.getPrenom() + " " + u.getNom());
        }
    }

    private void setupFilterCombos() {
        filterType.getItems().add("Tous");
        for (JournalAction.TypeAction type : JournalAction.TypeAction.values()) {
            filterType.getItems().add(type.getLibelle());
        }
        filterType.getSelectionModel().selectFirst();

        filterUser.getItems().add("Tous");
        for (Map.Entry<Integer, String> entry : userNames.entrySet()) {
            filterUser.getItems().add(entry.getValue());
        }
        filterUser.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        colDate.setCellValueFactory(cell -> {
            LocalDateTime dt = cell.getValue().getDateAction();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "");
        });

        colUser.setCellValueFactory(cell -> {
            Integer userId = cell.getValue().getUserId();
            String name = userId != null ? userNames.getOrDefault(userId, "ID:" + userId) : "Systeme";
            return new SimpleStringProperty(name);
        });

        colType.setCellValueFactory(cell -> {
            JournalAction.TypeAction type = cell.getValue().getTypeAction();
            return new SimpleStringProperty(type != null ? type.getLibelle() : "");
        });

        colDescription.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDescription()));

        colEntite.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEntite()));

        colIP.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAdresseIP()));
    }

    private void loadJournalEntries() {
        try {
            List<JournalAction> entries = journalService.getAll();
            refreshTable(entries);
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger le journal : " + e.getMessage());
        }
    }

    @FXML
    private void handleFilter() {
        try {
            List<JournalAction> entries;

            LocalDate debut = dateDebut.getValue();
            LocalDate fin = dateFin.getValue();

            if (debut != null && fin != null) {
                entries = journalService.getByDateRange(
                        debut.atStartOfDay(),
                        fin.atTime(23, 59, 59)
                );
            } else {
                entries = journalService.getAll();
            }

            String selectedType = filterType.getValue();
            if (selectedType != null && !"Tous".equals(selectedType)) {
                entries = entries.stream()
                        .filter(e -> e.getTypeAction() != null && e.getTypeAction().getLibelle().equals(selectedType))
                        .toList();
            }

            String selectedUser = filterUser.getValue();
            if (selectedUser != null && !"Tous".equals(selectedUser)) {
                entries = entries.stream()
                        .filter(e -> {
                            Integer uid = e.getUserId();
                            String name = uid != null ? userNames.get(uid) : null;
                            return selectedUser.equals(name);
                        })
                        .toList();
            }

            refreshTable(entries);
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Erreur lors du filtrage : " + e.getMessage());
        }
    }

    @FXML
    private void handleResetFilters() {
        dateDebut.setValue(null);
        dateFin.setValue(null);
        filterType.getSelectionModel().selectFirst();
        filterUser.getSelectionModel().selectFirst();
        loadJournalEntries();
    }

    private void refreshTable(List<JournalAction> entries) {
        journalData.clear();
        journalData.addAll(entries);
        statusLabel.setText(entries.size() + " entree(s)");
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
