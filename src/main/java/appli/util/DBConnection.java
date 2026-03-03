package appli.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String SERVEUR = "localhost";
    private static final String NOM_BDD = "hsp_java";
    private static final String UTILISATEUR = "root";
    private static final String MOT_DE_PASSE = "";

    private static DBConnection instance;
    private DBConnection() {}

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private String getUrl() {
        return "jdbc:mysql://" + SERVEUR + "/" + NOM_BDD + "?serverTimezone=UTC";
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(getUrl(), UTILISATEUR, MOT_DE_PASSE);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion a la base de donnees : " + e.getMessage());
            return null;
        }
    }

    public void closeConnection() {
        // Les connexions sont desormais gerees par les appelants via try-with-resources
    }
}
