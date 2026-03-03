module appli.hsp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires org.apache.pdfbox;

    opens appli to javafx.fxml;
    opens appli.ui.controller to javafx.fxml;
    opens appli.model to javafx.fxml;
    opens appli.ui.util to javafx.fxml;

    exports appli;
    exports appli.model;
    exports appli.ui.controller;
    exports appli.ui.util;
    exports appli.service;
    exports appli.repository;
    exports appli.dao;
    exports appli.security;
    exports appli.util;
}
