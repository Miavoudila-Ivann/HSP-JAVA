package appli.ui.controller;

import appli.model.User;
import appli.service.StatistiquesService;
import appli.service.StatistiquesService.Indicateurs;
import appli.util.Route;
import appli.util.Router;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

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

    private final StatistiquesService service = new StatistiquesService();

    @FXML
    public void initialize() {
        User user = Router.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getPrenom() + " " + user.getNom());
            roleLabel.setText(user.getRole().getLibelle());
        }
        chargerDonnees();
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
