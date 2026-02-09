package appli.repository;

import appli.dao.UserDAO;
import appli.model.User;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final UserDAO userDAO = new UserDAO();

    public Optional<User> getById(int id) {
        return Optional.ofNullable(userDAO.findById(id));
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(userDAO.findByEmail(email));
    }

    public List<User> getAll() {
        return userDAO.findAll();
    }

    public List<User> getByRole(User.Role role) {
        return userDAO.findByRole(role);
    }

    public User save(User user) {
        if (user.getId() == 0) {
            int id = userDAO.insert(user);
            user.setId(id);
        } else {
            userDAO.update(user);
        }
        return user;
    }

    public void updateDerniereConnexion(int userId) {
        userDAO.updateDerniereConnexion(userId);
    }

    public void delete(int id) {
        userDAO.delete(id);
    }
}
