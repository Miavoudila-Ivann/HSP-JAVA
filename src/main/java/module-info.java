module appli.bibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens appli to javafx.fxml;
    opens appli.ui.controller to javafx.fxml;
    opens appli.model to javafx.fxml;

    exports appli;
    exports appli.model;
    exports appli.ui.controller;
    exports appli.service;
    exports appli.repository;
    exports appli.dao;
    exports appli.security;
    exports appli.util;
}
