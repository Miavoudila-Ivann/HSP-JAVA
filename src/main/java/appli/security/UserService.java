package appli.security;

import appli.util.ValidationUtils;

/**
 * Service utilitaire pour la validation et le hachage de mot de passe.
 */
public class UserService {

    private UserService() {}

    /**
     * Valide les deux mots de passe saisis et retourne le hash si tout est correct.
     *
     * @param password        mot de passe en clair
     * @param confirmPassword confirmation du mot de passe
     * @return hash BCrypt du mot de passe
     * @throws IllegalArgumentException si les mots de passe sont invalides ou ne correspondent pas
     */
    public static String register(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            throw new IllegalArgumentException("Champs obligatoires.");
        }

        String strengthError = ValidationUtils.validatePasswordStrength(password);
        if (strengthError != null) {
            throw new IllegalArgumentException(strengthError);
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        return PasswordHasher.hash(password);
    }
}
