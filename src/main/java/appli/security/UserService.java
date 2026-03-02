package appli.security;

public class UserService {

    public static String register(String password, String confirmPassword) {

        if (password == null || confirmPassword == null) {
            throw new IllegalArgumentException("Champs obligatoires.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("Mot de passe trop court (8 caractères minimum).");
        }

        // Hash du mot de passe
        return PasswordHasher.hash(password);
    }
}
