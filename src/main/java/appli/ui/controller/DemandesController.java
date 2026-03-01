package appli.ui.controller;

import appli.model.DemandeProduit;
import appli.model.Produit;
import appli.model.User;
import appli.repository.UserRepository;
import appli.security.SessionManager;
import appli.service.StockService;
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
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandesController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // Section Medecin
    @FXML private VBox medecinSection;
    @FXML private TableView<DemandeProduit> mesDemandesTable;
    @FXML private TableColumn<DemandeProduit, String> colMesNum;
    @FXML private TableColumn<DemandeProduit, String> colMesProduit;
    @FXML private TableColumn<DemandeProduit, String> colMesQte;
    @FXML private TableColumn<DemandeProduit, String> colMesUrgence;
    @FXML private TableColumn<DemandeProduit, String> colMesStatut;
    @FXML private TableColumn<DemandeProduit, String> colMesDate;
    @FXML private TableColumn<DemandeProduit, String> colMesCommentaire;

    // Section Gestionnaire
    @FXML private VBox gestionnaireSection;
    @FXML private TableView<DemandeProduit> attenteDemandesTable;
    @FXML private TableColumn<DemandeProduit, String> colAttNum;
    @FXML private TableColumn<DemandeProduit, String> colAttProduit;
    @FXML private TableColumn<DemandeProduit, String> colAttQte;
    @FXML private TableColumn<DemandeProduit, String> colAttMedecin;
    @FXML private TableColumn<DemandeProduit, String> colAttUrgence;
    @FXML private TableColumn<DemandeProduit, String> colAttMotif;
    @FXML private TableColumn<DemandeProduit, String> colAttDate;

    @FXML private Button btnValider;
    @FXML private Button btnRefuser;
    @FXML private Label statusLabel;

    private final StockService stockService = new StockService();
    private final UserRepository userRepository = new UserRepository();

    private final ObservableList<DemandeProduit> mesDemandesData = FXCollections.observableArrayList();
    private final ObservableList<DemandeProduit> attenteDemandesData = FXCollections.observableArrayList();

    private final Map<Integer, String> produitNames = new HashMap<>();
    private final Map<Integer, String> userNames = new HashMap<>();
    private final Map<String, Produit> produitMap = new HashMap<>();

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        var user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        loadReferenceData();
        configurerVisibiliteParRole();
        setupMesDemandesColumns();
        setupAttenteColumns();

        mesDemandesTable.setItems(mesDemandesData);
        attenteDemandesTable.setItems(attenteDemandesData);

        btnValider.setDisable(true);
        btnRefuser.setDisable(true);

        attenteDemandesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> {
            btnValider.setDisable(n == null);
            btnRefuser.setDisable(n == null);
        });

        loadData();
    }

    private void loadReferenceData() {
        for (Produit p : stockService.getAllProduits()) {
            produitNames.put(p.getId(), p.getNom());
            produitMap.put(p.getCode() + " - " + p.getNom(), p);
        }
        for (User u : userRepository.getAll()) {
            userNames.put(u.getId(), u.getPrenom() + " " + u.getNom());
        }
    }

    private void configurerVisibiliteParRole() {
        boolean isMedecin = SessionManager.getInstance().isMedecin();
        boolean isGestionnaire = SessionManager.getInstance().isGestionnaire();
        boolean isAdmin = SessionManager.getInstance().isAdmin();

        medecinSection.setVisible(isMedecin || isAdmin);
        medecinSection.setManaged(isMedecin || isAdmin);

        gestionnaireSection.setVisible(isGestionnaire || isAdmin);
        gestionnaireSection.setManaged(isGestionnaire || isAdmin);
    }

    private void setupMesDemandesColumns() {
        colMesNum.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroDemande()));
        colMesProduit.setCellValueFactory(cell -> new SimpleStringProperty(produitNames.getOrDefault(cell.getValue().getProduitId(), "")));
        colMesQte.setCellValueFactory(cell -> {
            var d = cell.getValue();
            return new SimpleStringProperty(d.getQuantiteDemandee() + (d.getQuantiteLivree() > 0 ? " (livre:" + d.getQuantiteLivree() + ")" : ""));
        });
        colMesUrgence.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isUrgence() ? "OUI" : "Non"));
        colMesStatut.setCellValueFactory(cell -> {
            var s = cell.getValue().getStatut();
            return new SimpleStringProperty(s != null ? s.getLibelle() : "");
        });
        colMesDate.setCellValueFactory(cell -> {
            var dt = cell.getValue().getDateDemande();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "");
        });
        colMesCommentaire.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCommentaireTraitement()));
    }

    private void setupAttenteColumns() {
        colAttNum.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroDemande()));
        colAttProduit.setCellValueFactory(cell -> new SimpleStringProperty(produitNames.getOrDefault(cell.getValue().getProduitId(), "")));
        colAttQte.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantiteDemandee())));
        colAttMedecin.setCellValueFactory(cell -> new SimpleStringProperty(userNames.getOrDefault(cell.getValue().getMedecinId(), "")));
        colAttUrgence.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isUrgence() ? "URGENT" : "Normal"));
        colAttMotif.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMotif()));
        colAttDate.setCellValueFactory(cell -> {
            var dt = cell.getValue().getDateDemande();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "");
        });
    }

    private void loadData() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();

            if (SessionManager.getInstance().isMedecin() || SessionManager.getInstance().isAdmin()) {
                List<DemandeProduit> mesDemandes = stockService.getDemandesByMedecin(currentUser.getId());
                mesDemandesData.clear();
                mesDemandesData.addAll(mesDemandes);
            }

            if (SessionManager.getInstance().isGestionnaire() || SessionManager.getInstance().isAdmin()) {
                List<DemandeProduit> enAttente = stockService.getDemandesEnAttente();
                attenteDemandesData.clear();
                attenteDemandesData.addAll(enAttente);
            }

            statusLabel.setText(mesDemandesData.size() + " demande(s) | " + attenteDemandesData.size() + " en attente");
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Impossible de charger les demandes : " + e.getMessage());
        }
    }

    @FXML
    private void handleNewDemand() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle demande de produit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        ComboBox<String> produitCombo = new ComboBox<>();
        produitCombo.getItems().addAll(produitMap.keySet());
        produitCombo.setPromptText("Selectionner un produit");

        TextField quantiteField = new TextField();
        quantiteField.setPromptText("Quantite");

        CheckBox urgenceCheck = new CheckBox("Demande urgente");

        TextArea motifArea = new TextArea();
        motifArea.setPromptText("Motif de la demande");
        motifArea.setPrefRowCount(3);

        DatePicker dateBesoinPicker = new DatePicker(LocalDate.now().plusDays(1));

        grid.add(new Label("Produit* :"), 0, 0); grid.add(produitCombo, 1, 0);
        grid.add(new Label("Quantite* :"), 0, 1); grid.add(quantiteField, 1, 1);
        grid.add(urgenceCheck, 1, 2);
        grid.add(new Label("Motif :"), 0, 3); grid.add(motifArea, 1, 3);
        grid.add(new Label("Date besoin :"), 0, 4); grid.add(dateBesoinPicker, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;

            Produit produit = produitCombo.getValue() != null ? produitMap.get(produitCombo.getValue()) : null;
            if (produit == null) {
                AlertHelper.showError("Erreur", "Veuillez selectionner un produit");
                return;
            }
            if (!ValidationUtils.isNotEmpty(quantiteField.getText())) {
                AlertHelper.showError("Erreur", "La quantite est obligatoire");
                return;
            }

            try {
                int quantite = Integer.parseInt(quantiteField.getText().trim());
                stockService.creerDemandeProduit(produit.getId(), quantite, null, null,
                        dateBesoinPicker.getValue(), urgenceCheck.isSelected(), motifArea.getText().trim());
                AlertHelper.showInfo("Succes", "Demande de produit creee");
                loadData();
            } catch (NumberFormatException e) {
                AlertHelper.showError("Erreur", "La quantite doit etre un nombre");
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleValidate() {
        DemandeProduit selected = attenteDemandesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        AlertHelper.showTextAreaInput("Validation", "Commentaire de validation").ifPresent(commentaire -> {
            try {
                stockService.validerDemande(selected.getId(), commentaire);
                AlertHelper.showInfo("Succes", "Demande validee");
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleRefuse() {
        DemandeProduit selected = attenteDemandesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        AlertHelper.showTextAreaInput("Refus", "Motif du refus").ifPresent(motif -> {
            if (!ValidationUtils.isNotEmpty(motif)) {
                AlertHelper.showError("Erreur", "Le motif du refus est obligatoire");
                return;
            }
            try {
                stockService.refuserDemande(selected.getId(), motif);
                AlertHelper.showInfo("Succes", "Demande refusee");
                loadData();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadReferenceData();
        loadData();
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
