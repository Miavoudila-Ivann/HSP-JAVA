package appli.ui.controller;

import appli.model.CommandeFournisseur;
import appli.model.Fournisseur;
import appli.model.LigneCommande;
import appli.model.Produit;
import appli.security.RoleGuard;
import appli.service.StockService;
import appli.util.Route;
import appli.util.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controleur de la vue de gestion des commandes fournisseurs (commandes.fxml).
 * Permet au gestionnaire de creer des commandes, d'y ajouter des lignes de produits,
 * de les envoyer au fournisseur et de saisir les receptions partielles ou totales.
 */
public class CommandesController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private TableView<CommandeFournisseur> tableCommandes;
    @FXML private TableColumn<CommandeFournisseur, String> colNumero;
    @FXML private TableColumn<CommandeFournisseur, String> colFournisseur;
    @FXML private TableColumn<CommandeFournisseur, String> colDate;
    @FXML private TableColumn<CommandeFournisseur, String> colStatut;
    @FXML private TableColumn<CommandeFournisseur, String> colMontantHt;
    @FXML private TableColumn<CommandeFournisseur, String> colActions;

    @FXML private VBox sectionLignes;
    @FXML private Label labelCommandeSelectionnee;
    @FXML private TableView<LigneCommande> tableLignes;
    @FXML private TableColumn<LigneCommande, String> colLigneProduit;
    @FXML private TableColumn<LigneCommande, String> colLigneQteCmd;
    @FXML private TableColumn<LigneCommande, String> colLigneQteRecue;
    @FXML private TableColumn<LigneCommande, String> colLignePrix;
    @FXML private TableColumn<LigneCommande, String> colLigneHt;

    @FXML private Label labelMessage;

    private final StockService stockService = new StockService();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        appli.model.User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }

        setupFiltreStatut();
        setupColumnsCommandes();
        setupColumnsLignes();

        sectionLignes.setVisible(false);
        sectionLignes.setManaged(false);

        chargerCommandes(null);

        tableCommandes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        afficherLignesCommande(selected);
                    } else {
                        sectionLignes.setVisible(false);
                        sectionLignes.setManaged(false);
                    }
                });
    }

    private void setupFiltreStatut() {
        cbFiltreStatut.getItems().add("Toutes");
        for (CommandeFournisseur.Statut s : CommandeFournisseur.Statut.values()) {
            cbFiltreStatut.getItems().add(s.getLibelle());
        }
        cbFiltreStatut.getSelectionModel().selectFirst();
        cbFiltreStatut.setOnAction(e -> appliquerFiltre());
    }

    private void setupColumnsCommandes() {
        colNumero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroCommande()));
        colFournisseur.setCellValueFactory(c -> {
            Fournisseur f = c.getValue().getFournisseur();
            return new SimpleStringProperty(f != null ? f.getNom() : "ID " + c.getValue().getFournisseurId());
        });
        colDate.setCellValueFactory(c -> {
            var d = c.getValue().getDateCommande();
            return new SimpleStringProperty(d != null ? d.format(DT_FMT) : "");
        });
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatut() != null ? c.getValue().getStatut().getLibelle() : ""));
        colMontantHt.setCellValueFactory(c -> {
            BigDecimal m = c.getValue().getMontantHt();
            return new SimpleStringProperty(m != null ? m.setScale(2, java.math.RoundingMode.HALF_UP) + " €" : "0.00 €");
        });

        // Colonne Actions avec boutons
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEnvoyer = new Button("Envoyer");
            private final Button btnReceptionner = new Button("Receptionner");
            private final HBox box = new HBox(5, btnEnvoyer, btnReceptionner);

            {
                btnEnvoyer.setStyle("-fx-background-color: #6C5CE7; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-border-color: #1a1a1a; -fx-border-width: 1.5; -fx-border-radius: 6; " +
                        "-fx-background-radius: 6; -fx-cursor: hand;");
                btnReceptionner.setStyle("-fx-background-color: #00B894; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-border-color: #1a1a1a; -fx-border-width: 1.5; -fx-border-radius: 6; " +
                        "-fx-background-radius: 6; -fx-cursor: hand;");

                btnEnvoyer.setOnAction(e -> {
                    CommandeFournisseur cmd = getTableView().getItems().get(getIndex());
                    handleEnvoyerCommande(cmd);
                });
                btnReceptionner.setOnAction(e -> {
                    CommandeFournisseur cmd = getTableView().getItems().get(getIndex());
                    handleReceptionnerCommande(cmd);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CommandeFournisseur cmd = getTableView().getItems().get(getIndex());
                    CommandeFournisseur.Statut s = cmd.getStatut();
                    btnEnvoyer.setDisable(s != CommandeFournisseur.Statut.BROUILLON);
                    btnReceptionner.setDisable(
                            s == CommandeFournisseur.Statut.BROUILLON
                            || s == CommandeFournisseur.Statut.LIVREE
                            || s == CommandeFournisseur.Statut.ANNULEE);
                    setGraphic(box);
                }
            }
        });
    }

    private void setupColumnsLignes() {
        colLigneProduit.setCellValueFactory(c -> {
            Produit p = c.getValue().getProduit();
            if (p != null) {
                return new SimpleStringProperty("[" + p.getCode() + "] " + p.getNom());
            }
            return new SimpleStringProperty("Produit #" + c.getValue().getProduitId());
        });
        colLigneQteCmd.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getQuantiteCommandee())));
        colLigneQteRecue.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getQuantiteRecue())));
        colLignePrix.setCellValueFactory(c -> {
            BigDecimal p = c.getValue().getPrixUnitaire();
            return new SimpleStringProperty(p != null ? p.setScale(2, java.math.RoundingMode.HALF_UP) + " €" : "");
        });
        colLigneHt.setCellValueFactory(c -> {
            BigDecimal ht = c.getValue().getMontantHt();
            return new SimpleStringProperty(ht != null ? ht.setScale(2, java.math.RoundingMode.HALF_UP) + " €" : "");
        });
    }

    private void chargerCommandes(CommandeFournisseur.Statut statut) {
        try {
            List<CommandeFournisseur> commandes = statut == null
                    ? stockService.getAllCommandes()
                    : stockService.getCommandesByStatut(statut);
            tableCommandes.setItems(FXCollections.observableArrayList(commandes));
        } catch (Exception e) {
            showError("Erreur chargement commandes : " + e.getMessage());
        }
    }

    private void appliquerFiltre() {
        String selected = cbFiltreStatut.getValue();
        if (selected == null || "Toutes".equals(selected)) {
            chargerCommandes(null);
        } else {
            for (CommandeFournisseur.Statut s : CommandeFournisseur.Statut.values()) {
                if (s.getLibelle().equals(selected)) {
                    chargerCommandes(s);
                    return;
                }
            }
        }
    }

    private void afficherLignesCommande(CommandeFournisseur commande) {
        try {
            List<LigneCommande> lignes = stockService.getLignesCommande(commande.getId());
            tableLignes.setItems(FXCollections.observableArrayList(lignes));
            String fNom = commande.getFournisseur() != null ? commande.getFournisseur().getNom() : "";
            labelCommandeSelectionnee.setText(commande.getNumeroCommande()
                    + (fNom.isBlank() ? "" : " — " + fNom)
                    + " — " + commande.getStatut().getLibelle());
            sectionLignes.setVisible(true);
            sectionLignes.setManaged(true);
        } catch (Exception e) {
            showError("Erreur chargement lignes : " + e.getMessage());
        }
    }

    // ==================== Actions boutons ====================

    @FXML
    private void handleNouvelleCommande() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle commande fournisseur");
        dialog.setHeaderText("Creer une commande fournisseur");

        ButtonType btnCreer = new ButtonType("Creer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCreer, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Fournisseur
        ComboBox<Fournisseur> cbFournisseur = new ComboBox<>();
        cbFournisseur.setConverter(new StringConverter<>() {
            @Override public String toString(Fournisseur f) { return f != null ? f.getNom() : ""; }
            @Override public Fournisseur fromString(String s) { return null; }
        });
        try {
            cbFournisseur.getItems().addAll(stockService.getAllFournisseurs());
            if (!cbFournisseur.getItems().isEmpty()) cbFournisseur.getSelectionModel().selectFirst();
        } catch (Exception e) {
            showError("Erreur chargement fournisseurs : " + e.getMessage());
            return;
        }

        DatePicker dpLivraison = new DatePicker(LocalDate.now().plusDays(7));
        TextArea taNotes = new TextArea();
        taNotes.setPrefRowCount(3);
        taNotes.setPromptText("Notes (optionnel)");

        grid.add(new Label("Fournisseur *"), 0, 0);
        grid.add(cbFournisseur, 1, 0);
        grid.add(new Label("Date livraison prevue"), 0, 1);
        grid.add(dpLivraison, 1, 1);
        grid.add(new Label("Notes"), 0, 2);
        grid.add(taNotes, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnCreer) {
            if (cbFournisseur.getValue() == null) {
                showError("Veuillez selectionner un fournisseur.");
                return;
            }
            try {
                CommandeFournisseur cmd = stockService.creerCommande(
                        cbFournisseur.getValue().getId(),
                        dpLivraison.getValue(),
                        taNotes.getText().trim().isEmpty() ? null : taNotes.getText().trim()
                );
                showSuccess("Commande " + cmd.getNumeroCommande() + " creee (BROUILLON).");
                chargerCommandes(null);
                cbFiltreStatut.getSelectionModel().selectFirst();

                // Ouvrir dialog ajout lignes directement
                handleAjouterLignes(cmd);
            } catch (Exception e) {
                showError("Erreur creation commande : " + e.getMessage());
            }
        }
    }

    private void handleAjouterLignes(CommandeFournisseur commande) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter des lignes — " + commande.getNumeroCommande());
        dialog.setHeaderText("Ajouter des produits a la commande");
        dialog.getDialogPane().setPrefWidth(600);

        ButtonType btnTerminer = new ButtonType("Terminer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(btnTerminer);

        VBox content = new VBox(12);
        content.setPadding(new Insets(15));

        // Liste des lignes deja ajoutees
        ListView<String> listeLignes = new ListView<>();
        listeLignes.setPrefHeight(120);
        Label lblLignes = new Label("Lignes de commande :");

        // Formulaire ajout ligne
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        ComboBox<Produit> cbProduit = new ComboBox<>();
        cbProduit.setPrefWidth(220);
        cbProduit.setConverter(new StringConverter<>() {
            @Override public String toString(Produit p) { return p != null ? "[" + p.getCode() + "] " + p.getNom() : ""; }
            @Override public Produit fromString(String s) { return null; }
        });
        try {
            cbProduit.getItems().addAll(stockService.getAllProduits());
            if (!cbProduit.getItems().isEmpty()) cbProduit.getSelectionModel().selectFirst();
        } catch (Exception e) {
            showError("Erreur chargement produits : " + e.getMessage());
        }

        Spinner<Integer> spQuantite = new Spinner<>(1, 99999, 1);
        spQuantite.setEditable(true);
        TextField tfPrix = new TextField("0.00");
        tfPrix.setPromptText("Prix unitaire HT");
        TextField tfTva = new TextField("20");
        tfTva.setPromptText("TVA %");

        Label lblErreur = new Label();
        lblErreur.setStyle("-fx-text-fill: #D63031; -fx-font-size: 12;");

        Button btnAjouter = new Button("+ Ajouter produit");
        btnAjouter.setStyle("-fx-background-color: #6C5CE7; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-border-color: #1a1a1a; -fx-border-width: 2; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");

        grid.add(new Label("Produit *"), 0, 0);
        grid.add(cbProduit, 1, 0);
        grid.add(new Label("Quantite *"), 0, 1);
        grid.add(spQuantite, 1, 1);
        grid.add(new Label("Prix unitaire HT *"), 0, 2);
        grid.add(tfPrix, 1, 2);
        grid.add(new Label("TVA %"), 0, 3);
        grid.add(tfTva, 1, 3);
        grid.add(btnErreur(lblErreur), 0, 4, 2, 1);
        grid.add(btnAjouter, 1, 5);

        // Rafraichir la liste
        Runnable rafraichirListe = () -> {
            try {
                List<LigneCommande> lignes = stockService.getLignesCommande(commande.getId());
                listeLignes.getItems().clear();
                for (LigneCommande l : lignes) {
                    String pNom = l.getProduit() != null ? l.getProduit().getNom() : "Produit #" + l.getProduitId();
                    String ht = l.getMontantHt() != null
                            ? l.getMontantHt().setScale(2, java.math.RoundingMode.HALF_UP) + " €" : "";
                    listeLignes.getItems().add(pNom + "  x" + l.getQuantiteCommandee() + "  " + ht);
                }
            } catch (Exception ignored) {}
        };

        rafraichirListe.run();

        btnAjouter.setOnAction(e -> {
            lblErreur.setText("");
            if (cbProduit.getValue() == null) { lblErreur.setText("Selectionnez un produit."); return; }
            BigDecimal prix;
            BigDecimal tva;
            try { prix = new BigDecimal(tfPrix.getText().replace(",", ".")); }
            catch (NumberFormatException ex) { lblErreur.setText("Prix invalide."); return; }
            try { tva = new BigDecimal(tfTva.getText().replace(",", ".")); }
            catch (NumberFormatException ex) { lblErreur.setText("TVA invalide."); return; }

            try {
                stockService.ajouterLigneCommande(
                        commande.getId(),
                        cbProduit.getValue().getId(),
                        spQuantite.getValue(),
                        prix,
                        tva
                );
                rafraichirListe.run();
                spQuantite.getValueFactory().setValue(1);
                tfPrix.setText("0.00");
            } catch (Exception ex) {
                lblErreur.setText("Erreur : " + ex.getMessage());
            }
        });

        content.getChildren().addAll(lblLignes, listeLignes,
                new Separator(), new Label("Ajouter un produit :"), grid);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();

        chargerCommandes(null);
        cbFiltreStatut.getSelectionModel().selectFirst();
    }

    private Label btnErreur(Label lbl) { return lbl; }

    private void handleEnvoyerCommande(CommandeFournisseur commande) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Envoyer la commande");
        confirm.setHeaderText("Envoyer la commande " + commande.getNumeroCommande() + " ?");
        confirm.setContentText("Le statut passera de BROUILLON a ENVOYEE.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                stockService.envoyerCommande(commande.getId());
                showSuccess("Commande " + commande.getNumeroCommande() + " envoyee.");
                chargerCommandes(null);
            } catch (Exception e) {
                showError("Erreur : " + e.getMessage());
            }
        });
    }

    private void handleReceptionnerCommande(CommandeFournisseur commande) {
        List<LigneCommande> lignes = stockService.getLignesCommande(commande.getId());
        if (lignes.isEmpty()) {
            showError("Cette commande n'a aucune ligne.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Receptionner livraison — " + commande.getNumeroCommande());
        dialog.setHeaderText("Saisir les quantites recues");
        dialog.getDialogPane().setPrefWidth(600);

        ButtonType btnValider = new ButtonType("Valider la reception", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(15));

        // Emplacement et lot
        ComboBox<appli.model.EmplacementStock> cbEmplacement = new ComboBox<>();
        cbEmplacement.setConverter(new StringConverter<>() {
            @Override public String toString(appli.model.EmplacementStock e) { return e != null ? e.getNom() : ""; }
            @Override public appli.model.EmplacementStock fromString(String s) { return null; }
        });
        try {
            cbEmplacement.getItems().addAll(stockService.getEmplacementsDisponibles());
            if (!cbEmplacement.getItems().isEmpty()) cbEmplacement.getSelectionModel().selectFirst();
        } catch (Exception e) {
            showError("Erreur chargement emplacements : " + e.getMessage());
            return;
        }

        TextField tfLot = new TextField("LOT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        DatePicker dpPeremption = new DatePicker(LocalDate.now().plusYears(2));

        grid.add(new Label("Emplacement *"), 0, 0);
        grid.add(cbEmplacement, 1, 0);
        grid.add(new Label("Numero de lot *"), 0, 1);
        grid.add(tfLot, 1, 1);
        grid.add(new Label("Date de peremption"), 0, 2);
        grid.add(dpPeremption, 1, 2);

        // Lignes avec quantites
        int row = 3;
        Map<Integer, Spinner<Integer>> spinners = new HashMap<>();
        grid.add(new Label("Produit"), 0, row);
        grid.add(new Label("Qte cmd"), 1, row);
        grid.add(new Label("Qte recue"), 2, row);
        row++;

        for (LigneCommande l : lignes) {
            String pNom = l.getProduit() != null ? l.getProduit().getNom() : "Produit #" + l.getProduitId();
            int restant = l.getQuantiteCommandee() - l.getQuantiteRecue();
            Spinner<Integer> sp = new Spinner<>(0, restant, restant);
            sp.setEditable(true);
            spinners.put(l.getId(), sp);
            grid.add(new Label(pNom), 0, row);
            grid.add(new Label(String.valueOf(l.getQuantiteCommandee())), 1, row);
            grid.add(sp, 2, row);
            row++;
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);
        dialog.getDialogPane().setContent(scroll);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnValider) {
            if (cbEmplacement.getValue() == null) { showError("Selectionnez un emplacement."); return; }
            if (tfLot.getText().trim().isEmpty()) { showError("Saisissez un numero de lot."); return; }

            Map<Integer, Integer> lignesRecues = new HashMap<>();
            for (Map.Entry<Integer, Spinner<Integer>> entry : spinners.entrySet()) {
                lignesRecues.put(entry.getKey(), entry.getValue().getValue());
            }

            try {
                CommandeFournisseur updated = stockService.recevoirLivraison(
                        commande.getId(),
                        lignesRecues,
                        cbEmplacement.getValue().getId(),
                        tfLot.getText().trim(),
                        dpPeremption.getValue()
                );
                showSuccess("Livraison receptionnee. Statut : " + updated.getStatut().getLibelle());
                chargerCommandes(null);
            } catch (Exception e) {
                showError("Erreur reception : " + e.getMessage());
            }
        }
    }

    // ==================== Navigation ====================

    @FXML
    private void handleRetour() {
        Router.goTo(Route.DASHBOARD);
    }

    @FXML
    private void handleRefresh() {
        appliquerFiltre();
        tableCommandes.getSelectionModel().clearSelection();
        sectionLignes.setVisible(false);
        sectionLignes.setManaged(false);
    }

    @FXML
    private void handleLogout() {
        Router.logout();
    }

    // ==================== Utilitaires ====================

    private void showSuccess(String msg) {
        labelMessage.setText(msg);
        labelMessage.setStyle("-fx-text-fill: #00B894; -fx-font-weight: bold; -fx-font-size: 12;");
    }

    private void showError(String msg) {
        labelMessage.setText(msg);
        labelMessage.setStyle("-fx-text-fill: #D63031; -fx-font-weight: bold; -fx-font-size: 12;");
    }
}
