package appli.ui.controller;

import appli.model.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // Onglet Produits
    @FXML private TableView<Produit> produitTable;
    @FXML private TableColumn<Produit, String> colProdCode;
    @FXML private TableColumn<Produit, String> colProdNom;
    @FXML private TableColumn<Produit, String> colProdForme;
    @FXML private TableColumn<Produit, String> colProdDangerosite;
    @FXML private TableColumn<Produit, String> colProdStock;
    @FXML private TableColumn<Produit, String> colProdSeuil;

    @FXML private Button btnAddProduit;
    @FXML private Button btnEditProduit;

    // Onglet Fournisseurs
    @FXML private TableView<Fournisseur> fournisseurTable;
    @FXML private TableColumn<Fournisseur, String> colFournCode;
    @FXML private TableColumn<Fournisseur, String> colFournNom;
    @FXML private TableColumn<Fournisseur, String> colFournEmail;
    @FXML private TableColumn<Fournisseur, String> colFournTel;
    @FXML private TableColumn<Fournisseur, String> colFournVille;

    @FXML private Button btnAddFournisseur;
    @FXML private Button btnEditFournisseur;

    // Onglet Reapprovisionnement
    @FXML private ComboBox<String> reapProduitCombo;
    @FXML private ComboBox<String> reapFournisseurCombo;
    @FXML private TextField reapQuantiteField;
    @FXML private TextField reapLotField;
    @FXML private DatePicker reapDatePeremption;
    @FXML private TextField reapPrixField;
    @FXML private TextField reapCommandeField;

    @FXML private TableView<MouvementStock> mouvementTable;
    @FXML private TableColumn<MouvementStock, String> colMvtDate;
    @FXML private TableColumn<MouvementStock, String> colMvtType;
    @FXML private TableColumn<MouvementStock, String> colMvtQuantite;
    @FXML private TableColumn<MouvementStock, String> colMvtMotif;
    @FXML private TableColumn<MouvementStock, String> colMvtRef;

    @FXML private Label statusLabel;

    private final StockService stockService = new StockService();

    private final ObservableList<Produit> produitData = FXCollections.observableArrayList();
    private final ObservableList<Fournisseur> fournisseurData = FXCollections.observableArrayList();
    private final ObservableList<MouvementStock> mouvementData = FXCollections.observableArrayList();

    private final Map<String, Produit> produitMap = new HashMap<>();
    private final Map<String, Fournisseur> fournisseurMap = new HashMap<>();
    private final Map<Integer, Integer> stockQuantities = new HashMap<>();

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        var user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupProduitColumns();
        setupFournisseurColumns();
        setupMouvementColumns();

        produitTable.setItems(produitData);
        fournisseurTable.setItems(fournisseurData);
        mouvementTable.setItems(mouvementData);

        btnEditProduit.setDisable(true);
        btnEditFournisseur.setDisable(true);

        produitTable.getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> btnEditProduit.setDisable(n == null));
        fournisseurTable.getSelectionModel().selectedItemProperty().addListener((obs, old, n) -> btnEditFournisseur.setDisable(n == null));

        loadAll();
    }

    private void setupProduitColumns() {
        colProdCode.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        colProdNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNomComplet()));
        colProdForme.setCellValueFactory(cell -> {
            var f = cell.getValue().getForme();
            return new SimpleStringProperty(f != null ? f.getLibelle() : "");
        });
        colProdDangerosite.setCellValueFactory(cell -> {
            var nd = cell.getValue().getNiveauDangerosite();
            return new SimpleStringProperty(nd != null ? nd.getLibelle() : "");
        });
        colProdStock.setCellValueFactory(cell -> {
            int qty = stockQuantities.getOrDefault(cell.getValue().getId(), 0);
            return new SimpleStringProperty(String.valueOf(qty));
        });
        colProdSeuil.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getSeuilAlerteStock())));
    }

    private void setupFournisseurColumns() {
        colFournCode.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        colFournNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));
        colFournEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colFournTel.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelephone()));
        colFournVille.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getVille()));
    }

    private void setupMouvementColumns() {
        colMvtDate.setCellValueFactory(cell -> {
            var dt = cell.getValue().getDateMouvement();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FORMAT) : "");
        });
        colMvtType.setCellValueFactory(cell -> {
            var t = cell.getValue().getTypeMouvement();
            return new SimpleStringProperty(t != null ? t.getLibelle() : "");
        });
        colMvtQuantite.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getQuantite())));
        colMvtMotif.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMotif()));
        colMvtRef.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReferenceDocument()));
    }

    private void loadAll() {
        try {
            // Produits
            List<Produit> produits = stockService.getAllProduits();
            produitData.clear();
            stockQuantities.clear();
            produitMap.clear();
            for (Produit p : produits) {
                stockQuantities.put(p.getId(), stockService.getQuantiteTotale(p.getId()));
                String label = p.getCode() + " - " + p.getNom();
                produitMap.put(label, p);
            }
            produitData.addAll(produits);

            // Fournisseurs
            List<Fournisseur> fournisseurs = stockService.getAllFournisseurs();
            fournisseurData.clear();
            fournisseurMap.clear();
            for (Fournisseur f : fournisseurs) {
                String label = f.getCode() + " - " + f.getNom();
                fournisseurMap.put(label, f);
            }
            fournisseurData.addAll(fournisseurs);

            // Combos reapprovisionnement
            reapProduitCombo.getItems().clear();
            reapProduitCombo.getItems().addAll(produitMap.keySet());
            reapFournisseurCombo.getItems().clear();
            reapFournisseurCombo.getItems().addAll(fournisseurMap.keySet());

            // Mouvements recents
            mouvementData.clear();

            statusLabel.setText(produits.size() + " produit(s) | " + fournisseurs.size() + " fournisseur(s)");
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Erreur de chargement : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduit() {
        showProduitDialog(null);
    }

    @FXML
    private void handleEditProduit() {
        Produit selected = produitTable.getSelectionModel().getSelectedItem();
        if (selected != null) showProduitDialog(selected);
    }

    @FXML
    private void handleAddFournisseur() {
        showFournisseurDialog(null);
    }

    @FXML
    private void handleEditFournisseur() {
        Fournisseur selected = fournisseurTable.getSelectionModel().getSelectedItem();
        if (selected != null) showFournisseurDialog(selected);
    }

    @FXML
    private void handleReapprovisionner() {
        Produit produit = reapProduitCombo.getValue() != null ? produitMap.get(reapProduitCombo.getValue()) : null;
        Fournisseur fournisseur = reapFournisseurCombo.getValue() != null ? fournisseurMap.get(reapFournisseurCombo.getValue()) : null;

        if (produit == null) {
            AlertHelper.showError("Erreur", "Veuillez selectionner un produit");
            return;
        }

        String qtyStr = reapQuantiteField.getText().trim();
        String lot = reapLotField.getText().trim();
        String prixStr = reapPrixField.getText().trim();

        if (!ValidationUtils.isNotEmpty(qtyStr) || !ValidationUtils.isNotEmpty(lot)) {
            AlertHelper.showError("Erreur", "La quantite et le numero de lot sont obligatoires");
            return;
        }

        try {
            int quantite = Integer.parseInt(qtyStr);
            BigDecimal prix = ValidationUtils.isNotEmpty(prixStr) ? new BigDecimal(prixStr) : BigDecimal.ZERO;
            LocalDate datePeremption = reapDatePeremption.getValue();

            // Utiliser l'emplacement par defaut (id=1 = Pharmacie principale)
            int emplacementId = 1;

            stockService.reapprovisionner(produit.getId(), emplacementId, lot, quantite,
                    datePeremption, prix,
                    fournisseur != null ? fournisseur.getId() : null,
                    reapCommandeField.getText().trim());

            AlertHelper.showInfo("Succes", "Reapprovisionnement effectue : " + quantite + " x " + produit.getNom());

            reapQuantiteField.clear();
            reapLotField.clear();
            reapPrixField.clear();
            reapCommandeField.clear();
            reapDatePeremption.setValue(null);

            loadAll();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Erreur", "Quantite et prix doivent etre des nombres valides");
        } catch (Exception e) {
            AlertHelper.showError("Erreur", e.getMessage());
        }
    }

    private void showProduitDialog(Produit existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nouveau produit" : "Modifier produit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField codeField = new TextField();
        TextField nomField = new TextField();
        TextArea descField = new TextArea();
        descField.setPrefRowCount(2);
        ComboBox<Produit.Forme> formeCombo = new ComboBox<>();
        formeCombo.getItems().addAll(Produit.Forme.values());
        ComboBox<Produit.NiveauDangerosite> dangerCombo = new ComboBox<>();
        dangerCombo.getItems().addAll(Produit.NiveauDangerosite.values());
        TextField uniteField = new TextField();
        TextField prixField = new TextField();
        TextField seuilField = new TextField();

        if (existing != null) {
            codeField.setText(existing.getCode());
            nomField.setText(existing.getNom());
            descField.setText(existing.getDescription());
            formeCombo.setValue(existing.getForme());
            dangerCombo.setValue(existing.getNiveauDangerosite());
            uniteField.setText(existing.getUniteMesure());
            prixField.setText(existing.getPrixUnitaire() != null ? existing.getPrixUnitaire().toPlainString() : "");
            seuilField.setText(String.valueOf(existing.getSeuilAlerteStock()));
        }

        grid.add(new Label("Code* :"), 0, 0); grid.add(codeField, 1, 0);
        grid.add(new Label("Nom* :"), 0, 1); grid.add(nomField, 1, 1);
        grid.add(new Label("Description :"), 0, 2); grid.add(descField, 1, 2);
        grid.add(new Label("Forme :"), 0, 3); grid.add(formeCombo, 1, 3);
        grid.add(new Label("Dangerosite* :"), 0, 4); grid.add(dangerCombo, 1, 4);
        grid.add(new Label("Unite :"), 0, 5); grid.add(uniteField, 1, 5);
        grid.add(new Label("Prix unitaire :"), 0, 6); grid.add(prixField, 1, 6);
        grid.add(new Label("Seuil alerte :"), 0, 7); grid.add(seuilField, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;

            if (!ValidationUtils.isNotEmpty(codeField.getText()) || !ValidationUtils.isNotEmpty(nomField.getText())) {
                AlertHelper.showError("Erreur", "Le code et le nom sont obligatoires");
                return;
            }
            if (dangerCombo.getValue() == null) {
                AlertHelper.showError("Erreur", "Veuillez selectionner un niveau de dangerosite");
                return;
            }

            try {
                Produit produit = existing != null ? existing : new Produit();
                produit.setCode(codeField.getText().trim());
                produit.setNom(nomField.getText().trim());
                produit.setDescription(descField.getText().trim());
                produit.setForme(formeCombo.getValue());
                produit.setNiveauDangerosite(dangerCombo.getValue());
                produit.setUniteMesure(uniteField.getText().trim());
                if (ValidationUtils.isNotEmpty(prixField.getText())) {
                    produit.setPrixUnitaire(new BigDecimal(prixField.getText().trim()));
                }
                if (ValidationUtils.isNotEmpty(seuilField.getText())) {
                    produit.setSeuilAlerteStock(Integer.parseInt(seuilField.getText().trim()));
                }

                if (existing == null) {
                    stockService.creerProduit(produit);
                    AlertHelper.showInfo("Succes", "Produit cree");
                } else {
                    stockService.modifierProduit(produit);
                    AlertHelper.showInfo("Succes", "Produit modifie");
                }
                loadAll();
            } catch (NumberFormatException e) {
                AlertHelper.showError("Erreur", "Prix et seuil doivent etre des nombres valides");
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
    }

    private void showFournisseurDialog(Fournisseur existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Nouveau fournisseur" : "Modifier fournisseur");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField codeField = new TextField();
        TextField nomField = new TextField();
        TextField emailField = new TextField();
        TextField telField = new TextField();
        TextField adresseField = new TextField();
        TextField villeField = new TextField();

        if (existing != null) {
            codeField.setText(existing.getCode());
            nomField.setText(existing.getNom());
            emailField.setText(existing.getEmail());
            telField.setText(existing.getTelephone());
            adresseField.setText(existing.getAdresse());
            villeField.setText(existing.getVille());
        }

        grid.add(new Label("Code* :"), 0, 0); grid.add(codeField, 1, 0);
        grid.add(new Label("Nom* :"), 0, 1); grid.add(nomField, 1, 1);
        grid.add(new Label("Email :"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Telephone :"), 0, 3); grid.add(telField, 1, 3);
        grid.add(new Label("Adresse :"), 0, 4); grid.add(adresseField, 1, 4);
        grid.add(new Label("Ville :"), 0, 5); grid.add(villeField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;

            if (!ValidationUtils.isNotEmpty(codeField.getText()) || !ValidationUtils.isNotEmpty(nomField.getText())) {
                AlertHelper.showError("Erreur", "Le code et le nom sont obligatoires");
                return;
            }

            try {
                Fournisseur fournisseur = existing != null ? existing : new Fournisseur();
                fournisseur.setCode(codeField.getText().trim());
                fournisseur.setNom(nomField.getText().trim());
                fournisseur.setEmail(emailField.getText().trim());
                fournisseur.setTelephone(telField.getText().trim());
                fournisseur.setAdresse(adresseField.getText().trim());
                fournisseur.setVille(villeField.getText().trim());

                if (existing == null) {
                    stockService.creerFournisseur(fournisseur);
                    AlertHelper.showInfo("Succes", "Fournisseur cree");
                } else {
                    stockService.modifierFournisseur(fournisseur);
                    AlertHelper.showInfo("Succes", "Fournisseur modifie");
                }
                loadAll();
            } catch (Exception e) {
                AlertHelper.showError("Erreur", e.getMessage());
            }
        });
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
