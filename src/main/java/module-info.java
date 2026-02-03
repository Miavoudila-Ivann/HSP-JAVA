module appli.bibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens appli to javafx.fxml;
    exports appli;
    exports appli.auteur;
    opens appli.auteur to javafx.fxml;
}