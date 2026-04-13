package appli.security;

import appli.model.User;

/**
 * Gestionnaire de session utilisateur (Singleton).
 * Conserve en memoire l'utilisateur connecte pendant toute la duree de la session.
 * Utilise par le {@link appli.security.RoleGuard} et les services pour verifier
 * l'identite et le role de l'utilisateur courant.
 */
public class SessionManager {

    private static SessionManager instance;

    /** Utilisateur actuellement connecte, ou {@code null} si aucune session active. */
    private User currentUser;

    private SessionManager() {}

    /**
     * Retourne l'instance unique du SessionManager (thread-safe).
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Ouvre une session pour l'utilisateur donne.
     *
     * @param user l'utilisateur authentifie
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Ferme la session courante en effacant l'utilisateur.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Retourne l'utilisateur actuellement connecte.
     *
     * @return l'utilisateur courant, ou {@code null} si pas de session
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Indique si une session est active. */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Verifie si l'utilisateur connecte possede le role donne.
     *
     * @param role le role a verifier
     * @return {@code true} si l'utilisateur est connecte et a ce role
     */
    public boolean hasRole(User.Role role) {
        return isLoggedIn() && currentUser.getRole() == role;
    }

    /** @return {@code true} si l'utilisateur connecte est ADMIN */
    public boolean isAdmin() {
        return hasRole(User.Role.ADMIN);
    }

    /** @return {@code true} si l'utilisateur connecte est MEDECIN */
    public boolean isMedecin() {
        return hasRole(User.Role.MEDECIN);
    }

    /** @return {@code true} si l'utilisateur connecte est SECRETAIRE */
    public boolean isSecretaire() {
        return hasRole(User.Role.SECRETAIRE);
    }

    /** @return {@code true} si l'utilisateur connecte est GESTIONNAIRE */
    public boolean isGestionnaire() {
        return hasRole(User.Role.GESTIONNAIRE);
    }
}
