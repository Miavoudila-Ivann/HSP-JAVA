package appli.ui.controller;

import appli.model.*;
import appli.service.StockService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StockController {

    // Navigation
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label labelStatus;

    // ── TAB 1 : PRODUITS ──────────────────────────────────────────────────────
    @FXML private TextField tfSearchProduit;
    @FXML private TableView<Produit>                tableProduits;
    @FXML private TableColumn<Produit, String>      colProduitCode;
    @FXML private TableColumn<Produit, String>      colProduitNom;
    @FXML private TableColumn<Produit, String>      colProduitForme;
    @FXML private TableColumn<Produit, String>      colProduitStock;
    @FXML private TableColumn<Produit, String>      colProduitSeuil;
    @FXML private TableColumn<Produit, String>      colProduitPrix;
    @FXML private TableColumn<Produit, String>      colProduitDanger;
    @FXML private TableColumn<Produit, String>      colProduitActif;
    @FXML private Button    btnNouveauProduit;
    @FXML private Button    btnModifierProduit;
    @FXML private Button    btnToggleActifProduit;
    @FXML private ScrollPane scrollProduitForm;
    @FXML private Label     labelProduitFormTitre;
    @FXML private TextField tfProduitCode;
    @FXML private TextField tfProduitNom;
    @FXML private TextField tfProduitNomComm;
    @FXML private ComboBox<Produit.Forme>            cbProduitForme;
    @FXML private TextField tfProduitDosage;
    @FXML private TextField tfProduitUnite;
    @FXML private TextField tfProduitPrix;
    @FXML private TextField tfProduitSeuil;
    @FXML private ComboBox<Produit.NiveauDangerosite> cbProduitDanger;
    @FXML private CheckBox  cbProduitOrdonnance;
    @FXML private CheckBox  cbProduitStupefiant;
    @FXML private Label     labelProduitFormErreur;

    // ── TAB 2 : FOURNISSEURS ──────────────────────────────────────────────────
    @FXML private TableView<Fournisseur>              tableFournisseurs;
    @FXML private TableColumn<Fournisseur, String>   colFournisseurCode;
    @FXML private TableColumn<Fournisseur, String>   colFournisseurNom;
    @FXML private TableColumn<Fournisseur, String>   colFournisseurTel;
    @FXML private TableColumn<Fournisseur, String>   colFournisseurEmail;
    @FXML private TableColumn<Fournisseur, String>   colFournisseurDelai;
    @FXML private TableColumn<Fournisseur, String>   colFournisseurActif;
    @FXML private Button    btnNouveauFournisseur;
    @FXML private Button    btnModifierFournisseur;
    @FXML private Button    btnAssocierFournisseur;
    @FXML private ScrollPane scrollFournisseurForm;
    @FXML private Label     labelFournisseurFormTitre;
    @FXML private TextField tfFournisseurCode;
    @FXML private TextField tfFournisseurNom;
    @FXML private TextField tfFournisseurRS;
    @FXML private TextField tfFournisseurTel;
    @FXML private TextField tfFournisseurEmail;
    @FXML private TextField tfFournisseurContact;
    @FXML private TextField tfFournisseurDelai;
    @FXML private Label     labelFournisseurFormErreur;
    // Association
    @FXML private ComboBox<Produit>  cbAssoProduit;
    @FXML private TextField tfAssoRef;
    @FXML private TextField tfAssoPrix;
    @FXML private TextField tfAssoDelai;
    @FXML private TextField tfAssoQteMin;
    @FXML private CheckBox  cbAssoEstPrincipal;
    @FXML private Label     labelAssoErreur;
    @FXML private Label     labelAssoConfirm;

    // ── TAB 3 : DEMANDES ──────────────────────────────────────────────────────
    @FXML private Label labelDemandesCompteur;
    @FXML private TableView<DemandeProduit>              tableDemandes;
    @FXML private TableColumn<DemandeProduit, String>   colDemandeNum;
    @FXML private TableColumn<DemandeProduit, String>   colDemandeProduit;
    @FXML private TableColumn<DemandeProduit, String>   colDemandeQte;
    @FXML private TableColumn<DemandeProduit, String>   colDemandeMedecin;
    @FXML private TableColumn<DemandeProduit, String>   colDemandeDate;
    @FXML private TableColumn<DemandeProduit, String>   colDemandeUrgence;
    @FXML private TableColumn<DemandeProduit, String>   colDemandeMotif;
    @FXML private TextField tfCommentaireTraitement;
    @FXML private Button    btnValiderDemande;
    @FXML private Button    btnRefuserDemande;
    @FXML private Label     labelDemandeAction;

    // ── TAB 4 : RÉAPPROVISIONNEMENT ───────────────────────────────────────────
    @FXML private ComboBox<Produit>          cbReapproProduit;
    @FXML private ComboBox<EmplacementStock> cbReapproEmplacement;
    @FXML private TextField tfReapproLot;
    @FXML private TextField tfReapproQuantite;
    @FXML private DatePicker dpReapproPeremption;
    @FXML private TextField tfReapproPrix;
    @FXML private ComboBox<Fournisseur>      cbReapproFournisseur;
    @FXML private TextField tfReapproCommande;
    @FXML private Label     labelReapproErreur;
    @FXML private Label     labelReapproConfirm;

    // ── TAB 5 : ALERTES ───────────────────────────────────────────────────────
    @FXML private TextField tfJoursAlerte;
    @FXML private TableView<Produit>           tableStockBas;
    @FXML private TableColumn<Produit, String> colAlerteCode;
    @FXML private TableColumn<Produit, String> colAlerteNom;
    @FXML private TableColumn<Produit, String> colAlerteStock;
    @FXML private TableColumn<Produit, String> colAlerteSeuil;
    @FXML private TableColumn<Produit, String> colAlerteDanger;
    @FXML private TableView<Stock>             tablePeremption;
    @FXML private TableColumn<Stock, String>   colPeremProduit;
    @FXML private TableColumn<Stock, String>   colPeremLot;
    @FXML private TableColumn<Stock, String>   colPeremQte;
    @FXML private TableColumn<Stock, String>   colPeremDate;
    @FXML private TableColumn<Stock, String>   colPeremJours;

    // ── État ──────────────────────────────────────────────────────────────────
    private enum FormMode { NONE, CREATE, EDIT }
    private FormMode produitFormMode = FormMode.NONE;
    private FormMode fournisseurFormMode = FormMode.NONE;
    private Produit     produitEnEdition;
    private Fournisseur fournisseurEnEdition;

    private final StockService stockService = new StockService();
    private final ObservableList<Produit>        produitsList     = FXCollections.observableArrayList();
    private final ObservableList<Fournisseur>    fournisseursList = FXCollections.observableArrayList();
    private final ObservableList<DemandeProduit> demandesList     = FXCollections.observableArrayList();
    private final ObservableList<Produit>        stockBasList     = FXCollections.observableArrayList();
    private final ObservableList<Stock>          peremptionList   = FXCollections.observableArrayList();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter D_FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        configurerTab1Produits();
        configurerTab2Fournisseurs();
        configurerTab3Demandes();
        configurerTab4Reappro();
        configurerTab5Alertes();

        chargerProduits();
        chargerFournisseurs();
        chargerDemandes();
        chargerAlertes();
    }

    // =========================================================================
    // TAB 1 — PRODUITS
    // =========================================================================

    private void configurerTab1Produits() {
        cbProduitForme.getItems().addAll(Produit.Forme.values());
        cbProduitForme.setConverter(new StringConverter<>() {
            @Override public String toString(Produit.Forme f) { return f != null ? f.getLibelle() : ""; }
            @Override public Produit.Forme fromString(String s) { return null; }
        });

        cbProduitDanger.getItems().addAll(Produit.NiveauDangerosite.values());
        cbProduitDanger.setConverter(new StringConverter<>() {
            @Override public String toString(Produit.NiveauDangerosite d) { return d != null ? d.getLibelle() : ""; }
            @Override public Produit.NiveauDangerosite fromString(String s) { return null; }
        });
        cbProduitDanger.getSelectionModel().selectFirst();

        colProduitCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colProduitNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colProduitForme.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getForme() != null ? d.getValue().getForme().getLibelle() : ""));
        colProduitStock.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(stockService.getQuantiteTotale(d.getValue().getId()))));
        colProduitSeuil.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getSeuilAlerteStock())));
        colProduitPrix.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPrixUnitaire() != null ? d.getValue().getPrixUnitaire().toPlainString() : "—"));
        colProduitDanger.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNiveauDangerosite() != null ? d.getValue().getNiveauDangerosite().getLibelle() : ""));
        colProduitActif.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isActif() ? "Oui" : "Non"));

        tableProduits.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                if (!item.isActif()) setStyle("-fx-background-color: #fafafa; -fx-opacity: 0.6;");
                else if (stockService.getQuantiteTotale(item.getId()) <= item.getSeuilAlerteStock())
                    setStyle("-fx-background-color: #fff3e0;");
                else setStyle("");
            }
        });

        tableProduits.setItems(produitsList);
        tableProduits.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            boolean has = sel != null;
            btnModifierProduit.setDisable(!has);
            btnToggleActifProduit.setDisable(!has);
            if (has) btnToggleActifProduit.setText(sel.isActif() ? "Desactiver" : "Activer");
        });
    }

    private void chargerProduits() {
        try {
            produitsList.setAll(stockService.getAllProduits());
            // Mettre à jour cbAssoProduit et cbReapproProduit
            cbAssoProduit.getItems().setAll(produitsList);
            cbReapproProduit.getItems().setAll(produitsList);
        } catch (Exception e) {
            labelStatus.setText("Erreur chargement produits : " + e.getMessage());
        }
    }

    @FXML private void handleSearchProduit() {
        String filtre = tfSearchProduit.getText().trim().toLowerCase();
        if (filtre.isEmpty()) { chargerProduits(); return; }
        List<Produit> filtres = stockService.getAllProduits().stream()
                .filter(p -> p.getNom().toLowerCase().contains(filtre)
                          || p.getCode().toLowerCase().contains(filtre))
                .toList();
        produitsList.setAll(filtres);
    }

    @FXML private void handleAfficherTousProduits() {
        tfSearchProduit.clear();
        chargerProduits();
    }

    @FXML private void handleNouveauProduit() {
        produitFormMode = FormMode.CREATE;
        produitEnEdition = null;
        labelProduitFormTitre.setText("Nouveau produit");
        viderFormProduit();
        afficherFormProduit(true);
    }

    @FXML private void handleModifierProduit() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        produitFormMode = FormMode.EDIT;
        produitEnEdition = sel;
        labelProduitFormTitre.setText("Modifier produit");
        remplirFormProduit(sel);
        afficherFormProduit(true);
    }

    @FXML private void handleToggleActifProduit() {
        Produit sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        sel.setActif(!sel.isActif());
        try {
            stockService.modifierProduit(sel);
            chargerProduits();
            labelStatus.setText(sel.isActif() ? "Produit active." : "Produit desactive.");
        } catch (Exception e) {
            labelStatus.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML private void handleSauvegarderProduit() {
        labelProduitFormErreur.setText("");
        String code  = tfProduitCode.getText().trim();
        String nom   = tfProduitNom.getText().trim();
        String unite = tfProduitUnite.getText().trim();
        if (code.isEmpty())  { labelProduitFormErreur.setText("Le code est obligatoire."); return; }
        if (nom.isEmpty())   { labelProduitFormErreur.setText("Le nom est obligatoire."); return; }
        if (unite.isEmpty()) { labelProduitFormErreur.setText("L'unite de mesure est obligatoire."); return; }

        int seuil = 10;
        try {
            String s = tfProduitSeuil.getText().trim();
            if (!s.isEmpty()) seuil = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            labelProduitFormErreur.setText("Le seuil doit etre un entier.");
            return;
        }

        Produit p = (produitFormMode == FormMode.EDIT && produitEnEdition != null)
                ? produitEnEdition : new Produit();
        p.setCode(code);
        p.setNom(nom);
        p.setNomCommercial(orNull(tfProduitNomComm.getText()));
        p.setForme(cbProduitForme.getValue());
        p.setDosage(orNull(tfProduitDosage.getText()));
        p.setUniteMesure(unite);
        p.setSeuilAlerteStock(seuil);
        p.setNiveauDangerosite(cbProduitDanger.getValue() != null
                ? cbProduitDanger.getValue() : Produit.NiveauDangerosite.FAIBLE);
        p.setNecessiteOrdonnance(cbProduitOrdonnance.isSelected());
        p.setStupefiant(cbProduitStupefiant.isSelected());
        String prixStr = tfProduitPrix.getText().trim();
        if (!prixStr.isEmpty()) {
            try { p.setPrixUnitaire(new BigDecimal(prixStr)); }
            catch (NumberFormatException e) { labelProduitFormErreur.setText("Prix invalide."); return; }
        }

        try {
            if (produitFormMode == FormMode.CREATE) stockService.creerProduit(p);
            else                                    stockService.modifierProduit(p);
            afficherFormProduit(false);
            chargerProduits();
            labelStatus.setText("Produit enregistre : " + p.getNom());
        } catch (Exception e) {
            labelProduitFormErreur.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML private void handleAnnulerProduitForm() {
        afficherFormProduit(false);
    }

    private void afficherFormProduit(boolean visible) {
        scrollProduitForm.setVisible(visible);
        scrollProduitForm.setManaged(visible);
        if (!visible) produitFormMode = FormMode.NONE;
    }

    private void viderFormProduit() {
        tfProduitCode.clear(); tfProduitNom.clear(); tfProduitNomComm.clear();
        tfProduitDosage.clear(); tfProduitUnite.clear(); tfProduitPrix.clear();
        tfProduitSeuil.setText("10");
        cbProduitForme.setValue(null);
        cbProduitDanger.getSelectionModel().selectFirst();
        cbProduitOrdonnance.setSelected(false);
        cbProduitStupefiant.setSelected(false);
        labelProduitFormErreur.setText("");
    }

    private void remplirFormProduit(Produit p) {
        tfProduitCode.setText(p.getCode() != null ? p.getCode() : "");
        tfProduitNom.setText(p.getNom() != null ? p.getNom() : "");
        tfProduitNomComm.setText(p.getNomCommercial() != null ? p.getNomCommercial() : "");
        cbProduitForme.setValue(p.getForme());
        tfProduitDosage.setText(p.getDosage() != null ? p.getDosage() : "");
        tfProduitUnite.setText(p.getUniteMesure() != null ? p.getUniteMesure() : "");
        tfProduitPrix.setText(p.getPrixUnitaire() != null ? p.getPrixUnitaire().toPlainString() : "");
        tfProduitSeuil.setText(String.valueOf(p.getSeuilAlerteStock()));
        cbProduitDanger.setValue(p.getNiveauDangerosite());
        cbProduitOrdonnance.setSelected(p.isNecessiteOrdonnance());
        cbProduitStupefiant.setSelected(p.isStupefiant());
        labelProduitFormErreur.setText("");
    }

    // =========================================================================
    // TAB 2 — FOURNISSEURS
    // =========================================================================

    private void configurerTab2Fournisseurs() {
        cbAssoProduit.setConverter(new StringConverter<>() {
            @Override public String toString(Produit p) { return p != null ? p.getNom() : ""; }
            @Override public Produit fromString(String s) { return null; }
        });

        colFournisseurCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colFournisseurNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colFournisseurTel.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTelephone() != null ? d.getValue().getTelephone() : ""));
        colFournisseurEmail.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEmail() != null ? d.getValue().getEmail() : ""));
        colFournisseurDelai.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDelaiLivraisonJours() != null
                        ? d.getValue().getDelaiLivraisonJours() + " j" : "—"));
        colFournisseurActif.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isActif() ? "Oui" : "Non"));

        tableFournisseurs.setItems(fournisseursList);
        tableFournisseurs.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            boolean has = sel != null;
            btnModifierFournisseur.setDisable(!has);
            btnAssocierFournisseur.setDisable(!has);
        });
    }

    private void chargerFournisseurs() {
        try {
            fournisseursList.setAll(stockService.getAllFournisseurs());
            cbReapproFournisseur.getItems().setAll(fournisseursList);
        } catch (Exception e) {
            labelStatus.setText("Erreur chargement fournisseurs : " + e.getMessage());
        }
    }

    @FXML private void handleNouveauFournisseur() {
        fournisseurFormMode = FormMode.CREATE;
        fournisseurEnEdition = null;
        labelFournisseurFormTitre.setText("Nouveau fournisseur");
        viderFormFournisseur();
        afficherFormFournisseur(true);
    }

    @FXML private void handleModifierFournisseur() {
        Fournisseur sel = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        fournisseurFormMode = FormMode.EDIT;
        fournisseurEnEdition = sel;
        labelFournisseurFormTitre.setText("Modifier fournisseur");
        remplirFormFournisseur(sel);
        afficherFormFournisseur(true);
    }

    @FXML private void handleAssocierProduit() {
        Fournisseur sel = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        fournisseurEnEdition = sel;
        labelAssoErreur.setText("");
        labelAssoConfirm.setText("");
        cbAssoProduit.setValue(null);
        tfAssoRef.clear(); tfAssoPrix.clear();
        tfAssoDelai.setText("0"); tfAssoQteMin.setText("1");
        cbAssoEstPrincipal.setSelected(false);
        afficherFormFournisseur(true);
    }

    @FXML private void handleSauvegarderFournisseur() {
        labelFournisseurFormErreur.setText("");
        String code = tfFournisseurCode.getText().trim();
        String nom  = tfFournisseurNom.getText().trim();
        if (code.isEmpty()) { labelFournisseurFormErreur.setText("Le code est obligatoire."); return; }
        if (nom.isEmpty())  { labelFournisseurFormErreur.setText("Le nom est obligatoire."); return; }

        Fournisseur f = (fournisseurFormMode == FormMode.EDIT && fournisseurEnEdition != null)
                ? fournisseurEnEdition : new Fournisseur();
        f.setCode(code);
        f.setNom(nom);
        f.setRaisonSociale(orNull(tfFournisseurRS.getText()));
        f.setTelephone(orNull(tfFournisseurTel.getText()));
        f.setEmail(orNull(tfFournisseurEmail.getText()));
        f.setContactNom(orNull(tfFournisseurContact.getText()));
        String delaiStr = tfFournisseurDelai.getText().trim();
        if (!delaiStr.isEmpty()) {
            try { f.setDelaiLivraisonJours(Integer.parseInt(delaiStr)); }
            catch (NumberFormatException e) { labelFournisseurFormErreur.setText("Delai invalide."); return; }
        }

        try {
            if (fournisseurFormMode == FormMode.CREATE) stockService.creerFournisseur(f);
            else                                        stockService.modifierFournisseur(f);
            afficherFormFournisseur(false);
            chargerFournisseurs();
            labelStatus.setText("Fournisseur enregistre : " + f.getNom());
        } catch (Exception e) {
            labelFournisseurFormErreur.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML private void handleSauvegarderAssociation() {
        labelAssoErreur.setText("");
        labelAssoConfirm.setText("");
        if (fournisseurEnEdition == null) { labelAssoErreur.setText("Selectionnez d'abord un fournisseur."); return; }
        if (cbAssoProduit.getValue() == null) { labelAssoErreur.setText("Selectionnez un produit."); return; }
        String prixStr = tfAssoPrix.getText().trim();
        if (prixStr.isEmpty()) { labelAssoErreur.setText("Le prix achat est obligatoire."); return; }

        BigDecimal prix;
        try { prix = new BigDecimal(prixStr); }
        catch (NumberFormatException e) { labelAssoErreur.setText("Prix invalide."); return; }

        int delai = 0, qteMin = 1;
        try {
            String d = tfAssoDelai.getText().trim();
            if (!d.isEmpty()) delai = Integer.parseInt(d);
            String q = tfAssoQteMin.getText().trim();
            if (!q.isEmpty()) qteMin = Integer.parseInt(q);
        } catch (NumberFormatException e) {
            labelAssoErreur.setText("Delai ou quantite minimum invalide.");
            return;
        }

        try {
            stockService.associerProduitFournisseur(
                    cbAssoProduit.getValue().getId(),
                    fournisseurEnEdition.getId(),
                    orNull(tfAssoRef.getText()),
                    prix, delai, qteMin,
                    cbAssoEstPrincipal.isSelected());
            labelAssoConfirm.setText("Association enregistree.");
            cbAssoProduit.setValue(null);
            tfAssoRef.clear(); tfAssoPrix.clear();
            tfAssoDelai.setText("0"); tfAssoQteMin.setText("1");
            cbAssoEstPrincipal.setSelected(false);
        } catch (Exception e) {
            labelAssoErreur.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML private void handleAnnulerFournisseurForm() {
        afficherFormFournisseur(false);
    }

    private void afficherFormFournisseur(boolean visible) {
        scrollFournisseurForm.setVisible(visible);
        scrollFournisseurForm.setManaged(visible);
        if (!visible) fournisseurFormMode = FormMode.NONE;
    }

    private void viderFormFournisseur() {
        tfFournisseurCode.clear(); tfFournisseurNom.clear(); tfFournisseurRS.clear();
        tfFournisseurTel.clear(); tfFournisseurEmail.clear();
        tfFournisseurContact.clear(); tfFournisseurDelai.clear();
        labelFournisseurFormErreur.setText("");
    }

    private void remplirFormFournisseur(Fournisseur f) {
        tfFournisseurCode.setText(f.getCode() != null ? f.getCode() : "");
        tfFournisseurNom.setText(f.getNom() != null ? f.getNom() : "");
        tfFournisseurRS.setText(f.getRaisonSociale() != null ? f.getRaisonSociale() : "");
        tfFournisseurTel.setText(f.getTelephone() != null ? f.getTelephone() : "");
        tfFournisseurEmail.setText(f.getEmail() != null ? f.getEmail() : "");
        tfFournisseurContact.setText(f.getContactNom() != null ? f.getContactNom() : "");
        tfFournisseurDelai.setText(f.getDelaiLivraisonJours() != null
                ? f.getDelaiLivraisonJours().toString() : "");
        labelFournisseurFormErreur.setText("");
    }

    // =========================================================================
    // TAB 3 — DEMANDES EN ATTENTE
    // =========================================================================

    private void configurerTab3Demandes() {
        colDemandeNum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNumeroDemande()));
        colDemandeProduit.setCellValueFactory(d -> {
            DemandeProduit dem = d.getValue();
            String nom = (dem.getProduit() != null) ? dem.getProduit().getNom()
                    : "Produit #" + dem.getProduitId();
            return new SimpleStringProperty(nom);
        });
        colDemandeQte.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getQuantiteDemandee())));
        colDemandeMedecin.setCellValueFactory(d -> {
            DemandeProduit dem = d.getValue();
            String med = (dem.getMedecin() != null)
                    ? dem.getMedecin().getPrenom() + " " + dem.getMedecin().getNom()
                    : "Med. #" + dem.getMedecinId();
            return new SimpleStringProperty(med);
        });
        colDemandeDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDateDemande() != null
                        ? d.getValue().getDateDemande().format(DT_FMT) : ""));
        colDemandeUrgence.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isUrgence() ? "URGENT" : "Normal"));
        colDemandeMotif.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getMotif() != null ? d.getValue().getMotif() : ""));

        tableDemandes.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(DemandeProduit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                setStyle(item.isUrgence()
                        ? "-fx-background-color: #fff8e1; -fx-border-color: #FFC107; -fx-border-width: 0 0 0 3;"
                        : "");
            }
        });

        tableDemandes.setItems(demandesList);
        tableDemandes.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            boolean has = sel != null;
            btnValiderDemande.setDisable(!has);
            btnRefuserDemande.setDisable(!has);
        });
    }

    private void chargerDemandes() {
        try {
            List<DemandeProduit> demandes = stockService.getDemandesEnAttente();
            demandesList.setAll(demandes);
            labelDemandesCompteur.setText("Demandes en attente : " + demandes.size());
        } catch (Exception e) {
            labelStatus.setText("Erreur chargement demandes : " + e.getMessage());
        }
    }

    @FXML private void handleRafraichirDemandes() {
        chargerDemandes();
        labelDemandeAction.setText("");
        labelDemandeAction.setStyle("-fx-text-fill: #555;");
    }

    @FXML private void handleValiderDemande() {
        DemandeProduit sel = tableDemandes.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String commentaire = tfCommentaireTraitement.getText().trim();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la validation");
        confirm.setHeaderText("Valider la demande " + sel.getNumeroDemande() + " ?");
        confirm.setContentText("Produit : " + (sel.getProduit() != null ? sel.getProduit().getNom() : "Produit #" + sel.getProduitId())
                + "\nQuantite : " + sel.getQuantiteDemandee());
        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                DemandeProduit result = stockService.validerDemande(sel.getId(),
                        commentaire.isEmpty() ? null : commentaire);
                int livre = result.getQuantiteLivree() != null ? result.getQuantiteLivree() : 0;
                labelDemandeAction.setText("Demande validee. Livre : " + livre + "/" + sel.getQuantiteDemandee());
                labelDemandeAction.setStyle("-fx-text-fill: #388E3C;");
                tfCommentaireTraitement.clear();
                chargerDemandes();
            } catch (Exception e) {
                labelDemandeAction.setText("Erreur : " + e.getMessage());
                labelDemandeAction.setStyle("-fx-text-fill: #F44336;");
            }
        });
    }

    @FXML private void handleRefuserDemande() {
        DemandeProduit sel = tableDemandes.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String motif = tfCommentaireTraitement.getText().trim();
        if (motif.isEmpty()) {
            labelDemandeAction.setText("Un motif de refus est requis.");
            labelDemandeAction.setStyle("-fx-text-fill: #F44336;");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer le refus");
        confirm.setHeaderText("Refuser la demande " + sel.getNumeroDemande() + " ?");
        confirm.setContentText("Motif : " + motif);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                stockService.refuserDemande(sel.getId(), motif);
                labelDemandeAction.setText("Demande refusee.");
                labelDemandeAction.setStyle("-fx-text-fill: #F44336;");
                tfCommentaireTraitement.clear();
                chargerDemandes();
            } catch (Exception e) {
                labelDemandeAction.setText("Erreur : " + e.getMessage());
                labelDemandeAction.setStyle("-fx-text-fill: #F44336;");
            }
        });
    }

    // =========================================================================
    // TAB 4 — RÉAPPROVISIONNEMENT
    // =========================================================================

    private void configurerTab4Reappro() {
        cbReapproProduit.setConverter(new StringConverter<>() {
            @Override public String toString(Produit p) { return p != null ? p.getNom() + " (" + p.getCode() + ")" : ""; }
            @Override public Produit fromString(String s) { return null; }
        });
        cbReapproEmplacement.setConverter(new StringConverter<>() {
            @Override public String toString(EmplacementStock e) {
                return e != null ? e.getNom() + (e.getTypeEmplacement() != null
                        ? " — " + e.getTypeEmplacement().getLibelle() : "") : "";
            }
            @Override public EmplacementStock fromString(String s) { return null; }
        });
        cbReapproFournisseur.setConverter(new StringConverter<>() {
            @Override public String toString(Fournisseur f) { return f != null ? f.getNom() : ""; }
            @Override public Fournisseur fromString(String s) { return null; }
        });

        try {
            cbReapproEmplacement.getItems().setAll(stockService.getEmplacementsDisponibles());
        } catch (Exception e) {
            labelReapproErreur.setText("Emplacements non charges : " + e.getMessage());
        }
    }

    @FXML private void handleReapprovisionner() {
        labelReapproErreur.setText("");
        labelReapproConfirm.setText("");

        if (cbReapproProduit.getValue() == null)      { labelReapproErreur.setText("Selectionnez un produit."); return; }
        if (cbReapproEmplacement.getValue() == null)  { labelReapproErreur.setText("Selectionnez un emplacement."); return; }
        if (tfReapproLot.getText().trim().isEmpty())  { labelReapproErreur.setText("Le numero de lot est obligatoire."); return; }

        int quantite;
        try {
            quantite = Integer.parseInt(tfReapproQuantite.getText().trim());
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            labelReapproErreur.setText("La quantite doit etre un entier positif.");
            return;
        }

        BigDecimal prix = null;
        String prixStr = tfReapproPrix.getText().trim();
        if (!prixStr.isEmpty()) {
            try { prix = new BigDecimal(prixStr); }
            catch (NumberFormatException e) { labelReapproErreur.setText("Prix unitaire invalide."); return; }
        }

        Integer fournisseurId = cbReapproFournisseur.getValue() != null
                ? cbReapproFournisseur.getValue().getId() : null;

        try {
            Stock stock = stockService.reapprovisionner(
                    cbReapproProduit.getValue().getId(),
                    cbReapproEmplacement.getValue().getId(),
                    tfReapproLot.getText().trim(),
                    quantite,
                    dpReapproPeremption.getValue(),
                    prix,
                    fournisseurId,
                    orNull(tfReapproCommande.getText()));

            labelReapproConfirm.setText("Reapprovisionnement effectue. Stock ID : " + stock.getId()
                    + " — Nouvelle quantite : " + stock.getQuantite());
            // Réinitialiser le formulaire
            cbReapproProduit.setValue(null);
            cbReapproEmplacement.setValue(null);
            tfReapproLot.clear();
            tfReapproQuantite.clear();
            dpReapproPeremption.setValue(null);
            tfReapproPrix.clear();
            cbReapproFournisseur.setValue(null);
            tfReapproCommande.clear();
            // Rafraîchir la liste produits (stocks mis à jour)
            chargerProduits();
            chargerAlertes();
        } catch (Exception e) {
            labelReapproErreur.setText("Erreur : " + e.getMessage());
        }
    }

    // =========================================================================
    // TAB 5 — ALERTES STOCK
    // =========================================================================

    private void configurerTab5Alertes() {
        colAlerteCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colAlerteNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colAlerteStock.setCellValueFactory(d -> {
            int qte = stockService.getQuantiteTotale(d.getValue().getId());
            return new SimpleStringProperty(String.valueOf(qte));
        });
        colAlerteSeuil.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getSeuilAlerteStock())));
        colAlerteDanger.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNiveauDangerosite() != null
                        ? d.getValue().getNiveauDangerosite().getLibelle() : ""));

        tableStockBas.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                int qte = stockService.getQuantiteTotale(item.getId());
                setStyle(qte == 0
                        ? "-fx-background-color: #ffebee;"
                        : "-fx-background-color: #fff3e0;");
            }
        });
        tableStockBas.setItems(stockBasList);

        colPeremProduit.setCellValueFactory(d -> {
            Stock s = d.getValue();
            String nom = (s.getProduit() != null) ? s.getProduit().getNom()
                    : "Produit #" + s.getProduitId();
            return new SimpleStringProperty(nom);
        });
        colPeremLot.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getLot() != null ? d.getValue().getLot() : ""));
        colPeremQte.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getQuantite())));
        colPeremDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDatePeremption() != null
                        ? d.getValue().getDatePeremption().format(D_FMT) : ""));
        colPeremJours.setCellValueFactory(d -> {
            long jours = d.getValue().getJoursAvantPeremption();
            return new SimpleStringProperty(jours == Long.MAX_VALUE ? "—"
                    : (jours < 0 ? "PERIME" : jours + " j"));
        });

        tablePeremption.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Stock item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                long j = item.getJoursAvantPeremption();
                if (j < 0)  setStyle("-fx-background-color: #ffebee;");
                else if (j < 7) setStyle("-fx-background-color: #fce4ec;");
                else            setStyle("-fx-background-color: #fff3e0;");
            }
        });
        tablePeremption.setItems(peremptionList);
    }

    private void chargerAlertes() {
        int jours = 30;
        try {
            String s = tfJoursAlerte.getText().trim();
            if (!s.isEmpty()) jours = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {}

        try {
            stockBasList.setAll(stockService.getProduitsStockBas());
        } catch (Exception e) {
            labelStatus.setText("Erreur alertes stock bas : " + e.getMessage());
        }
        try {
            peremptionList.setAll(stockService.getStocksProchesPeremption(jours));
        } catch (Exception e) {
            labelStatus.setText("Erreur alertes peremption : " + e.getMessage());
        }
    }

    @FXML private void handleActualiserAlertes() {
        chargerAlertes();
        labelStatus.setText("Alertes actualisees.");
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    @FXML private void goToDashboard() { Router.goTo(Route.DASHBOARD); }
    @FXML private void handleLogout()  { Router.logout(); }

    // =========================================================================
    // Utilitaires
    // =========================================================================

    private String orNull(String s) {
        return (s != null && !s.trim().isEmpty()) ? s.trim() : null;
    }
}
