package appli;

import appli.util.Router;
import javafx.application.Application;
import javafx.stage.Stage;

public class StartApplication extends Application {

    @Override
    public void start(Stage stage) {
        Router.setPrimaryStage(stage);
        Router.setTitle("HSP - Connexion");
        Router.navigateTo("login");
    }

    @Override
    public void stop() {
        appli.util.DBConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
