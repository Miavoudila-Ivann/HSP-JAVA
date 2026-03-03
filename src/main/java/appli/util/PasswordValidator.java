package appli.util;

public class PasswordValidator {


    // Méthode principale de validation
    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }

        // Minimum 8 caractères
        if (password.length() < 8) {
            return false;
        }

        // Vérifications avec regex
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}