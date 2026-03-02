package appli.security;

public class AuthService {

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

        // Si tout est OK → hash
        return PasswordHasher.hash(password);
    }
}
