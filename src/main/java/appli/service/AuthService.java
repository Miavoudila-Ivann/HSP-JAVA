package appli.service;

import appli.model.JournalAction;
import appli.model.User;
import appli.repository.UserRepository;
import appli.security.PasswordHasher;
import appli.security.SessionManager;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();
    private final JournalService journalService = new JournalService();

    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userRepository.getByEmail(email);

        if (userOpt.isEmpty()) {
            journalService.logConnexion(null, false, "Email inconnu: " + email);
            return Optional.empty();
        }

        User user = userOpt.get();

        if (!user.isActif()) {
            journalService.logConnexion(user, false, "Compte desactive");
            return Optional.empty();
        }

        if (!PasswordHasher.verify(password, user.getPasswordHash())) {
            journalService.logConnexion(user, false, "Mot de passe incorrect");
            return Optional.empty();
        }

        SessionManager.getInstance().login(user);
        userRepository.updateDerniereConnexion(user.getId());
        journalService.logConnexion(user, true, "Connexion reussie");

        return Optional.of(user);
    }

    public void logout() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            journalService.logAction(currentUser, JournalAction.TypeAction.DECONNEXION, "Deconnexion");
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

        journalService.logAction(user, JournalAction.TypeAction.MODIFICATION, "Changement de mot de passe");

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
}
