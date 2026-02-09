package appli.security;

import appli.model.User;

public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(User.Role role) {
        return isLoggedIn() && currentUser.getRole() == role;
    }

    public boolean isAdmin() {
        return hasRole(User.Role.ADMIN);
    }

    public boolean isMedecin() {
        return hasRole(User.Role.MEDECIN);
    }

    public boolean isSecretaire() {
        return hasRole(User.Role.SECRETAIRE);
    }

    public boolean isGestionnaire() {
        return hasRole(User.Role.GESTIONNAIRE);
    }
}
