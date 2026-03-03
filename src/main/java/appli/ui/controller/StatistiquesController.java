package appli.ui.controller;

import appli.model.JournalAction.TypeAction;
import appli.model.User;
import appli.security.RoleGuard;
import appli.security.RoleGuard.Fonctionnalite;
import appli.security.SessionManager;
import appli.service.JournalService;
import appli.service.PDFExportService;
import appli.service.StatistiquesService;
import appli.service.StatistiquesService.Indicateurs;
import appli.ui.util.AlertHelper;
import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatistiquesController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private Label lblHospitalises;
    @FXML private Label lblEnAttente;
    @FXML private Label lblStocksCritiques;
    @FXML private Label lblTauxOccupation;

    @FXML private BarChart<String, Number> chartHospitalisations;
    @FXML private CategoryAxis axeHospiX;
    @FXML private NumberAxis axeHospiY;

    @FXML private PieChart chartGravite;

    @FXML private BarChart<String, Number> chartProduits;
    @FXML private CategoryAxis axeProduitsX;
    @FXML private NumberAxis axeProduitsY;

    @FXML private PieChart chartOccupation;
    @FXML private Button btnExportPDF;

    private final StatistiquesService service = new StatistiquesService();
    private final PDFExportService pdfExportService = new PDFExportService();
    private final JournalService journalService = new JournalService();

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }
        chargerDonnees();

        boolean canExport = RoleGuard.hasPermission(Fonctionnalite.EXPORT_DONNEES);
        btnExportPDF.setVisible(canExport);
        btnExportPDF.setManaged(canExport);
    }

    @FXML
    private void handleExportRapport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport statistiques");
        fileChooser.setInitialFileName("rapport_statistiques_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(lblHospitalises.getScene().getWindow());

        if (file != null) {
            try {
                Indicateurs indicateurs = service.getIndicateurs();
                Map<String, Integer> hospitalisations = service.getHospitalisationsParSemaine();
                Map<String, Integer> gravite = service.getRepartitionGravite();
                Map<String, Integer> produits = service.getProduitsLesPlusDemandes(5);
                Map<String, Double> occupation = service.getTauxOccupationParType();

                pdfExportService.exportRapportStatistiques(indicateurs, hospitalisations, gravite, produits, occupation, file);

                User currentUser = SessionManager.getInstance().getCurrentUser();
                journalService.logAction(currentUser, TypeAction.EXPORT,
                        "Export PDF rapport statistiques", "Statistiques", null);

                AlertHelper.showInfo("Succes", "Rapport statistiques exporte avec succes :\n" + file.getAbsolutePath());
            } catch (Exception e) {
                AlertHelper.showError("Erreur", "Impossible d'exporter le PDF : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        chargerDonnees();
    }

    private void chargerDonnees() {
        chargerIndicateurs();
        chargerChartHospitalisations();
        chargerChartGravite();
        chargerChartProduits();
        chargerChartOccupation();
    }

    private void chargerIndicateurs() {
        Indicateurs ind = service.getIndicateurs();
        lblHospitalises.setText(String.valueOf(ind.nbPatientsHospitalises()));
        lblEnAttente.setText(String.valueOf(ind.nbDossiersEnAttente()));
        lblStocksCritiques.setText(String.valueOf(ind.nbStocksCritiques()));
        lblTauxOccupation.setText(ind.tauxOccupationGlobal() + "%");
    }

    private void chargerChartHospitalisations() {
        chartHospitalisations.getData().clear();
        Map<String, Integer> data = service.getHospitalisationsParSemaine();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Hospitalisations");
        data.forEach((semaine, count) -> series.getData().add(new XYChart.Data<>(semaine, count)));
        chartHospitalisations.getData().add(series);
    }

    private void chargerChartGravite() {
        chartGravite.getData().clear();
        Map<String, Integer> data = service.getRepartitionGravite();
        data.forEach((label, count) ->
                chartGravite.getData().add(new PieChart.Data(label + " (" + count + ")", count)));
    }

    private void chargerChartProduits() {
        chartProduits.getData().clear();
        Map<String, Integer> data = service.getProduitsLesPlusDemandes(5);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Demandes");
        data.forEach((nom, count) -> {
            String shortName = nom.length() > 15 ? nom.substring(0, 15) + "..." : nom;
            series.getData().add(new XYChart.Data<>(shortName, count));
        });
        chartProduits.getData().add(series);
    }

    private void chargerChartOccupation() {
        chartOccupation.getData().clear();
        Map<String, Double> data = service.getTauxOccupationParType();
        data.forEach((type, taux) ->
                chartOccupation.getData().add(new PieChart.Data(type + " (" + taux + "%)", taux)));
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
