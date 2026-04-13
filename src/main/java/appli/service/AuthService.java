package appli.service;

import appli.model.JournalAction;
import appli.model.User;
import appli.repository.UserRepository;
import appli.security.PasswordHasher;
import appli.security.SessionManager;
import appli.security.TotpService;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service d'authentification et de gestion des comptes.
 * Gere la connexion (avec verification bcrypt, verrouillage apres echecs et 2FA TOTP),
 * la deconnexion, le changement de mot de passe et l'activation/desactivation du 2FA.
 * Utilise {@link appli.security.SessionManager} pour maintenir la session active.
 */
public class AuthService {

    private static final int MAX_TENTATIVES = 5;
    private static final int VERROUILLAGE_MINUTES = 30;

    private final UserRepository userRepository = new UserRepository();
    private final JournalService journalService = new JournalService();

    /** Statuts possibles retournes apres une tentative de connexion. */
    public enum LoginStatus {
        SUCCESS,
        IDENTIFIANTS_INVALIDES,
        COMPTE_VERROUILLE,
        COMPTE_DESACTIVE,
        TOTP_REQUIRED,
        TOTP_INVALIDE
    }

    /** Resultat de la tentative de connexion : statut + utilisateur (si authentifie). */
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

        // Mot de passe OK : verifier si 2FA est active
        if (user.isTotpEnabled()) {
            return new LoginResult(LoginStatus.TOTP_REQUIRED, user);
        }

        return completeLogin(user);
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

    /**
     * Change le mot de passe apres verification de l'ancien.
     * @return {@code true} si le changement a reussi
     */
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

    /**
     * Cree un nouvel utilisateur avec un mot de passe hache et journalise la creation.
     */
    public User createUser(String email, String password, String nom, String prenom, User.Role role) {
        String hashedPassword = PasswordHasher.hash(password);
        User newUser = new User(email, hashedPassword, nom, prenom, role);
        userRepository.save(newUser);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        journalService.logAction(currentUser, JournalAction.TypeAction.CREATION,
                "Creation utilisateur: " + email, "User", newUser.getId());

        return newUser;
    }

    /**
     * Finalise la connexion apres verification du code TOTP.
     * A appeler depuis TotpVerifyController.
     */
    public LoginResult completeTotpLogin(User user, String totpCode) {
        if (!TotpService.verifyCode(user.getTotpSecret(), totpCode)) {
            journalService.logConnexionEchec(user, "Code TOTP invalide");
            return new LoginResult(LoginStatus.TOTP_INVALIDE, null);
        }
        return completeLogin(user);
    }

    /**
     * Active le 2FA pour un utilisateur apres confirmation du premier code.
     */
    public boolean enableTotp(User user, String secret, String confirmationCode) {
        if (!TotpService.verifyCode(secret, confirmationCode)) {
            return false;
        }
        user.setTotpSecret(secret);
        user.setTotpEnabled(true);
        userRepository.save(user);
        journalService.logAction(user, JournalAction.TypeAction.MODIFICATION,
                "Activation 2FA", "User", user.getId());
        return true;
    }

    /** Desactive le 2FA pour un utilisateur. */
    public void disableTotp(User user) {
        user.setTotpSecret(null);
        user.setTotpEnabled(false);
        userRepository.save(user);
        journalService.logAction(user, JournalAction.TypeAction.MODIFICATION,
                "Desactivation 2FA", "User", user.getId());
    }

    // --- Finalisation de connexion ---

    private LoginResult completeLogin(User user) {
        user.setTentativesConnexion(0);
        user.setCompteVerrouille(false);
        user.setDateVerrouillage(null);
        userRepository.save(user);

        SessionManager.getInstance().login(user);
        userRepository.updateDerniereConnexion(user.getId());
        journalService.logConnexionReussie(user);

        return new LoginResult(LoginStatus.SUCCESS, user);
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
