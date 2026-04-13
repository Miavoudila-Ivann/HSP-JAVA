package appli.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion a la base de donnees MySQL.
 * Utilise le pattern Singleton pour partager une meme configuration.
 * Les connexions individuelles sont creees a la demande et doivent etre
 * fermees par l'appelant via try-with-resources.
 */
public class DBConnection {

    /** Adresse du serveur MySQL. */
    private static final String SERVEUR = "localhost";

    /** Nom de la base de donnees. */
    private static final String NOM_BDD = "hsp_java";

    /** Utilisateur MySQL. */
    private static final String UTILISATEUR = "root";

    /** Mot de passe MySQL (vide en developpement local). */
    private static final String MOT_DE_PASSE = "";

    private static DBConnection instance;

    private DBConnection() {}

    /**
     * Retourne l'instance unique de DBConnection (thread-safe).
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Construit l'URL JDBC avec le fuseau horaire UTC.
     */
    private String getUrl() {
        return "jdbc:mysql://" + SERVEUR + "/" + NOM_BDD + "?serverTimezone=UTC";
    }

    /**
     * Ouvre et retourne une nouvelle connexion JDBC.
     * L'appelant est responsable de fermer cette connexion (try-with-resources).
     *
     * @return une {@link Connection} active, ou {@code null} en cas d'erreur
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(getUrl(), UTILISATEUR, MOT_DE_PASSE);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion a la base de donnees : " + e.getMessage());
            return null;
        }
    }

    /**
     * Methode conservee pour compatibilite.
     * Les connexions sont desormais gerees par les appelants via try-with-resources.
     */
    public void closeConnection() {
        // Les connexions sont desormais gerees par les appelants via try-with-resources
    }
}
