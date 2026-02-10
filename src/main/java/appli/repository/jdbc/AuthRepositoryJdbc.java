package appli.repository.jdbc;

import appli.dao.LoginLogDAO;
import appli.dao.UserDAO;
import appli.model.JournalAction;
import appli.model.User;
import appli.repository.AuthRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthRepositoryJdbc implements AuthRepository {

    private final UserDAO userDAO = new UserDAO();
    private final LoginLogDAO loginLogDAO = new LoginLogDAO();

    @Override
    public Optional<User> findUserWithRolesByEmail(String email) {
        return Optional.ofNullable(userDAO.findByEmail(email));
    }

    @Override
    public void insertLoginLog(Integer userId, boolean success, String ip) {
        JournalAction log = new JournalAction();
        log.setUserId(userId);
        log.setTypeAction(success ? JournalAction.TypeAction.CONNEXION : JournalAction.TypeAction.ECHEC_CONNEXION);
        log.setDescription(success ? "Connexion reussie" : "Echec de connexion");
        log.setDateAction(LocalDateTime.now());
        log.setAdresseIP(ip);
        loginLogDAO.insertConnexion(log);
    }
}
