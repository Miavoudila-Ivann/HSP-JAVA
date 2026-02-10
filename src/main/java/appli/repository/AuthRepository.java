package appli.repository;

import appli.model.User;

import java.util.Optional;

public interface AuthRepository {

    Optional<User> findUserWithRolesByEmail(String email);

    void insertLoginLog(Integer userId, boolean success, String ip);
}
