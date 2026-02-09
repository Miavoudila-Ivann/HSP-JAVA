package appli.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Router {

    private static Stage primaryStage;
    private static Object currentController;
    private static final Map<String, Object> navigationData = new HashMap<>();

    private Router() {}

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void navigateTo(String viewName) {
        navigateTo(viewName, null);
    }

    public static void navigateTo(String viewName, Object data) {
        try {
            if (data != null) {
                navigationData.put(viewName, data);
            }

            String fxmlPath = "/appli/view/" + viewName + ".fxml";
            FXMLLoader loader = new FXMLLoader(Router.class.getResource(fxmlPath));
            Parent root = loader.load();

            currentController = loader.getController();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue " + viewName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object getCurrentController() {
        return currentController;
    }

    public static Object getNavigationData(String viewName) {
        return navigationData.get(viewName);
    }

    public static void clearNavigationData(String viewName) {
        navigationData.remove(viewName);
    }

    public static void setTitle(String title) {
        if (primaryStage != null) {
            primaryStage.setTitle(title);
        }
    }
}
