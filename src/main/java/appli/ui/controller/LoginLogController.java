package appli.ui.controller;

import appli.dao.LoginLogDAO;
import appli.model.JournalAction;
import appli.model.User;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoginLogController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private ComboBox<String> cbFiltreType;
    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;

    @FXML private TableView<JournalAction> tableLogs;
    @FXML private TableColumn<JournalAction, String> colDate;
    @FXML private TableColumn<JournalAction, String> colType;
    @FXML private TableColumn<JournalAction, String> colUtilisateur;
    @FXML private TableColumn<JournalAction, String> colDescription;
    @FXML private TableColumn<JournalAction, String> colIP;

    @FXML private Label lblTotal;

    private final LoginLogDAO loginLogDAO = new LoginLogDAO();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupTable();
        setupFiltres();
        chargerLogs();
    }

    private void setupTable() {
        colDate.setCellValueFactory(c -> {
            LocalDateTime d = c.getValue().getDateAction();
            return new SimpleStringProperty(d != null ? d.format(dtf) : "");
        });
        colType.setCellValueFactory(c -> {
            JournalAction.TypeAction t = c.getValue().getTypeAction();
            return new SimpleStringProperty(t != null ? t.getLibelle() : "");
        });
        colUtilisateur.setCellValueFactory(c -> {
            Integer uid = c.getValue().getUserId();
            return new SimpleStringProperty(uid != null ? "Utilisateur #" + uid : "Inconnu");
        });
        colDescription.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDescription() != null ? c.getValue().getDescription() : ""));
        colIP.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAdresseIP() != null ? c.getValue().getAdresseIP() : ""));

        // Color rows based on type
        tableLogs.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(JournalAction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getTypeAction() == JournalAction.TypeAction.ECHEC_CONNEXION) {
                    setStyle("-fx-background-color: #FFEEBA;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void setupFiltres() {
        cbFiltreType.setItems(FXCollections.observableArrayList(
                "Tous", "CONNEXION", "DECONNEXION", "ECHEC_CONNEXION"));
        cbFiltreType.setValue("Tous");
    }

    @FXML
    private void handleAppliquerFiltres() {
        chargerLogs();
    }

    @FXML
    private void handleResetFiltres() {
        cbFiltreType.setValue("Tous");
        dpDebut.setValue(null);
        dpFin.setValue(null);
        chargerLogs();
    }

    private void chargerLogs() {
        try {
            List<JournalAction> logs;

            LocalDate debut = dpDebut.getValue();
            LocalDate fin = dpFin.getValue();

            if (debut != null && fin != null) {
                LocalDateTime dtDebut = debut.atStartOfDay();
                LocalDateTime dtFin = fin.atTime(23, 59, 59);
                logs = loginLogDAO.findByDateRange(dtDebut, dtFin);
            } else {
                String filtre = cbFiltreType.getValue();
                if ("ECHEC_CONNEXION".equals(filtre)) {
                    logs = loginLogDAO.findEchecsConnexion();
                } else {
                    logs = loginLogDAO.findAll();
                }
            }

            // Apply type filter if date range not set
            if ((debut == null || fin == null)) {
                String filtre = cbFiltreType.getValue();
                if (filtre != null && !filtre.equals("Tous") && !filtre.equals("ECHEC_CONNEXION")) {
                    JournalAction.TypeAction type = JournalAction.TypeAction.valueOf(filtre);
                    logs = logs.stream().filter(l -> l.getTypeAction() == type).toList();
                }
            }

            tableLogs.setItems(FXCollections.observableArrayList(logs));
            lblTotal.setText(logs.size() + " entree(s)");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les logs : " + e.getMessage());
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
