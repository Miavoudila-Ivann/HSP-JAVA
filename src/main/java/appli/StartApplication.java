package appli;

import appli.util.Route;
import appli.util.Router;
import javafx.application.Application;
import javafx.stage.Stage;

public class StartApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        Router.setPrimaryStage(stage);
        Router.goTo(Route.LOGIN);
    }

    @Override
    public void stop() {
        appli.util.DBConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
