package appli.ui.controller;

import appli.model.DemandeProduit;
import appli.model.Produit;
import appli.model.User;
import appli.service.StockService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DemandesController {

    // Navigation
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // Formulaire nouvelle demande
    @FXML private ComboBox<Produit> cbProduit;
    @FXML private TextField  tfQuantite;
    @FXML private CheckBox   cbUrgence;
    @FXML private DatePicker dpDateBesoin;
    @FXML private TextArea   taMotif;
    @FXML private Label      labelErreur;
    @FXML private Label      labelConfirmation;

    // Historique
    @FXML private TableView<DemandeProduit>    tableHistorique;
    @FXML private TableColumn<DemandeProduit, String> colNumeroDemande;
    @FXML private TableColumn<DemandeProduit, String> colProduit;
    @FXML private TableColumn<DemandeProduit, String> colQuantite;
    @FXML private TableColumn<DemandeProduit, String> colUrgence;
    @FXML private TableColumn<DemandeProduit, String> colDateDemande;
    @FXML private TableColumn<DemandeProduit, String> colDateBesoin;
    @FXML private TableColumn<DemandeProduit, String> colStatutDemande;

    private final StockService stockService = new StockService();
    private final ObservableList<DemandeProduit> historiqueList = FXCollections.observableArrayList();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter D_FMT   = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        configurerComboProduit();
        configurerTableHistorique();
        chargerHistorique();
    }

    private void configurerComboProduit() {
        cbProduit.setConverter(new StringConverter<>() {
            @Override public String toString(Produit p) { return p != null ? p.getNom() : ""; }
            @Override public Produit fromString(String s) { return null; }
        });
        try {
            List<Produit> produits = stockService.getAllProduits();
            cbProduit.getItems().addAll(produits);
        } catch (Exception e) {
            labelErreur.setText("Impossible de charger les produits : " + e.getMessage());
        }
    }

    private void configurerTableHistorique() {
        colNumeroDemande.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNumeroDemande()));

        colProduit.setCellValueFactory(data -> {
            DemandeProduit d = data.getValue();
            String nom = (d.getProduit() != null) ? d.getProduit().getNom() : "Produit #" + d.getProduitId();
            return new SimpleStringProperty(nom);
        });

        colQuantite.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getQuantiteDemandee())));

        colUrgence.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isUrgence() ? "URGENT" : "Normal"));

        colDateDemande.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateDemande() != null
                        ? data.getValue().getDateDemande().format(DT_FMT) : ""));

        colDateBesoin.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateBesoin() != null
                        ? data.getValue().getDateBesoin().format(D_FMT) : "—"));

        colStatutDemande.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut() != null
                        ? data.getValue().getStatut().getLibelle() : ""));

        // Couleur de ligne selon statut
        tableHistorique.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DemandeProduit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    return;
                }
                switch (item.getStatut()) {
                    case REFUSEE, ANNULEE -> setStyle("-fx-background-color: #ffebee;");
                    case VALIDEE, LIVREE  -> setStyle("-fx-background-color: #e8f5e9;");
                    case EN_ATTENTE       -> setStyle(
                            item.isUrgence() ? "-fx-background-color: #fff8e1;" : "");
                    default               -> setStyle("");
                }
            }
        });

        tableHistorique.setItems(historiqueList);
    }

    private void chargerHistorique() {
        User user = Router.getCurrentUser();
        if (user == null) return;
        try {
            List<DemandeProduit> demandes = stockService.getDemandesByMedecin(user.getId());
            historiqueList.setAll(demandes);
        } catch (Exception e) {
            // Silently ignore — could be permission/data issue
        }
    }

    // =========================================================================
    // Actions
    // =========================================================================

    @FXML
    private void handleEnvoyerDemande() {
        labelErreur.setText("");
        labelConfirmation.setText("");

        // Validation
        if (cbProduit.getValue() == null) {
            labelErreur.setText("Veuillez selectionner un produit.");
            return;
        }
        int quantite;
        try {
            quantite = Integer.parseInt(tfQuantite.getText().trim());
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            labelErreur.setText("La quantite doit etre un entier positif.");
            return;
        }
        if (taMotif.getText().trim().isEmpty()) {
            labelErreur.setText("Le motif est obligatoire.");
            return;
        }

        try {
            DemandeProduit demande = stockService.creerDemandeProduit(
                    cbProduit.getValue().getId(),
                    quantite,
                    null,  // dossierId — non lié ici
                    null,  // hospitalisationId
                    dpDateBesoin.getValue(),
                    cbUrgence.isSelected(),
                    taMotif.getText().trim());

            // Succès : réinitialiser le formulaire
            cbProduit.setValue(null);
            tfQuantite.setText("1");
            cbUrgence.setSelected(false);
            dpDateBesoin.setValue(null);
            taMotif.clear();

            labelConfirmation.setText("Demande " + demande.getNumeroDemande() + " envoyee.");
            chargerHistorique();
        } catch (Exception e) {
            labelErreur.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleRafraichirHistorique() {
        chargerHistorique();
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    @FXML
    private void goToDashboard() {
        Router.goTo(Route.DASHBOARD);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }
}
