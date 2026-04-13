package appli;

import appli.util.Route;
import appli.util.Router;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entree de l'application JavaFX HSP (Hospital System Platform).
 * Configure la fenetre principale et demarre sur la vue de connexion.
 */
public class StartApplication extends Application {

    /**
     * Initialise la fenetre principale et navigue vers la page de connexion.
     *
     * @param stage la fenetre JavaFX principale fournie par le runtime
     */
    @Override
    public void start(Stage stage) {
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        Router.setPrimaryStage(stage);
        Router.goTo(Route.LOGIN);
    }

    /**
     * Appelee a la fermeture de l'application pour liberer les ressources.
     */
    @Override
    public void stop() {
        appli.util.DBConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
