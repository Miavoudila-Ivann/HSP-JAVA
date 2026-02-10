package appli.util;

import appli.model.User;
import appli.security.RoleGuard;
import appli.security.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SceneRouter central : gere la navigation entre les vues JavaFX.
 * Integre le controle d'acces par role (RoleGuard) et la session utilisateur (SessionManager).
 */
public class Router {

    private static Stage primaryStage;
    private static Object currentController;
    private static Route currentRoute;
    private static final Map<String, Object> navigationData = new HashMap<>();

    private Router() {}

    // --- Configuration du Stage ---

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // --- Navigation par Route (methode principale) ---

    /**
     * Navigue vers une route sans donnees supplementaires.
     */
    public static void goTo(Route route) {
        goTo(route, null);
    }

    /**
     * Navigue vers une route avec des donnees de navigation.
     * Verifie les permissions via RoleGuard avant d'afficher la vue.
     */
    public static void goTo(Route route, Object data) {
        // Controle d'acces : verifier que l'utilisateur a le droit d'acceder a cette vue
        if (route != Route.LOGIN && !RoleGuard.canAccessView(route.getViewName())) {
            System.err.println("Acces refuse a la vue : " + route.getViewName());
            return;
        }

        try {
            if (data != null) {
                navigationData.put(route.getViewName(), data);
            }

            FXMLLoader loader = new FXMLLoader(Router.class.getResource(route.getFxmlPath()));
            Parent root = loader.load();

            currentController = loader.getController();
            currentRoute = route;

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(route.getTitle());
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue " + route.getViewName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Navigation par String (retro-compatibilite) ---

    public static void navigateTo(String viewName) {
        navigateTo(viewName, null);
    }

    public static void navigateTo(String viewName, Object data) {
        Route route = findRoute(viewName);
        if (route != null) {
            goTo(route, data);
        } else {
            System.err.println("Route inconnue : " + viewName);
        }
    }

    // --- Session / Utilisateur courant ---

    /**
     * Retourne l'utilisateur actuellement connecte via le SessionManager.
     */
    public static User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }

    /**
     * Deconnecte l'utilisateur et redirige vers la page de login.
     */
    public static void logout() {
        SessionManager.getInstance().logout();
        navigationData.clear();
        goTo(Route.LOGIN);
    }

    // --- Accesseurs ---

    public static Object getCurrentController() {
        return currentController;
    }

    public static Route getCurrentRoute() {
        return currentRoute;
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

    // --- Utilitaire ---

    private static Route findRoute(String viewName) {
        for (Route route : Route.values()) {
            if (route.getViewName().equals(viewName)) {
                return route;
            }
        }
        return null;
    }
}
