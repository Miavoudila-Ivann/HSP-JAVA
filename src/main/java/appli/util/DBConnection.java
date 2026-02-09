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
    private Connection connection;

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
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(getUrl(), UTILISATEUR, MOT_DE_PASSE);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion a la base de donnees : " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
