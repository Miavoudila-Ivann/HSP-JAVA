package appli.service;

import appli.model.JournalAction;
import appli.model.User;
import appli.repository.UserRepository;
import appli.security.PasswordHasher;
import appli.security.SessionManager;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {

    private static final int MAX_TENTATIVES = 5;
    private static final int VERROUILLAGE_MINUTES = 30;

    private final UserRepository userRepository = new UserRepository();
    private final JournalService journalService = new JournalService();

    public enum LoginStatus {
        SUCCESS,
        IDENTIFIANTS_INVALIDES,
        COMPTE_VERROUILLE,
        COMPTE_DESACTIVE
    }

    public record LoginResult(LoginStatus status, User user) {
        public boolean isSuccess() {
            return status == LoginStatus.SUCCESS;
        }
    }

    /**
     * Tente de connecter un utilisateur.
     * Retourne un LoginResult indiquant le statut precis (succes, compte verrouille, etc.)
     */
    public LoginResult login(String email, String password) {
        Optional<User> userOpt = userRepository.getByEmail(email);

        // Email inconnu
        if (userOpt.isEmpty()) {
            journalService.logConnexionEchec(null, "Email inconnu: " + email);
            return new LoginResult(LoginStatus.IDENTIFIANTS_INVALIDES, null);
        }

        User user = userOpt.get();

        // Compte desactive
        if (!user.isActif()) {
            journalService.logConnexionEchec(user, "Compte desactive");
            return new LoginResult(LoginStatus.COMPTE_DESACTIVE, null);
        }

        // Compte verrouille : verifier si le delai est ecoule
        if (user.isCompteVerrouille()) {
            if (user.getDateVerrouillage() != null
                    && user.getDateVerrouillage().plusMinutes(VERROUILLAGE_MINUTES).isAfter(LocalDateTime.now())) {
                journalService.logConnexionEchec(user, "Compte verrouille (tentatives excessives)");
                return new LoginResult(LoginStatus.COMPTE_VERROUILLE, null);
            }
            // Delai ecoule : deverrouiller
            user.setCompteVerrouille(false);
            user.setTentativesConnexion(0);
            user.setDateVerrouillage(null);
            userRepository.save(user);
        }

        // Verification du mot de passe
        if (!PasswordHasher.verify(password, user.getPasswordHash())) {
            gererEchecConnexion(user);
            if (user.isCompteVerrouille()) {
                return new LoginResult(LoginStatus.COMPTE_VERROUILLE, null);
            }
            return new LoginResult(LoginStatus.IDENTIFIANTS_INVALIDES, null);
        }

        // Connexion reussie : reset tentatives et maj derniere connexion
        user.setTentativesConnexion(0);
        user.setCompteVerrouille(false);
        user.setDateVerrouillage(null);
        userRepository.save(user);

        SessionManager.getInstance().login(user);
        userRepository.updateDerniereConnexion(user.getId());

        journalService.logConnexionReussie(user);

        return new LoginResult(LoginStatus.SUCCESS, user);
    }

    /**
     * Deconnecte l'utilisateur courant et journalise la deconnexion.
     */
    public void logout() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            journalService.logAction(currentUser, JournalAction.TypeAction.DECONNEXION,
                    "Deconnexion de " + currentUser.getNomComplet());
        }
        SessionManager.getInstance().logout();
    }

    public boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }

    public User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!PasswordHasher.verify(oldPassword, user.getPasswordHash())) {
            return false;
        }

        user.setPasswordHash(PasswordHasher.hash(newPassword));
        userRepository.save(user);

        journalService.logAction(user, JournalAction.TypeAction.MODIFICATION,
                "Changement de mot de passe", "User", user.getId());

        return true;
    }

    public User createUser(String email, String password, String nom, String prenom, User.Role role) {
        String hashedPassword = PasswordHasher.hash(password);
        User newUser = new User(email, hashedPassword, nom, prenom, role);
        userRepository.save(newUser);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        journalService.logAction(currentUser, JournalAction.TypeAction.CREATION,
                "Creation utilisateur: " + email, "User", newUser.getId());

        return newUser;
    }

    // --- Logique de verrouillage ---

    private void gererEchecConnexion(User user) {
        int tentatives = user.getTentativesConnexion() + 1;
        user.setTentativesConnexion(tentatives);

        if (tentatives >= MAX_TENTATIVES) {
            // Verrouiller le compte
            user.setCompteVerrouille(true);
            user.setDateVerrouillage(LocalDateTime.now());
            userRepository.save(user);

            journalService.logConnexionEchec(user,
                    "Mot de passe incorrect - Compte VERROUILLE apres " + tentatives + " tentatives");
        } else {
            userRepository.save(user);

            journalService.logConnexionEchec(user,
                    "Mot de passe incorrect (tentative " + tentatives + "/" + MAX_TENTATIVES + ")");
        }
    }
}
